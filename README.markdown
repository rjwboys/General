# General Plugin for CraftBukkit #
## Version 4.0 [Vivaldi]
### README
- - -

To build General, you need to obtain the following dependencies and put them in a ../lib folder relative
to the root directory of the repository:

* Help.jar
* WorldEdit.jar
* BookWorm.jar
* CraftIRC.jar (version 3 or greater required)

The other dependencies can be automatically downloaded by typing
```ant update
```

Once you have done this, you can build with ant. For a full build, type
```ant build
```

The command-helper.txt file (in resources/) contains suggested aliases to add to CommandHelper's config.txt
if you are using CommandHelper. This includes things like /home, /spawn, /sethome, /reply, and /summon.