$ErrorActionPreference = "Stop"

$envPath = Join-Path $PSScriptRoot ".env"
if (Test-Path $envPath) {
    foreach ($rawLine in Get-Content $envPath) {
        $line = $rawLine.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
            continue
        }
        $parts = $line -split "=", 2
        if ($parts.Length -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim().Trim('"').Trim("'")
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

Set-Location $PSScriptRoot
mvn spring-boot:run

