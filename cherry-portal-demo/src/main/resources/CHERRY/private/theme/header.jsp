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

<script src="https://unpkg.com/vue@2.0.3/dist/vue.js"></script>

<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">

</head>
<body>
<!-- Navigation -->

<div class="navbar ">
  <div class="navbar-inner">
    <div class="container">
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
<!-- Current User -->
<ch:guest>

 <ul class="nav pull-right">
   <li class="dropdown" id="menuLogin">
     <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="navLogin">Login</a>
     <div class="dropdown-menu" style="padding:17px;">
       <form class="form" id="formLogin"> 
         <input v-model="username" name="username" id="username" type="text" placeholder="Username"> 
         <input v-model="password" name="password" id="password" type="password" placeholder="Password"><br>
         <button v-on:click="login" type="button" id="btnLogin" class="btn">Login</button>
       </form>
     </div>
   </li>
 </ul>
 <script>
 var loginVue = new Vue({
  el: '#menuLogin',
  data: {
    username: '',
    password: ''
  },
  methods: {
	  login: function() {
		  var result = sendPostRequest("/.api/base/login", {'username': this.username, 'password' : this.password });
		  if (result && result.successful && result.successful === true) {
			  window.location.reload();
		  } else {
			  alert("Login failed!");
		  }
	  }
  }
});
 </script>
</ch:guest>
<ch:guest switch="true">
 <ul class="nav pull-right">
   <li class="dropdown" id="menuUser">
     <a class="dropdown-toggle" href="#" data-toggle="dropdown" id="navUser"><ch:currentUser/></a>
	<ul class="dropdown-menu">
		<li><a v-on:click="logout" href="#">Abmelden</a></li>
	</ul>
   </li>
 </ul>
 <script>
 var logoutVue = new Vue({
  el: '#menuUser',
  data: {
  },
  methods: {
	  logout: function() {
		  var result = sendPostRequest("/.api/base/logout");
		  if (result && result.successful && result.successful === true) {
			  window.location.assign("/");
		  } else {
			  alert("Logout failed!");
		  }
	  }
  }
});
 </script>
 
</ch:guest>
</div>
</div>
</div>

<div class="container">
<!-- Breadcrumb -->
<ol class="breadcrumb">
<ch:path resource="<%=call.getNavigationResource()%>" iterator="step">
	<li><%=step.getString("title","")%>
</ch:path>
</ol>
<!-- Content -->