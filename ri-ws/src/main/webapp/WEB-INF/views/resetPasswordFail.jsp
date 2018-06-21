<%--
  Created by IntelliJ IDEA.
  User: mrelac
  Date: 06/14/2018
  Time: 08:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page isELIgnored="false"%>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Password not changed</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<body>


<jsp:useBean id="current" class="java.util.Date" />

<div class="alert alert-danger">
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <p>${current.toLocaleString()}: ${error}</p>
        </div>
    </c:if>

    <br />

    <a href="<c:url value="/login" />">Login</a>
    &nbsp;&nbsp;&nbsp;&nbsp;
</div>

</body>
</html>