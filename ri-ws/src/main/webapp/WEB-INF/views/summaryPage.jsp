<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>


<!DOCTYPE html>
<html>

<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Register Interest Summary page</title>
</head>


<body>
<div class="region region-content">
    <div class="block block-system">
        <div class="content">
            <div class="node node-gene">
                <h1 class="title" id="top">Register Interest Summary </h1>

                <div class="section">
                    <div class="inner">

                        <h3>Username: ${summary.emailAddress}</h3>

                        <div>
                            <a href="logout">Logout</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="changePasswordRequest">Reset password</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="account">Delete account</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;






                            <br />
                            <br />

                            <form id="registerForm" class="form-horizontal" action="registration/genereg/")>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <div class="form-actions">
                                    <input type="text" id="regAcc" class="btn btn-block btn-primary btn-default" placeholder="MGI:1924076" value="MGI:1924076" />
                                    &nbsp;&nbsp;
                                    <button type="submit" id="regButton">Go</button>
                                </div>
                            </form>

                            <form id="unregisterForm" class="form-horizontal" action="registration/gene/")>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <div class="form-actions">
                                    <input type="text" id="unregAcc" class="btn btn-block btn-primary btn-default" placeholder="MGI:1924076" value="MGI:1924076" />
                                    &nbsp;&nbsp;
                                    <button type="submit" id="unregButton">Go</button>
                                </div>
                            </form>


                        </div>

                        <c:choose>
                            <c:when test="${fn:length(summary.genes) eq 0}">
                                <h4>You have not yet registered interest in any genes.</h4>

                                <a href='${paHostname}${paContextRoot}/search?kw=*"'>IMPC Gene page</a>

                            </c:when>
                            <c:otherwise>

                                <h4>You have registered interest in the following ${fn:length(summary.genes)} genes:</h4>

                                <div id="summaryTableDiv">

                                    <style>
                                        table {
                                            font-family: arial, sans-serif;
                                            border-collapse: collapse;
                                            width: 100%;
                                        }
                                        td, th {
                                            border: 1px solid #dddddd;
                                            text-align: left;
                                            padding: 8px;
                                        }
                                        tr:nth-child(even) {
                                            background-color: #dddddd;}
                                    </style>
                                    <table id="summary-table" class='table tableSorter'>
                                        <thead>
                                            <tr>
                                                <th>Gene Symbol</th>
                                                <th>Gene MGI Accession Id</th>
                                                <th>Assignment Status</th>
                                                <th>Null Allele Production Status</th>
                                                <th>Conditional Allele Production Status</th>
                                                <th>Phenotyping Data Available</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>

                                        <tbody>

                                            <c:forEach var="gene" items="${summary.genes}" varStatus="loop">

                                                <tr>
                                                    <td>
                                                        <a href='${paHostname}${paContextRoot}/genes/${gene.mgiAccessionId}'>${gene.symbol}</a>
                                                    </td>
                                                    <td><a href="//www.informatics.jax.org/marker/${gene.mgiAccessionId}">${gene.mgiAccessionId}</a></td>
                                                    <td>${gene.riAssignmentStatus}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${gene.riNullAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                                <a href='${paHostname}${paContextRoot}/search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riNullAlleleProductionStatus}</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.riNullAlleleProductionStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${gene.riConditionalAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                                <a href='${paHostname}${paContextRoot}/search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riConditionalAlleleProductionStatus}</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.riConditionalAlleleProductionStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${gene.riPhenotypingStatus == 'Yes'}">
                                                                <a href='${paHostname}${paContextRoot}/genes/${gene.mgiAccessionId}#section-associations'>${gene.riPhenotypingStatus}</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.riPhenotypingStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <a href="${paHostname}$paContextRoot}/">Unregister</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

    $("#registerForm").on("submit", function(){
        // alert('registration/genereg/' + $("#regAcc").val());
        $.ajax({
            url: $("#registerForm").attr("action"),
            type: 'GET',
            contentType: "application/json",
            data: { id: $("#regAcc").val() },
            // data:$("#registerForm").serialize(),
            success: function(msg) {
                alert(msg);
            },
            error: function(e) {
                alert("error: " + e.val());
            }

        });
        return true;
    });

    $("#unregisterForm").on("submit", function(){
        $.ajax({
            url: $("#unregisterForm").attr("action"),
            type: 'GET',
            contentType: "application/json",
            data: { id: $("#unregAcc").val() },
            // data:$("#registerForm").serialize(),
            success: function(msg) {
                alert(msg);
            },
            error: function(e) {
                alert("error: " + e.val());
            }

        });
        return true;
    });



</script>
</body>
</html>