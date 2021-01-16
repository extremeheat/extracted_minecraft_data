package org.apache.logging.log4j.core.lookup;

import java.util.Locale;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "java",
   category = "Lookup"
)
public class JavaLookup extends AbstractLookup {
   private final SystemPropertiesLookup spLookup = new SystemPropertiesLookup();

   public JavaLookup() {
      super();
   }

   public String getHardware() {
      return "processors: " + Runtime.getRuntime().availableProcessors() + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model") + this.getSystemProperty(", instruction sets: ", "sun.cpu.isalist");
   }

   public String getLocale() {
      return "default locale: " + Locale.getDefault() + ", platform encoding: " + this.getSystemProperty("file.encoding");
   }

   public String getOperatingSystem() {
      return this.getSystemProperty("os.name") + " " + this.getSystemProperty("os.version") + this.getSystemProperty(" ", "sun.os.patch.level") + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model");
   }

   public String getRuntime() {
      return this.getSystemProperty("java.runtime.name") + " (build " + this.getSystemProperty("java.runtime.version") + ") from " + this.getSystemProperty("java.vendor");
   }

   private String getSystemProperty(String var1) {
      return this.spLookup.lookup(var1);
   }

   private String getSystemProperty(String var1, String var2) {
      String var3 = this.getSystemProperty(var2);
      return Strings.isEmpty(var3) ? "" : var1 + var3;
   }

   public String getVirtualMachine() {
      return this.getSystemProperty("java.vm.name") + " (build " + this.getSystemProperty("java.vm.version") + ", " + this.getSystemProperty("java.vm.info") + ")";
   }

   public String lookup(LogEvent var1, String var2) {
      byte var4 = -1;
      switch(var2.hashCode()) {
      case -1097462182:
         if (var2.equals("locale")) {
            var4 = 5;
         }
         break;
      case 3343:
         if (var2.equals("hw")) {
            var4 = 4;
         }
         break;
      case 3556:
         if (var2.equals("os")) {
            var4 = 3;
         }
         break;
      case 3767:
         if (var2.equals("vm")) {
            var4 = 2;
         }
         break;
      case 351608024:
         if (var2.equals("version")) {
            var4 = 0;
         }
         break;
      case 1550962648:
         if (var2.equals("runtime")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return "Java version " + this.getSystemProperty("java.version");
      case 1:
         return this.getRuntime();
      case 2:
         return this.getVirtualMachine();
      case 3:
         return this.getOperatingSystem();
      case 4:
         return this.getHardware();
      case 5:
         return this.getLocale();
      default:
         throw new IllegalArgumentException(var2);
      }
   }
}
