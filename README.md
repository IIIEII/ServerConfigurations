Server Configurations for TeamCity
==================================

This plugin helps you store and use server configurations with list of parameters (addresses, ports, credentials, etc).

Features:

 * Create server configuration templates within administration screen
 * Create server configuration by choosing template and filling template parameters
 * Attach server configuration to your projects and use all parameters in build configurations
 * Change server configurations in your projects to use another parameters without any reconfiguration
 * Server configurations are inherited from parent projects to all child projects

Configure template
-------------------------
![Template configuration](https://www.evernote.com/shard/s1/sh/b495670e-cb22-4174-9d2a-9cf79bbaa978/d32edfb3dc96849e9e2f73870c7fb656/res/5a928c8a-afb3-495a-8d24-666bb69edacb/skitch.png?resizeSmall&width=832)

Configure some server configurations
--------------------------------------------------
![Server configuration](https://www.evernote.com/shard/s1/sh/a8b92fae-3fb1-4ae6-912b-870e0e95394a/866500c1d6ae3c874a3ee91eaee67717/res/9d78e170-37c1-459c-a396-011f873a4777/skitch.png?resizeSmall&width=832)

Attach server configuration to project
-------------------------------------------------
![Project configuration](https://www.evernote.com/shard/s1/sh/29c09c8c-5eb1-4939-bff0-f8e431428484/b371fb599eda53c3eb4db25460c71f6a/res/55c3cac9-cea9-4289-a317-90ab73405f63/skitch.png?resizeSmall&width=832)

Use parameters in build configurations
---------------------------------------------------
Parameters will be available in build configurations with selected prefix. Password values will be hidden from parameters list and build log.

![Parameters usage](https://www.evernote.com/shard/s1/sh/f5a411aa-54c1-404b-8768-008a58c2ca37/18134bad3eeb6c5cfc4f5e831c96f6b6/res/6cda0af6-159c-4795-99d2-300cb5c5e064/skitch.png?resizeSmall&width=832)