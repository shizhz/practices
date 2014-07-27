#!/usr/bin/env python
# encoding: utf-8

import os
import sys
import shutil
import types

from parser import FeatureParseStateMachine, steps_registry, given, when, then
from model import Context

BASE_DIR = os.getcwd()

FEATURE_DIR = os.path.join(BASE_DIR, "features")
STEP_DIR = os.path.join(FEATURE_DIR, "steps")
FEATURE_FILE = os.path.join(FEATURE_DIR, "all.feature")
STEP_FILE = os.path.join(STEP_DIR, "all_steps.py")

def cleanup():
    shutil.rmtree(FEATURE_DIR)

def init():
    try:
        os.mkdir(FEATURE_DIR)
        os.mkdir(STEP_DIR)

        with open(FEATURE_FILE, 'w') as feature:
            feature.write("# Write your feature from here")

        with open(STEP_FILE, 'w') as step:
            step.write("# Define your scenario steps here")
    except Exception, e:
        print e
        sys.exit(1)

def run_pucumber():
    def load_all_step_files():
        for root, dirs, files in os.walk(STEP_DIR):
            step_files = map(lambda f: os.path.join(root, f), filter(lambda f: f.endswith('.py'), files))
            for step_file in step_files:
                execfile(step_file)

    def parse_all_feature_files():
        features = []
        for feature_file in filter(lambda f: os.path.isfile(f) and f.endswith('.feature'),\
                map(lambda f: os.path.join(FEATURE_DIR, f), os.listdir(FEATURE_DIR))):
            featureStateMachine = FeatureParseStateMachine()
            features.append(featureStateMachine.run(feature_file))

        return features

    def load_environment():
        execfile(os.path.join(FEATURE_DIR, 'environment.py'))

    def get_hook(hook_name):
        return globals()[hook_name] if globals().has_key(hook_name) and isinstance(globals()[hook_name], types.FunctionType) else lambda c: None


    load_all_step_files()
    from parser import steps_registry

    context = Context()
    before_feature = get_hook('before_feature')
    after_feature = get_hook('after_feature')

    for feature in parse_all_feature_files():
        print feature.desc
        before_feature(context)
        scenarios_result = [] # tuples, (result, succeeded_step_number, failed_step_number)
        for scenario in feature.scenarios:
            print scenario.desc
            steps_result = []
            for step in scenario.steps:
                print step, ':',
                step_pattern, step_func = steps_registry.get_step_definition(step)
                if not step_pattern:
                    print 'UNDEFINED'
                    steps_result.append(False)
                else:
                    try:
                        step_func(context)
                    except Exception, e:
                        print 'FAILED'
                        steps_result.append(False)
                    else:
                        print 'SUCCEEDED'
                        steps_result.append(True)
            scenarios_result.append((reduce(lambda x, y: x and y, steps_result), sum(map(lambda x : 1 if x else 0, steps_result)), sum(map(lambda x : 0 if x else 1, steps_result))))

        after_feature(context)

if __name__ == '__main__':
    try:
        globals()[sys.argv[1]]()
    except Exception, e:
        run_pucumber()
