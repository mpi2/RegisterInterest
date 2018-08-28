<%@ tag description="Overall Page template" pageEncoding="UTF-8" import="org.mousephenotype.ri.util.DrupalHttpProxy,java.net.URLEncoder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>


<%-- -------------------------------------------------------------------------- --%>
<%-- NOTE: All "magic" variables are defined in the DeploymentInterceptor class --%>
<%-- This includes such variables isbaseUrl, drupalBaseUrl and releaseVersion.. --%>
<%-- -------------------------------------------------------------------------- --%>


<%
    /*
     Get the menu JSON array from drupal, fallback to a default menu when drupal
     cannot be contacted
     */
    DrupalHttpProxy proxy = new DrupalHttpProxy(request);
    String url = (String) request.getAttribute("drupalBaseUrl");

    String content = proxy.getDrupalMenu(url);
    String[] menus = content.split("MAIN\\*MENU\\*BELOW");

    String baseUrl = (request.getAttribute("baseUrl") != null &&  ! ((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : (String) application.getInitParameter("baseUrl");
    jspContext.setAttribute("baseUrl", baseUrl);



    // Use the drupal destination parameter to redirect back to this page
    // after logging in
    String dest = (String) request.getAttribute("javax.servlet.forward.request_uri");
    String destUnEncoded = dest;
    if (request.getQueryString() != null) {
        dest += URLEncoder.encode("?" + request.getQueryString(), "UTF-8");
        destUnEncoded += "?" + request.getQueryString();
    }

    String usermenu = menus[0]
            .replace("current=menudisplaycombinedrendered", "destination=" + dest)
            .replace("user/register", "user/register?destination=" + dest)
            .replace(request.getContextPath(), baseUrl.substring(1));

    jspContext.setAttribute("usermenu", usermenu);
    jspContext.setAttribute("menu", menus[1]);

    String riBaseUrlWithScheme = (request.getAttribute("riBaseUrlWithScheme") != null &&  ! ((String) request.getAttribute("riBaseUrlWithScheme")).isEmpty()) ? (String) request.getAttribute("riBaseUrlWithScheme") : (String) application.getInitParameter("riBaseUrlWithScheme");
    jspContext.setAttribute("riBaseUrlWithScheme", riBaseUrlWithScheme);

    String paBaseUrlWithScheme = (request.getAttribute("paBaseUrlWithScheme") != null &&  ! ((String) request.getAttribute("paBaseUrlWithScheme")).isEmpty()) ? (String) request.getAttribute("paBaseUrlWithScheme") : (String) application.getInitParameter("paBaseUrlWithScheme");
    jspContext.setAttribute("paBaseUrlWithScheme", paBaseUrlWithScheme);
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
<%@attribute name="bodyTag" fragment="true"%>
<%@attribute name="addToFooter" fragment="true"%>

<c:set var="uri">${pageContext.request.requestURL}</c:set>
<c:set var="domain">${pageContext.request.serverName}</c:set>

<c:set var="queryStringPlaceholder">
    <c:choose>
        <c:when test="${not empty queryString}">${queryString}</c:when>
        <c:otherwise>Search genes, SOP, MP, images by MGI ID, gene symbol, synonym or name</c:otherwise>
    </c:choose>
</c:set>

<!DOCTYPE html>
<html lang="en">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><jsp:invoke fragment="title"></jsp:invoke> | International Mouse Phenotyping Consortium</title>


    <!--  NEW DESIGN CSS -->

    <!-- css -->
    <link href='//fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/css/vendor/jquery.ui/jquery.ui.core.css">
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/css/vendor/jquery.ui/jquery.ui.slider.css">
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/css/vendor/font-awesome/font-awesome.min.css">
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css">
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/js/vendor/jquery/jquery.fancybox-2.1.5/jquery.fancybox.css">
    <link rel="stylesheet" href="${drupalBaseUrl}/sites/all/modules/feedback_simple/feedback_simple.css">
    <link rel="stylesheet" href="${paBaseUrlWithScheme}/js/vendor/DataTables-1.10.4/extensions/TableTools/css/dataTables.tableTools.min.css">
    <%--<link rel="stylesheet" href="${paBaseUrlWithScheme}/css/searchPage.css">--%>

    <link href="${paBaseUrlWithScheme}/css/default.css" rel="stylesheet" type="text/css" />
    <%--<link href="${paBaseUrlWithScheme}/css/wdm.css" rel="stylesheet" type="text/css" />--%>

    <!-- EBI CSS -->
    <link href="${paBaseUrlWithScheme}/css/additionalStyling.css" rel="stylesheet" type="text/css" />

    <script>
        <%--
        Some browsers do not provide a console object see:
        http://stackoverflow.com/questions/690251/what-happened-to-console-log-in-ie8
        http://digitalize.ca/2010/04/javascript-tip-save-me-from-console-log-errors/
        // In case we forget to take out console statements. IE fails otherwise
        --%>
        try {
            console.log(" ");
        } catch (err) {
            var console = {};
            console.log = console.error = console.info = console.debug = console.warn = console.trace = console.dir = console.dirxml = console.group = console.groupEnd = console.time = console.timeEnd = console.assert = console.profile = function () {
            };
        }

        <c:forEach var="entry" items="${requestConfig}">
        var ${entry.key}="${entry.value}";
        </c:forEach>

    </script>

    <%--
    Include google tracking code on live site
    --%>
    <c:if test="${liveSite}">
        <script>
            (function (i, s, o, g, r, a, m) {
                i['GoogleAnalyticsObject'] = r;
                i[r] = i[r] || function () {
                        (i[r].q = i[r].q || []).push(arguments)
                    }, i[r].l = 1 * new Date();
                a = s.createElement(o),
                    m = s.getElementsByTagName(o)[0];
                a.async = 1;
                a.src = g;
                m.parentNode.insertBefore(a, m)
            })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

            ga('create', 'UA-23433997-1', 'auto');
            ga('send', 'pageview');
        </script>
    </c:if>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <style>
        #logoImage {margin: 5px; padding:5px;}
        .container .container .navbar .navbar-inner {width:100%}
        img#logoImage{margin-right:10px;padding-right: 30px;}
    </style>
    <![endif]-->

    <!-- NEW DESIGN JAVASCRIPT -->

    <!-- javascript -->
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/head.min.js?v=${version}"></script>
    <!--We're calling these from Google as this will download from the closest geographic location which will speed page-loads for Aussies and Kiwis-->
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>

    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/DataTables-1.10.4/media/js/jquery.dataTables.min.js?v=${version}"></script>
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/DataTables-1.10.4/extensions/TableTools/js/dataTables.tableTools.min.js?v=${version}"></script>
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/jquery.jeditable.js?v=${version}"></script>


    <!--[if lt IE 9 ]><script type="text/javascript" src="js/selectivizr-min.js"></script><![endif]-->
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.js?v=${version}"></script>
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/jquery/jquery.fancybox-2.1.5/jquery.fancybox.pack.js?v=${version}"></script>
    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/vendor/jquery/jquery.tablesorter.min.js?v=${version}"></script>

    <script type='text/javascript' src="${paBaseUrlWithScheme}/js/general/toggle.js?v=${version}"></script>

    <script type="text/javascript" src="${paBaseUrlWithScheme}/js/default.js?v=${version}"></script>

    <jsp:invoke fragment="header" />

</head>


<jsp:invoke fragment="bodyTag"/>

<c:if test='${!param["bare"].equalsIgnoreCase("true")}'>
<div id="feedback_simple">
    <a class="feedback_simple-right feedback_simple" style="top: 35%; height: 100px; width: 35px;" target="_self" href=""><img src="${drupalBaseUrl}/sites/all/modules/feedback_simple/feedback_simple.gif" /></a>
</div>
</c:if>

<div id="wrapper">
    <c:choose>
        <c:when test="${param['bare'] == null}">
            <script type="text/javascript" >
                // assign the url to feedback link dynamically
                // this won't work with hashtag change which is taken care of in search.jsp
                $('a.feedback_simple').attr('href', '/website-feedback?page=' + document.URL);
            </script>

            <header id="header">
                <div class="region region-header">
                    <div id="tn">
                        <div class="region region-usernavi">
                            <div id="block-system-user-menu" class="block block-system block-menu">
                                <div class="content">
                                    <ul class="menu">
                                        <li class="first leaf"><a href="/user/login?current=node/718" title="Login with your account" id="login">Login</a></li>
                                        <li class="last leaf"><a href="/user/register" title="Register for an account" id="register">Register</a></li>
                                    </ul>
                                    <div class="clear"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="logo">
                        <a href="/"><img src="/sites/all/themes/impc_v3a/img/logo/impc.png" alt="IMPC Logo" /></a>
                        <div id="logoslogan">International Mouse Phenotyping Consortium</div>
                    </div>
                    <nav id="mn">
                        <div class="region region-mainnavi">
                            <div id="block-menu-block-1" class="block block-menu-block">
                                <div class="content">
                                    <div class="menu-block-wrapper menu-block-1 menu-name-main-menu parent-mlid-0 menu-level-1">
                                        <ul class="menu">
                                            <li class="first expanded menu-mlid-3127">
                                                <a href="http://www.mousephenotype.org/data/batchQuery">Advanced Tools</a>
                                                <ul class="menu">
                                                    <li class="first leaf menu-mlid-4255"><a href="http://www.mousephenotype.org/impress">IMPReSS</a></li>
                                                    <li class="leaf menu-mlid-4047"><a href="https://www.mousephenotype.org/data/batchQuery">Batch Query</a></li>
                                                    <li class="leaf menu-mlid-4257"><a href="http://www.mousephenotype.org/data/alleleref">IMPC publications browser</a></li>
                                                    <li class="leaf menu-mlid-4049"><a href="http://www.mousephenotype.org/phenoview/">Phenoview</a></li>
                                                    <li class="leaf menu-mlid-4051"><a href="http://www.mousephenotype.org/data/documentation/data-access">REST API</a></li>
                                                    <li class="last leaf menu-mlid-4053"><a href="//www.mousephenotype.org/data/tools">Other Tools</a></li>
                                                </ul>
                                            </li>
                                            <li class="expanded menu-mlid-530">
                                                <a href="/objectives-and-background">About IMPC</a>
                                                <ul class="menu">
                                                    <li class="first leaf menu-mlid-3125"><a href="/objectives-and-background">Objectives and Background</a></li>
                                                    <li class="leaf menu-mlid-537"><a href="/about-impc/impc-members">IMPC Members</a></li>
                                                    <li class="leaf menu-mlid-3197"><a href="https://www.mousephenotype.org/sites/mousephenotype.org/files/IMPC%20Governance%20and%20Coordination%20v02%20October%202014.pdf">Governance Documentation</a></li>
                                                    <li class="leaf has-children menu-mlid-3525"><a href="/about-impc/coordination">Coordination</a></li>
                                                    <li class="leaf menu-mlid-3229"><a href="/about-impc/industry-sponsors">Industry Sponsors</a></li>
                                                    <li class="leaf menu-mlid-546"><a href="/about-impc/impc-secretariat">Secretariat</a></li>
                                                    <li class="leaf has-children menu-mlid-3223"><a href="/about-impc/publications">Additional Information</a></li>
                                                    <li class="leaf menu-mlid-3983"><a href="/about-impc/arrive-guidelines">ARRIVE Guidelines</a></li>
                                                    <li class="leaf menu-mlid-3975"><a href="/about-ikmc">About IKMC</a></li>
                                                    <li class="last leaf menu-mlid-4315"><a href="/about-impc/impc-privacy-policy">IMPC Privacy Policy</a></li>
                                                </ul>
                                            </li>
                                            <li class="expanded menu-mlid-526">
                                                <a href="/news" title="">News &amp; Events</a>
                                                <ul class="menu">
                                                    <li class="first leaf menu-mlid-4259"><a href="/news-events/meetings">Events</a></li>
                                                    <li class="last leaf menu-mlid-4041"><a href="http://www.mousephenotype.org/data/alleleref">References using IKMC and IMPC Resources</a></li>
                                                </ul>
                                            </li>
                                            <li class="leaf menu-mlid-559"><a href="/contact-us">Contact</a></li>
                                            <li class="last expanded menu-mlid-1220">
                                                <a href="/user?current=node/718">My IMPC</a>
                                                <ul class="menu">
                                                    <li class="first leaf menu-mlid-1126"><a href="/forum" title="">IMPC Forum</a></li>
                                                    <li class="leaf has-children menu-mlid-3133"><a href="http://www.mousephenotype.org/data/documentation/doc-overview">Documentation</a></li>
                                                    <li class="last leaf menu-mlid-4029"><a href="/my-impc/communications-materials">Communications Materials</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                            </div>
                        </div>
                    </nav>
                    <div class="clear"></div>
                </div>
            </header>

            <div id="main">
                <div class="breadcrumb">

                    <a href="${drupalBaseUrl}">Home</a><jsp:invokefragment="breadcrumb" /><%-- breadcrumbs here --%>
                </div>
                <div class='searchcontent'>
                    <div id='bigsearchbox' class='block'>
                        <i id='sicon' class='fa fa-search'></i>
                        <input id='s' value="search">
                        <i id='clearIcon' class='fa fa-times'></i>
                    </div>
                    <a><i class='fa fa-question-circle fa-2x searchExample'></i></a>
                    <div style="clear: both"></div>
                    <div id='batchQryLink'>
                        <a id='batchquery' href='${paBaseUrlWithScheme}/batchQuery'><i class='fa fa-th-list batchQuery'></i><span id='bqry'>Batch search</span></a>
                    </div>
                    <div style="clear: both"></div>
                </div>

                <jsp:doBody />
            </div>
            <!-- /main -->

            <footer id="footer">

                <div class="centercontent">
                    <div class="region region-footer">
                        <div id="block-block-7" class="block block-block">
                            <div class="content">
                                <img src="${paBaseUrlWithScheme}/img/footerLogos.jpg" />
                                <div class="clear"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="footerline">

                    <div class="centercontent">

                        <div id="footersitemap" class="twothird left">&nbsp;</div>
                        <div class="onethird right">

                            <div id="vnavi">
                                <ul>
                                    <li><a href="${paBaseUrlWithScheme}/release">Release: ${releaseVersion}</a></li>
                                    <li><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/">FTP</a></li>
                                    <li><a href="${paBaseUrlWithScheme}/documentation/index">Help/Documentation</a></li>
                                </ul>
                            </div>

                            <div class="clear"></div>

                            <p class="textright">&copy; 2016 IMPC &middot; International Mouse Phenotyping Consortium</p>

                            <div class="clear"></div>

                        </div>

                        <div class="clear"></div>

                    </div>

                </div>

                <jsp:invoke fragment="addToFooter"/>

                <script>
                    var localFrameworkVersion = '1.1';
                    var newDataProtectionNotificationBanner = document.createElement('script');
                    newDataProtectionNotificationBanner.src = 'https://ebi.emblstatic.net/web_guidelines/EBI-Framework/v1.3/js/ebi-global-includes/script/5_ebiFrameworkNotificationBanner.js?legacyRequest='+localFrameworkVersion;
                    document.head.appendChild(newDataProtectionNotificationBanner);
                    newDataProtectionNotificationBanner.onload = function() {
                        ebiFrameworkRunDataProtectionBanner(); // invoke the banner
                    };
                </script>
                <div data-data-protection-version="0.1" data-message="This website requires cookies, and the limited processing of your personal data in order to function. By using the site you are agreeing to this as outlined in our <a href='http://www.mousephenotype.org/about-impc/impc-privacy-policy'>Privacy policies</a>." data-service-id="mousephenotype-org" id="data-protection-message-configuration">&nbsp;</div>

            </footer>

        </c:when>
        <c:otherwise>
            <div id="main">
                <jsp:doBody />
            </div>
            <!-- /main -->
            <footer id="footer">
                <jsp:invoke fragment="addToFooter"/>
            </footer>
        </c:otherwise>
    </c:choose>

    <script type='text/javascript' src='${paBaseUrlWithScheme}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
    <script type='text/javascript' src='${paBaseUrlWithScheme}/js/utils/tools.js?v=${version}'></script>
    <script type='text/javascript' src='${paBaseUrlWithScheme}/js/general/ui.dropdownchecklist_modif.js?v=${version}'></script>
    <script type='text/javascript' src='${paBaseUrlWithScheme}/js/documentationConfig.js?v=${version}'></script>
    <c:choose>
        <c:when test="${param['bare'] == null}">
            <script type='text/javascript' src="${paBaseUrlWithScheme}/js/searchAndFacet/breadcrumbSearchBox.js?v=${version}"></script>
        </c:when>
    </c:choose>

</div> <!-- wrapper -->
</body>