package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
   RequirementsStrategy AND = var0 -> {
      String[][] var1 = new String[var0.size()][];
      int var2 = 0;

      for(String var4 : var0) {
         var1[var2++] = new String[]{var4};
      }

      return var1;
   };
   RequirementsStrategy OR = var0 -> new String[][]{var0.toArray(new String[0])};

   String[][] createRequirements(Collection<String> var1);
}
