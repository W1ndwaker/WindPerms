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
package me.windwaker.permissions.io.sql;

import java.util.Set;

import me.windwaker.permissions.io.GroupManager;
import me.windwaker.permissions.permissible.Group;

/*
 *            ------------------------------
 * Table:     |     permissions_groups      |
 *            ------------------------------
 * Fields:    |  name  |  def  |  per_world |
 *            ------------------------------
 * Data Type: |  text  |char(1)|   char(1)  |
 *            ------------------------------
 */
public class SqlGroupManager implements GroupManager {
	@Override
	public void load() {
	}

	@Override
	public void save() {
	}

	@Override
	public void addGroup(String name) {
	}

	@Override
	public void removeGroup(String name) {
	}

	@Override
	public void saveGroup(Group group) {
	}

	@Override
	public void loadGroup(String group) {
	}

	@Override
	public Group getDefaultGroup() {
		return null;
	}

	@Override
	public void setDefaultGroup(Group group) {
	}

	@Override
	public Group getGroup(String name) {
		return null;
	}

	@Override
	public Set<Group> getGroups() {
		return null;
	}

	@Override
	public void reloadInheritance() {
	}

	@Override
	public void clear() {
	}
}
