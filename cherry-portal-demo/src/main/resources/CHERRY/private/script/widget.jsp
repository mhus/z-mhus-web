<%@ taglib prefix="ch" uri="http://mhus.org/jsp/cherry/core_1" %>
<ch:load />

<div class="panel panel-default">
<div class="panel-heading"><%=resource.getString("title","")%></div>
  <div class="panel-body">
<ch:editor>
<button type="button" class="btn btn-default"><a href="<ch:editorLink/>"><span class="glyphicon glyphicon glyphicon-edit" aria-hidden="true"></span></a></button>
</ch:editor>
<%=resource.getString("text","")%>
  </div>
</div>
