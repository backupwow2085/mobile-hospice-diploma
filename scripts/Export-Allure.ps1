# Выгрузка результатов Allure из приватной папки debug-приложения на компьютер.
param(
    [string]$AdbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    [string]$Serial,
    [string]$OutputDirectory
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $projectRoot ("build\test-artifacts\" + (Get-Date -Format 'yyyyMMdd-HHmmss-fff'))
}
if ($Serial -and $Serial -notmatch '^[A-Za-z0-9_.:-]+$') {
    throw 'Недопустимый серийный номер устройства.'
}
if (-not (Test-Path -LiteralPath $AdbPath -PathType Leaf)) {
    throw "Не найден adb: $AdbPath"
}
if (Test-Path -LiteralPath $OutputDirectory) {
    throw "Для выгрузки укажите новую папку: $OutputDirectory"
}

$null = New-Item -ItemType Directory -Path $OutputDirectory
$OutputDirectory = (Resolve-Path -LiteralPath $OutputDirectory).Path
# Android сначала передаёт папку целиком как tar-архив; ниже мы распакуем его и сделаем ZIP.
$archivePath = Join-Path $OutputDirectory 'allure-results.tar'
$serialArguments = if ($Serial) { "-s $Serial " } else { '' }
# Запускаем adb с чтением бинарного потока. Эти настройки нужны для целостности архива.
$startInfo = New-Object System.Diagnostics.ProcessStartInfo
$startInfo.FileName = $AdbPath
$startInfo.Arguments = $serialArguments + 'exec-out run-as ru.edu.qamid tar -C files -cf - allure-results'
$startInfo.UseShellExecute = $false
$startInfo.CreateNoWindow = $true
$startInfo.RedirectStandardOutput = $true
$startInfo.RedirectStandardError = $true
$process = New-Object System.Diagnostics.Process
$process.StartInfo = $startInfo
$destination = [System.IO.File]::Create($archivePath)
try {
    $null = $process.Start()
    $errorTask = $process.StandardError.ReadToEndAsync()
    # Копируем байты напрямую. Обычное перенаправление через > в старом PowerShell портит архив.
    $process.StandardOutput.BaseStream.CopyTo($destination)
    $process.WaitForExit()
    $diagnostic = $errorTask.GetAwaiter().GetResult()
    if ($process.ExitCode -ne 0) {
        throw "Не удалось получить Allure-результаты с устройства: $diagnostic"
    }
} finally {
    # Закрываем файл и процесс и в случае успешной передачи, и при ошибке.
    $destination.Dispose()
    $process.Dispose()
}

# Проверяем, что архив распаковался и содержит результаты тестов, а не только пустую папку.
& tar.exe -xf $archivePath -C $OutputDirectory
if ($LASTEXITCODE -ne 0) {
    throw 'Не удалось распаковать результаты Allure.'
}
$resultsDirectory = Join-Path $OutputDirectory 'allure-results'
if (-not (Get-ChildItem -LiteralPath $resultsDirectory -Filter '*-result.json' -File)) {
    throw 'В выгрузке нет результатов выполненных тестов.'
}
# ZIP удобен для передачи и последующей сдачи; исходную папку тоже сохраняем.
$zipPath = Join-Path $OutputDirectory 'allure-results.zip'
Compress-Archive -LiteralPath $resultsDirectory -DestinationPath $zipPath
Write-Output "Результаты Allure: $resultsDirectory"
Write-Output "Архив: $zipPath"
