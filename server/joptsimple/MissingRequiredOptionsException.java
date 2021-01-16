package joptsimple;

import java.util.Collection;
import java.util.List;

class MissingRequiredOptionsException extends OptionException {
   private static final long serialVersionUID = -1L;

   protected MissingRequiredOptionsException(List<? extends OptionSpec<?>> var1) {
      super((Collection)var1);
   }

   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
