Set-Location "$PSScriptRoot"

# Stop stale backend listener on 8081 if present
$conn = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $conn) {
  Stop-Process -Id $conn.OwningProcess -Force
  Start-Sleep -Seconds 1
}

# Start backend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\backend'; mvn spring-boot:run"

# Start frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\\frontend'; npm run dev"

Write-Output "Started OEMS backend and frontend in separate terminals."
Write-Output "Frontend: http://localhost:5173"
Write-Output "Backend:  http://localhost:8081"
