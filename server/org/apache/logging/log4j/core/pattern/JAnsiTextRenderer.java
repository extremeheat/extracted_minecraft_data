package org.apache.logging.log4j.core.pattern;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.status.StatusLogger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderer.Code;

public final class JAnsiTextRenderer implements TextRenderer {
   public static final Map<String, Code[]> DefaultExceptionStyleMap;
   static final Map<String, Code[]> DefaultMessageStyleMap;
   private static final Map<String, Map<String, Code[]>> PrefedinedStyleMaps;
   private final String beginToken;
   private final int beginTokenLen;
   private final String endToken;
   private final int endTokenLen;
   private final Map<String, Code[]> styleMap;

   private static void put(Map<String, Code[]> var0, String var1, Code... var2) {
      var0.put(var1, var2);
   }

   public JAnsiTextRenderer(String[] var1, Map<String, Code[]> var2) {
      super();
      String var3 = "@|";
      String var4 = "|@";
      Object var5;
      if (var1.length > 1) {
         String var6 = var1[1];
         String[] var7 = var6.split(" ");
         var5 = new HashMap(var7.length + var2.size());
         ((Map)var5).putAll(var2);
         String[] var8 = var7;
         int var9 = var7.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String var11 = var8[var10];
            String[] var12 = var11.split("=");
            if (var12.length != 2) {
               StatusLogger.getLogger().warn("{} parsing style \"{}\", expected format: StyleName=Code(,Code)*", this.getClass().getSimpleName(), var11);
            } else {
               String var13 = var12[0];
               String var14 = var12[1];
               String[] var15 = var14.split(",");
               if (var15.length == 0) {
                  StatusLogger.getLogger().warn("{} parsing style \"{}\", expected format: StyleName=Code(,Code)*", this.getClass().getSimpleName(), var11);
               } else {
                  byte var17 = -1;
                  switch(var13.hashCode()) {
                  case -1967473866:
                     if (var13.equals("StyleMapName")) {
                        var17 = 2;
                     }
                     break;
                  case -1199624464:
                     if (var13.equals("BeginToken")) {
                        var17 = 0;
                     }
                     break;
                  case 1779889662:
                     if (var13.equals("EndToken")) {
                        var17 = 1;
                     }
                  }

                  Code[] var20;
                  int var21;
                  switch(var17) {
                  case 0:
                     var3 = var15[0];
                     continue;
                  case 1:
                     var4 = var15[0];
                     continue;
                  case 2:
                     String var18 = var15[0];
                     Map var19 = (Map)PrefedinedStyleMaps.get(var18);
                     if (var19 != null) {
                        ((Map)var5).putAll(var19);
                     } else {
                        StatusLogger.getLogger().warn("Unknown predefined map name {}, pick one of {}", var18, (Object)null);
                     }
                     continue;
                  default:
                     var20 = new Code[var15.length];
                     var21 = 0;
                  }

                  while(var21 < var20.length) {
                     var20[var21] = this.toCode(var15[var21]);
                     ++var21;
                  }

                  ((Map)var5).put(var13, var20);
               }
            }
         }
      } else {
         var5 = var2;
      }

      this.styleMap = (Map)var5;
      this.beginToken = var3;
      this.endToken = var4;
      this.beginTokenLen = var3.length();
      this.endTokenLen = var4.length();
   }

   public Map<String, Code[]> getStyleMap() {
      return this.styleMap;
   }

   private void render(Ansi var1, Code var2) {
      if (var2.isColor()) {
         if (var2.isBackground()) {
            var1.bg(var2.getColor());
         } else {
            var1.fg(var2.getColor());
         }
      } else if (var2.isAttribute()) {
         var1.a(var2.getAttribute());
      }

   }

   private void render(Ansi var1, Code... var2) {
      Code[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Code var6 = var3[var5];
         this.render(var1, var6);
      }

   }

   private String render(String var1, String... var2) {
      Ansi var3 = Ansi.ansi();
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         Code[] var8 = (Code[])this.styleMap.get(var7);
         if (var8 != null) {
            this.render(var3, var8);
         } else {
            this.render(var3, this.toCode(var7));
         }
      }

      return var3.a(var1).reset().toString();
   }

   public void render(String var1, StringBuilder var2, String var3) throws IllegalArgumentException {
      var2.append(this.render(var1, var3));
   }

   public void render(StringBuilder var1, StringBuilder var2) throws IllegalArgumentException {
      int var3 = 0;

      while(true) {
         int var4 = var1.indexOf(this.beginToken, var3);
         if (var4 == -1) {
            if (var3 == 0) {
               var2.append(var1);
               return;
            }

            var2.append(var1.substring(var3, var1.length()));
            return;
         }

         var2.append(var1.substring(var3, var4));
         int var5 = var1.indexOf(this.endToken, var4);
         if (var5 == -1) {
            var2.append(var1);
            return;
         }

         var4 += this.beginTokenLen;
         String var6 = var1.substring(var4, var5);
         String[] var7 = var6.split(" ", 2);
         if (var7.length == 1) {
            var2.append(var1);
            return;
         }

         String var8 = this.render(var7[1], var7[0].split(","));
         var2.append(var8);
         var3 = var5 + this.endTokenLen;
      }
   }

   private Code toCode(String var1) {
      return Code.valueOf(var1.toUpperCase(Locale.ENGLISH));
   }

   public String toString() {
      return "JAnsiMessageRenderer [beginToken=" + this.beginToken + ", beginTokenLen=" + this.beginTokenLen + ", endToken=" + this.endToken + ", endTokenLen=" + this.endTokenLen + ", styleMap=" + this.styleMap + "]";
   }

   static {
      HashMap var0 = new HashMap();
      HashMap var1 = new HashMap();
      put(var1, "Prefix", Code.WHITE);
      put(var1, "Name", Code.BG_RED, Code.WHITE);
      put(var1, "NameMessageSeparator", Code.BG_RED, Code.WHITE);
      put(var1, "Message", Code.BG_RED, Code.WHITE, Code.BOLD);
      put(var1, "At", Code.WHITE);
      put(var1, "CauseLabel", Code.WHITE);
      put(var1, "Text", Code.WHITE);
      put(var1, "More", Code.WHITE);
      put(var1, "Suppressed", Code.WHITE);
      put(var1, "StackTraceElement.ClassName", Code.YELLOW);
      put(var1, "StackTraceElement.ClassMethodSeparator", Code.YELLOW);
      put(var1, "StackTraceElement.MethodName", Code.YELLOW);
      put(var1, "StackTraceElement.NativeMethod", Code.YELLOW);
      put(var1, "StackTraceElement.FileName", Code.RED);
      put(var1, "StackTraceElement.LineNumber", Code.RED);
      put(var1, "StackTraceElement.Container", Code.RED);
      put(var1, "StackTraceElement.ContainerSeparator", Code.WHITE);
      put(var1, "StackTraceElement.UnknownSource", Code.RED);
      put(var1, "ExtraClassInfo.Inexact", Code.YELLOW);
      put(var1, "ExtraClassInfo.Container", Code.YELLOW);
      put(var1, "ExtraClassInfo.ContainerSeparator", Code.YELLOW);
      put(var1, "ExtraClassInfo.Location", Code.YELLOW);
      put(var1, "ExtraClassInfo.Version", Code.YELLOW);
      DefaultExceptionStyleMap = Collections.unmodifiableMap(var1);
      var0.put("Spock", DefaultExceptionStyleMap);
      var1 = new HashMap();
      put(var1, "Prefix", Code.WHITE);
      put(var1, "Name", Code.BG_RED, Code.YELLOW, Code.BOLD);
      put(var1, "NameMessageSeparator", Code.BG_RED, Code.YELLOW);
      put(var1, "Message", Code.BG_RED, Code.WHITE, Code.BOLD);
      put(var1, "At", Code.WHITE);
      put(var1, "CauseLabel", Code.WHITE);
      put(var1, "Text", Code.WHITE);
      put(var1, "More", Code.WHITE);
      put(var1, "Suppressed", Code.WHITE);
      put(var1, "StackTraceElement.ClassName", Code.BG_RED, Code.WHITE);
      put(var1, "StackTraceElement.ClassMethodSeparator", Code.BG_RED, Code.YELLOW);
      put(var1, "StackTraceElement.MethodName", Code.BG_RED, Code.YELLOW);
      put(var1, "StackTraceElement.NativeMethod", Code.BG_RED, Code.YELLOW);
      put(var1, "StackTraceElement.FileName", Code.RED);
      put(var1, "StackTraceElement.LineNumber", Code.RED);
      put(var1, "StackTraceElement.Container", Code.RED);
      put(var1, "StackTraceElement.ContainerSeparator", Code.WHITE);
      put(var1, "StackTraceElement.UnknownSource", Code.RED);
      put(var1, "ExtraClassInfo.Inexact", Code.YELLOW);
      put(var1, "ExtraClassInfo.Container", Code.WHITE);
      put(var1, "ExtraClassInfo.ContainerSeparator", Code.WHITE);
      put(var1, "ExtraClassInfo.Location", Code.YELLOW);
      put(var1, "ExtraClassInfo.Version", Code.YELLOW);
      var0.put("Kirk", Collections.unmodifiableMap(var1));
      var1 = new HashMap();
      DefaultMessageStyleMap = Collections.unmodifiableMap(var1);
      PrefedinedStyleMaps = Collections.unmodifiableMap(var0);
   }
}
