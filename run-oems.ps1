Set-Location "$PSScriptRoot"

# Resolve Java 21+ for backend runtime to avoid class version mismatch.
$javaHome = $null
$candidateJavaHomes = @(
  "C:\Users\$env:USERNAME\.jdk\jdk-21.0.8",
  "C:\Program Files\Java\jdk-25",
  "C:\Program Files\Java\jdk-21",
  "C:\Program Files\Microsoft\jdk-21.0.8.9-hotspot"
)

foreach ($candidate in $candidateJavaHomes) {
  if (Test-Path (Join-Path $candidate "bin\java.exe")) {
    $javaHome = $candidate
    break
  }
}

if ($null -eq $javaHome) {
  Write-Warning "Java 21+ not found in known locations. Backend may fail if default java is below 21."
}

# Stop stale backend listener on 8085 if present
$conn = Get-NetTCPConnection -LocalPort 8085 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $conn) {
  if ($conn.OwningProcess -gt 4) {
    Stop-Process -Id $conn.OwningProcess -Force
    Start-Sleep -Seconds 1
  }
}

# Ensure MongoDB is available on localhost:27017 before launching backend.
$mongoReady = $false

$mongoConn = Get-NetTCPConnection -LocalPort 27017 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $mongoConn) {
  $mongoReady = $true
}

if (-not $mongoReady) {
  $mongoService = Get-Service -Name MongoDB -ErrorAction SilentlyContinue
  if ($null -ne $mongoService) {
    if ($mongoService.Status -ne "Running") {
      try {
        Start-Service -Name MongoDB
        Write-Output "Started MongoDB Windows service."
        $mongoReady = $true
      } catch {
        Write-Warning "MongoDB service exists but could not be started. Try running PowerShell as Administrator and execute: Start-Service MongoDB"
      }
    } else {
      $mongoReady = $true
    }
  } else {
    Write-Warning "MongoDB Windows service not found. Install MongoDB Community Server and ensure it listens on localhost:27017"
  }
}

if (-not $mongoReady) {
  Write-Warning "Backend may fail because MongoDB is unreachable at mongodb://localhost:27017"
}

# Start backend
if ($null -ne $javaHome) {
  Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\backend'; `$env:JAVA_HOME='$javaHome'; `$env:Path='${javaHome}\\bin;' + `$env:Path; mvn spring-boot:run"
} else {
  Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\backend'; mvn spring-boot:run"
}

# Start frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\frontend'; npm run dev"

Write-Output "Started OEMS backend and frontend in separate terminals."
Write-Output "Frontend: http://localhost:5173"
Write-Output "Backend:  http://localhost:8085"
if ($null -ne $javaHome) {
  Write-Output "Backend JAVA_HOME: $javaHome"
}
