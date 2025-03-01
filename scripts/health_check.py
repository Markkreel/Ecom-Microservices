#!/usr/bin/env python3
import sys
import requests
import json
from typing import Dict, List

# Service endpoints configuration
SERVICE_ENDPOINTS = {
    "user-service": "http://localhost:8081/api/health",
    "product-service": "http://localhost:8082/api/health",
    "order-service": "http://localhost:8083/api/health",
    "notification-service": "http://localhost:8084/api/health",
}


def check_service_health(service_name: str, endpoint: str) -> Dict:
    try:
        response = requests.get(endpoint, timeout=5)
        status = "UP" if response.status_code == 200 else "DOWN"
        return {
            "service": service_name,
            "status": status,
            "statusCode": response.status_code,
        }
    except requests.RequestException as e:
        return {"service": service_name, "status": "DOWN", "error": str(e)}


def check_all_services() -> List[Dict]:
    results = []
    for service, endpoint in SERVICE_ENDPOINTS.items():
        health_status = check_service_health(service, endpoint)
        results.append(health_status)
    return results


def main():
    results = check_all_services()
    print(json.dumps(results, indent=2))

    # Check if any service is down
    down_services = [r for r in results if r["status"] == "DOWN"]
    if down_services:
        print("\nWarning: Some services are down!")
        sys.exit(1)
    sys.exit(0)


if __name__ == "__main__":
    main()
