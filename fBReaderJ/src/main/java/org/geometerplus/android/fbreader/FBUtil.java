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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;

import org.geometerplus.zlibrary.core.filesystem.ZLPhysicalFile;
import org.geometerplus.zlibrary.core.filetypes.FileTypeCollection;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookUtil;

import java.io.File;

public abstract class FBUtil {
	public static void shareBook(Activity activity, Book book) {
		try {
			final ZLPhysicalFile file = BookUtil.fileByBook(book).getPhysicalFile();
			if (file == null) {
				// That should be impossible
				return;
			}
			final CharSequence sharedFrom =
				Html.fromHtml(ZLResource.resource("sharing").getResource("sharedFrom").getValue());

			Uri contentUri;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				// Android 7.0+ 使用 FileProvider
				contentUri = android.support.v4.content.FileProvider.getUriForFile(
					activity,
					activity.getPackageName() + ".fileprovider",
					file.javaFile()
				);
			} else {
				contentUri = Uri.fromFile(file.javaFile());
			}

			Intent shareIntent = new Intent(Intent.ACTION_SEND)
				.setType(FileTypeCollection.Instance.rawMimeType(file).Name)
				.putExtra(Intent.EXTRA_SUBJECT, book.getTitle())
				.putExtra(Intent.EXTRA_TEXT, sharedFrom)
				.putExtra(Intent.EXTRA_STREAM, contentUri)
				.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			activity.startActivity(Intent.createChooser(shareIntent, "Share"));
		} catch (ActivityNotFoundException e) {
			// TODO: show toast
		}
	}
}
