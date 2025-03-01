#!/usr/bin/env python3
import sys
import subprocess
import platform
from pathlib import Path


def check_python_version():
    required_version = (3, 9)
    current_version = sys.version_info[:2]
    if current_version < required_version:
        print(
            f"Error: Python {required_version[0]}.{required_version[1]} or higher is required"
        )
        return False
    return True


def check_java_version():
    try:
        result = subprocess.run(
            ["java", "-version"],
            capture_output=True,
            text=True,
            stderr=subprocess.STDOUT,
            check=False,
        )
        if "version" not in result.stdout.lower():
            print("Error: Java is not installed")
            return False
        return True
    except FileNotFoundError:
        print("Error: Java is not installed")
        return False


def check_node_version():
    try:
        result = subprocess.run(
            ["node", "-v"], capture_output=True, text=True, check=False
        )
        version = result.stdout.strip().lstrip("v").split(".")
        if int(version[0]) < 14:
            print("Error: Node.js 14 or higher is required")
            return False
        return True
    except FileNotFoundError:
        print("Error: Node.js is not installed")
        return False


def check_docker():
    try:
        subprocess.run(["docker", "--version"], check=True, capture_output=True)
        subprocess.run(["docker-compose", "--version"], check=True, capture_output=True)
        return True
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("Error: Docker and Docker Compose are required")
        return False


def install_python_dependencies():
    services_dir = Path(__file__).parent.parent / "services"
    for service in services_dir.iterdir():
        requirements = service / "requirements.txt"
        if requirements.exists():
            print(f"Installing Python dependencies for {service.name}...")
            subprocess.run(
                [sys.executable, "-m", "pip", "install", "-r", str(requirements)],
                check=True,
            )


def install_node_dependencies():
    services_dir = Path(__file__).parent.parent / "services"
    for service in services_dir.iterdir():
        package_json = service / "package.json"
        if package_json.exists():
            print(f"Installing Node.js dependencies for {service.name}...")
            subprocess.run(["npm", "install"], cwd=service, check=True)


def main():
    print("Checking development environment requirements...\n")

    checks = [
        ("Python", check_python_version),
        ("Java", check_java_version),
        ("Node.js", check_node_version),
        ("Docker", check_docker),
    ]

    all_passed = True
    for name, check in checks:
        print(f"Checking {name}...")
        if not check():
            all_passed = False

    if not all_passed:
        print("\nPlease install the missing requirements and try again")
        sys.exit(1)

    print("\nAll system requirements met!")
    print("\nInstalling project dependencies...")

    try:
        install_python_dependencies()
        install_node_dependencies()
        print("\nSetup completed successfully!")
    except subprocess.CalledProcessError as e:
        print(f"\nError during dependency installation: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
