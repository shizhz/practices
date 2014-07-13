#!/usr/bin/env python
# encoding: utf-8

import unittest

try:
    import pucumber
except ImportError:
    import sys
    from os.path import dirname, abspath

    pucumber_dir = dirname(dirname(abspath(__file__)))
    sys.path.append(pucumber_dir)

    from pucumber.parser import FeatureParseStateMachine, InvalidStateException

class FeatureParseStateMachineTest(unittest.TestCase):
    def setUp(self):
        pass

    def _get_state_machine_with_state(self, state):
        stateMachine = FeatureParseStateMachine()
        stateMachine.current_state = state
        return stateMachine

    def test_start_state(self):
        stateMachine = FeatureParseStateMachine()
        self.assertEqual('start', stateMachine.current_state)

    def test_state_transision_from_feature_tag(self):
        stateMachine = self._get_state_machine_with_state('@')
        tag_state_transision = stateMachine.transisions['@']

        self.assertEqual('@', tag_state_transision('@another_tag'))
        self.assertEqual('feature', tag_state_transision('Feature: feature followed'))

        with self.assertRaises(InvalidStateException) as e:
            tag_state_transision('neither tag nor feature')
        self.assertTrue(e.exception.msg.startswith('The input line must start with one of'))

        with self.assertRaises(InvalidStateException) as e:
            tag_state_transision('when state here')
        self.assertTrue(e.exception.msg.startswith('The acceptable state after'))

    def test_state_transision_from_feature(self):
        stateMachine = self._get_state_machine_with_state('feature')
        feature_state_transision = stateMachine.transisions['feature']

        self.assertEqual('feature', feature_state_transision('desc crosses over multiple lines will not change the state'))
        self.assertEqual('scenario', feature_state_transision('Scenario: scenario desc'))

        with self.assertRaises(InvalidStateException) as e:
            feature_state_transision('@ tag again')

    def test_state_transision_from_scenario(self):
        stateMachine = self._get_state_machine_with_state('scenario')
        scenario_state_transision = stateMachine.transisions['scenario']

        self.assertEqual('scenario', scenario_state_transision('desc crosses over multiple lines'))
        self.assertEqual('given', scenario_state_transision('Given A context is given here'))

        with self.assertRaises(InvalidStateException) as e:
            scenario_state_transision('When should not be here')

    def test_state_transision_from_given(self):
        stateMachine = self._get_state_machine_with_state('given')
        given_state_transision = stateMachine.transisions['given']

        self.assertEqual('given', given_state_transision('Given more context given'))
        self.assertEqual('given', given_state_transision('This should be a given statement'))
        self.assertEqual('given', given_state_transision('And statement'))
        self.assertEqual('given', given_state_transision('But statement for given'))
        self.assertEqual('when', given_state_transision('When statement'))
        self.assertEqual('then', given_state_transision('then statement'))

        with self.assertRaises(InvalidStateException) as e:
            given_state_transision('Scenario: should not be here')

    def test_determine_current_state(self):
        stateMachine = FeatureParseStateMachine()
        stateMachine.determine_current_state('@this_should_be_a_feature_tag')
        self.assertEqual(stateMachine.current_state, '@')
        stateMachine.determine_current_state('Feature: this is a new feature')
        self.assertEqual(stateMachine.current_state, 'feature')

        stateMachine.determine_current_state('Scenario: new scenario')
        self.assertEqual(stateMachine.current_state, 'scenario')

        stateMachine.determine_current_state('Given statement')
        self.assertEqual(stateMachine.current_state, 'given')
        stateMachine.determine_current_state('more given statement')
        self.assertEqual(stateMachine.current_state, 'given')
        stateMachine.determine_current_state('And statement')
        self.assertEqual(stateMachine.current_state, 'given')
        stateMachine.determine_current_state('But statement')
        self.assertEqual(stateMachine.current_state, 'given')

        stateMachine.determine_current_state('When statement')
        self.assertEqual(stateMachine.current_state, 'when')
        stateMachine.determine_current_state('more when statement')
        self.assertEqual(stateMachine.current_state, 'when')
        stateMachine.determine_current_state('And statement')
        self.assertEqual(stateMachine.current_state, 'when')
        stateMachine.determine_current_state('But statement')
        self.assertEqual(stateMachine.current_state, 'when')

        stateMachine.determine_current_state('Then statement')
        self.assertEqual(stateMachine.current_state, 'then')
        stateMachine.determine_current_state('more then statement')
        self.assertEqual(stateMachine.current_state, 'then')
        stateMachine.determine_current_state('And statement')
        self.assertEqual(stateMachine.current_state, 'then')
        stateMachine.determine_current_state('But statement')
        self.assertEqual(stateMachine.current_state, 'then')

    def test_feature_parsing(self):
        stateMachine = FeatureParseStateMachine()
        feature = stateMachine.run("../features/init_cleanup.feature")

        self.assertTrue(feature.tags == ['init_cleanup'])
        self.assertEqual(2, len(feature.scenarios))

def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(FeatureParseStateMachineTest))
    return suite

if __name__ == '__main__':
   unittest.TextTestRunner(verbosity=2).run(suite())

