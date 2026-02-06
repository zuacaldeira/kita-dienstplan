#!/bin/bash

# Extract all ZIP files from weekly-schedules directory
# Skip graphical PDFs and organize by date

SOURCE_DIR="../data/weekly-schedules"
DEST_DIR="../data/extracted-schedules"

# Create destination directory
mkdir -p "$DEST_DIR"

# Counter for tracking
total_zips=0
total_pdfs=0
skipped_pdfs=0

echo "Starting extraction of ZIP files..."
echo "Source: $SOURCE_DIR"
echo "Destination: $DEST_DIR"
echo ""

# Loop through all ZIP files
for zip_file in "$SOURCE_DIR"/*.zip; do
    if [ -f "$zip_file" ]; then
        total_zips=$((total_zips + 1))
        zip_name=$(basename "$zip_file" .zip)

        echo "[$total_zips] Processing: $zip_name"

        # Create temporary directory for extraction
        temp_dir=$(mktemp -d)

        # Extract to temp directory
        unzip -q "$zip_file" -d "$temp_dir"

        # Process extracted files
        for pdf_file in "$temp_dir"/*.pdf "$temp_dir"/**/*.pdf; do
            if [ -f "$pdf_file" ]; then
                pdf_name=$(basename "$pdf_file")

                # Skip graphical PDFs
                if [[ "$pdf_name" == *"grafisch"* ]]; then
                    echo "  Skipping graphical: $pdf_name"
                    skipped_pdfs=$((skipped_pdfs + 1))
                    continue
                fi

                # Copy to destination
                cp "$pdf_file" "$DEST_DIR/"
                echo "  Extracted: $pdf_name"
                total_pdfs=$((total_pdfs + 1))
            fi
        done

        # Clean up temp directory
        rm -rf "$temp_dir"
    fi
done

echo ""
echo "Extraction complete!"
echo "Total ZIP files processed: $total_zips"
echo "Total PDFs extracted: $total_pdfs"
echo "Graphical PDFs skipped: $skipped_pdfs"
echo ""
echo "Extracted files are in: $DEST_DIR"
