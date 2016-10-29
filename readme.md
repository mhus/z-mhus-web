

feature:repo-add activemq 5.12.1
feature:repo-add cxf 3.1.5
feature:repo-add mvn:de.mhus.osgi/mhu-karaf-feature/1.3.1-SNAPSHOT/xml/features
feature:repo-add mvn:de.mhus.cherry/cherry-karaf-feature/1.0.0-SNAPSHOT/xml/features

feature:install cherry-demo


feature:install cherry-portal



bundle:persistentwatch add cherry-portal-api
bundle:persistentwatch add cherry-portal-impl
bundle:persistentwatch add cherry-editor-theme
bundle:persistentwatch add cherry-editor-impl

bundle:persistentwatch add cherry-content-base
bundle:persistentwatch add cherry-portal-demo


---

bundle:install -s mvn:de.mhus.cherry/cherry-renderer-jsp/1.0.0-SNAPSHOT
bundle:persistentwatch add cherry-renderer-jsp

---

To install demo:

- go to karaf root

mkdir cherry/demo
cp -r {src}/examples/test/webcontent cherry/demo/
cp {src}/examples/test/cherry-default-host.xml deploy/

---

Apache Configuration:

ProxyPass /forum !
ProxyPass / http://localhost:8080/tomcat-webapp/
ProxyPassReverse / http://localhost:8080/tomcat-webapp/
Alias /forum /var/www/forum



Images CC0:

https://pixabay.com/en/cherry-fruits-cherries-fruit-food-35288/

https://pixabay.com/en/japanese-cherry-trees-flowers-spring-324175/
https://pixabay.com/en/cherry-blossom-japanese-cherry-smell-1318258/

