<%@ page import="com.google.code.springextjs.direct.api.ExtJsDirectRemotingApiUtil" %>
<%@ page import="com.google.code.springextjs.direct.controller.impl.DemoExtJsDirectRemotingController" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title>Ext.Direct</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/scripts/extjs/resources/css/ext-all.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/extjs/ext-all.js"></script>

    <script type="text/javascript">
        <%= ExtJsDirectRemotingApiUtil.getExtDirectRemotingApiString(
                request.getContextPath() + "/controller/extjs/remoting/router",
                DemoExtJsDirectRemotingController.class,
                "TestAction")
        %>
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/example.js"></script>
    <style type="text/css">
        .padding {
             padding : 20px;
        }
        h2{
             color: blue;
             padding-bottom: 10px;
        }
        #out {
            padding: 5px;
            overflow:auto;
            border-width:0;
        }
        #out b {
            color:#555;
        }
        #out xmp {
            margin: 5px;
        }
        #out p {
            margin:0;
        }
    </style>
</head>
<body>

<div id="overview" class="padding">
    <h2>SpringExtJs examples in this application:</h2>
    <ul style="margin-left:10px;list-style-type:disc">
        <li>Remote methods</li><li>Tree loading</li>
        <li>Form loading</li>
        <li>Form validation</li>
        <li>Form submits</li>
    </ul>
</div>

<div id="form-info" class="padding">
    <h2>Form Handling Integrated with Spring 3 MVC Form Features</h2>
    <p>
        The form demonstrates remote form loading, Springs's localized server side validation integration,
        localized success message, and form submission to standard Spring MVC controller. 
    </p>
</div>

</body>
</html>