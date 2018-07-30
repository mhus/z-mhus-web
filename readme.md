
feature:repo-add mvn:de.mhus.osgi/mhu-karaf-feature/1.4.3-SNAPSHOT/xml/features
feature:install mhu-osgi-base
feature:install http

install -s mvn:de.mhus.cherry/web-api/1.0.0-SNAPSHOT
install -s mvn:de.mhus.cherry/web-core/1.0.0-SNAPSHOT
install -s mvn:de.mhus.cherry/web-util/1.0.0-SNAPSHOT

install -s mvn:de.mhus.cherry/web-jetty/1.0.0-SNAPSHOT




--------------------------------------------
Use javaxt http server
--------------------------------------------

1) Install javaxt port and javaxt connector

install -s mvn:de.mhus.ports/ports-javaxt/1.3.6-SNAPSHOT
install -s mvn:de.mhus.cherry/web-javaxt/1.0.0-SNAPSHOT

2) Modify configuration (optional)

mhus-config.xml default values:

<de.mhus.cherry.web.javaxt.JavaXtServer
	ssl="false"
	port="9080"
	threads="50"
	keystore="etc/keystore.jks"
	keystorePassword="password"
	truststore="etc/truststore.jks"
	truststorePassword="password"
/>

3) Enable SSL (Optional)

To enable ssl the minimum configuration must be

<de.mhus.cherry.web.javaxt.JavaXtServer
	ssl="true"
/>

create the key files using the keytool, by default they are located in the karaf/etc directory:

keytool -genkeypair -alias certificatekey -keyalg RSA -keysize 2048 -keystore etc/keystore.jks
keytool -export -alias certificatekey -keystore etc/keystore.jks -rfc -file etc/selfsignedcert.cer
keytool -import -alias certificatekey -file etc/selfsignedcert.cer -keystore etc/truststore.jks


