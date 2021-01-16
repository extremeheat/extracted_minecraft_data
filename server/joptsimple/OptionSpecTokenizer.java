package joptsimple;

import java.util.NoSuchElementException;

class OptionSpecTokenizer {
   private static final char POSIXLY_CORRECT_MARKER = '+';
   private static final char HELP_MARKER = '*';
   private String specification;
   private int index;

   OptionSpecTokenizer(String var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("null option specification");
      } else {
         this.specification = var1;
      }
   }

   boolean hasMore() {
      return this.index < this.specification.length();
   }

   AbstractOptionSpec<?> next() {
      if (!this.hasMore()) {
         throw new NoSuchElementException();
      } else {
         String var1 = String.valueOf(this.specification.charAt(this.index));
         ++this.index;
         if ("W".equals(var1)) {
            AbstractOptionSpec var2 = this.handleReservedForExtensionsToken();
            if (var2 != null) {
               return var2;
            }
         }

         ParserRules.ensureLegalOption(var1);
         Object var4;
         if (this.hasMore()) {
            boolean var3 = false;
            if (this.specification.charAt(this.index) == '*') {
               var3 = true;
               ++this.index;
            }

            var4 = this.hasMore() && this.specification.charAt(this.index) == ':' ? this.handleArgumentAcceptingOption(var1) : new NoArgumentOptionSpec(var1);
            if (var3) {
               ((AbstractOptionSpec)var4).forHelp();
            }
         } else {
            var4 = new NoArgumentOptionSpec(var1);
         }

         return (AbstractOptionSpec)var4;
      }
   }

   void configure(OptionParser var1) {
      this.adjustForPosixlyCorrect(var1);

      while(this.hasMore()) {
         var1.recognize(this.next());
      }

   }

   private void adjustForPosixlyCorrect(OptionParser var1) {
      if ('+' == this.specification.charAt(0)) {
         var1.posixlyCorrect(true);
         this.specification = this.specification.substring(1);
      }

   }

   private AbstractOptionSpec<?> handleReservedForExtensionsToken() {
      if (!this.hasMore()) {
         return new NoArgumentOptionSpec("W");
      } else if (this.specification.charAt(this.index) == ';') {
         ++this.index;
         return new AlternativeLongOptionSpec();
      } else {
         return null;
      }
   }

   private AbstractOptionSpec<?> handleArgumentAcceptingOption(String var1) {
      ++this.index;
      if (this.hasMore() && this.specification.charAt(this.index) == ':') {
         ++this.index;
         return new OptionalArgumentOptionSpec(var1);
      } else {
         return new RequiredArgumentOptionSpec(var1);
      }
   }
}
