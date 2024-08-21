package com.mojang.blaze3d.preprocessor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.util.StringUtil;

public abstract class GlslPreprocessor {
   private static final String C_COMMENT = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
   private static final String LINE_COMMENT = "//[^\\v]*";
   private static final Pattern REGEX_MOJ_IMPORT = Pattern.compile(
      "(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))"
   );
   private static final Pattern REGEX_VERSION = Pattern.compile("(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b");
   private static final Pattern REGEX_ENDS_WITH_WHITESPACE = Pattern.compile("(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

   public GlslPreprocessor() {
      super();
   }

   public List<String> process(String var1) {
      GlslPreprocessor.Context var2 = new GlslPreprocessor.Context();
      List var3 = this.processImports(var1, var2, "");
      var3.set(0, this.setVersion((String)var3.get(0), var2.glslVersion));
      return var3;
   }

   private List<String> processImports(String var1, GlslPreprocessor.Context var2, String var3) {
      int var4 = var2.sourceId;
      int var5 = 0;
      String var6 = "";
      ArrayList var7 = Lists.newArrayList();
      Matcher var8 = REGEX_MOJ_IMPORT.matcher(var1);

      while (var8.find()) {
         if (!isDirectiveDisabled(var1, var8, var5)) {
            String var9 = var8.group(2);
            boolean var10 = var9 != null;
            if (!var10) {
               var9 = var8.group(3);
            }

            if (var9 != null) {
               String var11 = var1.substring(var5, var8.start(1));
               String var12 = var3 + var9;
               String var13 = this.applyImport(var10, var12);
               if (!Strings.isNullOrEmpty(var13)) {
                  if (!StringUtil.endsWithNewLine(var13)) {
                     var13 = var13 + System.lineSeparator();
                  }

                  var2.sourceId++;
                  int var14 = var2.sourceId;
                  List var15 = this.processImports(var13, var2, var10 ? FileUtil.getFullResourcePath(var12) : "");
                  var15.set(0, String.format(Locale.ROOT, "#line %d %d\n%s", 0, var14, this.processVersions((String)var15.get(0), var2)));
                  if (!StringUtil.isBlank(var11)) {
                     var7.add(var11);
                  }

                  var7.addAll(var15);
               } else {
                  String var17 = var10 ? String.format(Locale.ROOT, "/*#moj_import \"%s\"*/", var9) : String.format(Locale.ROOT, "/*#moj_import <%s>*/", var9);
                  var7.add(var6 + var11 + var17);
               }

               int var18 = StringUtil.lineCount(var1.substring(0, var8.end(1)));
               var6 = String.format(Locale.ROOT, "#line %d %d", var18, var4);
               var5 = var8.end(1);
            }
         }
      }

      String var16 = var1.substring(var5);
      if (!StringUtil.isBlank(var16)) {
         var7.add(var6 + var16);
      }

      return var7;
   }

   private String processVersions(String var1, GlslPreprocessor.Context var2) {
      Matcher var3 = REGEX_VERSION.matcher(var1);
      if (var3.find() && isDirectiveEnabled(var1, var3)) {
         var2.glslVersion = Math.max(var2.glslVersion, Integer.parseInt(var3.group(2)));
         return var1.substring(0, var3.start(1)) + "/*" + var1.substring(var3.start(1), var3.end(1)) + "*/" + var1.substring(var3.end(1));
      } else {
         return var1;
      }
   }

   private String setVersion(String var1, int var2) {
      Matcher var3 = REGEX_VERSION.matcher(var1);
      return var3.find() && isDirectiveEnabled(var1, var3)
         ? var1.substring(0, var3.start(2)) + Math.max(var2, Integer.parseInt(var3.group(2))) + var1.substring(var3.end(2))
         : var1;
   }

   private static boolean isDirectiveEnabled(String var0, Matcher var1) {
      return !isDirectiveDisabled(var0, var1, 0);
   }

   private static boolean isDirectiveDisabled(String var0, Matcher var1, int var2) {
      int var3 = var1.start() - var2;
      if (var3 == 0) {
         return false;
      } else {
         Matcher var4 = REGEX_ENDS_WITH_WHITESPACE.matcher(var0.substring(var2, var1.start()));
         if (!var4.find()) {
            return true;
         } else {
            int var5 = var4.end(1);
            return var5 == var1.start();
         }
      }
   }

   @Nullable
   public abstract String applyImport(boolean var1, String var2);

   public static String injectDefines(String var0, ShaderDefines var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         int var2 = var0.indexOf(10);
         int var3 = var2 + 1;
         return var0.substring(0, var3) + var1.asSourceDirectives() + "#line 1 0\n" + var0.substring(var3);
      }
   }

   static final class Context {
      int glslVersion;
      int sourceId;

      Context() {
         super();
      }
   }
}
