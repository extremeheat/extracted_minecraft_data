package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StringReader implements ImmutableStringReader {
   private static final char SYNTAX_ESCAPE = '\\';
   private static final char SYNTAX_DOUBLE_QUOTE = '"';
   private static final char SYNTAX_SINGLE_QUOTE = '\'';
   private final String string;
   private int cursor;

   public StringReader(StringReader var1) {
      super();
      this.string = var1.string;
      this.cursor = var1.cursor;
   }

   public StringReader(String var1) {
      super();
      this.string = var1;
   }

   public String getString() {
      return this.string;
   }

   public void setCursor(int var1) {
      this.cursor = var1;
   }

   public int getRemainingLength() {
      return this.string.length() - this.cursor;
   }

   public int getTotalLength() {
      return this.string.length();
   }

   public int getCursor() {
      return this.cursor;
   }

   public String getRead() {
      return this.string.substring(0, this.cursor);
   }

   public String getRemaining() {
      return this.string.substring(this.cursor);
   }

   public boolean canRead(int var1) {
      return this.cursor + var1 <= this.string.length();
   }

   public boolean canRead() {
      return this.canRead(1);
   }

   public char peek() {
      return this.string.charAt(this.cursor);
   }

   public char peek(int var1) {
      return this.string.charAt(this.cursor + var1);
   }

   public char read() {
      return this.string.charAt(this.cursor++);
   }

   public void skip() {
      ++this.cursor;
   }

   public static boolean isAllowedNumber(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 == '.' || var0 == '-';
   }

   public static boolean isQuotedStringStart(char var0) {
      return var0 == '"' || var0 == '\'';
   }

   public void skipWhitespace() {
      while(this.canRead() && Character.isWhitespace(this.peek())) {
         this.skip();
      }

   }

   public int readInt() throws CommandSyntaxException {
      int var1 = this.cursor;

      while(this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(this);
      } else {
         try {
            return Integer.parseInt(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(this, var2);
         }
      }
   }

   public long readLong() throws CommandSyntaxException {
      int var1 = this.cursor;

      while(this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(this);
      } else {
         try {
            return Long.parseLong(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(this, var2);
         }
      }
   }

   public double readDouble() throws CommandSyntaxException {
      int var1 = this.cursor;

      while(this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(this);
      } else {
         try {
            return Double.parseDouble(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(this, var2);
         }
      }
   }

   public float readFloat() throws CommandSyntaxException {
      int var1 = this.cursor;

      while(this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(this);
      } else {
         try {
            return Float.parseFloat(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(this, var2);
         }
      }
   }

   public static boolean isAllowedInUnquotedString(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == '-' || var0 == '.' || var0 == '+';
   }

   public String readUnquotedString() {
      int var1 = this.cursor;

      while(this.canRead() && isAllowedInUnquotedString(this.peek())) {
         this.skip();
      }

      return this.string.substring(var1, this.cursor);
   }

   public String readQuotedString() throws CommandSyntaxException {
      if (!this.canRead()) {
         return "";
      } else {
         char var1 = this.peek();
         if (!isQuotedStringStart(var1)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(this);
         } else {
            this.skip();
            return this.readStringUntil(var1);
         }
      }
   }

   public String readStringUntil(char var1) throws CommandSyntaxException {
      StringBuilder var2 = new StringBuilder();
      boolean var3 = false;

      while(this.canRead()) {
         char var4 = this.read();
         if (var3) {
            if (var4 != var1 && var4 != '\\') {
               this.setCursor(this.getCursor() - 1);
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext(this, String.valueOf(var4));
            }

            var2.append(var4);
            var3 = false;
         } else if (var4 == '\\') {
            var3 = true;
         } else {
            if (var4 == var1) {
               return var2.toString();
            }

            var2.append(var4);
         }
      }

      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(this);
   }

   public String readString() throws CommandSyntaxException {
      if (!this.canRead()) {
         return "";
      } else {
         char var1 = this.peek();
         if (isQuotedStringStart(var1)) {
            this.skip();
            return this.readStringUntil(var1);
         } else {
            return this.readUnquotedString();
         }
      }
   }

   public boolean readBoolean() throws CommandSyntaxException {
      int var1 = this.cursor;
      String var2 = this.readString();
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(this);
      } else if (var2.equals("true")) {
         return true;
      } else if (var2.equals("false")) {
         return false;
      } else {
         this.cursor = var1;
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().createWithContext(this, var2);
      }
   }

   public void expect(char var1) throws CommandSyntaxException {
      if (this.canRead() && this.peek() == var1) {
         this.skip();
      } else {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(this, String.valueOf(var1));
      }
   }
}
