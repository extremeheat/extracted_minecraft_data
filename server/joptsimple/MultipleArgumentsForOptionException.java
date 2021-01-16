package joptsimple;

import java.util.Collection;
import java.util.Collections;

class MultipleArgumentsForOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   MultipleArgumentsForOptionException(OptionSpec<?> var1) {
      super((Collection)Collections.singleton(var1));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
