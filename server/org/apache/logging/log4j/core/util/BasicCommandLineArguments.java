package org.apache.logging.log4j.core.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class BasicCommandLineArguments {
   @Parameter(
      names = {"--help", "-?", "-h"},
      help = true,
      description = "Prints this help."
   )
   private boolean help;

   public BasicCommandLineArguments() {
      super();
   }

   public static <T extends BasicCommandLineArguments> T parseCommandLine(String[] var0, Class<?> var1, T var2) {
      JCommander var3 = new JCommander(var2);
      var3.setProgramName(var1.getName());
      var3.setCaseSensitiveOptions(false);
      var3.parse(var0);
      if (var2.isHelp()) {
         var3.usage();
      }

      return var2;
   }

   public boolean isHelp() {
      return this.help;
   }

   public void setHelp(boolean var1) {
      this.help = var1;
   }
}
