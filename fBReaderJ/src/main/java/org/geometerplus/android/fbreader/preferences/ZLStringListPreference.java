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
import android.preference.ListPreference;

import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

abstract class ZLStringListPreference extends ListPreference {
	protected final ZLResource myValuesResource;

	ZLStringListPreference(Context context, ZLResource resource) {
		this(context, resource, resource);
	}

	ZLStringListPreference(Context context, ZLResource resource, ZLResource valuesResource) {
		super(context);
		setTitle(resource.getValue());
		myValuesResource = valuesResource;

		final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");
		setPositiveButtonText(buttonResource.getResource("ok").getValue());
		setNegativeButtonText(buttonResource.getResource("cancel").getValue());
	}

	@Override
	protected void showDialog(Bundle state) {
		// 检查水墨屏主题
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
		if (zlibrary != null && zlibrary.InkThemeOption.getValue()) {
			// 直接设置对话框的主题样式
			getContext().getTheme().applyStyle(R.style.FBReader_PreferenceDialog_Ink, true);
		}
		super.showDialog(state);
	}

	protected final void setList(String[] values) {
		String[] texts = new String[values.length];
		for (int i = 0; i < values.length; ++i) {
			final ZLResource resource = myValuesResource.getResource(values[i]);
			texts[i] = resource.hasValue() ? resource.getValue() : values[i];
		}
		setLists(values, texts);
	}

	protected final void setLists(String[] values, String[] texts) {
		assert(values.length == texts.length);

		setEntryValues(values);

		// It appears that setEntries() DOES NOT perform any formatting on the char sequences
		// http://developer.android.com/reference/android/preference/ListPreference.html#setEntries(java.lang.CharSequence[])
		final String[] entries = new String[texts.length];
		for (int i = 0; i < texts.length; ++i) {
			try {
				entries[i] = String.format(texts[i]);
			} catch (Exception e) {
				entries[i] = texts[i];
			}
		}
		setEntries(entries);
	}

	protected final boolean setInitialValue(String value) {
		int index = 0;
		boolean found = false;
		final CharSequence[] entryValues = getEntryValues();
		if (value != null) {
			for (int i = 0; i < entryValues.length; ++i) {
				if (value.equals(entryValues[i])) {
					index = i;
					found = true;
					break;
				}
			}
		}
		setValueIndex(index);
		return found;
	}

	@Override
	public CharSequence getSummary() {
		// standard getSummary() calls extra String.format(), that causes exceptions in some cases
		return getEntry();
	}
}
