package me.neoblade298.neocore.util;

import java.util.HashMap;

public enum FontInfo {

	A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5),
	F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5),
	K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5),
	P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4),
	U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5),
	Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5),
	NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SIGN('@', 6),
	NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), KARAT('^', 5), AMPERSAND('&', 5), ASTERISK('*', 5),
	LEFT_PARENTHESIS('(', 4), RIGHT_PARENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5),
	EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3),
	COLON(':', 1), SEMICOLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4),
	RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), FORWARD_SLASH('/', 5), BACK_SLASH('\\', 5), PIPE('|', 1), TILDE('~', 5),
	TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

	private char character;
	private int length;
	private static HashMap<Character, FontInfo> charToFontInfo;
	
	static {
		charToFontInfo = new HashMap<Character, FontInfo>();
		charToFontInfo.put('a', a);
		charToFontInfo.put('b', b);
		charToFontInfo.put('c', c);
		charToFontInfo.put('d', d);
		charToFontInfo.put('e', e);
		charToFontInfo.put('f', f);
		charToFontInfo.put('g', g);
		charToFontInfo.put('h', h);
		charToFontInfo.put('i', i);
		charToFontInfo.put('j', j);
		charToFontInfo.put('k', k);
		charToFontInfo.put('l', l);
		charToFontInfo.put('m', m);
		charToFontInfo.put('n', n);
		charToFontInfo.put('o', o);
		charToFontInfo.put('p', p);
		charToFontInfo.put('q', q);
		charToFontInfo.put('r', r);
		charToFontInfo.put('s', s);
		charToFontInfo.put('t', t);
		charToFontInfo.put('u', u);
		charToFontInfo.put('v', v);
		charToFontInfo.put('w', w);
		charToFontInfo.put('x', x);
		charToFontInfo.put('y', y);
		charToFontInfo.put('z', z);
		charToFontInfo.put('A', A);
		charToFontInfo.put('B', B);
		charToFontInfo.put('C', C);
		charToFontInfo.put('D', D);
		charToFontInfo.put('E', E);
		charToFontInfo.put('F', F);
		charToFontInfo.put('G', G);
		charToFontInfo.put('H', H);
		charToFontInfo.put('I', I);
		charToFontInfo.put('J', J);
		charToFontInfo.put('K', K);
		charToFontInfo.put('L', L);
		charToFontInfo.put('M', M);
		charToFontInfo.put('N', N);
		charToFontInfo.put('O', O);
		charToFontInfo.put('P', P);
		charToFontInfo.put('Q', Q);
		charToFontInfo.put('R', R);
		charToFontInfo.put('S', S);
		charToFontInfo.put('T', T);
		charToFontInfo.put('U', U);
		charToFontInfo.put('V', V);
		charToFontInfo.put('W', W);
		charToFontInfo.put('X', X);
		charToFontInfo.put('Y', Y);
		charToFontInfo.put('Z', Z);
		charToFontInfo.put('1', NUM_1);
		charToFontInfo.put('2', NUM_2);
		charToFontInfo.put('3', NUM_3);
		charToFontInfo.put('4', NUM_4);
		charToFontInfo.put('5', NUM_5);
		charToFontInfo.put('6', NUM_6);
		charToFontInfo.put('7', NUM_7);
		charToFontInfo.put('8', NUM_8);
		charToFontInfo.put('9', NUM_9);
		charToFontInfo.put('0', NUM_0);
		charToFontInfo.put('!', EXCLAMATION_POINT);
		charToFontInfo.put('@', AT_SIGN);
		charToFontInfo.put('#', NUM_SIGN);
		charToFontInfo.put('$', DOLLAR_SIGN);
		charToFontInfo.put('%', PERCENT);
		charToFontInfo.put('^', KARAT);
		charToFontInfo.put('&', AMPERSAND);
		charToFontInfo.put('*', ASTERISK);
		charToFontInfo.put('(', LEFT_PARENTHESIS);
		charToFontInfo.put(')', RIGHT_PARENTHESIS);
		charToFontInfo.put('-', MINUS);
		charToFontInfo.put('_', UNDERSCORE);
		charToFontInfo.put('=', EQUALS_SIGN);
		charToFontInfo.put('+', PLUS_SIGN);
		charToFontInfo.put('`', TICK);
		charToFontInfo.put('~', TILDE);
		charToFontInfo.put(',', COMMA);
		charToFontInfo.put('<', LEFT_ARROW);
		charToFontInfo.put('.', PERIOD);
		charToFontInfo.put('>', RIGHT_ARROW);
		charToFontInfo.put('/', FORWARD_SLASH);
		charToFontInfo.put('?', QUESTION_MARK);
		charToFontInfo.put('[', LEFT_BRACKET);
		charToFontInfo.put(']', RIGHT_BRACKET);
		charToFontInfo.put('{', LEFT_CURL_BRACE);
		charToFontInfo.put('}', RIGHT_CURL_BRACE);
		charToFontInfo.put('\\', BACK_SLASH);
		charToFontInfo.put('|', PIPE);
		charToFontInfo.put(';', SEMICOLON);
		charToFontInfo.put(':', COLON);
		charToFontInfo.put(' ', SPACE);
		charToFontInfo.put('\'', SINGLE_QUOTE);
		charToFontInfo.put('"', DOUBLE_QUOTE);
	}

	FontInfo(char character, int length) {
		this.character = character;
		this.length = length;
	}

	public char getCharacter() {
		return this.character;
	}

	public int getLength() {
		return this.length;
	}

	public int getBoldLength() {
		if (this == FontInfo.SPACE) return this.getLength();
		return this.length + 1;
	}
	
	public static FontInfo getFontInfo(Character c) {
		return charToFontInfo.getOrDefault(c, DEFAULT);
	}
}
