# PowerShell script to run tests for all microservices

$ErrorActionPreference = 'Stop'

# Define services to test
$services = @(
    "notification-service",
    "order-service",
    "product-service",
    "user-service"
)

# Function to run tests for a service
function Run-ServiceTests {
    param (
        [string]$serviceName
    )
    
    Write-Host "`n=== Running tests for $serviceName ==="

    $scriptPath = $PSScriptRoot
    $serviceDir = Join-Path $scriptPath "..\services\$serviceName"
    if (-not (Test-Path $serviceDir)) {
        Write-Host "Service directory not found: $serviceDir" -ForegroundColor Red
        return $false
    }

    # Build test image
    Write-Host "Building test image for $serviceName..."
    docker build -t "${serviceName}-test" -f "$serviceDir\Dockerfile.test" "$serviceDir"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to build test image for $serviceName" -ForegroundColor Red
        return $false
    }

    # Run tests in container
    Write-Host "Running tests for $serviceName..."
    docker run --rm "${serviceName}-test"
    $testResult = $LASTEXITCODE

    # Clean up
    Write-Host "Cleaning up test image..."
    docker rmi "${serviceName}-test" -f

    return $testResult -eq 0
}

# Main execution
$failedServices = @()
$successCount = 0
$totalServices = $services.Count

Write-Host "Starting test execution for all microservices..."

foreach ($service in $services) {
    if (Run-ServiceTests -serviceName $service) {
        $successCount++
        Write-Host "✅ $service tests passed" -ForegroundColor Green
    } else {
        $failedServices += $service
        Write-Host "❌ $service tests failed" -ForegroundColor Red
    }
}

# Summary
Write-Host "`n=== Test Execution Summary ==="
Write-Host "Total services: $totalServices"
Write-Host "Successful: $successCount" -ForegroundColor Green
Write-Host "Failed: $($failedServices.Count)" -ForegroundColor Red

if ($failedServices.Count -gt 0) {
    Write-Host "`nFailed services:"
    foreach ($service in $failedServices) {
        Write-Host "- $service" -ForegroundColor Red
    }
    exit 1
}

Write-Host "`nAll tests completed successfully!" -ForegroundColor Green
exit 0