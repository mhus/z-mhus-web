Additional Commands
=============

	jdbc:createdbpool: Create a DB pool to another jdbc datasource
	jdbc:createdbfailover: Create a DB Failover to other jdbc datasources
	jdbc:createdelegate: Create a DB delegation to another jdbc datasource
	jdbc:blueprint: Print all known blueprints or the content of an blueprint
	
	bundle:rawlist: List of all installed bundles in the OSGi framework.
	bundle:reinstall: !! EXPERIMENTAL !! Remove the bundle(s) from the local maven repository and request reinstalation in karaf.
	
	java:gc: Calls the java garbage collector
	java:mem: Prints the current memory usage
	
JDBC Datasources
-------------

The provided datasources are 'virtual' datasources delegating to one ore more other datasources. This allows the cascading usage and
transparent changing of datasources. The target datasource do not need to exist if you define the create the delegating datasource.

Use the -o (online) option to create the datasource direct in the osgi framework and do not create a blueprint file. Otherwise
the commands will only create a blueprint file in the deploy directory of karaf. This has the effect to have a persistent configuration.

The fail over datasource is not quiet finished. It will round robin the defined datasource targets if a failure occurred. It only
recognize a failure if the 'create connection' method call. Another odd situation (like a read only status - disk/datasegment full) will not
be recognized as a fail over yet.

Cascading Sample
-------------

My application bundle expects the data source 'targetdb' to work proper. I want to use a pool and have deploved a database cluster. 
This will cause the following dependecies:

	Database 1: jdbc/db1
	Database 2: jdbc/db2
	Pool 1: db1_pool -> jdbc/db1
	Pool 2: db2_pool -> jdbc/db2
	Failover:db_failover -> db1_pool,db2_pool
	Delegate: targetdb -> db_failover


