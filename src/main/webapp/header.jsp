<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
    <title>Web Register</title>
    <%@ include file="bootstrap.jsp"%>

    <style>
        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }

        main > .container {
          padding: 60px 15px 0;
        }

        .footer {
          background-color: #f5f5f5;
        }

        .footer > .container {
          padding-right: 15px;
          padding-left: 15px;
        }

        code {
          font-size: 80%;
        }
    </style>
</head>
<body class="d-flex flex-column h-100">
    <header>
        <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
            <a class="navbar-brand" href="">Web Register</a>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="students">Students</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="subjects">Subjects</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="groups">Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="report">Report</a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>