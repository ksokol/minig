package org.minig.util;

/**
 * A {@code UnicodeEscaper} that escapes some set of Java characters using a
 * UTF-8 based percent encoding scheme. The set of safe characters (those which
 * remain unescaped) can be specified on construction.
 *
 * <p>When escaping a String, the following rules apply:
 * <ul>
 * <li>All specified safe characters remain unchanged.
 * <li>If {@code plusForSpace} was specified, the space character " " is
 *     converted into a plus sign {@code "+"}.
 * <li>All other characters are converted into one or more bytes using UTF-8
 *     encoding and each byte is then represented by the 3-character string
 *     "%XX", where "XX" is the two-digit, uppercase, hexadecimal representation
 *     of the byte value.
 * </ul>
 *
 * <p>For performance reasons the only currently supported character encoding of
 * this class is UTF-8.
 *
 * <p><b>Note</b>: This escaper produces uppercase hexadecimal sequences. From
 * <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>:<br>
 * <i>"URI producers and normalizers should use uppercase hexadecimal digits
 * for all percent-encodings."</i>
 *
 * @author David Beaumont
 * @since 15.0
 */
public final class PercentEscaper {

    /** The amount of padding (chars) to use when growing the escape buffer. */
    private static final int DEST_PAD = 32;

    private final char[] buffer = new char[1024];

    // In some escapers spaces are escaped to '+'
    private static final char[] PLUS_SIGN = { '+' };

    // Percent escapers output upper case hex digits (uri escapers require this).
    private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    /**
     * If true we should convert space to the {@code +} character.
     */
    private final boolean plusForSpace;

    /**
     * An array of flags where for any {@code char c} if {@code safeOctets[c]} is
     * true then {@code c} should remain unmodified in the output. If
     * {@code c > safeOctets.length} then it should be escaped.
     */
    private final boolean[] safeOctets;

    /**
     * Constructs a percent escaper with the specified safe characters and
     * optional handling of the space character.
     *
     * <p>Not that it is allowed, but not necessarily desirable to specify {@code %}
     * as a safe character. This has the effect of creating an escaper which has no
     * well defined inverse but it can be useful when escaping additional characters.
     *
     * @param safeChars a non null string specifying additional safe characters
     *        for this escaper (the ranges 0..9, a..z and A..Z are always safe and
     *        should not be specified here)
     * @param plusForSpace true if ASCII space should be escaped to {@code +}
     *        rather than {@code %20}
     * @throws IllegalArgumentException if any of the parameters were invalid
     */
    public PercentEscaper(String safeChars, boolean plusForSpace) {
        // TODO(user): Switch to static factory methods for creation now that class is final.
        // TODO(user): Support escapers where alphanumeric chars are not safe.
        // Avoid any misunderstandings about the behavior of this escaper
        if (safeChars.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException(
                    "Alphanumeric characters are always 'safe' and should not be " +
                            "explicitly specified");
        }
        safeChars += "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789";
        // Avoid ambiguous parameters. Safe characters are never modified so if
        // space is a safe character then setting plusForSpace is meaningless.
        if (plusForSpace && safeChars.contains(" ")) {
            throw new IllegalArgumentException(
                    "plusForSpace cannot be specified when space is a 'safe' character");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = createSafeOctets(safeChars);
    }

    /**
     * Creates a boolean array with entries corresponding to the character values
     * specified in safeChars set to true. The array is as small as is required to
     * hold the given character information.
     */
    private static boolean[] createSafeOctets(String safeChars) {
        int maxChar = -1;
        char[] safeCharArray = safeChars.toCharArray();
        for (char c : safeCharArray) {
            maxChar = Math.max(c, maxChar);
        }
        boolean[] octets = new boolean[maxChar + 1];
        for (char c : safeCharArray) {
            octets[c] = true;
        }
        return octets;
    }

    /*
     * Overridden for performance. For unescaped strings this improved the
     * performance of the uri escaper from ~760ns to ~400ns as measured by
     * {@link CharEscapersBenchmark}.
     */
    protected int nextEscapeIndex(CharSequence csq, int index, int end) {
        for (; index < end; index++) {
            char c = csq.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                break;
            }
        }
        return index;
    }

    /*
     * Overridden for performance. For unescaped strings this improved the
     * performance of the uri escaper from ~400ns to ~170ns as measured by
     * {@link CharEscapersBenchmark}.
     */
    public String escape(String s) {
        int slen = s.length();
        for (int index = 0; index < slen; index++) {
            char c = s.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                return escapeSlow(s, index);
            }
        }
        return s;
    }

    /**
     * Escapes the given Unicode code point in UTF-8.
     */
    protected char[] escape(int cp) {
        // We should never get negative values here but if we do it will throw an
        // IndexOutOfBoundsException, so at least it will get spotted.
        if (cp < safeOctets.length && safeOctets[cp]) {
            return null;
        } else if (cp == ' ' && plusForSpace) {
            return PLUS_SIGN;
        } else if (cp <= 0x7F) {
            // Single byte UTF-8 characters
            // Start with "%--" and fill in the blanks
            char[] dest = new char[3];
            dest[0] = '%';
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            dest[1] = UPPER_HEX_DIGITS[cp >>> 4];
            return dest;
        } else if (cp <= 0x7ff) {
            // Two byte UTF-8 characters [cp >= 0x80 && cp <= 0x7ff]
            // Start with "%--%--" and fill in the blanks
            char[] dest = new char[6];
            dest[0] = '%';
            dest[3] = '%';
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[1] = UPPER_HEX_DIGITS[0xC | cp];
            return dest;
        } else if (cp <= 0xffff) {
            // Three byte UTF-8 characters [cp >= 0x800 && cp <= 0xffff]
            // Start with "%E-%--%--" and fill in the blanks
            char[] dest = new char[9];
            dest[0] = '%';
            dest[1] = 'E';
            dest[3] = '%';
            dest[6] = '%';
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp];
            return dest;
        } else if (cp <= 0x10ffff) {
            char[] dest = new char[12];
            // Four byte UTF-8 characters [cp >= 0xffff && cp <= 0x10ffff]
            // Start with "%F-%--%--%--" and fill in the blanks
            dest[0] = '%';
            dest[1] = 'F';
            dest[3] = '%';
            dest[6] = '%';
            dest[9] = '%';
            dest[11] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[10] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0x7];
            return dest;
        } else {
            // If this ever happens it is due to bug in UnicodeEscaper, not bad input.
            throw new IllegalArgumentException(
                    "Invalid unicode character value " + cp);
        }
    }

    protected final String escapeSlow(String s, int index) {
        int end = s.length();

        // Get a destination buffer and setup some loop variables.
        char[] dest = buffer;
        int destIndex = 0;
        int unescapedChunkStart = 0;

        while (index < end) {
            int cp = codePointAt(s, index, end);
            if (cp < 0) {
                throw new IllegalArgumentException(
                        "Trailing high surrogate at end of input");
            }
            // It is possible for this to return null because nextEscapeIndex() may
            // (for performance reasons) yield some false positives but it must never
            // give false negatives.
            char[] escaped = escape(cp);
            int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;

                // This is the size needed to add the replacement, not the full
                // size needed by the string.  We only regrow when we absolutely must.
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + DEST_PAD;
                    dest = growBuffer(dest, destIndex, destLength);
                }
                // If we have skipped any characters, we need to copy them now.
                if (charsSkipped > 0) {
                    s.getChars(unescapedChunkStart, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
                    destIndex += escaped.length;
                }
                // If we dealt with an escaped character, reset the unescaped range.
                unescapedChunkStart = nextIndex;
            }
            index = nextEscapeIndex(s, nextIndex, end);
        }

        // Process trailing unescaped characters - no need to account for escaped
        // length or padding the allocation.
        int charsSkipped = end - unescapedChunkStart;
        if (charsSkipped > 0) {
            int endIndex = destIndex + charsSkipped;
            if (dest.length < endIndex) {
                dest = growBuffer(dest, destIndex, endIndex);
            }
            s.getChars(unescapedChunkStart, end, dest, destIndex);
            destIndex = endIndex;
        }
        return new String(dest, 0, destIndex);
    }

    private static int codePointAt(CharSequence seq, int index, int end) {
        if (index < end) {
            char c1 = seq.charAt(index++);
            if (c1 < Character.MIN_HIGH_SURROGATE ||
                    c1 > Character.MAX_LOW_SURROGATE) {
                // Fast path (first test is probably all we need to do)
                return c1;
            } else if (c1 <= Character.MAX_HIGH_SURROGATE) {
                // If the high surrogate was the last character, return its inverse
                if (index == end) {
                    return -c1;
                }
                // Otherwise look for the low surrogate following it
                char c2 = seq.charAt(index);
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
                throw new IllegalArgumentException(
                        "Expected low surrogate but got char '" + c2 +
                                "' with value " + (int) c2 + " at index " + index +
                                " in '" + seq + "'");
            } else {
                throw new IllegalArgumentException(
                        "Unexpected low surrogate character '" + c1 +
                                "' with value " + (int) c1 + " at index " + (index - 1) +
                                " in '" + seq + "'");
            }
        }
        throw new IndexOutOfBoundsException("Index exceeds specified range");
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }

}
