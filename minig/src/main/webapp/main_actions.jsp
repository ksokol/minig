<%@ page pageEncoding="UTF-8" %>

<table cellspacing="0" cellpadding="0" style="width: 100%;">
	<tbody>
		<tr>
			<td align="left" style="vertical-align: top;">
				<table cellspacing="0" cellpadding="0" style="width: 100%;">
					<tbody>
						<tr>
							<td align="left" width="" height="" style="vertical-align: top;" rowspan="1">
								<table cellspacing="0" cellpadding="0">
									<tbody>
										<tr>
											<td align="left" width="" height="" style="vertical-align: top;" colspan="1">
												<table cellspacing="3" cellpadding="0" class="actionBox">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: middle;">
																<button type="button" class="gwt-Button deleteButton" ng-disabled="!hasMailSelected()" ng-click="deleteMails()">Delete</button>
															</td>
															<td align="left" style="vertical-align: middle;">
																<table cellspacing="0" cellpadding="0">
																	<tbody>
																		<tr>
																			<td inline-folder-select="moveToFolder()" align="left" style="vertical-align: top;">
																				<div tabindex="0"
																					class="gwt-ToggleButton dropDownArrowButton noWrap"
																					ng-class="hasMailSelected() ? 'gwt-ToggleButton-up' : 'gwt-ToggleButton-up-disabled' "
																					role="button">
																					<input type="text" tabindex="-1" role="presentation" style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																					<div class="html-face">Move to</div>
																				</div>
																			</td>
																			<td inline-folder-select="copyToFolder()" align="left"
																				style="vertical-align: top;"><div
																					tabindex="0"
																					class="gwt-ToggleButton dropDownArrowButton noWrap"
																					ng-class="hasMailSelected() ? 'gwt-ToggleButton-up' : 'gwt-ToggleButton-up-disabled' "
																					role="button">
																					<input type="text" tabindex="-1"
																						role="presentation"
																						style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																					<div class="html-face">Copy to</div>
																				</div></td>
																		</tr>
																	</tbody>
																</table>
															</td>
															<td more-actions align="left" style="vertical-align: middle;">
																<div tabindex="0"
																	class="gwt-ToggleButton dropDownArrowButton noWrap"
																	ng-class="hasMailSelected() ? 'gwt-ToggleButton-up' : 'gwt-ToggleButton-up-disabled'"
																	role="button">
																	<input type="text" tabindex="-1" role="presentation"
																		style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																	<div class="html-face">More actions</div>

                                                                    <div class="bg-overlay-ma" style="top:0px;left:0px; position: absolute; z-index: 9999; display:none"></div>
																	<div id="more-actions" style="overflow: visible; position: absolute; left: 443px; top: 87px; display:none; z-index:10000;" class="gwt-PopupPanel">
                                                                        <div class="popupContent">
                                                                            <table>
                                                                                <colgroup>
                                                                                    <col>
                                                                                </colgroup>
                                                                                <tbody>
                                                                                <tr>
                                                                                    <td><a class="gwt-Anchor" data-event="mark-as-read">Mark as read</a></td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td><a class="gwt-Anchor" data-event="mark-as-unread">Mark as unread</a></td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td><a class="gwt-Anchor" data-event="add-star">Add star</a></td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td><a class="gwt-Anchor" data-event="remove-star">Remove star</a></td>
                                                                                </tr>
                                                                                </tbody>
                                                                            </table>
                                                                        </div>
                                                                    </div>
																</div>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr></tr>
										<tr>
											<td align="left" width="" height=""
												style="vertical-align: top;" colspan="1"><table
													cellspacing="3" cellpadding="0"
													class="panelActions selectionBox">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: top;">
																<div class="gwt-Label">Select:</div>
															</td>
															<td align="left" style="vertical-align: top;">
																<a ng-click="selectAll()" class="gwt-Anchor" href="javascript:;">All</a>
															</td>
															<td align="left" style="vertical-align: top;">
																<a ng-click="selectNone()" class="gwt-Anchor" href="javascript:;">None</a>
															</td>
														</tr>
													</tbody>
												</table></td>
										</tr>
									</tbody>
								</table></td>
							<td align="right" width="" height="" style="vertical-align: top;" rowspan="1">
                                <pagination />
                            </td>
						</tr>
					</tbody>
				</table>
            </td>
		</tr>
	</tbody>
</table>