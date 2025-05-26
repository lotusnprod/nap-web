#!/usr/bin/env python3
"""
Coverage Parser for Kover XML Reports

This tool parses Kover coverage reports and displays coverage information
sorted by coverage percentage, focusing on files that need improvement.
"""

import xml.etree.ElementTree as ET
import sys
import argparse
from pathlib import Path


def parse_coverage(xml_file):
    """Parse Kover XML coverage report and return coverage data."""
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    classes = []
    
    for package in root.findall('.//package'):
        for class_elem in package.findall('class'):
            class_name = class_elem.get('name')
            source_file = class_elem.get('sourcefilename')
            
            # Get the class-level LINE counter (last one in the class element)
            line_counters = class_elem.findall('.//counter[@type="LINE"]')
            line_counter = line_counters[-1] if line_counters else None
            
            if line_counter is not None:
                missed = int(line_counter.get('missed', '0'))
                covered = int(line_counter.get('covered', '0'))
                total = missed + covered
                
                if total > 0:
                    coverage = (covered / total) * 100
                    classes.append({
                        'name': class_name,
                        'file': source_file,
                        'missed': missed,
                        'covered': covered,
                        'total': total,
                        'coverage': coverage
                    })
    
    return classes


def filter_by_coverage(classes, min_coverage=0, max_coverage=100):
    """Filter classes by coverage percentage range."""
    return [c for c in classes if min_coverage <= c['coverage'] <= max_coverage]


def main():
    parser = argparse.ArgumentParser(description='Parse Kover coverage reports')
    parser.add_argument(
        'report_file',
        nargs='?',
        default='build/reports/kover/report.xml',
        help='Path to Kover XML report (default: build/reports/kover/report.xml)'
    )
    parser.add_argument(
        '--min',
        type=float,
        default=0,
        help='Minimum coverage percentage to show (default: 0)'
    )
    parser.add_argument(
        '--max',
        type=float,
        default=99.99,
        help='Maximum coverage percentage to show (default: 99.99)'
    )
    parser.add_argument(
        '--file',
        help='Filter by source file name (partial match)'
    )
    parser.add_argument(
        '--limit',
        type=int,
        default=20,
        help='Maximum number of results to show (default: 20)'
    )
    parser.add_argument(
        '--sort',
        choices=['coverage', 'name', 'file'],
        default='coverage',
        help='Sort results by (default: coverage)'
    )
    parser.add_argument(
        '--reverse',
        action='store_true',
        help='Reverse sort order'
    )
    
    args = parser.parse_args()
    
    # Check if report file exists
    report_path = Path(args.report_file)
    if not report_path.exists():
        print(f"Error: Report file not found: {args.report_file}")
        sys.exit(1)
    
    try:
        # Parse coverage data
        classes = parse_coverage(args.report_file)
        
        # Filter by coverage range
        filtered = filter_by_coverage(classes, args.min, args.max)
        
        # Filter by file name if specified
        if args.file:
            filtered = [c for c in filtered if args.file.lower() in c['file'].lower()]
        
        # Sort results
        if args.sort == 'coverage':
            filtered.sort(key=lambda x: x['coverage'], reverse=not args.reverse)
        elif args.sort == 'name':
            filtered.sort(key=lambda x: x['name'], reverse=args.reverse)
        elif args.sort == 'file':
            filtered.sort(key=lambda x: x['file'], reverse=args.reverse)
        
        # Display results
        if not filtered:
            print("No classes found matching the criteria.")
            return
        
        print(f"Classes with coverage between {args.min}% and {args.max}%:")
        print("-" * 100)
        print(f"{'Coverage':>8} | {'Covered':>7} | {'Total':>7} | {'File':<30} | Class")
        print("-" * 100)
        
        for i, cls in enumerate(filtered[:args.limit]):
            print(f"{cls['coverage']:7.2f}% | {cls['covered']:7d} | {cls['total']:7d} | "
                  f"{cls['file']:<30} | {cls['name']}")
        
        if len(filtered) > args.limit:
            print(f"\n... and {len(filtered) - args.limit} more results")
        
        # Summary statistics
        total_lines = sum(c['total'] for c in filtered)
        covered_lines = sum(c['covered'] for c in filtered)
        if total_lines > 0:
            overall_coverage = (covered_lines / total_lines) * 100
            print(f"\nOverall coverage for filtered results: {overall_coverage:.2f}% "
                  f"({covered_lines}/{total_lines} lines)")
    
    except Exception as e:
        print(f"Error parsing coverage report: {e}")
        sys.exit(1)


if __name__ == '__main__':
    main()