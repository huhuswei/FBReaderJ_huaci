/*
 * Copyright (C) 2010-2015 FBReader.ORG Limited <contact@fbreader.org>
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

package org.geometerplus.android.fbreader;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.util.Log;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.book.Book;

import org.geometerplus.android.fbreader.api.PluginApi;
import org.geometerplus.android.fbreader.api.FBReaderIntents;

class RunPluginAction extends FBAndroidAction {
	private final Uri myUri;
	private final Book myBook;

	RunPluginAction(FBReader baseActivity, FBReaderApp fbreader, Uri uri) {
		super(baseActivity, fbreader);
		myUri = uri;
		myBook = null;
	}

	RunPluginAction(FBReader baseActivity, FBReaderApp fbreader, Uri uri, Book book) {
		super(baseActivity, fbreader);
		myUri = uri;
		myBook = book;
	}

	@Override
	protected void run(Object ... params) {
		if (myUri == null) {
			return;
		}
		BaseActivity.hideBars();
		try {
			Intent intent = new Intent(PluginApi.ACTION_RUN, myUri);
			// 传递书籍信息给插件，以便插件显示和保存进度
			if (myBook != null) {
				intent.putExtra(FBReaderIntents.Key.PLUGIN_BOOK_TITLE, myBook.getTitle());
				intent.putExtra(FBReaderIntents.Key.PLUGIN_BOOK_FILE, myBook.getPath());
				intent.putExtra(FBReaderIntents.Key.PLUGIN_BOOK, myBook.getId());
				Log.d("RunPluginAction", "Sending book info to plugin: " + myBook.getTitle() + ", id=" + myBook.getId());
			}
			// 不使用startActivityForResult，因为大多数插件不会返回结果
			// 书籍已经在FBReader中提前添加到recent列表了
			OrientationUtil.startActivity(BaseActivity, intent);
		} catch (ActivityNotFoundException e) {
			Log.e("RunPluginAction", "Plugin not found for URI: " + myUri);
		}
	}
}
