#!/bin/bash

# Exit on error
set -e

CONFIG_FILE=$1

# Basic check if config exists
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Error: Config file $CONFIG_FILE not found!"
    exit 1
fi

# Extract identifying info
FEATURE_NAME=$(yq '.name' "$CONFIG_FILE")

echo "--- 🗑️ Destroying Feature Stack: $FEATURE_NAME ---"

# 1. Kill any existing port-forwards for this feature
echo "Cleaning up local port-forwards..."
# Kills processes listening on the specific Nakama ports used in deploy.sh
lsof -ti:7350,7349,7351 | xargs kill -9 2>/dev/null || true

# 2. Uninstall Helm Charts
# We use --wait to ensure resources are deleted before we remove the namespace
echo "Uninstalling Nakama..."
helm uninstall nakama -n "$FEATURE_NAME" --wait || echo "Nakama already uninstalled."

echo "Uninstalling CockroachDB..."
helm uninstall cockroachdb -n "$FEATURE_NAME" --wait || echo "CockroachDB already uninstalled."

echo "Uninstalling Prometheus..."
helm uninstall prometheus -n "$FEATURE_NAME" --wait || echo "Prometheus already uninstalled."

# 3. Delete Agones Fleet
# We use the generated temp file if it exists, or delete by label
echo "Deleting Agones Fleet..."
if [ -f "fleet-tmp.yaml" ]; then
    kubectl delete -n default -f fleet-tmp.yaml --ignore-not-found
else
    # Fallback: Delete the fleet specifically named for this feature
    kubectl delete fleet "fleet-$FEATURE_NAME" -n default --ignore-not-found
fi

# 4. Remove the Namespace
# This is the "scorched earth" step that catches any remaining stray resources
echo "Deleting Namespace: $FEATURE_NAME..."
kubectl delete ns "$FEATURE_NAME" --ignore-not-found

echo "--- ✅ Cleanup of $FEATURE_NAME Complete ---"