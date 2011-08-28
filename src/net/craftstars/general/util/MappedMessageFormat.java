package net.craftstars.general.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import static java.text.CharacterIterator.DONE;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides formatting similar to that of MessageFormat, with the following two differences:
 * <ul>
 * <li>Instead of using single quotes (') as an escape character, it uses backquotes (`). All the rules of escape
 * characters are the same as for MessageFormat apart from the character used.</li>
 * <li>Instead of numeric indices for arguments (such as {2}, {7}), it uses named arguments (such as {name},
 * {date}). Subformats work the same way (eg {date,date,full} instead of {2,date,full}).
 * </ul>
 * @see MessageFormat
 */
public class MappedMessageFormat extends Format {
	private static final long serialVersionUID = 8986672595156092199L;
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
		pattern = pattern.replace("'", "{" + keys.size() + "}").replace('`', '\'');
		return pattern;
	}
	
	private String indexedToNamed(String pattern) {
		String[] keyArray = keys.toArray(new String[0]);
		for(int i = 0; i < keyArray.length; i++)
			pattern = pattern.replace("{" + i, "{" + keyArray[i]);
		// To make things easier, we use ` instead of ' for the escape character; thus we need to
		// convert escapes and apostrophes.
		String apostrophe = "{" + keys.size() + "}";
		pattern = pattern.replace('\'', '`').replace(apostrophe, "'");
		return pattern;
	}
	
	public StringBuffer format(Map<String,Object> formatKeys) {
		return format(formatKeys, new StringBuffer());
	}
	
	public StringBuffer format(Map<String,Object> formatKeys, StringBuffer dest) {
		return format(formatKeys, dest, null);
	}
	
	public Map<String, Object> parse(String source) {
		return parse(source, new ParsePosition(0));
	}
	
	public Map<String, Object> parse(String source, ParsePosition pos) {
		Object[] results = format.parse(source, pos);
		Map<String, Object> map = new LinkedHashMap<String,Object>();
		int i = 0;
		for(String key : keys) {
			if(i >= results.length) break;
			map.put(key, results[i]);
			i++;
		}
		return map;
	}
	
	public static StringBuffer format(String format, Map<String,Object> args) {
		return new MappedMessageFormat(format).format(args);
	}
	
	// And now all the methods that defer directly to the internal MessageFormat object
	
	void applyPattern(String pattern) {
		format.applyPattern(namedToIndexed(pattern));
	}
	
	@Override@SuppressWarnings("unchecked")
	public MappedMessageFormat clone() {
		return new MappedMessageFormat((MessageFormat) format.clone(), (LinkedHashSet<String>) keys.clone());
	}
	
	@Override@SuppressWarnings("unchecked")
	public StringBuffer format(Object obj, StringBuffer dest, FieldPosition pos) {
		return format((Map<String, Object>) obj, dest, pos);
	}
	
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return parse(source, pos);
	}
	
	public StringBuffer format(Map<String,Object> formatKeys, StringBuffer dest, FieldPosition pos) {
		Object defaultVal = "";
		if(formatKeys.containsKey(null)) defaultVal = formatKeys.get(null);
		String[] keyArray = keys.toArray(new String[0]);
		Object[] values = new Object[keyArray.length+1];
		for(int i = 0; i < keyArray.length; i++)
			values[i] = (formatKeys.containsKey(keyArray[i]) ? formatKeys.get(keyArray[i]) : defaultVal);
		values[keyArray.length] = "''";
		return format.format(values, dest, pos);
	}
	
	public Format[] getFormats() {
		return format.getFormats();
	}
	
	public Map<String, Format> getFormatsByArgument() {
		Format[] formatsByIndex = format.getFormatsByArgumentIndex();
		Map<String, Format> map = new LinkedHashMap<String, Format>();
		int i = 0;
		for(String argument : keys) {
			if(i >= formatsByIndex.length) break;
			map.put(argument, formatsByIndex[i]);
			i++;
		}
		return map;
	}
	
	public void setFormats(Format[] formats) {
		format.setFormats(formats);
	}
	
	public void setFormatsByArgument(LinkedHashMap<String, Format> formats) {
		format.setFormatsByArgumentIndex(formats.values().toArray(new Format[0]));
	}
	
	public void setFormat(int iFormat, Format newFormat) {
		format.setFormat(iFormat, newFormat);
	}
	
	public void setFormatByArgument(String argument, Format newFormat) {
		keys.add(argument); // Ensure the argument is in the set; if it is, this is a no-op
		List<String> allKeys = Arrays.asList(keys.toArray(new String[0]));
		int i= allKeys.indexOf(argument);
		format.setFormatByArgumentIndex(i, newFormat);
	}
	
	public Locale getLocale() {
		return format.getLocale();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof MappedMessageFormat)) return false;
		MappedMessageFormat compare = (MappedMessageFormat) other;
		return format.equals(compare.format) && keys.equals(compare.keys);
	}
	
	@Override
	public int hashCode() {
		return format.hashCode() ^ keys.hashCode();
	}
	
	void setLocale(Locale locale) {
		format.setLocale(locale);
	}
	
	String toPattern() {
		String pattern = format.toPattern();
		return indexedToNamed(pattern);
	}
	
	public static class Field extends Format.Field {
		private static final long serialVersionUID = 1823044325757450021L;

		protected Field(String name) {
			super(name);
		}
		
		@Override
		protected Object readResolve() throws InvalidObjectException {
			if(this.getClass() != MappedMessageFormat.Field.class) {
				throw new InvalidObjectException("subclass didn't correctly implement readResolve");
			}
			return ARGUMENT;
		}
		
		public final static Field ARGUMENT = new Field("message argument field");
	}
	
	@Override@SuppressWarnings("unchecked")
	public AttributedCharacterIterator formatToCharacterIterator(Object arguments) {
		return formatToCharacterIterator((Map<String,Object>)arguments);
	}
	
	public AttributedCharacterIterator formatToCharacterIterator(Map<String,Object> arguments) {
		AttributedCharacterIterator iter = format.formatToCharacterIterator(arguments);
		AttributedString str = new AttributedString(format(arguments).toString());
		char c = iter.first();
		while(c != DONE) {
			Map<Attribute,Object> attributes = iter.getAttributes();
			int i = iter.getIndex();
			for(Attribute attribute : attributes.keySet()) {
				if(attribute == MessageFormat.Field.ARGUMENT)
					str.addAttribute(attribute, Field.ARGUMENT, i, i + 1);
				else str.addAttribute(attribute, attributes.get(attribute), i, i + 1);
			}
			c = iter.next();
		}
		return str.getIterator();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, InvalidObjectException {
		in.defaultReadObject();
		if(keys.size() != format.getFormatsByArgumentIndex().length)
			throw new InvalidObjectException("Could not reconstruct MappedMessageFormat from corrupt stream.");
	}
}
