#!/usr/bin/env python
# encoding: utf-8

import unittest
from test_suites import parser_test

if __name__ == '__main__':
    allsuites = unittest.TestSuite([parser_test.suite()])
    unittest.TextTestRunner(verbosity=2).run(allsuites)
