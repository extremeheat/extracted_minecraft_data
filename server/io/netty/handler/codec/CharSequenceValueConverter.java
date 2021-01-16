package io.netty.handler.codec;

import io.netty.util.AsciiString;
import io.netty.util.internal.PlatformDependent;
import java.text.ParseException;
import java.util.Date;

public class CharSequenceValueConverter implements ValueConverter<CharSequence> {
   public static final CharSequenceValueConverter INSTANCE = new CharSequenceValueConverter();
   private static final AsciiString TRUE_ASCII = new AsciiString("true");

   public CharSequenceValueConverter() {
      super();
   }

   public CharSequence convertObject(Object var1) {
      return (CharSequence)(var1 instanceof CharSequence ? (CharSequence)var1 : var1.toString());
   }

   public CharSequence convertInt(int var1) {
      return String.valueOf(var1);
   }

   public CharSequence convertLong(long var1) {
      return String.valueOf(var1);
   }

   public CharSequence convertDouble(double var1) {
      return String.valueOf(var1);
   }

   public CharSequence convertChar(char var1) {
      return String.valueOf(var1);
   }

   public CharSequence convertBoolean(boolean var1) {
      return String.valueOf(var1);
   }

   public CharSequence convertFloat(float var1) {
      return String.valueOf(var1);
   }

   public boolean convertToBoolean(CharSequence var1) {
      return AsciiString.contentEqualsIgnoreCase(var1, TRUE_ASCII);
   }

   public CharSequence convertByte(byte var1) {
      return String.valueOf(var1);
   }

   public byte convertToByte(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).byteAt(0) : Byte.parseByte(var1.toString());
   }

   public char convertToChar(CharSequence var1) {
      return var1.charAt(0);
   }

   public CharSequence convertShort(short var1) {
      return String.valueOf(var1);
   }

   public short convertToShort(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).parseShort() : Short.parseShort(var1.toString());
   }

   public int convertToInt(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).parseInt() : Integer.parseInt(var1.toString());
   }

   public long convertToLong(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).parseLong() : Long.parseLong(var1.toString());
   }

   public CharSequence convertTimeMillis(long var1) {
      return DateFormatter.format(new Date(var1));
   }

   public long convertToTimeMillis(CharSequence var1) {
      Date var2 = DateFormatter.parseHttpDate(var1);
      if (var2 == null) {
         PlatformDependent.throwException(new ParseException("header can't be parsed into a Date: " + var1, 0));
         return 0L;
      } else {
         return var2.getTime();
      }
   }

   public float convertToFloat(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).parseFloat() : Float.parseFloat(var1.toString());
   }

   public double convertToDouble(CharSequence var1) {
      return var1 instanceof AsciiString ? ((AsciiString)var1).parseDouble() : Double.parseDouble(var1.toString());
   }
}
