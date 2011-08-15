# General Plugin for CraftBukkit #
## Version 3.5 [Schoenberg]
### README
- - -

To build General, you need to obtain the following dependencies and put them in a ../lib folder relative
to the root directory of the repository:

* BOSEconomy.jar
* craftbukkit.jar
* Help.jar
* iConomy.jar (for iConomy 5)
* iConomy4.jar

Once you have done this, you can build with ant. For a full build, type
```ant dist
```

The command-helper.txt file (in resources/) contains suggested aliases to add to CommandHelper's config.txt
if you are using CommandHelper. This includes things like /home, /spawn, /sethome, /reply, and /summon.