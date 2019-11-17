<%@ page import="ua.sumy.stpp.web.register.model.Subject" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Objects" %>
<%@ page import="ua.sumy.stpp.web.register.model.Group" %>
<%@ page import="java.util.List" %>

<%@ include file="header.jsp" %>

<main role="main" class="flex-shrink-0">
    <div class="container">
        <% String error = request.getParameter("error"); %>
        <% if (Objects.nonNull(error) && !error.isEmpty() && !error.isBlank()) { %>
        <div class="alert alert-danger" role="alert">
            <%= error %>
        </div>
        <% } %>

        <% Group createdGroup = (Group) request.getAttribute("group"); %>
        <% if (!Objects.isNull(createdGroup)) { %>
        <div class="alert alert-success" role="alert">
            Додано нову групу "<%= createdGroup.getCode() %>"!
        </div>
        <% } %>

        <form method="POST">
            <div class="form-group">
                <div class="input-group mb-3">
                    <label for="groupCodeInput1" class="form-control" >Код групи:</label>
                    <input name="code" type="text" class="form-control" id="groupCodeInput1" placeholder="E-28">
                </div>
            </div>

            <% List<Subject> subjects = (List<Subject>) request.getAttribute("subjects"); %>
            <% if (Objects.isNull(subjects) || subjects.isEmpty()) { %>
            <div class="alert alert-secondary" role="alert">
                Додайте предмети щоб створити групу!
            </div>
            <% } else { %>
                <% for (int i = 0; i < subjects.size(); i++) { %>
                    <% Subject subject = subjects.get(i); %>
                    <div class="form-check form-check-inline">
                        <input name="subjects" class="form-check-input" type="checkbox" id="inlineCheckbox<%=i%>" value="<%= subject.getId() %>">
                        <label class="form-check-label" for="inlineCheckbox<%=i%>"><%= subject.getName() %></label>
                    </div>
                <% } %>
            <% } %>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Додати групу</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="footer.jsp" %>