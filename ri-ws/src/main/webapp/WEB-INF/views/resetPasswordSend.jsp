<%--
  Created by IntelliJ IDEA.
  User: mrelac
  Date: 06/14/2018
  Time: 08:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@ page isELIgnored="false"%>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Reset Password Sent</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<body>

<jsp:useBean id="current" class="java.util.Date" />

<div class="alert alert-success">
 <p>${current.toLocaleString()}: An e-mail containing a reset password link has been sent to <i>${emailAddress}</i>.
     Any previous links are no longer valid. This link is valid for ${PASSWORD_RESET_TTL_MINUTES} minutes.</p>

    <br />

    <a href="<c:url value="/login" />">Login</a>
    &nbsp;&nbsp;&nbsp;&nbsp;
</div>

</body>
</html>