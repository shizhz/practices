#!/usr/bin/env python

'''
https://brilliant.org/community-problem/her-name-is-cheryl/?group=qxhsG7xhUqHO
'''

near_symmetric_threshold = 25


def natural_numbers():
    """make a stream to generate natural numbers"""
    i = 1
    while True:
        yield i
        i = i + 1


def is_palindrome(number):
    """check a number is symmetric or not"""
    s = str(number)
    for i in range(len(s) / 2):
        if s[i] != s[-1 - i]:
            return False

    return True


def sum_of_reversed_number(number):
    """the sum of this number and it's reversed number"""
    s = str(number)
    result = number
    for i in range(len(s)):
        weight = int(s[i]) * pow(10, i)
        result = result + weight

    return result


def smallest_near_symmetric():
    """find the smallest near-symmetric number according the description"""
    for i in natural_numbers():
        j = 1
        middle_value = sum_of_reversed_number(i)
        while j < near_symmetric_threshold:
            if is_palindrome(middle_value):
                break
            else:
                middle_value = sum_of_reversed_number(middle_value)
                j = j + 1
        else:
            print 'The smallest near symmetric is: ', i
            break

print smallest_near_symmetric()
