Server Configurations for TeamCity
==================================

This plugin helps you store and use server configurations with list of parameters (addresses, ports, credentials, etc).

Features:

 * Create server configuration templates within administration screen
 * Create server configuration by choosing template and filling template parameters
 * Attach server configuration to your projects and use all parameters in build configurations
 * Change server configurations in your projects to use another parameters without any reconfiguration
 * Server configurations are inherited from parent projects to all child projects
 * Attach different server configurations in your project to different branches


Configure template
-------------------------
![Template configuration](https://www.evernote.com/shard/s1/sh/b495670e-cb22-4174-9d2a-9cf79bbaa978/d32edfb3dc96849e9e2f73870c7fb656/res/5a928c8a-afb3-495a-8d24-666bb69edacb/skitch.png?resizeSmall&width=832)

Configure some server configurations
--------------------------------------------------
![Server configuration](https://www.evernote.com/shard/s1/sh/a8b92fae-3fb1-4ae6-912b-870e0e95394a/866500c1d6ae3c874a3ee91eaee67717/res/9d78e170-37c1-459c-a396-011f873a4777/skitch.png?resizeSmall&width=832)

Attach server configuration to project
-------------------------------------------------
![Project configuration](https://www.evernote.com/shard/s1/sh/a9dd537b-1d2f-4748-bcd1-7ed8eca4d34d/49768cf8709fc114a4ea6bd8597568a5/res/545b992f-7b4d-45f4-8356-cb32057f0671/skitch.png?resizeSmall&width=832)
![Project configuration](https://www.evernote.com/shard/s1/sh/3e0f88e9-7b3f-4d29-9c71-611788127976/08ff0710028278b21874a1c0908ecd3d/res/3fe566f8-d429-412d-a631-ec28e3ffebe8/skitch.png?resizeSmall&width=832)

Use parameters in build configurations
---------------------------------------------------
Parameters will be available in build configurations with selected prefix. Password values will be hidden from parameters list and build log.

![Parameters usage](https://www.evernote.com/shard/s1/sh/f5a411aa-54c1-404b-8768-008a58c2ca37/18134bad3eeb6c5cfc4f5e831c96f6b6/res/6cda0af6-159c-4795-99d2-300cb5c5e064/skitch.png?resizeSmall&width=832)