package joptsimple;

import java.util.Collections;
import java.util.Locale;
import joptsimple.internal.Messages;

class AlternativeLongOptionSpec extends ArgumentAcceptingOptionSpec<String> {
   AlternativeLongOptionSpec() {
      super(Collections.singletonList("W"), true, Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "description"));
      this.describedAs(Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "arg.description"));
   }

   protected void detectOptionArgument(OptionParser var1, ArgumentList var2, OptionSet var3) {
      if (!var2.hasMore()) {
         throw new OptionMissingRequiredArgumentException(this);
      } else {
         var2.treatNextAsLongOption();
      }
   }
}
