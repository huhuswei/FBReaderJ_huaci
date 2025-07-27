package org.geometerplus.android.fbreader.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnhancedLanguageDetector {

    // 语言特征库 (语言代码 -> 语言信息)
    private static final Map<String, LanguageInfo> LANGUAGE_FEATURES = new HashMap<>();

    static {
        // 欧洲语言
        LANGUAGE_FEATURES.put("en", new LanguageInfo("English", true, "[a-zA-Z]+"));
        LANGUAGE_FEATURES.put("fr", new LanguageInfo("French", true, "[a-zA-ZÀ-ÿ]+"));
        LANGUAGE_FEATURES.put("es", new LanguageInfo("Spanish", true, "[a-zA-ZÁ-ÿ]+"));
        LANGUAGE_FEATURES.put("de", new LanguageInfo("German", true, "[a-zA-ZÄÖÜäöüß]+"));
        LANGUAGE_FEATURES.put("it", new LanguageInfo("Italian", true, "[a-zA-ZÀ-ÿ]+"));
        LANGUAGE_FEATURES.put("pt", new LanguageInfo("Portuguese", true, "[a-zA-ZÁ-ÿ]+"));
        LANGUAGE_FEATURES.put("ru", new LanguageInfo("Russian", true, "[\\u0400-\\u04FF]+"));
        LANGUAGE_FEATURES.put("uk", new LanguageInfo("Ukrainian", true, "[\\u0400-\\u04FF\\u0500-\\u052F]+"));
        LANGUAGE_FEATURES.put("pl", new LanguageInfo("Polish", true, "[a-zA-ZĄĆĘŁŃÓŚŹŻąćęłńóśźż]+"));
        LANGUAGE_FEATURES.put("nl", new LanguageInfo("Dutch", true, "[a-zA-ZÀ-ÿ]+"));
        LANGUAGE_FEATURES.put("sv", new LanguageInfo("Swedish", true, "[a-zA-ZÅÄÖåäö]+"));
        LANGUAGE_FEATURES.put("fi", new LanguageInfo("Finnish", true, "[a-zA-ZÅÄÖåäö]+"));
        LANGUAGE_FEATURES.put("da", new LanguageInfo("Danish", true, "[a-zA-ZÅÆØåæø]+"));
        LANGUAGE_FEATURES.put("no", new LanguageInfo("Norwegian", true, "[a-zA-ZÅÆØåæø]+"));
        LANGUAGE_FEATURES.put("cs", new LanguageInfo("Czech", true, "[a-zA-ZÁČĎÉĚÍŇÓŘŠŤÚŮÝŽáčďéěíňóřšťúůýž]+"));
        LANGUAGE_FEATURES.put("hu", new LanguageInfo("Hungarian", true, "[a-zA-ZÁÉÍÓÖŐÚÜŰáéíóöőúüű]+"));
        LANGUAGE_FEATURES.put("ro", new LanguageInfo("Romanian", true, "[a-zA-ZĂÂÎȘȚăâîșț]+"));
        LANGUAGE_FEATURES.put("el", new LanguageInfo("Greek", true, "[\\u0370-\\u03FF]+"));
        LANGUAGE_FEATURES.put("bg", new LanguageInfo("Bulgarian", true, "[\\u0400-\\u04FF]+"));

        // 亚洲语言
        LANGUAGE_FEATURES.put("zh", new LanguageInfo("Chinese", false, "[\\u4e00-\\u9fa5]"));
        LANGUAGE_FEATURES.put("ja", new LanguageInfo("Japanese", false, "[\\u3040-\\u309F\\u30A0-\\u30FF\\u4e00-\\u9fa5]"));
        LANGUAGE_FEATURES.put("ko", new LanguageInfo("Korean", false, "[\\uAC00-\\uD7A3]"));
        LANGUAGE_FEATURES.put("th", new LanguageInfo("Thai", false, "[\\u0E00-\\u0E7F]+"));
        LANGUAGE_FEATURES.put("vi", new LanguageInfo("Vietnamese", true, "[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠ-ỹ]+"));

        // 中东语言
        LANGUAGE_FEATURES.put("ar", new LanguageInfo("Arabic", false, "[\\u0600-\\u06FF\\u0750-\\u077F]+"));
        LANGUAGE_FEATURES.put("fa", new LanguageInfo("Persian", false, "[\\u0600-\\u06FF\\u0750-\\u077F\\uFB8A\\u067E\\u0686\\u0698\\u06AF]+"));
        LANGUAGE_FEATURES.put("he", new LanguageInfo("Hebrew", false, "[\\u0590-\\u05FF]+"));
        LANGUAGE_FEATURES.put("ur", new LanguageInfo("Urdu", false, "[\\u0600-\\u06FF\\u0750-\\u077F\\uFB8A\\u067E\\u0686\\u0698\\u06AF]+"));

        // 南亚语言
        LANGUAGE_FEATURES.put("hi", new LanguageInfo("Hindi", false, "[\\u0900-\\u097F]+"));
        LANGUAGE_FEATURES.put("bn", new LanguageInfo("Bengali", false, "[\\u0980-\\u09FF]+"));
        LANGUAGE_FEATURES.put("pa", new LanguageInfo("Punjabi", false, "[\\u0A00-\\u0A7F]+"));
        LANGUAGE_FEATURES.put("gu", new LanguageInfo("Gujarati", false, "[\\u0A80-\\u0AFF]+"));
        LANGUAGE_FEATURES.put("ta", new LanguageInfo("Tamil", false, "[\\u0B80-\\u0BFF]+"));
        LANGUAGE_FEATURES.put("te", new LanguageInfo("Telugu", false, "[\\u0C00-\\u0C7F]+"));
        LANGUAGE_FEATURES.put("kn", new LanguageInfo("Kannada", false, "[\\u0C80-\\u0CFF]+"));
        LANGUAGE_FEATURES.put("ml", new LanguageInfo("Malayalam", false, "[\\u0D00-\\u0D7F]+"));
        LANGUAGE_FEATURES.put("si", new LanguageInfo("Sinhala", false, "[\\u0D80-\\u0DFF]+"));

        // 其他
        LANGUAGE_FEATURES.put("tr", new LanguageInfo("Turkish", true, "[a-zA-ZÇĞİÖŞÜçğıöşü]+"));
        LANGUAGE_FEATURES.put("id", new LanguageInfo("Indonesian", true, "[a-zA-Z]+"));
        LANGUAGE_FEATURES.put("ms", new LanguageInfo("Malay", true, "[a-zA-Z]+"));
    }

    /**
     * 检测文本的主要语言
     * @param text 输入文本
     * @return 检测到的语言信息，如果无法识别返回null
     */
    public static LanguageInfo detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        // 统计各语言特征字符出现的次数
        Map<String, Integer> languageScores = new HashMap<>();

        for (Map.Entry<String, LanguageInfo> entry : LANGUAGE_FEATURES.entrySet()) {
            String langCode = entry.getKey();
            String pattern = entry.getValue().getCharacterPattern();

            int count = countMatches(text, pattern);
            languageScores.put(langCode, count);
        }

        // 找出得分最高的语言
        String detectedLang = languageScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return detectedLang != null ? LANGUAGE_FEATURES.get(detectedLang) : null;
    }

    /**
     * 统计文本中匹配正则表达式的字符数
     */
    private static int countMatches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 语言信息类
     */
    public static class LanguageInfo {
        private final String name;
        private final boolean needsSpaces;
        private final String characterPattern;

        public LanguageInfo(String name, boolean needsSpaces, String characterPattern) {
            this.name = name;
            this.needsSpaces = needsSpaces;
            this.characterPattern = characterPattern;
        }

        public String getName() {
            return name;
        }

        public boolean needsSpaces() {
            return needsSpaces;
        }

        public String getCharacterPattern() {
            return characterPattern;
        }

        @Override
        public String toString() {
            return String.format("%s (需要空格: %s)", name, needsSpaces ? "是" : "否");
        }
    }

    // 测试
    public static void main(String[] args) {
        String[] testTexts = {
                "Hello world", // 英语
                "Bonjour le monde", // 法语
                "Hola mundo", // 西班牙语
                "你好世界", // 中文
                "こんにちは世界", // 日语
                "안녕하세요 세계", // 韩语
                "สวัสดีชาวโลก", // 泰语
                "Xin chào thế giới", // 越南语
                "Привет мир", // 俄语
                "مرحبا بالعالم", // 阿拉伯语
                "नमस्ते दुनिया", // 印地语
                "Γειά σου Κόσμε", // 希腊语
                "שלום עולם", // 希伯来语
                "Merhaba Dünya", // 土耳其语
                "Hallo Welt", // 德语
                "Ciao mondo" // 意大利语
        };

        for (String text : testTexts) {
            LanguageInfo info = detectLanguage(text);
            System.out.printf("文本: \"%s\"\n检测结果: %s\n\n", text, info);
        }
    }
}