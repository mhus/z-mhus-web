# Content Concept

There are a lot of content located in a CMS. But content differs and different content types need to be handled different. In contrast to other modern CMS this one will not handle everything in the same way.

## Content Types

To classify content we can use different concepts. By presentation, usage, target group, topic ...

For a CMS it's important how to work with the content internal. Is it static or dynamic content, how are creating the content, do we need stages or workflows to approve content.

User created content is classified by
* editorial content: Needs approval, versioning, maybe two instances (editor and publish), it's more static then dynamic, describes the basic structure of the content in the CMS system; e.g. navigation structure, web pages, announcements
* user/visitor content: Created by end users, is more dynamic then static, do not need versioning but an archive/trash can, is growing with the visitors; e.g. page ratings, comments, bulletin boards, blogs, user wiki

Structure content:
* User data
* Authorization
* Host Configuration

There is one more important content type, it's the developed content:
* Developed binaries: Developed by software developer, needs to be deployed in a maintenance window, it's very static; e.g. Bundles for new widget or page types
* Developed Layout: Developed by content designers, needs to be deployed in a maintenance window, it's static content; e.g. Themes or widget/page layouts
* Server Configuration: Created by developers or maintainers, needs to be approved in a pre-productive system, will be deployed direct or in a maintenance window; e.g. database configuration, performance optimization, clustering

There are other content also:
* Content from external sources, e.g. other CMS system

A special word for editorial content: Specially images and attachments (files) can be classified in
* Page dependent: The files are specially provided for this page, e.g. icons for special topics (should be rarely used)
* Central design resources: Files provided for the hole system, e.g. Official images of the CEO or buildings, icons
* Central work resources: Files provided by others, e.g. Statistics, PDF files, Contract  or billing information


## Findings

* Editors do never create new widgets or redesign dialogs, this is in every case delegated to developers
* CMS systems do not provide versioning for the full structure, only for single assets. This will break the published structure in the case you redesign you content
* A CMS needs powerful tools to maintenance the content and allow batch work programming to manipulate it


## Rules

* Do not mix user content and developed and structure content, each of them will be created in different ways. Specially developed content should not be included in the user content itself (it's very 'enticing' because they could be stored in the same way)
* Every user content needs to have an identifier. It's the ID vs. Path linking problem (if you store the path a structural change will break the link, if you store the identifier a recreation of the content will break the link). But a ID can be accesses and linked fast (IDs are far away from evil, but it' not the answer for every question).
* Separate navigation structure and pages. Pages can be multiple for the same navigation node and can be recreated if needed.

## How to do it

This is the solution I found to bring all this together

1. Developed content is in every case stored in bundles, no theme template is stored in the user content structure
2. There are one data source for the navigation structure, it depends of nodes
3. A Navigation Node contains two types of sub nodes: Other navigation nodes and page nodes. There can be multiple navigation and page nodes. Page node names starts with an '_' (underscore). The pages are even connected if the structure is changed
4. I need a pluggable logic to find the correct page which is presenting the current navigation node
5. It's possible to connect other data sources at the page. In this way every page has it's own dynamic content sources. The are even connected if the structure of the navigation is changed.
6. Under the page node are more editorial content nodes are possible. E.g. widgets to show or images or attachments for this page
