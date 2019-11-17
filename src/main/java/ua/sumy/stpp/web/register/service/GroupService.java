package ua.sumy.stpp.web.register.service;

import ua.sumy.stpp.web.register.dao.GroupDao;
import ua.sumy.stpp.web.register.dao.GroupsSubjectsDao;
import ua.sumy.stpp.web.register.dao.StudentDao;
import ua.sumy.stpp.web.register.dao.SubjectDao;
import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Student;
import ua.sumy.stpp.web.register.model.Subject;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class GroupService {
    private final static Logger log = Logger.getLogger(GroupService.class.getName());

    private final GroupDao groupDao;
    private final SubjectDao subjectDao;
    private final StudentDao studentDao;
    private final GroupsSubjectsDao groupsSubjectsDao;

    public GroupService(DataSource dataSource) {
        this.groupDao = new GroupDao(dataSource);
        this.subjectDao = new SubjectDao(dataSource);
        this.studentDao = new StudentDao(dataSource);
        this.groupsSubjectsDao = new GroupsSubjectsDao(dataSource);
    }

    public Optional<Group> createGroup(String code, Set<Integer> subjects) {
        groupDao.createGroup(code);

        Optional<Group> createdGroup = groupDao.getGroup(code);
        if (createdGroup.isEmpty()) {
            log.severe(String.format("Error getting created group with code %s.", code));
            return createdGroup;
        }

        groupsSubjectsDao.createGroupSubjectsEntry(createdGroup.get().getId(), subjects);

        log.fine(String.format("Created new entry for group %s.", code));

        return createdGroup;
    }

    public Set<Group> getAllGroups() {
        return groupDao.getAllGroups();
    }

    public Group getGroupById(int id) {
        Optional<Group> group = this.groupDao.getGroup(id);
        if (group.isEmpty()) {
            log.warning(String.format("Trying to get not existing group by id: %d", id));
        }
        return group.orElse(null);
    }

    public Set<Subject> getGroupSubjects(String code) {
        Set<Subject> subjects = new HashSet<>();
        Optional<Group> existingGroup = this.groupDao.getGroup(code);
        if (existingGroup.isEmpty()) {
            log.warning(String.format("Trying to get not subjects of not existing group %s.", code));
            return subjects;
        }

        Set<Integer> subjectIds = this.groupsSubjectsDao.getGroupSubjects(existingGroup.get().getId());
        for (Integer subjectId : subjectIds) {
            Optional<Subject> subject = this.subjectDao.getSubject(subjectId);
            if (subject.isPresent()) {
                subjects.add(subject.get());
            } else {
                log.warning(String.format("Cannot get subject by id: %d", subjectId));
            }
        }

        return subjects;
    }

    public Set<Student> getGroupStudents(String code) {
        Optional<Group> existingGroup = this.groupDao.getGroup(code);
        if (existingGroup.isEmpty()) {
            log.warning(String.format("Trying to get not subjects of not existing group %s.", code));
            return new HashSet<>();
        }
        return this.studentDao.getGroupStudents(existingGroup.get().getId());
    }
}
