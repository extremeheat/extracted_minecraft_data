package net.minecraft.commands.functions;

import com.google.common.collect.ImmutableList;
import java.util.List;

public record StringTemplate(List<String> segments, List<String> variables) {
   public StringTemplate(List<String> var1, List<String> var2) {
      super();
      this.segments = var1;
      this.variables = var2;
   }

   public static StringTemplate fromString(String var0) {
      ImmutableList.Builder var1 = ImmutableList.builder();
      ImmutableList.Builder var2 = ImmutableList.builder();
      int var3 = var0.length();
      int var4 = 0;
      int var5 = var0.indexOf(36);

      while(var5 != -1) {
         if (var5 != var3 - 1 && var0.charAt(var5 + 1) == '(') {
            var1.add(var0.substring(var4, var5));
            int var6 = var0.indexOf(41, var5 + 1);
            if (var6 == -1) {
               throw new IllegalArgumentException("Unterminated macro variable");
            }

            String var7 = var0.substring(var5 + 2, var6);
            if (!isValidVariableName(var7)) {
               throw new IllegalArgumentException("Invalid macro variable name '" + var7 + "'");
            }

            var2.add(var7);
            var4 = var6 + 1;
            var5 = var0.indexOf(36, var4);
         } else {
            var5 = var0.indexOf(36, var5 + 1);
         }
      }

      if (var4 == 0) {
         throw new IllegalArgumentException("No variables in macro");
      } else {
         if (var4 != var3) {
            var1.add(var0.substring(var4));
         }

         return new StringTemplate(var1.build(), var2.build());
      }
   }

   public static boolean isValidVariableName(String var0) {
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
         var2.append((String)this.segments.getLast());
      }

      CommandFunction.checkCommandLineLength(var2);
      return var2.toString();
   }
}
