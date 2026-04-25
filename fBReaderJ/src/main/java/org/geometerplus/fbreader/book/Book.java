/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.book;

public final class Book extends AbstractBook {
	private final String myPath;

	public Book(long id, String path, String title, String encoding, String language) {
		super(id, title, encoding, language);
		if (path == null) {
			throw new IllegalArgumentException("Creating book with no file");
		}
		myPath = path;
	}

	/**
	 * 从外部插件书籍信息创建Book对象（用于recent列表显示）
	 * @param title 书籍标题
	 * @param filePath 文件路径（可选）
	 * @param hash 书籍Hash（可选，用于识别）
	 */
	public static Book fromExternalBook(String title, String filePath, String hash) {
		if (title == null) {
			return null;
		}
		// 使用Hash或文件路径作为唯一标识
		String path = filePath != null ? filePath : (hash != null ? hash : "external:" + title);
		return new Book(0, path, title, null, null);
	}

	@Override
	public String getPath() {
		return myPath;
	}

	@Override
	public int hashCode() {
		return myPath.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Book)) {
			return false;
		}
		return myPath.equals(((Book)o).myPath);
	}
}
