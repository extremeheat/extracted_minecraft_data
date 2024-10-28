package net.minecraft.commands.functions;

import com.google.common.collect.ImmutableList;
import java.util.List;

public record StringTemplate(List<String> segments, List<String> variables) {
   public StringTemplate(List<String> var1, List<String> var2) {
      super();
      this.segments = var1;
      this.variables = var2;
   }

   public static StringTemplate fromString(String var0, int var1) {
      ImmutableList.Builder var2 = ImmutableList.builder();
      ImmutableList.Builder var3 = ImmutableList.builder();
      int var4 = var0.length();
      int var5 = 0;
      int var6 = var0.indexOf(36);

      while(true) {
         while(var6 != -1) {
            if (var6 != var4 - 1 && var0.charAt(var6 + 1) == '(') {
               var2.add(var0.substring(var5, var6));
               int var7 = var0.indexOf(41, var6 + 1);
               if (var7 == -1) {
                  throw new IllegalArgumentException("Unterminated macro variable in macro '" + var0 + "' on line " + var1);
               }

               String var8 = var0.substring(var6 + 2, var7);
               if (!isValidVariableName(var8)) {
                  throw new IllegalArgumentException("Invalid macro variable name '" + var8 + "' on line " + var1);
               }

               var3.add(var8);
               var5 = var7 + 1;
               var6 = var0.indexOf(36, var5);
            } else {
               var6 = var0.indexOf(36, var6 + 1);
            }
         }

         if (var5 == 0) {
            throw new IllegalArgumentException("Macro without variables on line " + var1);
         }

         if (var5 != var4) {
            var2.add(var0.substring(var5));
         }

         return new StringTemplate(var2.build(), var3.build());
      }
   }

   private static boolean isValidVariableName(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (!Character.isLetterOrDigit(var2) && var2 != '_') {
            return false;
         }
      }

      return true;
   }

   public String substitute(List<String> var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < this.variables.size(); ++var3) {
         var2.append((String)this.segments.get(var3)).append((String)var1.get(var3));
         CommandFunction.checkCommandLineLength(var2);
      }

      if (this.segments.size() > this.variables.size()) {
         var2.append((String)this.segments.get(this.segments.size() - 1));
      }

      CommandFunction.checkCommandLineLength(var2);
      return var2.toString();
   }

   public List<String> segments() {
      return this.segments;
   }

   public List<String> variables() {
      return this.variables;
   }
}
