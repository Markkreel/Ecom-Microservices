#!/usr/bin/env python3
import os
import sys
import subprocess
from pathlib import Path


def run_migrations(service_name):
    service_path = Path(__file__).parent.parent / "services" / service_name

    if not service_path.exists():
        print(f"Error: Service '{service_name}' not found")
        sys.exit(1)

    if service_name == "order-service":
        # Run alembic migrations for order service
        try:
            subprocess.run(["alembic", "upgrade", "head"], cwd=service_path, check=True)
            print(f"Successfully migrated database for {service_name}")
        except subprocess.CalledProcessError as e:
            print(f"Error running migrations for {service_name}: {e}")
            sys.exit(1)
    else:
        print(f"No database migrations configured for {service_name}")


def main():
    if len(sys.argv) < 2:
        print("Usage: python db_migrate.py <service-name>")
        print("Example: python db_migrate.py order-service")
        sys.exit(1)

    service_name = sys.argv[1]
    run_migrations(service_name)


if __name__ == "__main__":
    main()
