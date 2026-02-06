#!/usr/bin/env python3
"""
Analyze PDF structure to understand schedule format
"""
import pdfplumber
import sys

def analyze_pdf(pdf_path):
    print(f"Analyzing: {pdf_path}\n")

    with pdfplumber.open(pdf_path) as pdf:
        print(f"Total pages: {len(pdf.pages)}\n")

        for page_num, page in enumerate(pdf.pages, 1):
            print(f"=== Page {page_num} ===")
            print(f"Size: {page.width} x {page.height}")

            # Extract text
            text = page.extract_text()
            print(f"\n--- Text (first 500 chars) ---")
            print(text[:500])

            # Extract tables
            tables = page.extract_tables()
            print(f"\n--- Tables found: {len(tables)} ---")

            for table_num, table in enumerate(tables, 1):
                print(f"\nTable {table_num}:")
                print(f"Rows: {len(table)}, Columns: {len(table[0]) if table else 0}")

                # Print first few rows
                for row_num, row in enumerate(table[:10], 1):
                    print(f"Row {row_num}: {row}")

            print("\n" + "="*50 + "\n")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 analyze_pdf.py <pdf_path>")
        sys.exit(1)

    analyze_pdf(sys.argv[1])
