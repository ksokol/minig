<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html ng-app="minigApp">
	<head>	
		<!-- Force rendering with google chrome for IE users -->
		<meta http-equiv="X-UA-Compatible" content="chrome=1" />
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		
		<link rel="icon" type="image/gif" href="resources/images/favicon.ico">
		
		<link rel="stylesheet" href="resources/css/minig.css" type="text/css" />
		
		<title>MiniG</title>
	 	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.0-rc.2/angular.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.0-rc.2/angular-resource.js"></script>
		<script src="http://momentjs.com/downloads/moment.min.js"></script>
		
		<script src="resources/js/config.js"></script>
		<script src="resources/js/service.js"></script>
		<script src="resources/js/filter.js"></script>
		<script src="resources/js/directive.js"></script>
		<script src="resources/js/resource.js"></script>
		<script src="resources/js/controller.js"></script>	
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
														<img loading-indicator id="spinner" src="resources/images/spinner_moz.gif" class="gwt-Image" style="display: none;">
													</td>
													<td align="left" style="vertical-align: top;">
														<div id="username" class="gwt-Label userNameLabel">
															<security:authentication property="name" />
														</div>
													</td>
													<td align="left" style="vertical-align: top;">
														<a class="gwt-Anchor logoutLabel" href="j_spring_security_logout">Sign out</a>
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
														<table  style="display:none;" cellspacing="3" cellpadding="0" class="notificationMessage">
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
<td align="left" width="22%" height="" style="vertical-align: top;" rowspan="1">

	<jsp:include page="aside.html"></jsp:include>

	</td>
		<td align="left" width="" height="" style="vertical-align: top; width: 100%;">
		
		<jsp:include page="main.jsp"></jsp:include>
		
		</td>
		
		</tr>
		</tbody>
		</table>
	</body>
</html>
