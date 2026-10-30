# Этот скрипт только помогает запускать тесты из PowerShell.
# Сами сценарии находятся в tests, а экранные действия — в screens.
param(
    [string]$JavaHome,
    [string]$AdbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    [string]$Serial,
    [string]$TestClass
)

$ErrorActionPreference = 'Stop'
# Находим корень проекта относительно папки scripts, независимо от текущей папки терминала.
$projectRoot = Split-Path -Parent $PSScriptRoot
if ($JavaHome) {
    $env:JAVA_HOME = $JavaHome
}
if (-not (Test-Path -LiteralPath $AdbPath -PathType Leaf)) {
    throw "Не найден adb: $AdbPath"
}
if ($Serial -and $Serial -notmatch '^[A-Za-z0-9_.:-]+$') {
    throw 'Недопустимый серийный номер устройства.'
}
if ($TestClass -and $TestClass -notmatch '^ru\.edu\.qamid\.tests\.[A-Za-z]+(#[A-Za-z0-9_]+)?$') {
    throw 'Укажите класс ru.edu.qamid.tests.ИмяКласса или класс#метод.'
}
# adb — программа из Android SDK, которая устанавливает APK и общается с эмулятором.
$adbArguments = @()
if ($Serial) {
    $adbArguments = @('-s', $Serial)
}
# Проверяем выбор устройства, чтобы случайно не запустить тесты на другом подключённом телефоне.
$connectedDevices = @(& $AdbPath devices | Select-Object -Skip 1 |
    Where-Object { $_ -match '^\S+\s+device$' } | ForEach-Object { ($_ -split '\s+')[0] })
if ($Serial) {
    if ($Serial -notin $connectedDevices) {
        throw "Устройство $Serial не подключено или не разрешено."
    }
} elseif ($connectedDevices.Count -ne 1) {
    throw 'Подключите одно устройство либо укажите -Serial.'
}

# В конце вернём прежнюю настройку устройства в терминале.
$previousAndroidSerial = $env:ANDROID_SERIAL
$runFailed = $true
$apkStaging = $null
$previousAnimations = @{}
Push-Location -LiteralPath $projectRoot
try {
    if ($Serial) {
        $env:ANDROID_SERIAL = $Serial
    }
    # Собираем два файла: приложение и отдельный APK с нашими тестами.
    & .\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw 'Не удалось собрать приложение и тестовый APK.'
    }
    # Установку выполняем отдельно: в этом окружении установщик Gradle зависал.
    # Временная папка также убирает кириллицу из пути к передаваемому APK.
    $apkStaging = Join-Path ([System.IO.Path]::GetTempPath()) ("fmh-smoke-" + [guid]::NewGuid().ToString('N'))
    $null = New-Item -ItemType Directory -Path $apkStaging
    Copy-Item -LiteralPath 'app\build\outputs\apk\debug\app-debug.apk' -Destination (Join-Path $apkStaging 'app.apk')
    Copy-Item -LiteralPath 'app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk' -Destination (Join-Path $apkStaging 'tests.apk')
    # Останавливаем прежний процесс перед установкой, но не очищаем данные приложения.
    & $AdbPath @adbArguments shell am force-stop ru.edu.qamid
    & $AdbPath @adbArguments shell am force-stop ru.edu.qamid.test
    foreach ($apkName in @('app.apk', 'tests.apk')) {
        & $AdbPath @adbArguments install --no-streaming -r (Join-Path $apkStaging $apkName)
        if ($LASTEXITCODE -ne 0) {
            throw "Не удалось установить $apkName."
        }
    }
    # Прямой запуск adb обходит testOptions Gradle. Отключаем анимации на время прогона,
    # а в finally возвращаем прежние значения настроек устройства.
    foreach ($animation in @('window_animation_scale', 'transition_animation_scale', 'animator_duration_scale')) {
        $previousAnimations[$animation] = ((& $AdbPath @adbArguments shell settings get global $animation) -join '').Trim()
        & $AdbPath @adbArguments shell settings put global $animation 0 | Out-Null
    }
    # Убираем только предыдущий отчёт, чтобы не смешивать результаты разных запусков.
    & $AdbPath @adbArguments shell run-as ru.edu.qamid test -d files/allure-results 2>$null
    if ($LASTEXITCODE -eq 0) {
        & $AdbPath @adbArguments shell run-as ru.edu.qamid rm -r files/allure-results
        if ($LASTEXITCODE -ne 0) {
            throw 'Не удалось очистить результаты предыдущего прогона.'
        }
    }
    # Без TestClass запускается весь пакет; с ним можно проверить один класс или метод.
    $testSelection = @('-e', 'package', 'ru.edu.qamid.tests')
    if ($TestClass) { $testSelection = @('-e', 'class', $TestClass) }
    # Журнал пополняется во время прогона, а не только в самом конце.
    $runOutput = @(& $AdbPath @adbArguments shell am instrument -w -r @testSelection ru.edu.qamid.test/androidx.test.runner.AndroidJUnitRunner 2>&1 |
        Tee-Object -FilePath (Join-Path $projectRoot 'build\smoke-last-run.log'))
    $adbExitCode = $LASTEXITCODE
    $runOutput
    # Сам adb иногда завершает команду успешно даже при красном тесте: проверяем итог JUnit.
    # Подойдёт и после добавления новых тестов; пустой прогон из нуля тестов успехом не считаем.
    $runFailed = $adbExitCode -ne 0 -or (($runOutput -join "`n") -notmatch '(?m)^OK \([1-9]\d* tests?\)\s*$')
    # Даже при BUG-003 выгружаем результаты; затем скрипт сообщит о неуспешном прогоне.
    $testArtifactDirectory = Join-Path $projectRoot ("build\test-artifacts\" + (Get-Date -Format 'yyyyMMdd-HHmmss-fff'))
    & (Join-Path $PSScriptRoot 'Export-Allure.ps1') -AdbPath $AdbPath -Serial $Serial -OutputDirectory $testArtifactDirectory
    Copy-Item -LiteralPath (Join-Path $projectRoot 'build\smoke-last-run.log') -Destination (Join-Path $testArtifactDirectory 'instrumentation.log')
} finally {
    foreach ($animation in $previousAnimations.Keys) {
        if ($previousAnimations[$animation] -eq 'null') {
            & $AdbPath @adbArguments shell settings delete global $animation | Out-Null
        } else {
            & $AdbPath @adbArguments shell settings put global $animation $previousAnimations[$animation] | Out-Null
        }
    }
    # Удаляем только два созданных нами временных APK и их пустую папку.
    if ($apkStaging -and (Test-Path -LiteralPath $apkStaging)) {
        foreach ($apkName in @('app.apk', 'tests.apk')) {
            $temporaryApk = Join-Path $apkStaging $apkName
            if (Test-Path -LiteralPath $temporaryApk) {
                Remove-Item -LiteralPath $temporaryApk
            }
        }
        Remove-Item -LiteralPath $apkStaging
    }
    Pop-Location
    $env:ANDROID_SERIAL = $previousAndroidSerial
}
if ($runFailed) {
    throw 'Прогон завершился с ошибкой. Проверьте build/smoke-last-run.log и выгруженные Allure-результаты.'
}
