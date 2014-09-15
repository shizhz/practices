#!/usr/bin/env python
# encoding: utf-8

import os
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
    if not os.path.exists(VERSION_DIR):
        os.makedirs(VERSION_DIR)

    if not os.path.exists(VERSION_FILE):
        open(VERSION_FILE, 'w').close()

    with open(VERSION_FILE) as vf:
        versions = {}
        for line in filter(lambda line : len(line) > 0, map(lambda line : line.strip("\n"), vf.readlines())):
            pair = line.split('=')
            versions[pair[0]] = pair[1]

        return versions

def get_project_name(project_entry):
    return filter(lambda ele : len(ele) > 0, project_entry[0].split('/')[::-1])[0]

def get_unupgraded_commits(project):
    last_sha = VERSIONS.get(get_project_name(project))
    commits = git.Repo(project[0]).log()[::-1]
    commits_sha = map(lambda commit : commit.id, commits)
    last_sha_index = -1
    try:
        last_sha_index = commits_sha.index(last_sha)
    except Exception, e:
        pass

    return commits[last_sha_index + 1:]

def list_classes(project, java_file):
    try:
        if java_file.index(ROOT_PACKAGE):
            pass
    except Exception, e:
        return []
    package_path = java_file[java_file.index(ROOT_PACKAGE) + 1:]
    root_class_dir = project[1]
    glob_class_path = package_path[:-5] + "*.class"
    return glob.glob(os.path.join(root_class_dir, glob_class_path))

def package_path(project, clazz):
    return clazz[len(project[1]):]


def backup(project, commit, files):
    backup_dir = os.path.join(VERSION_DIR, '_'.join(commit.message.split()) + "_" + commit.id)
    print 'Backup files to directory %s: ' % backup_dir
    for clazz in files:
        pp = package_path(project, clazz)
        old_file = os.path.join(project[2], pp)
        if os.path.exists(old_file):
            backup_file = os.path.join(backup_dir, pp)
            if not os.path.exists(os.path.dirname(backup_file)):
                os.makedirs(os.path.dirname(backup_file))
            print "\t", old_file
            shutil.move(old_file, backup_file)

def upgrade(project, files):
    print 'Upgrade files: '
    for clazz in files:
        dst = os.path.join(project[2], package_path(project, clazz))
        print "\t", clazz, ' -> ', dst
        if not os.path.exists(os.path.dirname(dst)):
            os.makedirs(os.path.dirname(dst))
        shutil.copy(clazz, dst)

def upgrade_commit(project, commit):
    copy_files = []
    for changed_file in commit.stats.files.keys():
        if changed_file.endswith('.java'):
            copy_files.extend(list_classes(project, changed_file))

    backup(project, commit, copy_files)
    upgrade(project, copy_files)

def upgrade_project(project):
    for commit in get_unupgraded_commits(project):
        print 'Upgrade from commit: ', commit.id, '-'.join(commit.message.split())
        upgrade_commit(project, commit)
        print '-' * 30

    try:
        VERSIONS[get_project_name(project)] = commit.id
    except UnboundLocalError:
        print 'Nothing to upgrade for project: ', get_project_name(project)


def upgrade_log():
    with open(VERSION_FILE, 'w') as vf:
        for k, v in VERSIONS.iteritems():
            vf.write(k + "=" + v)

def main():
    read_version()
    upgrade_project(choose_project())
    upgrade_log()

PROJECTS = [ ("/Users/zzshi/Projects/ut-workshop/", "/Users/zzshi/Projects/ut-workshop/java/maven/target/classes/", "/Users/zzshi/WEB-INF/classes/") ]
ROOT_PACKAGE = '/org/'
ROOT_DIR = "/Users/zzshi/Projects/inmedius-ci/"
VERSION_DIR = ROOT_DIR + "versions"
VERSION_FILE = os.path.join(VERSION_DIR, 'version.log')
VERSIONS = read_version()

if __name__ == '__main__':
    main()


