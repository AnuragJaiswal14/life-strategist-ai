npx prisma generate
$components = @(
    @{name="auth"; hasController=$false},
    @{name="users"; hasController=$true},
    @{name="logs"; hasController=$true},
    @{name="reports"; hasController=$true},
    @{name="prisma"; hasController=$false},
    @{name="ai"; hasController=$false}
)

foreach ($c in $components) {
    npx nest g module $($c.name)
    npx nest g service $($c.name) --no-spec
    if ($c.hasController) {
        npx nest g controller $($c.name) --no-spec
    }
}
