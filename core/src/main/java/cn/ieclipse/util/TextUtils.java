/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ieclipse.util;

import java.util.Iterator;
import java.util.regex.Pattern;

public class TextUtils {
    private TextUtils() {
        /* cannot be instantiated */ }
        
    public static void getChars(CharSequence s, int start, int end, char[] dest,
            int destoff) {
        Class<? extends CharSequence> c = s.getClass();
        
        if (c == String.class)
            ((String) s).getChars(start, end, dest, destoff);
        else if (c == StringBuffer.class)
            ((StringBuffer) s).getChars(start, end, dest, destoff);
        else if (c == StringBuilder.class)
            ((StringBuilder) s).getChars(start, end, dest, destoff);
        else {
            for (int i = start; i < end; i++)
                dest[destoff++] = s.charAt(i);
        }
    }
    
    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens
     *            an array objects to be joined. Strings will be formed from the
     *            objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Object[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            }
            else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens
     *            an array objects to be joined. Strings will be formed from the
     *            objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }
    
    /**
     * String.split() returns [''] when the string to be split is empty. This
     * returns []. This does not remove any empty strings from the result. For
     * example split("a,", "," ) returns {"a", ""}.
     *
     * @param text
     *            the string to split
     * @param expression
     *            the regular expression to match
     * @return an array of strings. The array will be empty if text is empty
     *         
     * @throws NullPointerException
     *             if expression or text is null
     */
    public static String[] split(String text, String expression) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        else {
            return text.split(expression, -1);
        }
    }
    
    /**
     * Splits a string on a pattern. String.split() returns [''] when the string
     * to be split is empty. This returns []. This does not remove any empty
     * strings from the result.
     * 
     * @param text
     *            the string to split
     * @param pattern
     *            the regular expression to match
     * @return an array of strings. The array will be empty if text is empty
     *         
     * @throws NullPointerException
     *             if expression or text is null
     */
    public static String[] split(String text, Pattern pattern) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        else {
            return pattern.split(text, -1);
        }
    }
    
    /**
     * An interface for splitting strings according to rules that are opaque to
     * the user of this interface. This also has less overhead than split, which
     * uses regular expressions and allocates an array to hold the results.
     *
     * <p>
     * The most efficient way to use this class is:
     *
     * <pre>
     * // Once
     * TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(delimiter);
     *
     * // Once per string to split
     * splitter.setString(string);
     * for (String s : splitter) {
     *     ...
     * }
     * </pre>
     */
    public interface StringSplitter extends Iterable<String> {
        public void setString(String string);
    }
    
    /**
     * A simple string splitter.
     *
     * <p>
     * If the final character in the string to split is the delimiter then no
     * empty string will be returned for the empty string after that delimeter.
     * That is, splitting <tt>"a,b,"</tt> on comma will return <tt>"a", "b"</tt>
     * , not <tt>"a", "b", ""</tt>.
     */
    public static class SimpleStringSplitter
            implements StringSplitter, Iterator<String> {
        private String mString;
        private char mDelimiter;
        private int mPosition;
        private int mLength;
        
        /**
         * Initializes the splitter. setString may be called later.
         * 
         * @param delimiter
         *            the delimeter on which to split
         */
        public SimpleStringSplitter(char delimiter) {
            mDelimiter = delimiter;
        }
        
        /**
         * Sets the string to split
         * 
         * @param string
         *            the string to split
         */
        public void setString(String string) {
            mString = string;
            mPosition = 0;
            mLength = mString.length();
        }
        
        public Iterator<String> iterator() {
            return this;
        }
        
        public boolean hasNext() {
            return mPosition < mLength;
        }
        
        public String next() {
            int end = mString.indexOf(mDelimiter, mPosition);
            if (end == -1) {
                end = mLength;
            }
            String nextString = mString.substring(mPosition, end);
            mPosition = end + 1; // Skip the delimiter.
            return nextString;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Returns true if the string is null or 0-length.
     * 
     * @param str
     *            the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    
    /**
     * Returns the length that the specified CharSequence would have if spaces
     * and ASCII control characters were trimmed from the start and end, as by
     * {@link String#trim}.
     */
    public static int getTrimmedLength(CharSequence s) {
        int len = s.length();
        
        int start = 0;
        while (start < len && s.charAt(start) <= ' ') {
            start++;
        }
        
        int end = len;
        while (end > start && s.charAt(end - 1) <= ' ') {
            end--;
        }
        
        return end - start;
    }
    
    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p>
     * <i>Note: In platform versions 1.1 and earlier, this method only worked
     * well if both the arguments were instances of String.</i>
     * </p>
     * 
     * @param a
     *            first CharSequence to check
     * @param b
     *            second CharSequence to check
     * @return true if a and b are equal
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b)
            return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            }
            else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Html-encode the string.
     * 
     * @param s
     *            the string to be encoded
     * @return the encoded string
     */
    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    // http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe,
                    // U+0027) was introduced in
                    // XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead
                    // of &apos; to work as expected in HTML 4 user agents.
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Returns whether the given CharSequence contains any printable characters.
     */
    public static boolean isGraphic(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            int gc = Character.getType(cp);
            if (gc != Character.CONTROL && gc != Character.FORMAT
                    && gc != Character.SURROGATE && gc != Character.UNASSIGNED
                    && gc != Character.LINE_SEPARATOR
                    && gc != Character.PARAGRAPH_SEPARATOR
                    && gc != Character.SPACE_SEPARATOR) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether this character is a printable character.
     *
     * This does not support non-BMP characters and should not be used.
     *
     * @deprecated Use {@link #isGraphic(CharSequence)} instead.
     */
    @Deprecated
    public static boolean isGraphic(char c) {
        int gc = Character.getType(c);
        return gc != Character.CONTROL && gc != Character.FORMAT
                && gc != Character.SURROGATE && gc != Character.UNASSIGNED
                && gc != Character.LINE_SEPARATOR
                && gc != Character.PARAGRAPH_SEPARATOR
                && gc != Character.SPACE_SEPARATOR;
    }
    
    /**
     * Returns whether the given CharSequence contains only digits.
     */
    public static boolean isDigitsOnly(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @hide
     */
    public static boolean isPrintableAscii(final char c) {
        final int asciiFirst = 0x20;
        final int asciiLast = 0x7E; // included
        return (asciiFirst <= c && c <= asciiLast) || c == '\r' || c == '\n';
    }
    
    /**
     * @hide
     */
    public static boolean isPrintableAsciiOnly(final CharSequence str) {
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!isPrintableAscii(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Does a comma-delimited list 'delimitedString' contain a certain item?
     * (without allocating memory)
     *
     * @hide
     */
    public static boolean delimitedStringContains(String delimitedString,
            char delimiter, String item) {
        if (isEmpty(delimitedString) || isEmpty(item)) {
            return false;
        }
        int pos = -1;
        int length = delimitedString.length();
        while ((pos = delimitedString.indexOf(item, pos + 1)) != -1) {
            if (pos > 0 && delimitedString.charAt(pos - 1) != delimiter) {
                continue;
            }
            int expectedDelimiterPos = pos + item.length();
            if (expectedDelimiterPos == length) {
                // Match at end of string.
                return true;
            }
            if (delimitedString.charAt(expectedDelimiterPos) == delimiter) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Pack 2 int values into a long, useful as a return value for a range
     * 
     * @see #unpackRangeStartFromLong(long)
     * @see #unpackRangeEndFromLong(long)
     * @hide
     */
    public static long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | end;
    }
    
    /**
     * Get the start value from a range packed in a long by
     * {@link #packRangeInLong(int, int)}
     * 
     * @see #unpackRangeEndFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public static int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }
    
    /**
     * Get the end value from a range packed in a long by
     * {@link #packRangeInLong(int, int)}
     * 
     * @see #unpackRangeStartFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public static int unpackRangeEndFromLong(long range) {
        return (int) (range & 0x00000000FFFFFFFFL);
    }
    
    private static String[] EMPTY_STRING_ARRAY = new String[] {};
}
