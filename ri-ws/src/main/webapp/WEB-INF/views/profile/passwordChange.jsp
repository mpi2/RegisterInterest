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
    <title>Change Password</title>
</head>

<body>

<div>
    <h1 class="title" id="top">Changing password for <i>${user}</i></h1>
    <form:form action="passwordChange" method="post">
        <c:if test="${showCurrent == true}">
            <input type="password" placeholder="Current password" name="currentPassword" value="${currentPassword}"><br /><br />
        </c:if>

        <input type="password" placeholder="New password" name="newPassword" value="${newPassword}"><br /><br />
        <input type="password" placeholder="Repeat password" name="repeatPassword" value="${repeatPassword}"><br /><br />
        <input type="submit" value="Change Password">
        <a href="resetPasswordRequest">Rreset password</a>
    </form:form>

    ${errorStatus}

</div>

</body>
</html>