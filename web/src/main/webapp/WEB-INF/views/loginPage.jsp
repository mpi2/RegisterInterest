<%--
  Created by IntelliJ IDEA.
  User: mrelac
  Date: 06/06/2018
  Time: 08:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page isELIgnored="false"%>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Login</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<body>
<div id="mainWrapper">
    <div class="login-container">
        <div class="login-card">
            <div class="login-form">
                <form action="login" method="post" class="form-horizontal">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger">
                            <p>Invalid username and password.</p>
                        </div>
                    </c:if>
                    <c:if test="${param.logout != null}">
                        <div class="alert alert-success">
                            <p>You have been logged out successfully.</p>
                        </div>
                    </c:if>
                    <c:if test="${param.deleted != null}">
                        <div class="alert alert-success">
                            <p>Your account has been deleted as requested.</p>
                        </div>
                    </c:if>
                    <div class="input-group input-sm">
                        <label class="input-group-addon" for="username"><i class="fa fa-user"></i></label>
                        <input type="text" class="form-control" id="username" name="ssoId" placeholder="Enter Username" required />
                    </div>
                    <div class="input-group input-sm">
                        <label class="input-group-addon" for="password"><i class="fa fa-lock"></i></label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required />
                    </div>

                    <div class="form-actions">
                        <input type="submit" class="btn btn-block btn-primary btn-default" value="Log in" />
                    </div>

                    <br/>

                    <a href="${riBaseUrl}/changePasswordRequest">New account</a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="${riBaseUrl}/changePasswordRequest">Forgot password?</a>

                    <br />
                    <br />

                    <div>
                        Forum
                        <br />
                        <a href='http://www.mousephenotype.org/user/login' title="Login to Forum">Login</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href='http://www.mousephenotype.org/user/register' title="Register for Forum">Register</a>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>