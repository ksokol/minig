<div class="gwt-TabPanelBottom" role="tabpanel">
    <div style="width: 100%; height: 100%; padding: 0px; margin: 0px;" aria-hidden="false">
        <table cellspacing="0" cellpadding="0" style="width: 100%; height: 100%;" aria-hidden="false">
            <tbody>
            <tr>
                <td align="left" style="vertical-align: top;">
                    <table cellspacing="0" cellpadding="0" style="width: 100%;">
                        <tbody>
                        <tr>
                            <td width="" height="" align="left" style="vertical-align: top;" colspan="1">
                                <!-- <jsp:include page="main_actions.jsp"></jsp:include> -->

                                <main-actions />
                            </td>
                        </tr>
                        <tr>
                            <td width="" height="" align="left" style="vertical-align: top;">
                                <table cellspacing="0" cellpadding="0" class="conversationDisplay" style="width: 100%;">
                                    <tbody>
                                    <tr>
                                        <td align="left" style="vertical-align: top;">
                                            <div class="conversationTitle" style="width: 100%;"><b>{{mail.subject}}</b></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" style="vertical-align: top;">
                                            <table cellspacing="0" cellpadding="0" style="width: 100%;">
                                                <tbody>
                                                <tr>
                                                    <td align="left" style="vertical-align: top;">
                                                        <table cellspacing="0" cellpadding="0" style="width: 100%;">
                                                            <tbody>
                                                            <tr>
                                                                <td align="left" style="vertical-align: top;">
                                                                    <table class="singleMessageHeader"
                                                                           style="width: 100%;">
                                                                        <colgroup>
                                                                            <col>
                                                                        </colgroup>
                                                                        <tbody>
                                                                        <tr>
                                                                            <td ng-click="clickStar()" class="star">
                                                                                <img ng-show="mail.starred" src="resources/images/starred.gif">
                                                                                <img ng-hide="mail.starred" src="resources/images/unstarred.gif">
                                                                            </td>
                                                                            <td class="recipientsCol">
                                                                                <div class="gwt-HTML recipients">
                                                                                    <sender-panel></sender-panel>
                                                                                    &nbsp;to&nbsp;
                                                                                    <recipient-panel></recipient-panel>
                                                                                </div>
                                                                            </td>
                                                                            <td>
                                                                                <a class="gwt-Anchor recipientsDetails" ng-click="toggleDetails()" ng-show="hideDetails">show detail</a>
                                                                                <a class="gwt-Anchor recipientsDetails" ng-click="toggleDetails()" ng-hide="hideDetails">hide detail</a>
                                                                            </td>
                                                                            <td>
                                                                                <div class="gwt-Label noWrap recipientsDate">{{mail.date | timeago}}</div>
                                                                            </td>
                                                                        </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td align="left" style="vertical-align: top;">
                                                                    <table cellspacing="0" cellpadding="0" style="width: 100%;" class="singleMessageBody">
                                                                        <tbody>
                                                                        <tr>
                                                                            <td align="left" style="vertical-align: top;">
                                                                                <table class="messageDetails" ng-hide="hideDetails">
                                                                                    <colgroup>
                                                                                        <col>
                                                                                    </colgroup>
                                                                                    <tbody>
                                                                                    <tr>
                                                                                        <td class="keys">From</td>
                                                                                        <td>
                                                                                            <sender-panel></sender-panel>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td class="keys">To</td>
                                                                                        <td>
                                                                                            <recipient-panel></recipient-panel>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td class="keys">Date</td>
                                                                                        <td>
                                                                                            <div class="gwt-Label">{{mail.date | dateformat}}</div>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td class="keys">Subject</td>
                                                                                        <td>
                                                                                            <div class="gwt-Label">{{mail.subject}}</div>
                                                                                        </td>
                                                                                    </tr>
                                                                                    </tbody>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="left" style="vertical-align: top;">
                                                                                <table cellspacing="0" cellpadding="0" class="messageText">
                                                                                    <tbody>
                                                                                        <td>{{mail.body.plain}}</td>
                                                                                    </tbody>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="left" style="vertical-align: top;">
                                                                                <table cellspacing="0" cellpadding="0">
                                                                                    <tbody></tbody>
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td align="left"
                                                                                style="vertical-align: top;">
                                                                                <table cellspacing="3" cellpadding="0"
                                                                                       style="width: 100%;"
                                                                                       class="replyZone">
                                                                                    <tbody>
                                                                                    <tr>
                                                                                        <td width="" height=""
                                                                                            align="left"
                                                                                            style="vertical-align: top;"
                                                                                            colspan="1">
                                                                                            <table cellspacing="4"
                                                                                                   cellpadding="0">
                                                                                                <tbody>
                                                                                                <tr>
                                                                                                    <td align="left"
                                                                                                        style="vertical-align: top;">
                                                                                                        <a class="gwt-Anchor replyLink"
                                                                                                           href="javascript:;">Reply</a>
                                                                                                    </td>
                                                                                                    <td align="left"
                                                                                                        style="vertical-align: top;">
                                                                                                        <a class="gwt-Anchor replyToAllLink"
                                                                                                           href="javascript:;">Reply
                                                                                                            to all</a>
                                                                                                    </td>
                                                                                                    <td align="left"
                                                                                                        style="vertical-align: top;">
                                                                                                        <a class="gwt-Anchor forwardLink"
                                                                                                           href="javascript:;">Forward</a>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                </tbody>
                                                                                            </table>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="" height=""
                                                                                            align="left"
                                                                                            style="vertical-align: top;">
                                                                                            <textarea
                                                                                                    class="gwt-TextArea"
                                                                                                    style="width: 40em;"
                                                                                                    rows="3"></textarea>
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
                                                            </tbody>
                                                        </table>
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

                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
    </div>



</div>