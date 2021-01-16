package joptsimple;

import java.util.Collections;
import java.util.List;

class NoArgumentOptionSpec extends AbstractOptionSpec<Void> {
   NoArgumentOptionSpec(String var1) {
      this(Collections.singletonList(var1), "");
   }

   NoArgumentOptionSpec(List<String> var1, String var2) {
      super(var1, var2);
   }

   void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4) {
      var3.add(this);
   }

   public boolean acceptsArguments() {
      return false;
   }

   public boolean requiresArgument() {
      return false;
   }

   public boolean isRequired() {
      return false;
   }

   public String argumentDescription() {
      return "";
   }

   public String argumentTypeIndicator() {
      return "";
   }

   protected Void convert(String var1) {
      return null;
   }

   public List<Void> defaultValues() {
      return Collections.emptyList();
   }
}
