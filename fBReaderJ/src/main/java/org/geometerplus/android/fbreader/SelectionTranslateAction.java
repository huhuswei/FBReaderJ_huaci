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

package org.geometerplus.android.fbreader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import java.util.List;

import org.geometerplus.android.fbreader.util.EnhancedLanguageDetector;
import org.geometerplus.android.fbreader.util.EnhancedLanguageDetector.*;
import org.geometerplus.fbreader.fbreader.*;
import org.geometerplus.android.fbreader.dict.DictionaryUtil;
import org.geometerplus.fbreader.book.UID;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

public class SelectionTranslateAction extends FBAndroidAction {
	private static final String INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID = "com.mmjang.ankihelper.fbreader.bookmark.id";
	private static final String INTENT_ANKIHELPER_NOTE = "com.mmjang.ankihelper.note";
	private static final String INTENT_ANKIHELPER_TARGET_WORD = "com.mmjang.ankihelper.target_word";
	private static final String INTENT_ANKIHELPER_URL = "com.mmjang.ankihelper.url";

	SelectionTranslateAction(FBReader baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	@Override
	protected void run(Object ... params) {
//		final FBView fbview = Reader.getTextView();
//		final DictionaryHighlighting dictionaryHilite = new DictionaryHighlighting(fbview);
//		DictionaryUtil.openTextInDictionary(
//			BaseActivity,
//			fbview.getSelectedSnippet().getText(),
//			fbview.getCountOfSelectedWords() == 1,
//			fbview.getSelectionStartY(),
//			fbview.getSelectionEndY(),
//			new Runnable() {
//				public void run() {
//					fbview.addHighlighting(dictionaryHilite);
//					Reader.getViewWidget().repaint();
//				}
//			}
//		);
//		fbview.clearSelection();

		FBView fbview = this.Reader.getTextView();
		boolean isAnkiHelper = isAppInstalled(this.BaseActivity, "com.mmjang.ankihelper");
		boolean isQuizHelper = isAppInstalled(this.BaseActivity, "com.mmjang.quizhelper");
		String title = this.Reader.getCurrentBook().getTitle();
		String noteHtml = "from <i>" + title + "</i> - " + (Math.round(100.0f * this.Reader.getCurrentBook().getProgress().toFloat()) + "%") + "<br/><a id='fb_source' href='" + getJumpIntentString(this.Reader) + "'>jump to source</a>";
		Intent intent;
		if (this.Reader.getTextView().getCountOfSelectedWords() > 4) {
			String text = this.Reader.getTextView().getSelectedSnippet().getText();
			intent = new Intent();
			if (isAnkiHelper) {
				intent.setClassName("com.mmjang.ankihelper", "com.mmjang.ankihelper.ui.popup.PopupActivity");
			}
			if (isQuizHelper) {
				intent.setClassName("com.mmjang.quizhelper", "com.mmjang.quizhelper.ui.popup.PopupActivity");
			}
			if (isAnkiHelper || isQuizHelper) {
				intent.setAction("android.intent.action.SEND");
				intent.putExtra("android.intent.extra.TEXT", text);
				intent.putExtra(INTENT_ANKIHELPER_NOTE, noteHtml);
				intent.setType("text/plain");
				this.BaseActivity.startActivity(intent);
				fbview.clearSelection();
				return;
			}
			return;
		}
		ZLTextElement element;
		String word;
		ZLTextWordCursor cur = new ZLTextWordCursor(this.Reader.getTextView().getStartCursor());
		cur.moveTo(this.Reader.getTextView().getSelectionStartPosition());
		ZLTextElement targetEle = cur.getElement();
		StringBuilder sb = new StringBuilder();
		sb.append(targetEle.toString());
		while (!cur.isStartOfParagraph()) {
			cur.previousWord();
			element = cur.getElement();
			if (element instanceof ZLTextWord) {
				word = ((ZLTextWord) element).getString();
				if (word.matches(".*[.?;!。？；！]")) {
					break;
				}
				LanguageInfo info = EnhancedLanguageDetector.detectLanguage(word);
				if(info.needsSpaces()) {
					sb.insert(0, " ");
				}
				sb.insert(0, word);
			}
		}
		cur.moveTo(this.Reader.BookTextView.getSelectionStartPosition());
		while (!cur.isEndOfParagraph()) {
			cur.nextWord();
			element = cur.getElement();
			if (element instanceof ZLTextWord) {
				word = ((ZLTextWord) element).getString();
				if (word.matches(".*[.?;!。？；！]")) {
//					sb.append(" ");
					sb.append(word);
					break;
				}
				LanguageInfo info = EnhancedLanguageDetector.detectLanguage(word);
				if(info.needsSpaces()) {
					sb.append(" ");
				}
				sb.append(word);
			}
		}
		String stringBuilder = sb.toString();
		String str = "";
		String replaceAll = this.Reader.getTextView().getSelectedSnippet().getText().toString().replaceAll("[?!.,;\"？！。，；“”‘’《》「」【】]", "").replaceAll("'s$", "").replaceAll("'$", "");; //targetEle.toString().replaceAll("[?!.,;\"]", "").replaceAll("'s$", "").replaceAll("'$", "");
		intent = new Intent();
		if (isAnkiHelper) {
			intent.setClassName("com.mmjang.ankihelper", "com.mmjang.ankihelper.ui.popup.PopupActivity");
		}
		if (isQuizHelper) {
			intent.setClassName("com.mmjang.quizhelper", "com.mmjang.quizhelper.ui.popup.PopupActivity");
		}
		if (isAnkiHelper || isQuizHelper) {
			intent.setAction("android.intent.action.SEND");
			intent.putExtra("android.intent.extra.TEXT", stringBuilder);
			if (!replaceAll.isEmpty()) {
				intent.putExtra(INTENT_ANKIHELPER_TARGET_WORD, replaceAll);
			}
			if (!str.isEmpty()) {
				intent.putExtra(INTENT_ANKIHELPER_URL, str);
			}
			intent.putExtra(INTENT_ANKIHELPER_NOTE, noteHtml);
			intent.setType("text/plain");
			this.BaseActivity.startActivity(intent);
			fbview.clearSelection();
		}
	}

	public static String getJumpIntentString(FBReaderApp fBReaderApp) {
		List<UID> uids = fBReaderApp.Model.Book.uids();
		UID uid = null;
		if (uids.size() > 0) {
			uid = (UID) uids.get(0);
		}
		if (uid == null) {
			return null;
		}
		String type = uid.Type;
		String id = uid.Id;
		ZLTextPosition start = fBReaderApp.BookTextView.getSelectionStartPosition();
		ZLTextPosition end = fBReaderApp.BookTextView.getSelectionEndPosition();
		String position = start.getParagraphIndex() + "@" + start.getElementIndex() + "@" + start.getCharIndex() + "@" + end.getParagraphIndex() + "@" + end.getElementIndex() + "@" + end.getCharIndex();
		return String.format("intent:#Intent;package=org.geometerplus.zlibrary.ui.android;action=android.intent.action.SEND;category=android.intent.category.DEFAULT;type=text/plain;component=org.geometerplus.zlibrary.ui.android/org.geometerplus.android.fbreader.FBReader;S.ID=%s;S.TYPE=%s;S.POSITION=%s;end;", new Object[]{id, type, position});
	}

	public static boolean isAppInstalled(Context context, String str) {
		try {
			context.getPackageManager().getApplicationInfo(str, 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}
