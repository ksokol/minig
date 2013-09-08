<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>	
		<!-- Force rendering with google chrome for IE users -->
		<meta http-equiv="X-UA-Compatible" content="chrome=1" />
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		
		
		<!-- default locale on this one -->
		<!--meta name="gwt:property" content="locale=en"-->
		
		<!-- TODO -->
		<link rel="stylesheet" href="assets/minig.css" type="text/css" />
		
		<title>MiniG</title>
		<script language="javascript" src="minig/minig.nocache.js">
			
		</script>
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
	
		
		<iframe src="javascript:''" id="__gwt_historyFrame"
			style="width: 0; height: 0; border: 0"> </iframe>
	
		<div id="webmail_root"></div>	
	</body>
</html>
