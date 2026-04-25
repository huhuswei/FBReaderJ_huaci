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

import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;

class ZLBooleanPreference extends ZLCheckBoxPreference {
	private final ZLBooleanOption myOption;
	private final String myOptionGroup;

	ZLBooleanPreference(Context context, ZLBooleanOption option, ZLResource resource) {
		super(context, resource);
		myOption = option;
		myOptionGroup = getOptionGroupName(option);
		setChecked(option.getValue());
	}

	private String getOptionGroupName(ZLBooleanOption option) {
		try {
			java.lang.reflect.Field field = option.getClass().getSuperclass().getDeclaredField("myId");
			field.setAccessible(true);
			Object stringPair = field.get(option);
			java.lang.reflect.Field groupField = stringPair.getClass().getDeclaredField("Group");
			groupField.setAccessible(true);
			String group = (String) groupField.get(stringPair);
			java.lang.reflect.Field nameField = stringPair.getClass().getDeclaredField("Name");
			nameField.setAccessible(true);
			String name = (String) nameField.get(stringPair);
			return group + "::" + name;
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	protected void onClick() {
		super.onClick();
		final boolean newValue = isChecked();
		final boolean oldValue = myOption.getValue();
		myOption.setValue(newValue);

		// 水墨屏主题切换时，重建并返回到 PreferenceActivity 主页面
		if (newValue != oldValue && "LookNFeel::InkTheme".equals(myOptionGroup)) {
			if (getContext() instanceof android.app.Activity) {
				android.app.Activity activity = (android.app.Activity) getContext();
				activity.recreate();
				activity.finish();
			}
		}
	}
}
