<%@page import="de.mhus.cherry.portal.api.WidgetApi"%>
<%@page import="de.mhus.osgi.sop.api.Sop"%>
<%@page import="de.mhus.lib.cao.CaoNode"%>
<%@page import="de.mhus.cherry.portal.api.CallContext"%>
<%@page import="java.io.File"%>
<p><%
CallContext call = (CallContext)request.getAttribute(CallContext.REQUEST_ATTRIBUTE_NAME);
CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
%>
<%=res.getString("title")%>
</p>
