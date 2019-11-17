<%@ page import="java.util.Objects" %>
<%@ page import="ua.sumy.stpp.web.register.model.Group" %>
<%@ page import="java.util.Set" %>

<%@ include file="header.jsp" %>

<main role="main" class="flex-shrink-0">
    <div class="container">
        <% String error = request.getParameter("error"); %>
        <% if (Objects.nonNull(error) && !error.isEmpty() && !error.isBlank()) { %>
        <div class="alert alert-danger" role="alert">
            <%= error %>
        </div>
        <% } %>

        <% String createdStudentName = request.getParameter("student"); %>
        <% if (Objects.nonNull(createdStudentName) && !createdStudentName.isEmpty() && !createdStudentName.isBlank()) { %>
        <div class="alert alert-success" role="alert">
            Додано нового студента "<%= createdStudentName %>"!
        </div>
        <% } %>

        <form method="POST">
            <div class="form-group">
                <div class="input-group mb-3">
                    <label for="studentName1" class="form-control" >Ім'я студента:</label>
                    <input name="name" type="text" class="form-control" id="studentName1" placeholder="Іванов Іван Іванович">
                </div>
            </div>
            <div class="form-group">
                <div class="input-group mb-3">
                    <label for="groupCode1" class="form-control" >Група:</label>
                    <select name="group" class="form-control" id="groupCode1">
                        <% Set<Group> groups = (Set<Group>) request.getAttribute("groups"); %>
                        <% if (Objects.isNull(groups) || groups.isEmpty()) { %>
                        <div class="alert alert-secondary" role="alert">
                            Додайте групи щоб додати студента!
                        </div>
                        <% } else { %>
                            <% for (Group group : groups) { %>
                            <option value="<%= group.getCode() %>"><%= group.getCode() %></option>
                            <% } %>
                        <% } %>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Додати студента</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="footer.jsp" %>