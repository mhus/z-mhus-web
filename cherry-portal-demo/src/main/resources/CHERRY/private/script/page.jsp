<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
<%@ taglib prefix="ch" uri="http://mhus.org/jsp/cherry/core_1" %>
<ch:load />
<div class="page-header">
<h1><ch:print name="resource" attribute="title"/></h1>
<ch:editor>
<button type="button" class="btn btn-default"><a href="<ch:editorLink/>"><span class="glyphicon glyphicon glyphicon-edit" aria-hidden="true"></span></a></button>
</ch:editor><ch:print name="resource" attribute="text"/>
</div>
<ch:widgets name="resource" iterator="child">
<div>
  <ch:render name="child"/>
</div>
</ch:widgets>
