# General Plugin for CraftBukkit #
## Version 4.1 [Dowland]
### README
- - -

To build General, you need to first download the dependencies. They can be automatically downloaded by typing
```ant update
```

This places the jars in "../lib" relative to the project's root directory. If you prefer to
place the dependencies elsewhere, use the following command:
```ant -Dlib=path/to/dir update
```

Once you have done this, you can build with ant. For a full build, type
```ant build
```

The command-helper.txt file (in resources/) contains suggested aliases to add to CommandHelper's config.txt
if you are using CommandHelper. This includes things like /home, /spawn, /sethome, /reply, and /summon.

For reference, in case the update task fails for some reason (such as an out-of-date URL), the following
compile-time dependencies are required:

* Help.jar
* CratIRC.jar (version 3 or greater)
* BookWorm.jar
* craftbukkit.jar (not bukkit.jar)

The following dependencies are economy plugins required to build AllPay:

* EconXP.jar
* BOSEconomy.jar
* Essentials.jar
* iConomy6.jar
* iConomy5.jar
* iConomy4.jar
* RealShop.jar
* MultiCurrency.jar