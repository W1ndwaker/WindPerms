/**
 * The Permissions project.
 * Copyright (C) 2012 Walker Crouse
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.windwaker.permissions;

import net.windwaker.permissions.api.GroupManager;
import net.windwaker.permissions.api.Permissions;
import net.windwaker.permissions.api.UserManager;
import net.windwaker.permissions.api.permissible.Permissible;
import net.windwaker.permissions.api.permissible.User;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;

/**
 * @author Windwaker
 */
public class PermissionsHandler implements Listener {

	private final UserManager userManager = Permissions.getUserManager();
	private final GroupManager groupManager = Permissions.getGroupManager();
	private final Logger logger = Logger.getInstance();
	
	@EventHandler(order = Order.EARLIEST)
	public void onGroupsGet(PermissionGetGroupsEvent event) {
		User user = userManager.getUser(event.getSubject().getName());
		if (user == null) {
			return;
		}
		
		String[] group = {user.getGroup().getName()};
		if (group != null) {
			event.setGroups(group);
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onGroupCheck(PermissionGroupEvent event) {
		User user = userManager.getUser(event.getSubject().getName());
		if (user == null) {
			return;
		}
		
		String name = event.getGroup();
		if (user.getGroup().getName().equalsIgnoreCase(name)) {
			event.setResult(true);
		} else {
			event.setResult(false);
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onNodeCheck(PermissionNodeEvent event) {
		String name = event.getSubject().getName();
		Permissible subject = userManager.getUser(name);
		if (subject == null) {
			subject = groupManager.getGroup(name);
		}
		
		if (subject == null) {
			return;
		}
		
		if (subject.hasPermission(event.getNode())) {
			event.setResult(Result.ALLOW);
		} else {
			event.setResult(Result.DENY);
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getName();
		User user = userManager.getUser(playerName);
		if (user != null) {
			logger.info(playerName + " returned, found Permissions profile.");
			return;
		}

		logger.info(playerName + " does not have a Permissions profile, creating...");
		userManager.addUser(playerName);

	}

	/*
	@EventHandler(order = Order.EARLIEST)
	public void onDataCheck(RetrieveDataEvent event) {
		String name = event.getSubject().getName();
		Permissible subject = userManager.getUser(name);
		if (subject == null) {
			subject = groupManager.getGroup(name);
		}
		
		if (subject == null) {
			return;
		}
		
		String node = event.getNode();

		if (subject.getMetadata(node) != null) {
			event.setResult(subject.getMetadata(node));
		}
	}*/
}
