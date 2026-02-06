#!/usr/bin/env python3
"""
Analyze full PDF table structure
"""
import pdfplumber
import sys
import json

def analyze_pdf(pdf_path):
    print(f"Analyzing: {pdf_path}\n")

    with pdfplumber.open(pdf_path) as pdf:
        page = pdf.pages[0]

        # Extract text for header
        text = page.extract_text()
        lines = text.split('\n')
        print("First 5 lines:")
        for i, line in enumerate(lines[:5]):
            print(f"{i}: {line}")

        # Extract tables
        tables = page.extract_tables()
        if tables:
            table = tables[0]
            print(f"\n\nFull table structure ({len(table)} rows):")
            for i, row in enumerate(table):
                print(f"Row {i:2d}: {json.dumps(row, ensure_ascii=False)}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 analyze_pdf_full.py <pdf_path>")
        sys.exit(1)

    analyze_pdf(sys.argv[1])
