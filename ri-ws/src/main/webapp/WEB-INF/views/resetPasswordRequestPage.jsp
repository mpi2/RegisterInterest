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
    <title>Reset password</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<body>

<div id="mainWrapper">
    <div class="login-container">
        <div class="login-card">
            <div class="login-form">
                <c:url var="resetPasswordEmail" value="resetPasswordEmail" />
                <form action="${resetPasswordEmail}" method="post" class="form-horizontal">

                    <c:choose>
                        <c:when test="${param.error != null}">
                            <div class="alert alert-danger">
                                <p>Invalid email address.</p>
                            </div>
                        </c:when>
                        <c:when test="${not empty status}">
                            <div class="alert alert-success">
                                <p>${status}</p>
                            </div>
                        </c:when>
                    </c:choose>

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    Send an e-mail to the address below to reset the password.

                    <br />

                    <c:choose>
                        <c:when test="${not empty emailAddress}">
                            <div class="input-group input-sm">
                                <label class="input-group-addon"><i class="fa">${emailAddress}</i></label>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="input-group input-sm">
                                <label class="input-group-addon" for="username"><i class="fa fa-user"></i></label>
                                <input type="text" class="form-control" id="username" name="emailAddress" placeholder="Enter e-mail address" required />
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="form-actions">
                        <input type="submit" class="btn btn-block btn-primary btn-default" value="Send e-mail to reset password" />
                    </div>

                    <br />

                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>