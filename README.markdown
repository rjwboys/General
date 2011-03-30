# General Plugin for CraftBukkit #
## Version 3.3.1 [Dvorak]
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
* /mobspawn
* /general reload|help|motd|item

Available Permissions nodes:

* general.basic (blanket permission covering general.time, general.playerlist, general.who, general.away, general.tell, general.getpos)
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
* general.give.mass (to use the /items command)
* general.give.group.<groupname> (to restrict certain items)
* general.give.any (to allow access to items not in a group if item whitelist mode is enabled) 
* general.spawn
* general.spawn.set
* general.spawn.other (to teleport someone other than yourself to the spawn point)
* general.getpos (this also controls access to /compass and /where)
* general.getpos.other
* general.tell
* general.away
* general.clear
* general.clear.other
* general.take
* general.take.other
* general.heal
* general.mobspawn
* general.kit.<kitname>
* general.kit-now (to bypass delays on kits)
* general.admin (allows access to /general administrative commands)