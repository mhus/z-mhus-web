<%@page import="de.mhus.lib.core.logging.MLogUtil"%>
<%@page import="de.mhus.lib.core.MLog"%>
<%@page import="de.mhus.lib.core.directory.ResourceNode"%>
<%@page import="de.mhus.cherry.portal.api.WidgetApi"%>
<%@page import="de.mhus.osgi.sop.api.Sop"%>
<%@page import="de.mhus.lib.cao.CaoNode"%>
<%@page import="de.mhus.cherry.portal.api.CallContext"%>
<%
CallContext call = (CallContext)request.getAttribute(CallContext.REQUEST_ATTRIBUTE_NAME);
CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
%>
<html>
<head>
<title><%=res.getString("title")%></title>
</head>
<body>
<h1>This is a Page</h1>
<%
for (ResourceNode sub : res.getNodes()) {
	%><div><%
 try {
	 out.flush();
	Sop.getApi(WidgetApi.class).doRender(call, sub);
  } catch (Throwable t) {
	  MLogUtil.log().w(t);
  }
	%></div><%
}
%>
</body>
</html>