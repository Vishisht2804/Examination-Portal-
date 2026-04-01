Set-Location "$PSScriptRoot"

# Stop backend on 8081 if active
$conn = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($null -ne $conn) {
  Stop-Process -Id $conn.OwningProcess -Force
  Write-Output "Stopped backend process on port 8081"
} else {
  Write-Output "No backend process found on port 8081"
}

# Optionally stop Vite by process name (best-effort)
Get-Process -Name node -ErrorAction SilentlyContinue | ForEach-Object {
  try {
    Stop-Process -Id $_.Id -Force
  } catch {}
}
Write-Output "Stopped node processes (if any)."
