package org.apache.logging.log4j.core.util;

import java.util.ArrayList;
import java.util.List;

public enum ExtensionLanguageMapping {
   JS("js", "JavaScript"),
   JAVASCRIPT("javascript", "JavaScript"),
   GVY("gvy", "Groovy"),
   GROOVY("groovy", "Groovy"),
   BSH("bsh", "beanshell"),
   BEANSHELL("beanshell", "beanshell"),
   JY("jy", "jython"),
   JYTHON("jython", "jython"),
   FTL("ftl", "freemarker"),
   FREEMARKER("freemarker", "freemarker"),
   VM("vm", "velocity"),
   VELOCITY("velocity", "velocity"),
   AWK("awk", "awk"),
   EJS("ejs", "ejs"),
   TCL("tcl", "tcl"),
   HS("hs", "jaskell"),
   JELLY("jelly", "jelly"),
   JEP("jep", "jep"),
   JEXL("jexl", "jexl"),
   JEXL2("jexl2", "jexl2"),
   RB("rb", "ruby"),
   RUBY("ruby", "ruby"),
   JUDO("judo", "judo"),
   JUDI("judi", "judo"),
   SCALA("scala", "scala"),
   CLJ("clj", "Clojure");

   private final String extension;
   private final String language;

   private ExtensionLanguageMapping(String var3, String var4) {
      this.extension = var3;
      this.language = var4;
   }

   public String getExtension() {
      return this.extension;
   }

   public String getLanguage() {
      return this.language;
   }

   public static ExtensionLanguageMapping getByExtension(String var0) {
      ExtensionLanguageMapping[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ExtensionLanguageMapping var4 = var1[var3];
         if (var4.extension.equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   public static List<ExtensionLanguageMapping> getByLanguage(String var0) {
      ArrayList var1 = new ArrayList();
      ExtensionLanguageMapping[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ExtensionLanguageMapping var5 = var2[var4];
         if (var5.language.equals(var0)) {
            var1.add(var5);
         }
      }

      return var1;
   }
}
