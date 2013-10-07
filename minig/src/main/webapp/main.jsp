<%@ page pageEncoding="UTF-8" %>

<div class="gwt-TabPanelBottom" role="tabpanel" ng-controller="MailOverviewCtrl">
	<div style="width: 100%; height: 100%; padding: 0px; margin: 0px;" aria-hidden="false">
		<table cellspacing="0" cellpadding="0" style="width: 100%; height: 100%;" aria-hidden="false">
			<tbody>
				<tr>
					<td align="left" style="vertical-align: top;">
						<table cellspacing="0" cellpadding="0" style="width: 100%;">
							<tbody>
								<tr>
									<td align="left" width="" height=""	style="vertical-align: top;" colspan="1">
										<jsp:include page="main_actions.jsp"></jsp:include>
									</td>
								</tr>
								<tr>
									<td align="left" width="" height="" style="vertical-align: top;" colspan="1">
										<div class="dataGrid">
											<table class="conversationTable">
												<colgroup>
													<col>
													<col>
													<col>
													<col>
													<col>
													<col>
													<col width="80px">
												</colgroup>
												<tbody>
													<tr ng-repeat="mail in mails">
														<td class="convCb">
															<span class="gwt-CheckBox">
																<input type="checkbox" value="on" id="gwt-uid-284" tabindex="0" ng-model="mail.selected" ng-change="checkSelection()">
																<label for="gwt-uid-284"></label>
															</span>
														</td>
														<td class="convStar">
															<img ng-show="mail.starred" src="resources/images/starred.gif">
															<img ng-hide="mail.starred" src="resources/images/unstarred.gif">
														</td>
														<td class="convStar">
															<img ng-show="showIcon(mail)" ng-src="resources/images/{{whichIcon(mail)}}.png">
														</td>
														<td class="convRecip" ng-class="!mail.read ? 'bold' : ''">
															<div class="gwt-Label" style="white-space: nowrap;" title="{{mail.sender.email}}">{{mail.sender.email}}</div>
														</td>
														<td class="conversationAndPreviewCol">
															<div class="gwt-HTML" style="white-space: nowrap;">
																<span ng-class="!mail.read ? 'conversationUnreadLabel' : ''">{{mail.subject}}</span>
															</div>
														</td>
														<td class="convAttach">
															<img ng-show="mail.attachments.length > 0" src="resources/images/paperclip.gif">
														</td>
														<td class="convDate" ng-class="!mail.read ? 'bold' : ''">
															<div class="gwt-Label" style="white-space: nowrap;" title="{{mail.date | date:'yyyy-MM-dd HH:mm:ss'}}">{{mail.date | timeago}}</div>
														</td>
													</tr>
												</tbody>
											</table>
										</div>
									</td>
								</tr>
								<tr></tr>
								<tr>
									<td align="left" width="" height="" style="vertical-align: top;" colspan="1">
										<jsp:include page="main_actions.jsp"></jsp:include>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	
	<div
		style="width: 100%; height: 100%; padding: 0px; margin: 0px; display: none;"
		aria-hidden="true">
		<table cellspacing="0" cellpadding="0"
			style="width: 100%; height: 100%; display: none;" aria-hidden="true">
			<tbody>
				<tr>
					<td align="left" width="" height="" style="vertical-align: top;"
						colspan="1"><table cellspacing="4" cellpadding="0">
							<tbody>
								<tr>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Send</button></td>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Save now</button></td>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Discard</button></td>
									<td align="left" style="vertical-align: middle;"><div
											class="gwt-Label"></div></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td align="left" width="" height="" style="vertical-align: top;"
						colspan="1"><table cellspacing="0" cellpadding="0"
							class="enveloppe">
							<tbody>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0" class="enveloppeField">
											<tbody>
												<tr>
													<td align="left" style="vertical-align: middle;"><div
															class="gwt-Label">To:</div></td>
													<td align="left" width="100%" style="vertical-align: top;"><div
															class="wrap">
															<div></div>
															<input type="text" class="gwt-TextBox">
														</div></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0" class="enveloppeField"
											style="display: none;" aria-hidden="true">
											<tbody>
												<tr>
													<td align="left" style="vertical-align: middle;"><div
															class="gwt-Label">Cc:</div></td>
													<td align="left" width="100%" style="vertical-align: top;"><div
															class="wrap">
															<div></div>
															<input type="text" class="gwt-TextBox">
														</div></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0" class="enveloppeField"
											style="display: none;" aria-hidden="true">
											<tbody>
												<tr>
													<td align="left" style="vertical-align: middle;"><div
															class="gwt-Label">Bcc:</div></td>
													<td align="left" width="100%" style="vertical-align: top;"><div
															class="wrap">
															<div></div>
															<input type="text" class="gwt-TextBox">
														</div></td>
												</tr>
											</tbody>
										</table></td>
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
																	<td align="left" style="vertical-align: top;"><a
																		class="gwt-Anchor" href="javascript:;"
																		aria-hidden="false">Add Cc</a></td>
																	<td align="left" style="vertical-align: top;"><a
																		class="gwt-Anchor" href="javascript:;"
																		aria-hidden="false">Add Bcc</a></td>
																	<td align="left" style="vertical-align: top;"><a
																		class="gwt-Anchor" href="javascript:;"
																		style="display: none;" aria-hidden="true">Edit
																			subject</a></td>
																</tr>
															</tbody>
														</table></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0" class="enveloppeField"
											aria-hidden="false">
											<tbody>
												<tr>
													<td align="left" style="vertical-align: middle;"><div
															class="gwt-Label">Subject:</div></td>
													<td align="left" width="100%" style="vertical-align: top;"><input
														type="text" class="gwt-TextBox"></td>
												</tr>
											</tbody>
										</table></td>
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
																	<td align="left" style="vertical-align: top;"><table
																			cellspacing="0" cellpadding="0">
																			<tbody></tbody>
																		</table></td>
																</tr>
																<tr>
																	<td align="left" style="vertical-align: top;"><table
																			cellspacing="0" cellpadding="0" class="panelActions">
																			<tbody>
																				<tr>
																					<td align="left" style="vertical-align: top;"><div
																							class="gwt-Label"></div></td>
																					<td align="left" style="vertical-align: top;"><a
																						class="gwt-Anchor" href="javascript:;">Attach
																							a file</a></td>
																				</tr>
																			</tbody>
																		</table></td>
																</tr>
															</tbody>
														</table></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0">
											<tbody>
												<tr>
													<td align="left" style="vertical-align: top;"><div
															class="gwt-Label"></div></td>
													<td align="left" style="vertical-align: middle;"><span
														class="enveloppeField"><input type="checkbox"
															value="on" id="gwt-uid-1" tabindex="0"><label
															for="gwt-uid-1">Very important message</label></span></td>
													<td align="left" style="vertical-align: top;"><span
														class="gwt-CheckBox"><input type="checkbox"
															value="on" id="gwt-uid-2" tabindex="0"><label
															for="gwt-uid-2">Ask for a disposition
																notification</label></span></td>
													<td align="left" style="vertical-align: top;"><span
														class="gwt-CheckBox"><input type="checkbox"
															value="on" id="gwt-uid-3" tabindex="0"><label
															for="gwt-uid-3">Return receipt</label></span></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td align="left" width="" height="" style="vertical-align: top;"><table
							cellspacing="0" cellpadding="0" style="width: 100%;">
							<tbody>
								<tr>
									<td align="left" style="vertical-align: top;"><table
											cellspacing="0" cellpadding="0" class="bodyEditor">
											<tbody>
												<tr>
													<td align="left" width="" height=""
														style="vertical-align: top;" colspan="1"><div
															style="width: 100%; height: 35px;">
															<div
																style="width: 100%; height: 100%; padding: 0px; margin: 0px;"
																aria-hidden="false">
																<table cellspacing="0" cellpadding="0"
																	class="panelActions" style="width: 100%; height: 100%;"
																	aria-hidden="false">
																	<tbody>
																		<tr>
																			<td align="left" style="vertical-align: middle;"><a
																				class="gwt-Anchor" href="javascript:;">Rich
																					formatting »</a></td>
																		</tr>
																	</tbody>
																</table>
															</div>
															<div
																style="width: 100%; height: 100%; padding: 0px; margin: 0px; display: none;"
																aria-hidden="true">
																<table class="panelActions"
																	style="width: 100%; height: 100%; display: none;"
																	aria-hidden="true">
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
																											<td align="left"
																												style="vertical-align: middle;"><div
																													tabindex="0"
																													class="gwt-ToggleButton gwt-ToggleButton-up"
																													role="button" title="Toggle Bold"
																													aria-pressed="false">
																													<input type="text" tabindex="-1"
																														role="presentation"
																														style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;"><img
																														border="0"
																														style="width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAJElEQVR42mNgGAVDBvwngCk2FJ/YMDVwNAxJN5Am6XAUDBcAAEbIOsY2U3mTAAAAAElFTkSuQmCC) no-repeat 0px 0px;"
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
																														class="gwt-Image">
																												</div></td>
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
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
																														src="https://sokol-web.de/minig/minig/clear.cache.gif"
																														onload="this.__gwtLastUnhandledEvent=&quot;load&quot;;"
																														class="gwt-Image">
																												</div></td>
																										</tr>
																									</tbody>
																								</table></td>
																						</tr>
																					</tbody>
																				</table></td>
																			<td style="vertical-align: middle;"><a
																				class="gwt-Anchor" href="javascript:;">«&nbsp;Hide</a></td>
																		</tr>
																	</tbody>
																</table>
															</div>
														</div></td>
												</tr>
												<tr>
													<td align="left" width="" height=""
														style="vertical-align: top;"><div
															class="whiteBackground">
															<iframe class="gwt-RichTextArea" style="height: 324px;"></iframe>
														</div></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td align="left" width="" height="" style="vertical-align: top;"
						colspan="1"><table cellspacing="4" cellpadding="0">
							<tbody>
								<tr>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Send</button></td>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Save now</button></td>
									<td align="left" style="vertical-align: top;"><button
											type="button" class="gwt-Button">Discard</button></td>
									<td align="left" style="vertical-align: middle;"><div
											class="gwt-Label"></div></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div
		style="width: 100%; height: 100%; padding: 0px; margin: 0px; display: none;"
		aria-hidden="true">
		<table cellspacing="0" cellpadding="0"
			style="width: 100%; height: 100%; display: none;" aria-hidden="true">
			<tbody>
				<tr>
					<td align="left" style="vertical-align: top;"><div
							style="width: 100%;">
							<div
								style="width: 100%; height: 100%; padding: 0px; margin: 0px; display: none;"
								aria-hidden="true">
								<table cellspacing="0" cellpadding="0"
									style="width: 100%; height: 100%; display: none;"
									aria-hidden="true">
									<tbody>
										<tr>
											<td align="left" width="" height=""
												style="vertical-align: top;" colspan="1"><table
													cellspacing="3" cellpadding="0">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: middle;"><div
																	class="gwt-HTML">Create a folder:</div></td>
															<td align="left" style="vertical-align: top;"><input
																type="text" class="gwt-TextBox"></td>
															<td align="left" style="vertical-align: top;"><button
																	type="button" class="gwt-Button">Create</button></td>
															<td align="left" style="vertical-align: top;"><button
																	type="button" class="gwt-Button" style="display: none;"
																	aria-hidden="true">Cancel</button></td>
														</tr>
													</tbody>
												</table></td>
										</tr>
										<tr>
											<td align="left" width="" height=""
												style="vertical-align: top;"><table cellspacing="0"
													cellpadding="0" style="width: 100%;">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: top;"><table
																	style="width: 100%;" class="folderSettingsTable">
																	<colgroup>
																		<col>
																		<col>
																		<col>
																		<col>
																		<col>
																	</colgroup>
																	<tbody>
																		<tr>
																			<td width="70%">&nbsp;</td>
																			<td>&nbsp;</td>
																			<td>&nbsp;</td>
																			<td>&nbsp;</td>
																			<td>&nbsp;</td>
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
						</div></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>