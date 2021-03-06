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
package me.windwaker.permissions.permissible;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.windwaker.permissions.WindPerms;
import me.windwaker.permissions.io.GroupManager;

import org.spout.api.data.DataValue;

/**
 * Represents a group of users.
 * @author Windwaker
 */
public class Group extends Permissible {
	private final GroupManager groupManager;
	private final Map<Group, Boolean> indirectInheritedGroups = new HashMap<Group, Boolean>();
	private final Map<Group, Boolean> inheritedGroups = new HashMap<Group, Boolean>();
	private final Set<User> users = new HashSet<User>();

	/**
	 * Constructs a new Group with the specified name.
	 * @param name
	 */
	public Group(WindPerms plugin, String name) {
		super(name);
		groupManager = plugin.getGroupManager();
	}

	/**
	 * Gets the users in the group
	 * @return users in group
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * Adds a user
	 * @param user to remove
	 * @return true if group already contained user
	 */
	public boolean addUser(User user) {
		return users.add(user);
	}

	/**
	 * Removes a user from the group
	 * @param user to remove
	 * @return true if group already contained user
	 */
	public boolean removeUser(User user) {
		return users.remove(user);
	}

	/**
	 * Gets groups that were not inherited indirectly
	 * @return map of groups
	 */
	public Map<Group, Boolean> getIndirectInheritedGroups() {
		return indirectInheritedGroups;
	}

	/**
	 * Gets any inherited groups.
	 * @return inherited groups.
	 */
	public Map<Group, Boolean> getInheritedGroups() {
		return inheritedGroups;
	}

	/**
	 * Adds an inherited group.
	 * @param group to inherit
	 */
	public void setInheritedGroup(Group group, boolean inherit) {
		setInheritedGroup(group, inherit, true);
	}

	private void setInheritedGroup(Group group, boolean inherit, boolean direct) {
		if (direct) {
			// direct groups save to disk
			inheritedGroups.put(group, inherit);
		} else {
			// inherited do not save to disk
			indirectInheritedGroups.put(group, inherit);
		}

		if (inherit) {
			inheritGroups(group);
		}

		// load data from inherited groups
		inheritData();
		if (autoSave) save();
	}

	public void reloadInheritance() {
		for (Map.Entry<Group, Boolean> entry : indirectInheritedGroups.entrySet()) {
			if (entry.getValue()) {
				inheritGroups(entry.getKey());
			}
		}
		for (Map.Entry<Group, Boolean> entry : inheritedGroups.entrySet()) {
			if (entry.getValue()) {
				inheritGroups(entry.getKey());
			}
		}
	}

	private void inheritGroups(Group group) {
		// no circle inheritance here
		if (group.isAssignableFrom(this))
			throw new IllegalStateException("Group " + group.getName() + " already inherits " + name + ". Two groups may not inherit each other.");
		// inherit the groups inherited groups
		for (Map.Entry<Group, Boolean> entry : group.getIndirectInheritedGroups().entrySet())
			setInheritedGroup(entry.getKey(), entry.getValue(), false);
		for (Map.Entry<Group, Boolean> entry : group.getInheritedGroups().entrySet())
			setInheritedGroup(entry.getKey(), entry.getValue(), false);
	}

	private void inheritData() {
		inheritAll(indirectInheritedGroups);
		inheritAll(inheritedGroups);
	}

	private void inheritAll(Map<Group, Boolean> groupMap) {
		for (Map.Entry<Group, Boolean> entry : groupMap.entrySet()) {
			if (entry.getValue()) {
				Group group = entry.getKey();
				// inherit nodes
				for (Map.Entry<String, Boolean> node : group.getInheritedPermissions().entrySet())
					inheritedNodes.put(node.getKey(), node.getValue());
				for (Map.Entry<String, Boolean> node : group.getPermissions().entrySet())
					inheritedNodes.put(node.getKey(), node.getValue());
				// inherit data
				for (Map.Entry<String, DataValue> data : group.getInheritedMetadataMap().entrySet())
					inheritedMetadata.put(data.getKey(), data.getValue());
				for (Map.Entry<String, DataValue> data : group.getMetadataMap().entrySet())
					inheritedMetadata.put(data.getKey(), data.getValue());
			}
		}
	}

	/**
	 * Whether or not this group inherits the given group.
	 * @param group to check
	 * @return true if inherits
	 */
	public boolean isAssignableFrom(Group group) {
		if (inheritedGroups.containsKey(group)) {
			return inheritedGroups.get(group);
		} else if (indirectInheritedGroups.containsKey(group)) {
			return indirectInheritedGroups.get(group);
		}
		return false;
	}

	@Override
	public void setMetadata(String node, DataValue value) {
		super.setMetadata(node, value);
		groupManager.reloadInheritance();
	}

	@Override
	public void setPermission(String node, boolean state) {
		super.setPermission(node, state);
		groupManager.reloadInheritance();
	}

	@Override
	public void save() {
		groupManager.saveGroup(this);
	}
}
