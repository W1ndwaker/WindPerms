/*
 * Copyright (c) 2012-2013 ${developer}, <http://windwaker.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.windwaker.permissions.io.yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.windwaker.permissions.WindPerms;
import me.windwaker.permissions.io.GroupManager;
import me.windwaker.permissions.io.UserManager;
import me.windwaker.permissions.permissible.Group;
import me.windwaker.permissions.permissible.User;
import org.apache.commons.io.FileUtils;

import org.spout.api.Spout;
import org.spout.api.data.DataValue;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

import static org.spout.api.Spout.*;

/**
 * Flat-file implementation of UserManager done in YAML.
 * @author Windwaker
 */
public class YamlUserManager implements UserManager {
	private final File file;
	private final YamlConfiguration data;
	private final Set<User> users = new HashSet<User>();
	private final WindPerms plugin;
	private final GroupManager groupManager;

	public YamlUserManager(WindPerms plugin) {
		this.plugin = plugin;
		groupManager = plugin.getGroupManager();
		file = new File(plugin.getDataFolder(), "users.yml");
		data = new YamlConfiguration(file);
	}

	@Override
	public void load() {
		try {

			debug("Loading user data...");
			data.load();
			data.setPathSeparator("/");
			if (!data.getNode("users").isAttached()) {
				debug("\tNo data found, adding defaults.");
				addDefaults();
			}

			Set<String> names = data.getNode("users").getKeys(false);
			if (!names.isEmpty()) {
				plugin.getLogger().info("Loading user data...");
			}

			// Load users
			for (String name : names) {
				debug("\tLoading user: " + name);
				loadUser(name);
			}

			if (!names.isEmpty()) {
				plugin.getLogger().info("User data loaded. " + users.size() + " unique users loaded!");
			}
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to load user data: " + e.getMessage());
		}
	}

	@Override
	public void save() {
		for (User user : users) {
			saveUser(user);
		}
	}

	private void addDefaults() {
		try {
			FileUtils.copyInputStreamToFile(Spout.getFileSystem().getResourceStream("file://WindPerms/users.yml"), file);
			data.load();
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to add defaults: " + e.getMessage());
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to copy defaults to configuration file: " + e.getMessage());
		}
	}

	private void loadPermissions(User user) {
		debug("Loading permission nodes for user: " + user.getName());
		String path = "users/" + user.getName();
		Set<String> nodes = data.getNode(path + "/permissions").getKeys(false);
		for (String node : nodes) {
			boolean value = data.getNode(path + "/permissions/" + node).getBoolean();
			debug("\tNode: " + node);
			debug("\tValue: " + value);
			user.setPermission(node, value);
		}
	}

	private void loadData(User user) {
		debug("Loading metadata for user: " + user.getName());
		String path = "users/" + user.getName();
		Set<String> nodes = data.getNode(path + "/metadata").getKeys(false);
		for (String node : nodes) {
			Object value = data.getNode(path + "/metadata/" + node).getValue();
			debug("\tKey: " + node);
			debug("\tValue: " + value);
			user.setMetadata(node, value);
		}
	}

	@Override
	public void saveUser(User user) {
		try {
			String path = "users/" + user.getName();
			savePermissions(user);
			saveData(user);
			String groupName = user.getGroup() != null ? user.getGroup().getName() : "";
			data.getNode(path + "/group").setValue(groupName);
			data.save();
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to save user: " + user.getName() + ": " + e.getMessage());
		}
	}

	@Override
	public void loadUser(String user) {
		// Create new user
		String path = "users/" + user;
		User u = new User(plugin, user);
		// Turn off auto-saving for the user while loading - data will not save to disk.
		u.setAutoSave(false);
		// Load permissions and data
		loadPermissions(u);
		loadData(u);
		// Load group
		Group group = groupManager.getGroup(data.getNode(path + "/group").getString(groupManager.getDefaultGroup().getName()));
		u.setGroup(group);
		group.addUser(u);
		// Turn auto-save back on and add user.
		u.setAutoSave(true);
		debug("\tLoaded " + u.getName());
		users.add(u);
	}

	private void savePermissions(User user) {
		debug("Saving permission nodes for user: " + user.getName());
		String path = "users/" + user.getName();
		Set<Map.Entry<String, Boolean>> perms = user.getPermissions().entrySet();
		for (Map.Entry<String, Boolean> perm : perms) {
			String node = perm.getKey();
			boolean value = perm.getValue();
			debug("\tNode: " + node);
			debug("\tValue: " + value);
			data.getNode(path + "/permissions/" + node).setValue(value);
		}
	}

	private void saveData(User user) {
		debug("Saving metadata for user: " + user.getName());
		String path = "users/" + user.getName();
		Set<Map.Entry<String, DataValue>> values = user.getMetadataMap().entrySet();
		for (Map.Entry<String, DataValue> value : values) {
			String key = value.getKey();
			DataValue v = value.getValue();
			debug("\tKey: " + key);
			debug("\tValue: " + v);
			data.getNode(path + "/metadata/" + key).setValue(v);
		}
	}

	@Override
	public void addUser(String username) {
		try {
			String path = "users/" + username;
			data.getNode(path + "/group").setValue(groupManager.getDefaultGroup().getName());
			data.save();
			loadUser(username);
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to add user " + username + ": " + e.getMessage());
		}
	}

	@Override
	public void removeUser(String username) {
		try {
			User user = getUser(username);
			if (user == null) {
				return;
			}
			users.remove(user);
			data.getNode("users/" + username).setValue(null);
			data.save();
		} catch (ConfigurationException e) {
			plugin.getLogger().severe("Failed to remove user " + username + ": " + e.getMessage());
		}
	}

	@Override
	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public Set<User> getUsers() {
		return users;
	}

	@Override
	public void clear() {
		users.clear();
	}
}
