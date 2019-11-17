<%@ page import="ua.sumy.stpp.web.register.model.Group" %>
<%@ page import="ua.sumy.stpp.web.register.model.Subject" %>
<%@ page import="ua.sumy.stpp.web.register.model.Student" %>
<%@ page import="ua.sumy.stpp.web.register.model.Mark" %>
<%@ page import="java.util.*" %>
<%@ include file="header.jsp" %>

<main role="main" class="flex-shrink-0">
    <% String selectedGroupCode = Objects.requireNonNullElse(request.getParameter("group"), ""); %>
    <div class="container">
        <form method="GET">
            <div class="form-group">
                <div class="input-group mb-3">
                    <label for="groupCodeInput1" class="form-control">Код групи:</label>
                    <select name="group" class="form-control" id="groupCodeInput1">
                        <% Set<Group> groups = (Set<Group>) request.getAttribute("groups"); %>
                        <% if (Objects.isNull(groups) || groups.isEmpty()) { %>
                        <div class="alert alert-secondary" role="alert">
                            Додайте групи щоб обрахувати звіт!
                        </div>
                        <% } else { %>
                            <% for (Group group : groups) { %>
                            <option value="<%= group.getCode() %>"
                                    <% if (selectedGroupCode.equals(group.getCode())) { %> selected="selected" <% } %>>
                                <%= group.getCode() %>
                            </option>
                            <% } %>
                        <% } %>
                    </select>
                    <button type="submit" class="btn btn-primary">Обрати групу</button>
                </div>
            </div>
        </form>

        <% Set<Subject> subjects = (Set<Subject>) request.getAttribute("subjects"); %>
        <% List<Student> students = (List<Student>) request.getAttribute("students"); %>
        <% if (Objects.isNull(subjects) || Objects.isNull(students)) { %>
        <div class="alert alert-secondary" role="alert">
            Додайте предмети або (і) студентів щоб обрахувати звіт!
        </div>
        <% } else { %>
        <% List<Mark> emptyMarkList = Collections.nCopies(students.size(), new Mark()); %>
        <% List<Mark> averageMarks = Objects.requireNonNullElse((List<Mark>) request.getAttribute("average_marks"), emptyMarkList); %>
        <% List<Mark> qualitativeMarks = Objects.requireNonNullElse((List<Mark>) request.getAttribute("qualitative_marks"), emptyMarkList); %>
        <% List<Mark> absoluteMarks = Objects.requireNonNullElse((List<Mark>) request.getAttribute("absolute_marks"), emptyMarkList); %>

        <% Map<String, List<Mark>> emptyMarksMap = new HashMap<>(students.size()); %>
        <% Map<String, List<Mark>> marks = Objects.requireNonNullElse((Map<String, List<Mark>>) request.getAttribute("marks"), emptyMarksMap); %>
        <form method="POST">
            <div class="form-group">
                <input name="group" type="hidden" value="<%= selectedGroupCode %>" />
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Ім'я студента</th>
                            <% for (Subject subject : subjects) { %>
                            <th scope="col"><%= subject.getName() %></th>
                            <% } %>
                            <th scope="col">Середня оцінка</th>
                            <th scope="col">Якість успішності</th>
                            <th scope="col">Абсолютна успішність</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (int i = 0; i < students.size(); i++) { %>
                    <% Student student = students.get(i); %>
                        <input name="students" id="<%=i%>" type="hidden" value="<%=student.getId()%>" />
                        <tr>
                            <th scope="row"><%= i + 1 %></th>
                            <td><%= student.getName() %></td>
                            <% List<Mark> studentMarks = marks.getOrDefault(student.getName(), Collections.nCopies(subjects.size(), new Mark())); %>
                            <% for (int j = 0; j < studentMarks.size(); j++) { %>
                            <% Mark mark = studentMarks.get(j); %>
                            <td>
                                <input name="marks_<%= student.getId() %>"
                                       id="<%= j %>"
                                       type="text" class="form-control" aria-label="mark"
                                       value="<%= mark.getValue() %>">
                            </td>
                            <% } %>
                            <td><%= averageMarks.get(i).getValue() %></td>
                            <td><%= qualitativeMarks.get(i).getValue() %>%</td>
                            <td><%= absoluteMarks.get(i).getValue() %>%</td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
                <button type="submit" class="btn btn-primary">Обрахувати звіт</button>
            </div>
        </form>
        <% } %>
    </div>
</main>

<%@ include file="footer.jsp" %>