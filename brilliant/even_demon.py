#!/usr/bin/env python

'''
https://brilliant.org/community-problem/escape-the-even-demon/?group=vrRkUw70QkmE
'''


def survivor(n):
    """produce the survivor position in the circle of number n"""
    circle = range(1, n + 1)
    eat_from = 1
    last_in_circle = circle[-1]
    while len(circle) > 1:
        circle = filter(lambda (i, x): (i - eat_from) % 2 != 0, enumerate(circle))
        circle = map(lambda x: x[1], circle)
        eat_from = last_in_circle != circle[-1] + 0 # if the last person in the circle is eaten, then eat from the second position next time, otherwise, eat from the first position next time
        last_in_circle = circle[-1]

    return circle

print survivor(1000)
