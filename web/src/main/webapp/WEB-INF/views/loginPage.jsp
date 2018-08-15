<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<%--<c:set var="paBaseUrl" value="${paBaseUrl}" />--%>

<t:genericpage>

    <jsp:attribute name="title">Register Interest Summary page</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${riBaseUrl}/login">Register Interest</a> &raquo; Login</jsp:attribute>





    <jsp:attribute name="bodyTag">
        <body>
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div id="mainWrapper" style="display: flex">
            <div class="login-container" style="width: 45%">

                <h1>Register Interest</h1>
                <p>
                    The Register Interest system is a place where you can register and unregister genes of interest,
                    change your Register Interest password, and delete all of the genes for which you have registered.
                </p>

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

                            <div class="form-item form-type-textfield form-item-name">
                                <input type="text" class="form-conrol required" id="username" name="ssoId" placeholder="Enter Username" required />
                            </div>

                            <div class="form-item form-type-password form-item-pass">
                                <input type="password" class="form-control  required" id="password" name="password" placeholder="Enter Password" required />
                            </div>

                            <div class="form-actions">
                                <input type="submit" class="btn btn-block btn-primary btn-default" value="Log in" />
                            </div>

                            <br/>

                            <a href="${riBaseUrl}/changePasswordRequest">New account</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="${riBaseUrl}/changePasswordRequest">Forgot password?</a>

                        </form>
                    </div>
                </div>
            </div>

            <div style="width: 10%"></div>

            <div style="width: 45%">
                <h1>Forum</h1>
                <p>
                    The IMPC forum is a place where you can apply for roles, create bookmarks, edit your IMPC Forum details, view and manage subscriptions, and send private messages.
                </p>

                <br />

                <a href="${drupalBaseUrl}/user/login">Log in to the IMPC forum</a>
            </div>
        </div>

        <script type="text/javascript">

            $(document).ready(function () {

                // Disable drupal login links on the page
                $('ul.menu li.leaf').css('display', 'none');
            });
        </script>

    </jsp:body>
</t:genericpage>