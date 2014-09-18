#!/usr/bin/env python
# encoding: utf-8

import sys
import os
from os.path import exists, join, dirname
import glob
import shutil
import git


def display_projects():
    print 'Choose the number of project:'
    for i in range(len(PROJECTS)):
        print '\t', i, ' ', get_project_name(PROJECTS[i])

def choose_project():
    while True:
        display_projects()
        print ' > ',
        project_number = raw_input()
        try:
            if int(project_number) in range(len(PROJECTS)):
                return PROJECTS[int(project_number)]
        except ValueError, e:
            print 'FUCK YOU, make it a correct project number!!'

def read_version():
    if not exists(VERSION_DIR):
        os.makedirs(VERSION_DIR)

    if not exists(VERSION_FILE):
        open(VERSION_FILE, 'w').close()

    with open(VERSION_FILE) as vf:
        return dict((x, y) for x, y in map(lambda line : line.split('='), filter(lambda line : len(line) > 0, map(lambda line : line.strip("\n"), vf.readlines()))))

def get_project_name(project_entry):
    return filter(lambda ele : len(ele) > 0, project_entry[0].split('/')[::-1])[0]

def get_project_commits(project):
    return git.Repo(project[0]).log()[::-1]

def get_unupgraded_commits(project):
    last_sha = VERSIONS.get(get_project_name(project))
    commits = get_project_commits(project)
    commits_sha = map(lambda commit : commit.id, commits)

    if any(last_sha == sha for sha in commits_sha):
        return commits[commits_sha.index(last_sha) + 1:]
    else:
        return commits[1:]

def list_classes(project, java_file):
    if len(java_file.split(ROOT_PACKAGE)) == 1:
        return []
    package_path = java_file[java_file.index(ROOT_PACKAGE) + 1:]
    root_class_dir = project[1]
    glob_class_path = package_path[:-5] + "*.class"
    return glob.glob(join(root_class_dir, glob_class_path))

def package_path(project, clazz):
    return clazz[len(project[1]):]

def get_project_backup_dir(project):
    return join(VERSION_DIR, get_project_name(project))

def get_commit_back_dir(project, commit):
    return join(get_project_backup_dir(project), '_'.join(commit.message.split()) + "_" + commit.id)


def backup_for_commit(project, commit, files):
    backup_dir = get_commit_back_dir(project, commit)
    print 'Backup files to directory %s: ' % backup_dir
    for clazz in files:
        pp = package_path(project, clazz)
        old_file = join(project[2], pp)
        if exists(old_file):
            backup_file = join(backup_dir, pp)
            if not exists(dirname(backup_file)):
                os.makedirs(dirname(backup_file))
            print "\t", old_file
            shutil.move(old_file, backup_file)

def upgrade_files(project, files):
    print 'Upgrade files: '
    for clazz in files:
        dst = join(project[2], package_path(project, clazz))
        print "\t", clazz, ' -> ', dst
        if not exists(dirname(dst)):
            os.makedirs(dirname(dst))
        shutil.copy(clazz, dst)

def upgrade_commit(project, commit):
    copy_files = []
    for changed_file in commit.stats.files.keys():
        if changed_file.endswith('.java'):
            copy_files.extend(list_classes(project, changed_file))

    backup_for_commit(project, commit, copy_files)
    upgrade_files(project, copy_files)

def upgrade_project(project):
    for commit in get_unupgraded_commits(project):
        print 'Upgrade from commit: ', commit.id, '-'.join(commit.message.split())
        upgrade_commit(project, commit)
        print '-' * 30

    try:
        VERSIONS[get_project_name(project)] = commit.id
    except UnboundLocalError:
        print 'Nothing to upgrade for project: ', get_project_name(project)

def get_upgraded_commits(project):
    all_commits = get_project_commits(project)
    return all_commits[:len(all_commits) - len(get_unupgraded_commits(project))]

def list_backup_versions(project):
    backup_dir = get_project_backup_dir(project)
    if not exists(backup_dir):
        return []

    return filter(exists, map(lambda commit: get_commit_back_dir(project, commit), get_upgraded_commits(project)))[::-1]


def rollback_project(project):
    backup_versions = list_backup_versions(project)
    print backup_versions

def upgrade_log():
    with open(VERSION_FILE, 'w') as vf:
        for k, v in VERSIONS.iteritems():
            vf.write(k + "=" + v)

def upgrade():
    read_version()
    upgrade_project(choose_project())
    upgrade_log()

def rollback():
    read_version()
    rollback_project(choose_project())
    upgrade_log()

def main():
    try:
        globals()[sys.argv[1]]()
    except Exception, e:
        print 'Usage: ', sys.argv[0], ' with either options ', HANDLERS
        raise e

handlers = {
        'upgrade': upgrade,
        'rollback': rollback
        }

PROJECTS = [ ("/Users/zzshi/Projects/ut-workshop/", "/Users/zzshi/Projects/ut-workshop/java/maven/target/classes/", "/Users/zzshi/WEB-INF/classes/") ]
ROOT_PACKAGE = '/org/'
ROOT_DIR = "/Users/zzshi/Projects/practices/inmedius-ci/"
VERSION_DIR = ROOT_DIR + "versions"
VERSION_FILE = join(VERSION_DIR, 'version.log')
VERSIONS = read_version()
HANDLERS = ['upgrade', 'rollback']

if __name__ == '__main__':
    main()


