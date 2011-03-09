# General Plugin for CraftBukkit #
## Version 3.1.2 [Wagner]
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
* /clear
* /take
* /heal
* /mobspawn (not implemented yet)
* /general reload|help|motd

Available Permissions nodes:

* general.time
* general.time.set
* general.teleport
* general.teleport.other (to teleport one player to another where neither is you)
* general.teleport.coords (to teleport to specific locations)
* general.teleport.other.mass (to teleport many people at once)
* general.playerlist
* general.who
* general.give
* general.give.infinite
* general.give.group.<groupname> (to restrict certain items)
* general.give.group.other (to allow access to items not in a group) 
* general.spawn
* general.spawn.set
* general.spawn.other (to teleport someone other than yourself to the spawn point)
* general.getpos (this also controls access to /compass and /where)
* general.getpos.other
* general.tell
* general.clear
* general.clear.other
* general.take
* general.take.other
* general.heal
* general.mspawn
* general.admin (allows access to /general administrative commands)