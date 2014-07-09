#!/usr/bin/env python
# encoding: utf-8

import os

from behave import given, when, then


@given(u'I am in a writable directory')
def in_writable_directory(context):
    os.chdir(context.test_directory)

    assert os.access(os.getcwd(), os.W_OK)

@when(u'I run "pucumber init"')
def run_pucumber_init(context):
    status_code = os.system('pucumber init')
    assert status_code is 0

@then(u'I will get the following directory layout')
def check_basic_directory_layout(context):
    test_directory = context.test_directory

    assert os.path.isdir(os.path.join(test_directory, 'features'))
    assert os.path.isfile(os.path.join(test_directory, 'features', 'all.feature'))
    assert os.path.isdir(os.path.join(test_directory, 'features', 'steps'))
    assert os.path.isfile(os.path.join(test_directory, 'features', 'steps', 'all_steps.py'))

@given(u'I am in a directory with pucumber basic directory')
def in_pucumber_root_directory(context):
    assert os.path.isdir(os.path.join(context.test_directory, 'features'))

@when(u'I run "pucumber cleanup"')
def run_pucumber_cleanup(context):
    status_code = os.system('pucumber cleanup')
    assert status_code is 0

@then(u'The directory "features" will be removed recursively')
def check_cleanup(context):
    assert not os.path.isdir(os.path.join(context.test_directory, 'features'))

