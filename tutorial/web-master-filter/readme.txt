feature:install http
feature:install http-whiteboard
feature:install scr

install -s mvn:de.mhus.osgi.tutorial/web-master-filter/1.0.4-SNAPSHOT

bundle:watch mhus-pax-web-jetty
bundle:watch web-master-filter

feature:repo-add cxf 2.7.9
feature:install cxf