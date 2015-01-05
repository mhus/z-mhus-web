feature:install http
feature:install http-whiteboard
feature:install scr
feature:install jdbc
feature:install openjpa

install -s mvn:org.codehaus.jackson/jackson-core-asl/1.9.5
install -s mvn:org.codehaus.jackson/jackson-mapper-asl/1.9.5
install -s mvn:javax.portlet/portlet-api/2.0
 
install -s mvn:de.mhus.lib/mhu-lib-annotations/3.2.8-SNAPSHOT
install -s mvn:de.mhus.lib/mhu-lib-core/3.2.8-SNAPSHOT
install -s mvn:de.mhus.lib/mhu-lib-persistence/3.2.8-SNAPSHOT
install -s mvn:de.mhus.lib/mhu-lib-logging/3.2.8-SNAPSHOT
install -s mvn:de.mhus.lib/mhu-lib-karaf/3.2.8-SNAPSHOT
install -s mvn:de.mhus.lib/mhu-lib-j2ee/3.2.8-SNAPSHOT

uninstall -f org.ops4j.pax.web.pax-web-jetty

install -s mvn:org.ops4j.pax.web/pax-web-jsp/3.1.0
install -s 'wrap:mvn:com.sun.org.apache/jaxp-ri/1.4$Bundle-SymbolicName=jaxp-ri&Bundle-Version=1.4&Export-Package=com.sun.org.apache*;version="1.4",\!*'

install -s mvn:de.mhus.osgi.cherry/web-virtualization-api/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/web-virtualization-service/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/web-virtualization-impl/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/web-processor-jsp/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/web-processor-php/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/mhus-pax-web-jetty/1.0.0-SNAPSHOT

install -s mvn:de.mhus.osgi.cherry/web-sample-application/1.0.0-SNAPSHOT
install -s mvn:de.mhus.osgi.cherry/web-commands/1.0.0-SNAPSHOT


bundle:watch mhu-lib-core
bundle:watch mhus-pax-web-jetty
bundle:watch web-virtualization-api
bundle:watch web-virtualization-service
bundle:watch web-virtualization-impl
bundle:watch web-processor-jsp
bundle:watch web-processor-php
bundle:watch web-sample-application

feature:repo-add cxf 2.7.9
feature:install cxf

vhosts/default.xml:

<hosts>
  <virtualhost>
    <host name="localhost:8181" />
    <application id="default" />

    <directories serverRoot="/tmp/localhost" />

  </virtualhost>
</hosts>
