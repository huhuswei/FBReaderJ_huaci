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

import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;

import org.geometerplus.zlibrary.core.options.*;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.network.SQLiteCookieDatabase;

import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.util.InkThemeUtil;

public abstract class ZLPreferenceActivity extends android.preference.PreferenceActivity {
	public static String SCREEN_KEY = "screen";

	private final HashMap<String,Screen> myScreenMap = new HashMap<String,Screen>();
	private Boolean myPendingInkTheme;
	private boolean myIsInkTheme;

	protected class Screen {
		public final ZLResource Resource;
		private final PreferenceScreen myScreen;

		private Screen(ZLResource root, String resourceKey) {
			Resource = root.getResource(resourceKey);
			myScreen = getPreferenceManager().createPreferenceScreen(ZLPreferenceActivity.this);
			myScreen.setTitle(Resource.getValue());
			myScreen.setSummary(Resource.getResource("summary").getValue());
		}

		public void setSummary(CharSequence summary) {
			myScreen.setSummary(summary);
		}

		public Screen createPreferenceScreen(String resourceKey) {
			Screen screen = new Screen(Resource, resourceKey);
			myScreen.addPreference(screen.myScreen);
			return screen;
		}

		public Preference addPreference(Preference preference) {
			myScreen.addPreference(preference);
			return preference;
		}

		public Preference addOption(ZLBooleanOption option, String resourceKey) {
			return addPreference(new ZLBooleanPreference(
				ZLPreferenceActivity.this, option, Resource.getResource(resourceKey)
			));
		}

		public Preference addOption(ZLColorOption option, String resourceKey) {
			return addPreference(new ZLColorPreference(
				ZLPreferenceActivity.this, Resource, resourceKey, option
			));
		}

		public Preference addOption(ZLIntegerRangeOption option, String resourceKey) {
			return addPreference(new ZLIntegerRangePreference(
				ZLPreferenceActivity.this, Resource.getResource(resourceKey), option
			));
		}

		public <T extends Enum<T>> Preference addOption(ZLEnumOption<T> option, String key) {
			return addPreference(
				new ZLEnumPreference<T>(ZLPreferenceActivity.this, option, Resource.getResource(key))
			);
		}

		public <T extends Enum<T>> Preference addOption(ZLEnumOption<T> option, String key, String valuesKey) {
			return addPreference(
				new ZLEnumPreference<T>(ZLPreferenceActivity.this, option, Resource.getResource(key), Resource.getResource(valuesKey))
			);
		}
	}

	private PreferenceScreen myScreen;
	final ZLResource Resource;

	ZLPreferenceActivity(String resourceKey) {
		Resource = ZLResource.resource(resourceKey);
	}

	Screen createPreferenceScreen(String resourceKey) {
		final Screen screen = new Screen(Resource, resourceKey);
		myScreenMap.put(resourceKey, screen);
		myScreen.addPreference(screen.myScreen);
		return screen;
	}

	public Preference addPreference(Preference preference) {
		myScreen.addPreference(preference);
		return preference;
	}

	public Preference addOption(ZLBooleanOption option, String resourceKey) {
		ZLBooleanPreference preference =
			new ZLBooleanPreference(ZLPreferenceActivity.this, option, Resource.getResource(resourceKey));
		myScreen.addPreference(preference);
		return preference;
	}

	/*
	protected Category createCategory() {
		return new CategoryImpl(myScreen, Resource);
	}
	*/

	protected abstract void init(Intent intent);

	@Override
	protected void onCreate(Bundle bundle) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
		myIsInkTheme = zlibrary != null && zlibrary.InkThemeOption.getValue();
		if (myIsInkTheme) {
			InkThemeUtil.applyInkThemeToActivity(this);
		}

		// 保存主题状态
		if (zlibrary != null) {
			myPendingInkTheme = zlibrary.InkThemeOption.getValue();
		}

		super.onCreate(bundle);

		// 水墨屏主题：设置 Preference 列表背景和文字颜色
		if (myIsInkTheme) {
			final android.widget.ListView listView = getListView();
			if (listView != null) {
				listView.setBackgroundColor(getResources().getColor(org.geometerplus.zlibrary.ui.android.R.color.ink_background));
				listView.setCacheColorHint(getResources().getColor(org.geometerplus.zlibrary.ui.android.R.color.ink_background));
				listView.setDivider(getResources().getDrawable(org.geometerplus.zlibrary.ui.android.R.color.ink_divider));
			}
		}

		Thread.setDefaultUncaughtExceptionHandler(new org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(this));

		SQLiteCookieDatabase.init(this);

		myScreen = getPreferenceManager().createPreferenceScreen(this);

		final Intent intent = getIntent();
		final Uri data = intent.getData();
		final String screenId;
		if (Intent.ACTION_VIEW.equals(intent.getAction())
				&& data != null && "fbreader-preferences".equals(data.getScheme())) {
			screenId = data.getEncodedSchemeSpecificPart();
		} else {
			screenId = intent.getStringExtra(SCREEN_KEY);
		}

		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				init(intent);
				final Screen screen = myScreenMap.get(screenId);
				setPreferenceScreen(screen != null ? screen.myScreen : myScreen);
				// 水墨屏主题：在 PreferenceScreen 设置完成后应用样式
				// 使用 post() 延迟执行确保视图完全加载
				if (myIsInkTheme) {
					applyInkThemeToPreferences();
					applyInkThemeToDialogs();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		OrientationUtil.setOrientation(this, getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 检测水墨屏主题变化并重建 Activity
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
		if (zlibrary != null) {
			final boolean currentInkTheme = zlibrary.InkThemeOption.getValue();
			if (myPendingInkTheme == null) {
				myPendingInkTheme = currentInkTheme;
			} else if (myPendingInkTheme != currentInkTheme) {
				myPendingInkTheme = currentInkTheme;
				// 主题变化时必须 recreate 才能正确应用新主题
				recreate();
			}
		}

		// 水墨屏主题：应用样式到 Preference 列表
		// 使用延迟执行确保视图完全渲染
		if (myIsInkTheme) {
			applyInkThemeToPreferences();
			applyInkThemeToDialogs();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		OrientationUtil.setOrientation(this, intent);
	}

	// 公共方法：应用水墨屏主题到对话框
	public static void applyInkThemeToDialog(android.app.Dialog dialog, android.content.Context context) {
		InkThemeUtil.applyInkThemeToDialog(dialog, context);
	}

	// 水墨屏主题：为所有 Preference 组件应用水墨风格
	private void applyInkThemeToPreferences() {
		InkThemeUtil.applyInkThemeToPreferences(this);
	}

	// 水墨屏主题：应用样式到对话框
	private void applyInkThemeToDialogs() {
		if (!myIsInkTheme) {
			return;
		}
		InkThemeUtil.applyInkThemeToDialogs(this);
	}
}
