<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<!DOCTYPE html>
<html>

<head>
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

                            <h3>Username: ${emailAddress}</h3>

                            <div>
                                <a href="logout">Logout</a>
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <a href="resetPasswordRequest">Reset password</a>
                            </div>

                            <c:choose>
                                <c:when test="${fn:length(summaryList[0].genes) eq 0}">
                                    <h4>You have not yet registered interest in any genes.</h4>

                                    <a href='${paHostname}${paContextRoot}/search?kw=*"'>IMPC Gene page</a>

                                </c:when>
                                <c:otherwise>

                                    <h4>You have registered interest in the following ${fn:length(summaryList)} genes:</h4>

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
                                        <table id="summary-table"
                                               class='table tableSorter'>
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
                                                <c:forEach var="contactGene" items="${summaryList}" varStatus="loop">

                                                    <c:forEach var="gene" items="${contactGene.genes}" varStatus="loop">

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
                                                            <td>Unregister</td>
                                                        </tr>

                                                    </c:forEach>

                                                </tbody>
                                            </table>
                                        </div>

                                    </c:forEach>

                                </c:otherwise>
                            </c:choose>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>