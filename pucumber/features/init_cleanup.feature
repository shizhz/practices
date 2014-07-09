@init_cleanup
Feature: Create/cleanup pucumber directory layout

    As a pucumber user
    I want to use pucumber to create a basic feature directory layout
    So that I can know where to put features and steps and get started easily

    | Basic Directory Layout:
    |   features/
    |      +- *.features
    |      +- steps/*.py

    Scenario: Create pucumber basic directory layout
        Given I am in a writable directory
        When I run "pucumber init"
        Then I will get the following directory layout:
        '''
            | +- features/
            | +- features/all.feature
            | +- features/steps/
            | +- features/steps/all_steps.py
        '''

    Scenario: Cleanup pucumber basic directory layout
        Given I am in a directory with pucumber basic directory
        When I run "pucumber cleanup"
        Then The directory "features" will be removed recursively

