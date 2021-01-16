package joptsimple;

import java.util.Collections;

class UnrecognizedOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnrecognizedOptionException(String var1) {
      super(Collections.singletonList(var1));
   }

   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
