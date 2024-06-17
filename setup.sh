#!/bin/bash
set -e

# Function to create the .env file if it doesn't exist
add-env() {
  local ENV_FILE=".env"
  local ENV_DIST_FILE=".env.dist"

  # Check if the .env file exists
  if [ ! -f "$ENV_FILE" ]; then
    echo "$ENV_FILE not found. Creating from $ENV_DIST_FILE."
    if [ -f "$ENV_DIST_FILE" ]; then
      cp "$ENV_DIST_FILE" "$ENV_FILE"
      echo "$ENV_FILE created."
    else
      echo "$ENV_DIST_FILE not found. Please create this file with the necessary environment variables."
    fi
  else
    echo "$ENV_FILE already exists. No changes made."
  fi
}

# Function to create the inventory file if it doesn't exist
add-inventory() {
    local INV_FILE="ansible/inventory"
    local INV_DIST_FILE="ansible/inventory.dist"

    if [ ! -f "$INV_FILE" ]; then
        echo "$INV_FILE not found. Creating from $INV_DIST_FILE."
        if [ -f "$INV_DIST_FILE" ]; then
            cp "$INV_DIST_FILE" "$INV_FILE"
            echo "$INV_FILE created."
        else
            echo "$INV_DIST_FILE not found. Please create this file with the necessary content."
        fi
    else
        echo "$INV_FILE already exists. No changes made."
    fi
}

# Function to create the vars.yml file if it doesn't exist
add-vars() {
    local VARS_FILE="ansible/vars.yml"
    local VARS_DIST_FILE="ansible/vars.dist.yml"

    if [ ! -f "$VARS_FILE" ]; then
        echo "$VARS_FILE not found. Creating from $VARS_DIST_FILE."
        if [ -f "$VARS_DIST_FILE" ]; then
            cp "$VARS_DIST_FILE" "$VARS_FILE"
            echo "$VARS_FILE created."
        else
            echo "$VARS_DIST_FILE not found. Please create this file with the necessary variables."
        fi
    else
        echo "$VARS_FILE already exists. No changes made."
    fi
}

main() {
    case "$1" in
        add-env)
            add-env
            ;;
        add-inventory)
            add-inventory
            ;;
        add-vars)
            add-vars
            ;;
        *)
            echo "Usage: $0 {add-env|add-inventory|add-vars}"
            ;;
    esac
}

main "$@"