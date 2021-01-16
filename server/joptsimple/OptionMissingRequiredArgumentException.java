package joptsimple;

import java.util.Arrays;
import java.util.Collection;

class OptionMissingRequiredArgumentException extends OptionException {
   private static final long serialVersionUID = -1L;

   OptionMissingRequiredArgumentException(OptionSpec<?> var1) {
      super((Collection)Arrays.asList(var1));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
