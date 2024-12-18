import os
import shutil
from glob import glob
from pathlib import Path

# Source directory
source_dir = "/home/ubuntu/git/Polypheny-UI/dist"

# Base target directory with wildcard
base_target_dir = "/home/ubuntu/.polypheny/*/ui"

# Get all matching target directories
target_dirs = glob(base_target_dir)

# Copy files from source to each target directory
for target_dir in target_dirs:
    if os.path.isdir(target_dir):  # Ensure it's a directory
        print(f"Copying to: {target_dir}")
        # Use shutil.copytree to copy, handling overwriting manually
        for root, _, files in os.walk(source_dir):
            relative_path = os.path.relpath(root, source_dir)
            dest_dir = os.path.join(target_dir, relative_path)
            Path(dest_dir).mkdir(exist_ok=True, parents=True)

            for file in files:
                source_file = os.path.join(root, file)
                dest_file = os.path.join(dest_dir, file)
                shutil.copy2(source_file, dest_file)  # Overwrite existing files
    else:
        print(f"Skipping non-directory: {target_dir}")

print("Copy operation completed.")