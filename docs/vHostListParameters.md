# Virtual Host List Configuration Parameters

A virtual host object offers the ability to configure behavor of the presentation with ordered lists of strings. Every list is accessible by name and can be manipulated in runtime. There is no need to define a configuration list the list consumer need to define a fallback mechanism if the list is not defined.

## servlet_host_allowed

A list of regular expressions to define the allowed accessors (client host name). If the list is not defined there is no restriction.

## servlet_host_allowed_< servlet name >

The same behavior then servlet_host_allowed but specified for the given area. In this way the control editor can be hidden for the hole internet an is accessible only for a define client.

* 'servlet_host_allowed_content': The content presentation (Website)
* 'servlet_host_allowed_api': The API callback and dynamic extension (e.g. login, logout) 
* 'servlet_host_allowed_control': The Content Editor area

## List Control

List controls lists are used to control listings of something. For example filter and order viewable plugins.

The list contains the names of the viewable plugins. With the following syntax

* the 'name' Shows the component with this name
* Ignore a 'name', use (!) befor the name
* Use the star (*) to add all other in alphapetic order, this only makes sense at the end of the list

*Example:*

To edit a widget there are the following plugins are present: acl,actions,compose,debug,edit,parameters,versions

The following list (comma seperated) is defined: edit,compose,acl,!debug,*

This will result in: edit,compose,acl,actions,parameters,versions

### de.mhus.cherry.editor.impl.pages.PageControlControl:de.mhus.cherry.portal.api.control.EditorFactory

### de.mhus.cherry.editor.impl.editor.EditorSpace:de.mhus.cherry.portal.api.control.EditorControlFactory

### de.mhus.cherry.editor.impl.pages.PagesSpace:de.mhus.cherry.portal.api.control.PageControlFactory
