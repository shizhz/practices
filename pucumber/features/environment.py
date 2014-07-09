#!/usr/bin/env python
# encoding: utf-8

import os

def before_feature(context, feature):
    if 'init_cleanup' in feature.tags:
        context.working_directory = os.getcwd()
        context.test_directory = '/tmp/'

def after_feature(context, feature):
    if 'init_cleanup' in feature.tags:
        del context.working_directory
        del context.test_directory
