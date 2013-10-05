<%@ page pageEncoding="UTF-8" %>

<table cellspacing="0" cellpadding="0" style="width: 100%;">
	<tbody>
		<tr>
			<td align="left" style="vertical-align: top;"><table
					cellspacing="0" cellpadding="0" style="width: 100%;">
					<tbody>
						<tr>
							<td align="left" width="" height=""
								style="vertical-align: top;" rowspan="1"><table
									cellspacing="0" cellpadding="0">
									<tbody>
										<tr>
											<td align="left" width="" height=""
												style="vertical-align: top;" colspan="1"><table
													cellspacing="3" cellpadding="0" class="actionBox">
													<tbody>
														<tr>
															<td align="left"
																style="vertical-align: middle;"><button
																	type="button" class="gwt-Button deleteButton"
																	disabled="">Delete</button></td>
															<td align="left"
																style="vertical-align: middle;"><table
																	cellspacing="0" cellpadding="0">
																	<tbody>
																		<tr>
																			<td align="left"
																				style="vertical-align: top;"><div
																					tabindex="0"
																					class="gwt-ToggleButton dropDownArrowButton noWrap gwt-ToggleButton-up-disabled"
																					role="button">
																					<input type="text" tabindex="-1"
																						role="presentation"
																						style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																					<div class="html-face">Move to</div>
																				</div></td>
																			<td align="left"
																				style="vertical-align: top;"><div
																					tabindex="0"
																					class="gwt-ToggleButton dropDownArrowButton noWrap gwt-ToggleButton-up-disabled"
																					role="button">
																					<input type="text" tabindex="-1"
																						role="presentation"
																						style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																					<div class="html-face">Copy to</div>
																				</div></td>
																		</tr>
																	</tbody>
																</table></td>
															<td align="left"
																style="vertical-align: middle;"><div
																	tabindex="0"
																	class="gwt-ToggleButton dropDownArrowButton noWrap gwt-ToggleButton-up-disabled"
																	role="button">
																	<input type="text" tabindex="-1"
																		role="presentation"
																		style="opacity: 0; height: 1px; width: 1px; z-index: -1; overflow: hidden; position: absolute;">
																	<div class="html-face">More actions</div>
																</div></td>
														</tr>
													</tbody>
												</table></td>
										</tr>
										<tr></tr>
										<tr>
											<td align="left" width="" height=""
												style="vertical-align: top;" colspan="1"><table
													cellspacing="3" cellpadding="0"
													class="panelActions selectionBox">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: top;"><div
																	class="gwt-Label">Select:</div></td>
															<td align="left" style="vertical-align: top;"><a
																class="gwt-Anchor" href="javascript:;">All</a></td>
															<td align="left" style="vertical-align: top;"><a
																class="gwt-Anchor" href="javascript:;">None</a></td>
														</tr>
													</tbody>
												</table></td>
										</tr>
									</tbody>
								</table></td>
							<td align="right" width="" height=""
								style="vertical-align: top;" rowspan="1"><table
									cellspacing="0" cellpadding="0">
									<tbody>
										<tr>
											<td align="left" style="vertical-align: top;"><table
													cellspacing="3" cellpadding="0">
													<tbody>
														<tr>
															<td align="left" style="vertical-align: top;">
																<button ng-hide="isFirstPage()" ng-click="firstPage()"
																	type="button" class="gwt-Button noWrap"
																	>« Newest</button></td>
															<td align="left" style="vertical-align: top;">
																<button 
																	ng-hide="isFirstPage()"
																	ng-click="previousPage()"
																	type="button" class="gwt-Button noWrap"
																	>‹ Newer</button></td>
															<td align="left"
																style="vertical-align: middle;"><div
																	class="gwt-Label noWrap">{{pageStatus()}}</div></td>
															
															<td align="left" style="vertical-align: top;">																									
																<button ng-click="nextPage()" ng-hide="isLastPage()"
																	type="button" class="gwt-Button noWrap"
																	>Older ›</button>
															</td>
															
															<td align="left" style="vertical-align: top;">
																<button ng-show="!isLastPage()" ng-click="lastPage()" 
																	type="button" class="gwt-Button noWrap"
																	>Oldest »</button></td>
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
	</tbody>
</table>