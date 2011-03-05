# General Plugin for CraftBukkit #
## Version 3.0 [Bach]
### README
- - -
This is the first draft of this README. It's not done. Build instructions coming soon.

This version of the plugin is probably stable, but use at your own risk.

Commands:

* /who
* /time
* /tell
* /teleport
* /summon
* /spawn
* /online
* /give
* /getpos
* /compass

Available Permissions nodes:

* general.time
* general.time.set
* general.teleport
* general.teleport.other (to teleport one player to another where neither is you)
* general.teleport.coords (to teleport to specific locations)
* general.teleport.other.mass (to teleport many people at once)
* general.summon
* general.playerlist
* general.who
* general.give
* general.give.infinite
* general.give.group.<groupname> (to restrict certain items)
* general.spawn
* general.spawn.set
* general.spawn.other (to teleport someone other than yourself to the spawn point)
* general.getpos (this also controls access to /compass and /where)
* general.getpos.other
* general.tell
* general.admin (allows access to the /general administrative command)