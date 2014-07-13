#!/usr/bin/env python
# encoding: utf-8

import re

from model import Feature, Scenario

class InvalidStateException(Exception):
    def __init__(self, msg):
        self.msg = msg

    def __repr__(self):
        return self.msg

    def __str__(self):
        return self.msg

class FeatureParseStateMachine(object):
    """A finite state machine for parsing feature definition"""
    def __init__(self):
        self.states = ['start', '@', 'feature', 'scenario', 'given', 'when', 'then']
        self.start_state = 'start'
        self.current_state = self.start_state
        self.end_states = ['then']
        self._register_state_transisions()
        self._register_parsers()

    def _register_state_transisions(self):
        self.transisions = {}
        def register_transision(valid_states, use_current_state=True):
            def transision(line):
                if not line:
                    return self.current_state
                else:
                    determine_state = lambda states : filter(lambda s : line.strip().lower().startswith(s), states)
                    actual_state = determine_state(self.states)
                    acceptable_state = determine_state(valid_states)
                    if actual_state != acceptable_state:
                        raise InvalidStateException("The acceptable state after %s is %s, but got %s" % (self.current_state, valid_states, actual_state))
                    if not use_current_state and not actual_state:
                        raise InvalidStateException("The input line must start with one of %s" % valid_states)

                    return acceptable_state[0] if acceptable_state else self.current_state
            return transision

        self.transisions['start'] = register_transision(['@', 'feature'], use_current_state=False)
        self.transisions['@'] = register_transision(['@', 'feature'], use_current_state=False)
        self.transisions['feature'] = register_transision(['feature', 'scenario'])
        self.transisions['scenario'] = register_transision(['scenario', 'given'])
        self.transisions['given'] = register_transision(['given', 'when', 'then'])
        self.transisions['when'] = register_transision(['when', 'then'])
        self.transisions['then'] = register_transision(['then', 'scenario'])

    def _register_parsers(self):
        self.parsers = {}

        self.parsers['@'] = lambda feature, line : feature.tags.append(line[1:].strip())
        self.parsers['feature'] = lambda feature, line: setattr(feature, 'desc', feature.desc + line)

        def scenario_parser(feature, line):
            if not feature.scenarios or feature.scenarios[-1].steps:
                feature.scenarios.append(Scenario())
            scenario = feature.scenarios[-1]
            scenario.desc = scenario.desc + line + "\n"
        self.parsers['scenario'] = scenario_parser

        step_parser = lambda feature, line: feature.scenarios[-1].steps.append(line.strip() if not line.strip().lower().startswith(self.current_state) else line.strip()[len(self.current_state):].strip())
        self.parsers['given'] = step_parser
        self.parsers['when'] = step_parser
        self.parsers['then'] = step_parser

    def parse(self, feature, line):
        self.parsers[self.current_state](feature, line)

    def determine_current_state(self, line):
        self.current_state = self.transisions[self.current_state](line)

    def reset(self):
        self.current_state = self.start_state

    def run(self, file_name):
        feature = Feature()
        with open(file_name) as f:
            for line in f.readlines():
                if line.strip():
                    self.determine_current_state(line)
                    self.parse(feature, line)

        if self.current_state not in self.end_states:
            print 'WARNING: Feature file: %s seems not right' % file_name

        return feature


class StepsRegistry(object):
    def __init__(self):
        self.steps_definition = {}

    def add_step_definition(self, regexp, step):
        self.steps_definition[regexp] = step

    def get_step_definition(self, step_desc):
        for pattern, step_definition in self.steps_definition.iteritems():
            if re.match(pattern, step_desc):
                return pattern, step_definition

        return None, None

steps_registry = StepsRegistry()

def _step_decorator(regexp):
    def wrapper(func):
        steps_registry.add_step_definition(regexp, func)
        return func
    return wrapper

given = _step_decorator
when = _step_decorator
then = _step_decorator
