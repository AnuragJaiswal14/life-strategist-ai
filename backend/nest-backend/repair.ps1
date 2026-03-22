$ErrorActionPreference = "Stop"

$modules = @("auth", "users", "logs", "reports", "ai", "prisma")
foreach ($mod in $modules) {
    if (Test-Path $mod) {
        if (-Not (Test-Path "src\$mod")) {
            New-Item -ItemType Directory -Path "src\$mod" -Force | Out-Null
        }
        Copy-Item -Path "$mod\*" -Destination "src\$mod\" -Recurse -Force
        Remove-Item -Path $mod -Recurse -Force
        Write-Host "Moved $mod to src/"
    }
}

Write-Host "Running npm install..."
npm install

Write-Host "Generating Prisma client..."
npx prisma generate

Write-Host "Pushing schema to DB..."
npx prisma db push

Write-Host "Starting NestJS server..."
npm run start:dev
