<%@page import="de.mhus.lib.core.directory.ResourceNode"
%><%@page import="de.mhus.lib.cao.CaoNode"
%><%@taglib prefix="ch" uri="http://mhus.org/jsp/cherry/core_1"
%><ch:load /><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title><%=call.getNavigationResource().getString("title","")%></title>
<ch:htmlHead/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

</head>
<body><div class="container">
<!-- Navigation -->
<ul class="nav nav-pills">
<ch:children resource="<%=call.getNavigationResource().getConnection().getRoot() %>" iterator="nav1" >
	<li role="presentation" class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown"  role="button" aria-haspopup="true" aria-expanded="false" href="/<%=nav1.getName()%>"><%=nav1.getString("title","?")%> <span class="caret"></span></a>
		<ul class="dropdown-menu">
		<ch:children resource="<%=nav1%>" iterator="nav2" >
			<li><a href="/<%=nav1.getName()%>/<%=nav2.getName()%>"><%=nav2.getString("title","?")%></a></li>
		</ch:children>
		</ul>
	</li>	
</ch:children>
</ul>
<!-- Breadcrumb -->
<ol class="breadcrumb">
<ch:path resource="<%=call.getNavigationResource()%>" iterator="step">
	<li><%=step.getString("title","")%>
</ch:path>
</ol>
<!-- Content -->