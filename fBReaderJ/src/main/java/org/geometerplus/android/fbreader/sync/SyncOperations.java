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

package org.geometerplus.android.fbreader.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.geometerplus.fbreader.fbreader.options.SyncOptions;
import org.geometerplus.android.fbreader.api.FBReaderIntents;

public abstract class SyncOperations {
	public static void enableSync(Context context, SyncOptions options) {
		final String action = options.Enabled.getValue()
			? FBReaderIntents.Action.SYNC_START : FBReaderIntents.Action.SYNC_STOP;
		final Intent intent = new Intent(context, SyncService.class).setAction(action);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intent);
		} else {
			context.startService(intent);
		}
	}

	public static void quickSync(Context context, SyncOptions options) {
		if (options.Enabled.getValue()) {
			final Intent intent = new Intent(context, SyncService.class)
					.setAction(FBReaderIntents.Action.SYNC_QUICK_SYNC);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(intent);
			} else {
				context.startService(intent);
			}
		}
	}
}
