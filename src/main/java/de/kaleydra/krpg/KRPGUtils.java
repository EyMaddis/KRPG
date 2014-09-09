package de.kaleydra.krpg;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class KRPGUtils {
	
	private static LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
	static {		
		roman_numerals.put("M", 1000);
		roman_numerals.put("CM", 900);
		roman_numerals.put("D", 500);
		roman_numerals.put("CD", 400);
		roman_numerals.put("C", 100);
		roman_numerals.put("XC", 90);
		roman_numerals.put("L", 50);
		roman_numerals.put("XL", 40);
		roman_numerals.put("X", 10);
		roman_numerals.put("IX", 9);
		roman_numerals.put("V", 5);
		roman_numerals.put("IV", 4);
		roman_numerals.put("I", 1);
	}
	
	public static boolean hasColorCode(String msg) {
		if (msg == null)
			return false;
		return msg.contains("§");
	}

	public static boolean shouldSaveItemOnRespawn(ItemStack item) {
		if (item == null)
			return false;
		if (!item.hasItemMeta())
			return false;
		final ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasDisplayName() && hasColorCode(itemMeta.getDisplayName());
	}

	// http://stackoverflow.com/questions/9073150/converting-roman-numerals-to-decimal
	public static int romanToArabic(java.lang.String romanNumber) {
		int decimal = 0;
		int lastNumber = 0;
		String romanNumeral = romanNumber.toUpperCase();
		/*
		 * operation to be performed on upper cases even if user enters roman
		 * values in lower case chars
		 */
		for (int x = romanNumeral.length() - 1; x >= 0; x--) {
			char convertToDecimal = romanNumeral.charAt(x);

			switch (convertToDecimal) {
			case 'M':
				decimal = processDecimal(1000, lastNumber, decimal);
				lastNumber = 1000;
				break;

			case 'D':
				decimal = processDecimal(500, lastNumber, decimal);
				lastNumber = 500;
				break;

			case 'C':
				decimal = processDecimal(100, lastNumber, decimal);
				lastNumber = 100;
				break;

			case 'L':
				decimal = processDecimal(50, lastNumber, decimal);
				lastNumber = 50;
				break;

			case 'X':
				decimal = processDecimal(10, lastNumber, decimal);
				lastNumber = 10;
				break;

			case 'V':
				decimal = processDecimal(5, lastNumber, decimal);
				lastNumber = 5;
				break;

			case 'I':
				decimal = processDecimal(1, lastNumber, decimal);
				lastNumber = 1;
				break;
			}
		}
		return decimal;
	}

	// http://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
//	/**
//	 * 
//	 * @param number
//	 * @return
//	 * @throws IllegalArgumentException
//	 */
//	public static int romanToArabic(String number) throws IllegalArgumentException {
//		if (number == null || number.isEmpty())
//			return 0;
//		if (number.startsWith("M"))
//			return 1000 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("CM"))
//			return 900 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("D"))
//			return 500 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("CD"))
//			return 400 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("C"))
//			return 100 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("XC"))
//			return 90 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("L"))
//			return 50 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("XL"))
//			return 40 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("X"))
//			return 10 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("IX"))
//			return 9 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("V"))
//			return 5 + romanToArabic(number.substring(0, 1));
//		if (number.startsWith("IV"))
//			return 4 + romanToArabic(number.substring(0, 2));
//		if (number.startsWith("I"))
//			return 1 + romanToArabic(number.substring(0, 1));
//		throw new IllegalArgumentException("Could not convert \"" + number + "\" to arabic number!");
//	}

	public static String arabicToRoman(int number) {
		String res = "";
		for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
			int matches = number / entry.getValue();
			res += repeat(entry.getKey(), matches);
			number = number % entry.getValue();
		}
		return res;

	}

	private static String repeat(String s, int n) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	private static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
		if (lastNumber > decimal) {
			return lastDecimal - decimal;
		} else {
			return lastDecimal + decimal;
		}
	}
}
