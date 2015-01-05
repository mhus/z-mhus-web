feature:install http
feature:install http-whiteboard
feature:install scr

install -s mvn:de.mhus.osgi.web/web-access-log/1.0.4-SNAPSHOT

bundle:watch mhus-pax-web-jetty
bundle:watch web-access-log

feature:repo-add cxf 2.7.9
feature:install cxf