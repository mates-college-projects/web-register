<%@ page import="java.util.Objects" %>
<%@ page import="ua.sumy.stpp.web.register.model.Subject" %>
<%@ include file="header.jsp" %>

<main role="main" class="flex-shrink-0">
    <div class="container">
        <% String error = request.getParameter("error"); %>
        <% if (Objects.nonNull(error) && !error.isEmpty() && !error.isBlank()) { %>
        <div class="alert alert-danger" role="alert">
            <%= error %>
        </div>
        <% } %>

        <% Subject createdSubject = (Subject) request.getAttribute("subject"); %>
        <% if (!Objects.isNull(createdSubject)) { %>
        <div class="alert alert-success" role="alert">
            Додано новий предмет "<%= createdSubject.getName() %>"!
        </div>
        <% } %>

        <form method="POST">
            <div class="form-group">
                <div class="input-group mb-3">
                    <label for="subjectName1" class="form-control" >Назва предмету:</label>
                    <input name="subject_name" type="text" class="form-control" id="subjectName1" placeholder="Програмування">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Додати предмет</button>
                </div>
            </div>
        </form>
    </div>
</main>

<%@ include file="footer.jsp" %>