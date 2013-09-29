<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html ng-app>
	<head>	
		<!-- Force rendering with google chrome for IE users -->
		<meta http-equiv="X-UA-Compatible" content="chrome=1" />
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		
		<!-- TODO -->
		<link rel="stylesheet" href="assets/minig.css" type="text/css" />
		
		<title>MiniG</title>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.0-rc.2/angular.js"></script>
		<script src="resources/js/controller.js"></script>		
		<style>
			aside {
				float:left;
			}
			.bold {
				font-weight: bold;
			}
		</style>
	</head>
	<body id="page_body">	
		<table cellspacing="0" cellpadding="0" style="width: 100%;"
			class="heading">
			<tbody>
				<tr>
					<td width="" height="" align="right" style="vertical-align: top;"
						rowspan="1"><table cellspacing="4" cellpadding="0"
							class="headingStdLinks">
							<tbody>
								<tr>
									<td align="left" style="vertical-align: middle;"><img id="spinner"
										src="minig/images/spinner_moz.gif" class="gwt-Image"
										style="display: none;" aria-hidden="true"></td>
									<td align="left" style="vertical-align: top;"><div
											id="username" class="gwt-Label userNameLabel"><security:authentication property="name" /></div></td>
									<td align="left" style="vertical-align: top;"><a
										class="gwt-Anchor logoutLabel" href="j_spring_security_logout">Sign
											out</a></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
			</tbody>
		</table>

		<aside ng-controller="FolderListCtrl">
			<input type="text" ng-model="query">
			<ul>
				<li ng-repeat="folder in folders | filter:query"><a ng-href="#/{{folder.id}}">{{folder.id}}</a></li>
			</ul>
		</aside>
		
		<section ng-controller="MailOverviewCtrl">
			<table>
				<thead>					
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th>Sender</th>
						<th>Subject</th>
						<th>Date</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="mail in mails">
						<td><input type="checkbox"></td>
						<td>
							<img ng-show="mail.starred" src="resources/images/starred.gif">
							<img ng-hide="mail.starred" src="resources/images/unstarred.gif">
						</td>
						<td>
							<img ng-show="showIcon(mail)" src="resources/images/{{whichIcon(mail)}}.png">
						</td>
						<td>
							<img ng-show="mail.attachments.length > 0" src="resources/images/paperclip.gif">
						</td>
						<td ng-class="!mail.read ? 'bold' : ''">{{mail.sender.email}}</td>
						<td ng-class="!mail.read ? 'bold' : ''">{{mail.subject}}</td>
						<td ng-class="!mail.read ? 'bold' : ''">{{mail.date | date:'yyyy-MM-dd HH:mm:ss'}}</td>
					</tr>
				</tbody>
			</table>
		</section>
	</body>
</html>
