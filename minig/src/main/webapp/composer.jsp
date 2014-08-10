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


<table
        cellspacing="0" cellpadding="0" class="bodyEditor">



<tbody>
<tr>
<td align="left" width="" height="" style="vertical-align: top;" colspan="1">
    <div style="width: 100%; height: 35px;">
        <div style="width: 100%; height: 100%; padding: 0px; margin: 0px;" aria-hidden="false" ng-hide="showRichFormatting">
            <table cellspacing="0" cellpadding="0" class="panelActions" style="width: 100%; height: 100%;" aria-hidden="false">
                <tbody>
                <tr>
                    <td align="left" style="vertical-align: middle;"><a
                            class="gwt-Anchor" ng-click="showRichFormatting = true">{{ "Rich formatting »" | i18n}}
                    </a></td>
                </tr>
                </tbody>
            </table>
        </div>
<div style="width: 100%; height: 100%; padding: 0px; margin: 0px;" aria-hidden="true" ng-show="showRichFormatting">
<table class="panelActions" style="width: 100%; height: 100%;" aria-hidden="true" ng-show="showRichFormatting">
<colgroup>
    <col>
</colgroup>
<tbody>
<tr>
<td width="100%"><table cellspacing="0"
                        cellpadding="0">
<tbody>
<tr>
<td align="left" style="vertical-align: top;"><table
        cellspacing="0" cellpadding="0"
        style="width: 100%;">
<tbody>
<tr>
<td align="left" style="vertical-align: middle;">
    <div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Toggle Bold"
        aria-pressed="false">
    <input type="text" tabindex="-1" role="presentation" style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
    <img border="0"
        style="width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAJElEQVR42mNgGAVDBvwngCk2FJ/YMDVwNAxJN5Am6XAUDBcAAEbIOsY2U3mTAAAAAElFTkSuQmCC) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div>

</td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Toggle Italic"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAHt7e4SEhP///yH5BAEAAAMALAAAAAAUABQAAAIgnI+py+0Po0Sg1jmqwOHiGnDeBIoAlgGhZFnoC8fyDBUAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Toggle Underline"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAHt7e////////yH5BAEAAAIALAAAAAAUABQAAAIplI+py+0PF5hA0Prm0ZBbnIGe441NCZJiegKBEJgtFdXKhdv6zve+UAAAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        class="gwt-HTML">&nbsp;</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Font"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUALMAAAAAABodIicrMzQ6RE5XZltld2h0iHWCmYKRqo+fu5yuzKm83f///////////////yH5BAEAAA8ALAAAAAAUABQAAARA8MlJq704622A/4CgeYsCGNKBYgUgdcqWTp5cBSBhP+ZKf5gDIGGpYQiAxY42WEoWJ+ejZUMAYtblACjter/eCAA7) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Size"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAIiXsv///////yH5BAEAAAIALAAAAAAUABQAAAIxlI+py+0PGZi0OnBmvhkbD2lSJYoLFngqaB1pYgqx8CKzUndP/oFLWiJFhsSi8RgpAAA7) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Text Color"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUALMAAAAAAAAA/wB7AP8AAACEAP8A/3t7e4SEhAD/////AP///////////////////////yH5BAEAAA8ALAAAAAAUABQAAARa8MlJq73Ygs17fsC0iV8ojedHoWrFtmJozDSNjcagG0hv3DLd4NBD/C64w2G2XGo6h4T0ICAIjhmDNGGwErAYg1LcPKgMgbShwAZfDunAgV1wW5hNsR3G7/MjADs=) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Background Color"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAP////37B////yH5BAEAAAMALAAAAAAUABQAAAI5nI+py+0PF5i0KoEFyJNnYXwb1pFf+HFjCg4suaYoW7JzWsvum587HfO9NMHMTVTEMCqTiPMJjUIKADs=) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        class="gwt-HTML">&nbsp;</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Left Justify"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIejI+py+0PE5i01hitxTzoD3QOOIkZeZkLqrbuCxsFADs=) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Center"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIdjI+py+0PE5i01oiDtTnuD3QQOIkPyZkNqrbuCxcAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Right Justify"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAH0lEQVR42mNgGAXUBv/JxIPfxaNhOBqGIysMRwHpAADGwEe5v4tWjAAAAABJRU5ErkJggg==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-ToggleButton gwt-ToggleButton-up"
        role="button" title="Toggle Strikethrough"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAABAP///yH5BAEAAAEALAAAAAAUABQAAAInjI+py+0PHZggVDhPxtd0uVmRFYLUSY1p1K3PVzZhzFTniOf6zjMFADs=) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Indent Right"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAP///////wAAACH5BAEAAAIALAAAAAAUABQAAAIjlI+py+0Po5wHVIBzVphqa3zbpUkfeSYdJYCtOLLyTNf2LRQAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Indent Left"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAP///////wAAACH5BAEAAAIALAAAAAAUABQAAAIjlI+py+0Po5wIGICzVpgKbX1gd4wTeI1htVKhmnnyTNf2fRQAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button"
        title="Insert Horizontal Rule"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIajI+py+0Po5y0OoCz1mr73H2eRZbmiabqUwAAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Insert Ordered List"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIjjI+py+0MwIMn2rvkTHZ7132VcZWZSEbommqiW2Lnq7L2vRUAOw==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Insert Unordered List"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAIklEQVR42mNgGPHgPxKmmYH/icD0c+FopIxGymikjALaAAC2dzXLcl5hjgAAAABJRU5ErkJggg==) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
<td align="left"
    style="vertical-align: middle;"><div
        tabindex="0"
        class="gwt-PushButton gwt-PushButton-up"
        role="button" title="Remove Formatting"
        aria-pressed="false">
    <input type="text" tabindex="-1"
           role="presentation"
           style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
        border="0"
        style="width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABB0lEQVR42tWUsQ2DMBBFyQSICShCTwEdlijooECiCR0jMAIjMAIjMAILINHSMQIj/OTAJpAQTEApctKXsDk9f9+drCh/G03ToKoqqXYDVVWl5E3xHHnwk5GmKbquQxiGE4T+kXtd18W+PAiUJMk8+c1RnucoigK76ye+y7Kc3M0d9X0P0tfNIbcCuNvRVlCtBJDqeQrGAYNM0zzvjgovgPzq5+J1XE7BeAdXBxjj/nNtGPLDsiybgK7rYg4TEjBYFuA4+NgIDlhoCb0AmoYRZq/D2rZFXde7tQmjYIxJHwMhGFfApmsywPMAPzjucHBmP5yx0RkCH4gi4BYfnwJytljHMX72MN8BS744G4gNMaUAAAAASUVORK5CYII=) no-repeat 0px 0px;"
        src="resources/images/clear.cache.gif"
        class="gwt-Image">
</div></td>
</tr>
</tbody>
</table></td>
</tr>
</tbody>
</table></td>
<td style="vertical-align: middle;"><a
        class="gwt-Anchor" ng-click="showRichFormatting = false">«&nbsp;Hide</a></td>
</tr>
</tbody>
</table>
</div>
</div></td>
</tr>
    <tr>
        <td align="left" width="" height="" style="vertical-align: top;">
            <div class="whiteBackground">
            <iframe class="gwt-RichTextArea" style="height: 324px;"></iframe>
        </div></td>
    </tr>
</tbody>
</table></td>
</tr>
</tbody>
</table></td>
</tr>

</tbody>
</table>
</div>
</div>