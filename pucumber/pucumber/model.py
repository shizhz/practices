#!/usr/bin/env python
# encoding: utf-8

from enum import Enum

class Status(Enum):
    SUCCEEDED = True
    FAILED = False

class Feature(object):
    def __init__(self):
        self.desc = ''
        self.status = None
        self.scenarios = []
        self.tags = []

    def add_scenario(self, scenario):
        self.scenario.push(scenario)

    def add_tag(self, tag):
        self.tags.push(tag)

    def run(self):
        self.status = Status.SUCCEEDED
        for scenario in self.scenarios:
            scenario.run()
            self.status = self.status and scenario.status

class Scenario(object):
    def __init__(self):
        self.desc = ""
        self.status = None
        self.steps = []

    def add_step(self, step):
        self.steps.push(step)

    def run(self):
        self.status = Status.SUCCEEDED
        for step in self.steps:
            step.run()
            self.status = self.status and step.status

class Context(object): pass
