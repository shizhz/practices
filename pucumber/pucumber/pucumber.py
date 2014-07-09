#!/usr/bin/env python
# encoding: utf-8

import os
import sys
import shutil

BASE_DIR = os.getcwd()

FEATURE_DIR = os.path.join(BASE_DIR, "features")
STEP_DIR = os.path.join(FEATURE_DIR, "steps")
FEATURE_FILE = os.path.join(FEATURE_DIR, "all.feature")
STEP_FILE = os.path.join(STEP_DIR, "all_steps.py")

def cleanup():
    for directory in [FEATURE_DIR, STEP_DIR]:
        if os.path.isdir(directory):
            print 'Remove: ', directory
            shutil.rmtree(directory)

def init():
    try:
        os.mkdir(FEATURE_DIR)
        os.mkdir(STEP_DIR)

        with open(FEATURE_FILE, 'w') as feature:
            feature.write("# Write your feature from here")

        with open(STEP_FILE, 'w') as step:
            step.write("# Define your scenario steps here")
    except Exception, e:
        #cleanup()
        sys.exit(1)

def run_pucumber():
    pass

if __name__ == '__main__':
    try:
        globals()[sys.argv[1]]()
    except Exception, e:
        run_pucumber()
