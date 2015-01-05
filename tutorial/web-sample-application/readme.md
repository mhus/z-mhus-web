

install -s mvn:de.mhus.osgi.tutorial/web-sample-application/1.0.4-SNAPSHOT


bundle:watch web-sample-application

vhosts/default.xml:

<hosts>
  <virtualhost>
    <host name="localhost:8181" />
    <application id="sample" />

    <directories serverRoot="/tmp/localhost" />

  </virtualhost>
</hosts>
