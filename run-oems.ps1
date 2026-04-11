Set-Location "$PSScriptRoot"

# Stop stale backend listener on 8085 if present
$conn = Get-NetTCPConnection -LocalPort 8085 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $conn) {
  Stop-Process -Id $conn.OwningProcess -Force
  Start-Sleep -Seconds 1
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
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\backend'; mvn spring-boot:run"

# Start frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\frontend'; npm run dev"

Write-Output "Started OEMS backend and frontend in separate terminals."
Write-Output "Frontend: http://localhost:5173"
Write-Output "Backend:  http://localhost:8085"
