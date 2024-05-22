#!/bin/bash

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

