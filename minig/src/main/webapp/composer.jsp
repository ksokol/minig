<%@ page pageEncoding="UTF-8" %>
<div class="gwt-TabPanelBottom" role="tabpanel">
<div style="width: 100%; height: 100%; padding: 0px; margin: 0px;" aria-hidden="true">
<table cellspacing="0" cellpadding="0" style="width: 100%; height: 100%;" aria-hidden="true">
<tbody>
<tr>
    <td align="left" width="" height="" style="vertical-align: top;" colspan="1"><table cellspacing="4" cellpadding="0">
        <tbody>
            <tr>
                <td align="left" style="vertical-align: top;">
                    <button type="button" class="gwt-Button" ng-click="send()">Send</button>
                </td>
                <td align="left" style="vertical-align: top;">
                    <button type="button" class="gwt-Button" ng-click="save()">Save now</button>
                </td>
                <td align="left" style="vertical-align: top;">
                    <button type="button" class="gwt-Button" ng-click="discard()">Discard</button>
                </td>
                <td align="left" style="vertical-align: top;">
                    <button type="button" class="gwt-Button" ng-click="delete()">Delete</button>
                </td>
                <td align="left" style="vertical-align: middle;">
                    <div class="gwt-Label"></div>
                </td>
            </tr>
        </tbody>
    </table></td>
</tr>
<tr>
    <td align="left" width="" height="" style="vertical-align: top;" colspan="1">
        <table cellspacing="0" cellpadding="0" class="enveloppe">
        <tbody>
        <tr>
            <td align="left" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0" class="enveloppeField">
                    <tbody>
                        <tr>
                            <td align="left" style="vertical-align: middle;">
                                <div class="gwt-Label">To:</div>
                            </td>
                            <td align="left" width="100%" style="vertical-align: top;">
                                <recipient-input recipients="mail.to" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr ng-show="showCc">
            <td align="left" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0" class="enveloppeField" aria-hidden="true">
                    <tbody>
                        <tr>
                            <td align="left" style="vertical-align: middle;">
                                <div class="gwt-Label">Cc:</div>
                            </td>
                            <td align="left" width="100%" style="vertical-align: top;">
                                <recipient-input recipients="mail.cc" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr ng-show="showBcc">
            <td align="left" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0" class="enveloppeField" aria-hidden="true">
                <tbody>
                <tr>
                    <td align="left" style="vertical-align: middle;">
                        <div class="gwt-Label">Bcc:</div>
                    </td>
                    <td align="left" width="100%" style="vertical-align: top;">
                        <recipient-input recipients="mail.bcc" />
                    </td>
                </tr>
                </tbody>
            </table>
            </td>
        </tr>
        <tr>
            <td align="left" style="vertical-align: top;"><table
                    cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td align="left" style="vertical-align: top;"><div
                            class="gwt-Label"></div></td>
                    <td align="left" style="vertical-align: top;"><table
                            cellspacing="2" cellpadding="0" class="panelActions">
                        <tbody>
                        <tr>
                            <td align="left" style="vertical-align: top;" ng-hide="showCc">
                                <a class="gwt-Anchor" ng-click="showCc = true" aria-hidden="false">Add Cc</a>
                            </td>
                            <td align="left" style="vertical-align: top;" ng-hide="showBcc">
                                <a class="gwt-Anchor" ng-click="showBcc = true" aria-hidden="false">Add Bcc</a></td>
                        </tr>
                        </tbody>
                    </table></td>
                </tr>
                </tbody>
            </table></td>
        </tr>
        <tr>
            <td align="left" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0" class="enveloppeField" aria-hidden="false">
                    <tbody>
                        <tr>
                            <td align="left" style="vertical-align: middle;">
                                <div class="gwt-Label">Subject:</div>
                            </td>
                            <td align="left" width="100%" style="vertical-align: top;">
                                <input type="text" class="gwt-TextBox subject-input" ng-model="mail.subject">
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td align="left" style="vertical-align: top;"><table
                    cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td align="left" style="vertical-align: top;"><table
                            cellspacing="0" cellpadding="0" class="enveloppeField">
                        <tbody>
                        <tr>
                            <td align="left" style="vertical-align: top;">
                                <attachment-panel mail="mail" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <attachment-upload mail="mail"/>
                            </td>
                        </tr>
                        </tbody>
                    </table></td>
                </tr>
                </tbody>
            </table></td>
        </tr>
        <tr>
            <td align="left" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td align="left" style="vertical-align: top;">
                                <div class="gwt-Label"></div>
                            </td>
                            <td align="left" style="vertical-align: middle;">
                                <span class="enveloppeField">
                                    <input type="checkbox" value="true" id="gwt-uid-1" tabindex="0" ng-model="mail.highPriority">
                                    <label for="gwt-uid-1">Very important message</label>
                                </span>
                            </td>
                            <td align="left" style="vertical-align: top;">
                                <span class="gwt-CheckBox">
                                    <input type="checkbox" value="on" id="gwt-uid-2" tabindex="0" ng-model="mail.askForDispositionNotification">
                                    <label for="gwt-uid-2">Ask for a disposition notification</label>
                                </span>
                            </td>
                            <td align="left" style="vertical-align: top;">
                                <span class="gwt-CheckBox">
                                    <input type="checkbox" value="true" id="gwt-uid-3" tabindex="0"  ng-model="mail.receipt">
                                    <label for="gwt-uid-3">Return receipt</label>
                                </span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        </tbody>
    </table></td>
</tr>
<tr>
<td align="left" width="" height="" style="vertical-align: top;"><table
        cellspacing="0" cellpadding="0" style="width: 100%;">
<tbody>
<tr>
<td align="left" style="vertical-align: top;">
    <table cellspacing="0" cellpadding="0" class="bodyEditor">
        <tbody>
            <tr>
                <td align="left" width="" height="" style="vertical-align: top;">
                    <div class="whiteBackground">
                        <text-angular ng-model="mail.body.html"></text-angular>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</td>
</tr>
</tbody>
</table></td>
</tr>

</tbody>
</table>
</div>
</div>