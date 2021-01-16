package joptsimple;

import java.util.Collections;

class OptionArgumentConversionException extends OptionException {
   private static final long serialVersionUID = -1L;
   private final String argument;

   OptionArgumentConversionException(OptionSpec<?> var1, String var2, Throwable var3) {
      super(Collections.singleton(var1), var3);
      this.argument = var2;
   }

   Object[] messageArguments() {
      return new Object[]{this.argument, this.singleOptionString()};
   }
}
