package joptsimple;

import java.util.Collections;
import java.util.List;

class UnconfiguredOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnconfiguredOptionException(String var1) {
      this(Collections.singletonList(var1));
   }

   UnconfiguredOptionException(List<String> var1) {
      super(var1);
   }

   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
