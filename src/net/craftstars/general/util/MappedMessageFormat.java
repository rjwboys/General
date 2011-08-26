package net.craftstars.general.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class MappedMessageFormat extends Format {
	private MessageFormat format;
	private LinkedHashSet<String> keys;
	
	public MappedMessageFormat(String pattern) {
		this(pattern, Locale.getDefault());
	}
	
	public MappedMessageFormat(String pattern, Locale locale) {
		format = new MessageFormat(namedToIndexed(pattern), locale);
	}
	
	private MappedMessageFormat(MessageFormat fmt, LinkedHashSet<String> k) {
		format = fmt;
		keys = k;
	}
	
	private String namedToIndexed(String pattern) {
		keys = new LinkedHashSet<String>();
		Pattern regex = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9-]*)[,\\}]");
		Matcher mat = regex.matcher(pattern);
		while(mat.find()) keys.add(mat.group(1));
		String[] keyArray = keys.toArray(new String[0]);
		for(int i = 0; i < keyArray.length; i++)
			pattern = pattern.replaceAll("\\{" + keyArray[i] + "([,\\}])", "\\{" + i + "$1");
		// To make things easier, we use ` instead of ' for the escape character; thus we need to
		// convert escapes and apostrophes.
		pattern = pattern.replace('`', '\u0001').replace("'", "''''").replace('\u0001', '`');
		return pattern;
	}
	
	@SuppressWarnings("unused")
	private String indexedToNamed(String pattern) {
		String[] keyArray = keys.toArray(new String[0]);
		for(int i = 0; i < keyArray.length; i++)
			pattern = pattern.replace("{" + i, "{" + keyArray[i]);
		// To make things easier, we use ` instead of ' for the escape character; thus we need to
		// convert escapes and apostrophes. This is likely imperfect.
		pattern = pattern.replace("''''", "\u0001").replace('\'', '`').replace('\u0001', '\'');
		return pattern;
	}
	
	public StringBuffer format(Map<String,Object> formatKeys) {
		return format(formatKeys, new StringBuffer());
	}
	
	public StringBuffer format(Map<String,Object> formatKeys, StringBuffer dest) {
		return format(formatKeys, dest, null);
	}
	
	@Override@SuppressWarnings("unchecked")
	public StringBuffer format(Object obj, StringBuffer dest, FieldPosition pos) {
		return format((Map<String, Object>) obj, dest, pos);
	}
	
	@Override
	public Object parseObject(String arg0, ParsePosition arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// And now all the methods that defer directly to the internal MessageFormat object
	
	void applyPattern(String pattern) {
		format.applyPattern(namedToIndexed(pattern));
	}
	
	@Override@SuppressWarnings("unchecked")
	public MappedMessageFormat clone() {
		return new MappedMessageFormat((MessageFormat) format.clone(), (LinkedHashSet<String>) keys.clone());
	}
	
	public StringBuffer format(Map<String,Object> formatKeys, StringBuffer dest, FieldPosition pos) {
		Object defaultVal = "";
		if(formatKeys.containsKey(null)) defaultVal = formatKeys.get(null);
		String[] keyArray = keys.toArray(new String[0]);
		Object[] values = new Object[keyArray.length];
		for(int i = 0; i < keyArray.length; i++)
			values[i] = (formatKeys.containsKey(keyArray[i]) ? formatKeys.get(keyArray[i]) : defaultVal);
		return format.format(values, dest, pos);
	}
	
	// TODO: Add all the other methods here...
}
