Set-Location "$PSScriptRoot"

# Stop backend on 8085 if active
$conn = Get-NetTCPConnection -LocalPort 8085 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $conn) {
  Stop-Process -Id $conn.OwningProcess -Force
  Write-Output "Stopped backend process on port 8085"
} else {
  Write-Output "No backend process found on port 8085"
}

# Stop frontend on 5173 if active
$frontendConn = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $frontendConn) {
  Stop-Process -Id $frontendConn.OwningProcess -Force
  Write-Output "Stopped frontend process on port 5173"
} else {
  Write-Output "No frontend process found on port 5173"
}

