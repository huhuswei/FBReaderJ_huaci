/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
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

package org.geometerplus.android.fbreader.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;

import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

public abstract class ZLStringPreference extends EditTextPreference {
	private String myValue;

	protected ZLStringPreference(Context context, ZLResource rootResource, String resourceKey) {
		super(context);

		ZLResource resource = rootResource.getResource(resourceKey);
		setTitle(resource.getValue());
	}

	@Override
	protected void showDialog(Bundle state) {
		// 检查水墨屏主题
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
		if (zlibrary != null && zlibrary.InkThemeOption.getValue()) {
			// 移除之前的对话框(如果有)
			try {
				java.lang.reflect.Method removeMethod = DialogPreference.class.getDeclaredMethod("removeDialog");
				removeMethod.setAccessible(true);
				removeMethod.invoke(this);
			} catch (Exception e) {
				// 忽略
			}
		}
		super.showDialog(state);
	}

	protected void setValue(String value) {
		setSummary(value);
		setText(value);
		myValue = value;
	}

	protected final String getValue() {
		return myValue;
	}

	@Override
	protected void onDialogClosed(boolean result) {
		if (result) {
			setValue(getEditText().getText().toString());
		}
		super.onDialogClosed(result);
	}
}
