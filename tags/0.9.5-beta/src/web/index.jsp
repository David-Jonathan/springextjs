<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title>Ext.Direct</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/scripts/extjs/resources/css/ext-all.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/extjs/ext-all.js"></script>

    <script type="text/javascript">
        <%= com.google.code.springextjs.remoting.util.ExtJsRemotingUtil.createExtRemotingApiString(
                request.getContextPath() + "/controller/extjs/remoting/router",
                com.google.code.springextjs.sample.RemotingController.class,
                "TestAction")
        %>
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/example.js"></script>
    <style type="text/css">
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

<h1>Ext.Direct Generic Remoting with SpringExtJS</h1>


</body>
</html>