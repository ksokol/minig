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
														<td class="convStar" ng-click="clickStar()">
															<img ng-show="mail.starred" src="resources/images/starred.gif">
															<img ng-hide="mail.starred" src="resources/images/unstarred.gif">
														</td>
														<td class="convStar">
															<img ng-show="showIcon(mail)" ng-src="resources/images/{{whichIcon(mail)}}.png">
														</td>
														<td class="convRecip" ng-class="!mail.read ? 'bold' : ''">
															<div class="gwt-Label" style="white-space: nowrap;" title="{{mail.sender.email}}">{{mail.sender | displayName}}</div>
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
</div>