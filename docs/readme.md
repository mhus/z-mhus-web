# Cherry-Web

Cherry-Web is a framework to create web applications and portal applications. It provides a powerful
environment where the application will be emedded as a plugin.

## Overview

Creating a portal application is not an easy thing. A portal (in contras to a simple web presence) needs to challenge ...

* Access control
* Versioning
* Link management
* Editor and published instances
* User feedback data flow
* Multi language support
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

A big challange is to save link health ...

### Changeability

Every discussed topic should be done in another way too. Plugable components offer the possibility to change
the behavior for everything.
