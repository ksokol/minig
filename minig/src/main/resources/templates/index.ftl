<#assign jwr=JspTaglibs["/META-INF/jawr.tld"] />

<!DOCTYPE html>
<html ng-app="minigApp">
<head>
    <!-- Force rendering with google chrome for IE users -->
    <meta http-equiv="X-UA-Compatible" content="chrome=1"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <link rel="icon" type="image/gif" href="images/favicon.ico">
    <title>MiniG</title>

    <@jwr.style src="/bundles/styles.css" />
    <@jwr.script src="/bundles/javascript.js" />

</head>
<body id="page_body">
<table cellspacing="0" cellpadding="0">
    <tbody>
    <tr>
        <td align="left" width="" height="" style="vertical-align: top;" rowspan="1">
            <table cellspacing="0" cellpadding="0" style="width: 100%;" class="heading">
                <tbody>
                <tr>
                    <td width="" height="" align="right" style="vertical-align: top;" rowspan="1">
                        <table cellspacing="4" cellpadding="0" class="headingStdLinks">
                            <tbody>
                            <tr>
                                <td align="left" style="vertical-align: middle;">
                                    <loading-indicator></loading-indicator>
                                </td>
                                <td align="left" style="vertical-align: top;">
                                    <div id="username" class="gwt-Label userNameLabel"></div>
                                </td>
                                <td align="left" style="vertical-align: top;">
                                    <a class="gwt-Anchor logoutLabel" href="logout">Sign out</a>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>
            <table cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td align="left" width="" height="1.4em" style="vertical-align: top;" colspan="2">
                        <table notification cellspacing="0" cellpadding="0" class="statusPanel">
                            <tbody>
                            <tr>
                                <td align="center" style="vertical-align: top;">
                                    <table style="display:none;" cellspacing="3" cellpadding="0"
                                           class="notificationMessage">
                                        <tbody>
                                        <tr>
                                            <td align="left" style="vertical-align: top;">
                                                <div id="notification-label" class="gwt-Label"></div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td align="left" width="16%" height="" style="vertical-align: top;" rowspan="1">
                        <#include "aside.ftl">
                    </td>
                    <td align="left" width="" height="" style="vertical-align: top; width: 100%;">
                        <div ng-view></div>
                    </td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>