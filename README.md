## WindPerms

### NOTE: WindPerms is no longer being actively developed / maintained.

### Download
![Status]

[Latest Build] | [Recommended Build]

### About
WindPerms is a plugin for the Spout platform that provides permissible actions between player's on a Spout server.

Copyright (c) 2012 Walker Crouse <http://windwaker.me/>

### Licensing
WindPerms is licensed under the MIT license viewable in the [LICENSE.txt] file.
The latest source can be seen on [GitHub].
Download the [Latest Build] or the latest [Recommended Build] to get started.

### Installation
Installation of WindPerms is simple, just place the WindPerms JAR file in the 'plugins' directory of your Spout server. All configuration files will be automatically generated with some defaults that you can edit as you please.

### Operation
A guide to operating WindPerms can be found [here][Wiki].

### Compiling
WindPerms uses Maven for handling it's dependencies.
* [Install Maven][Maven].
* Run `mvn` in the root directory of the project.

### Contact
I can be reached by the following methods.
* Email me at walkercrouse@hotmail.com
* Private message me on http://spout.org
* Private message me on #spout on irc://irc.esper.net:55555

### Caveats
All bugs should be reported [here][Issues].

### Changelog
The projects history can be viewed in it's entirety [here][Commits].

### Donate
I put a lot of time and care into my projects to try and bring the community the best product I can, and I do it all for free! Donations are most definitely appreciated and I will be eternally grateful if you can spare a few dollars.
If you do want to donate you can do so [here][Donate]. Thanks!

### For Developers
**NOTE:** It is absolutely unnecessary for your plugin to use WindPerms as a dependency. Think of WindPerms as the engine of SpoutAPI's permissions system. **The only library required is [SpoutAPI]**

**Checking if someone/something has permission:**
```java
if (subject.hasPermission("foo.bar") {
    // subject has the permission node for 'foo.bar' in WindPerms
}
```

**Checking if someone/something is in a specified group:**
```java
if (subject.isInGroup("admin") {
    // the subject is an 'admin' in WindPerms
}
```

**Fetching data from WindChat's metadata table:**
You will note that in a definition of a WindPerm's user or group, each subject is given it's own data table. In the format of:
```yaml
metadata:
  build: true
```
To fetch data from this table we do something similar to the following...
```java
if (subject.hasData("build") && subject.getData("build").getBoolean()) {
    // the subject is allowed to build and has 'build: true' in their data table
}
```

Users inherit their data from their groups but is overridden by conflicting data values in their table. For instance, if the group 'admin' had the value `build: true` any users in that group would now also have `build: true`. However, if that user specifically overrides that value in their user data, WindPerms will use the user's version. The same principle goes for any inherited data such as permission nodes. Also note that none of these methods have setters, this is because the user is meant to set this value in WindPerms and not the developer; if you find yourself needing to set data, you are doing something wrong.

[Status]: http://build.spout.org/job/WindPerms/badge/icon/
[GitHub Icon]: http://forums.spout.org/attachments/github-png.1022/
[Jenkins Icon]: http://forums.spout.org/attachments/jenkins-png.1023/
[Latest Build]: http://build.spout.org/job/WindPerms/lastSuccessfulBuild/
[Recommended Build]: http://build.spout.org/job/WindPerms/Recommended/
[LICENSE.txt]: http://github.com/W1ndwaker/WindPerms/LICENSE.txt/
[GitHub]: http://github.com/W1ndwaker/WindPerms/
[Donate]: https://www.paypal.com/us/cgi-bin/webscr?cmd=_flow&SESSION=4TN0_fr0Gi-575SxsOoYeRENqYWhhKsx4GwKTY1SrhwQTXOFbAeTG1uQ_PG&dispatch=5885d80a13c0db1f8e263663d3faee8db02a037e263542f58098410815cf7df7
[SpoutAPI]: https://github.com/SpoutDev/SpoutAPI
[Issues]: https://github.com/W1ndwaker/WindPerms/issues
[Commits]: https://github.com/W1ndwaker/WindPerms/commits/master
[Wiki]: https://github.com/W1ndwaker/WindPerms/wiki
[Maven]: http://maven.apache.org/download.cgi
