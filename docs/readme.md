# Cherry-Web

Cherry-Web is a framework to create web applications and portal applications. It provides a powerful
environment where the application will be emedded as a plugin.

## Overview

Creating a portal application is not an easy thing. A portal (in contrast to a simple web presence) needs to challenge ...

* Access control
* Versioning
* Link management
* Editor and publisher instances
* User created content
* Multi language and multi content support
* Structure stability
* Virtual host encapsulation
* Changeability

### Access control

There is no need to explain the need and role of access control. It consists of authentication 
(user management) and authorization (group and ACL management).

In difference to a web presence with only a public and editor access the portal application needs to
control functionality until asset and action level. This is the biggest performance killer.

### Versioning

Versioning means to have a history of something. Most time versioning means to have more then one of
an asset. One of them are the current, maybe another one is the active visible. This excludes the
structure of the content. Changes in the structure are not traceable or recoverable.

This means the main content structure not the user feedback data.

### Link management

A big challenge is to keep working links after structure changes. Links should follow the right path even after changing
a nodes name or moving a sub tree to another location. A very special case is the recreation of a navigation node. In this case the link to the node id will break too.

### Editor and publisher instance

In serious environments the presence is split in editor (WIP) and publisher (Active) server instances. Sometimes a staging should be deployed too. It depends on the workflow of the content. A common process is WIP -> Staging -> Approved -> Active, following this process editing and published instance are different environments. For small projects this could mean one server with two different virtual hosts. For enterprise projects a zoo of editor and publisher nodes will be present.

### User created content

Most times a rare discussed topic are user created content. This includes user feedback like page ranking, user comments, bulletin boards, wiki ...

Every content created by the users can't be part of the web content itself because it dose not follow the WIP -> ... -> Active content workflow. For user content you need a completely different data store. But the data needs to be woven in the page content no allow AJAX calls working at the current node and to avoid integrity problems moving the node structure.

User content will not be approved usually. And it's simply shared over editing and publishing instances.

### Multi language and multi content support

The most even small web presences and portals need to have multiple content for the sam node structure. Requirements are multiple language support or different pages for guest and logged in users.

This means a navigation node refers depending of the user request to different content. Again needs to be save for changing the node structure.

### Structure stability

One of the main underlying problems is the stability of the structure. What happens if the navigation node structure changes. This case happens very often and can produce a lot of extra work and hidden trouble for the editors and unhappy users.

### Changeability

Every discussed topic should be done in another way too. Pluggable components offer the possibility to change
the behavior for everything.
