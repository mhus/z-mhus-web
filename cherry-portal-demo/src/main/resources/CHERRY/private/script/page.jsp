<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
<%@ taglib prefix="ch" uri="http://mhus.org/jsp/cherry/core_1" %>
<ch:load />
<html>
<head>
<title><%=resource.getString("title")%></title>
</head>
<body>
<a href="<ch:editor_link/>">E</a>
<h1>This is the Page: <%=resource.getString("title")%></h1>

<ch:children resource="<%=resource%>" iterator="child">
<div>
  <ch:render resource="<%=child%>"/>
</div>
</ch:children>
</body>
</html>