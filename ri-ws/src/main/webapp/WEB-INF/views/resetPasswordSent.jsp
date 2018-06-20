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
</head>

<body>

<div>
    <jsp:useBean id="current" class="java.util.Date" />
    ${current.toLocaleString()} - An e-mail containing a reset password link has been sent to <i>${emailAddress}</i>. The link is valid for ${PASSWORD_RESET_TTL_MINUTES} minutes.
    <br /><br />
    <a href="<c:url value="/login" />">Login</a>
    &nbsp;&nbsp;&nbsp;&nbsp;

</div>

</body>
</html>