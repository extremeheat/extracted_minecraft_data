package net.minecraft.advancements;

import java.util.Collection;
import java.util.Iterator;

public interface RequirementsStrategy {
   RequirementsStrategy AND = (var0) -> {
      String[][] var1 = new String[var0.size()][];
      int var2 = 0;

      String var4;
      for(Iterator var3 = var0.iterator(); var3.hasNext(); var1[var2++] = new String[]{var4}) {
         var4 = (String)var3.next();
      }

      return var1;
   };
   RequirementsStrategy OR = (var0) -> {
      return new String[][]{(String[])var0.toArray(new String[0])};
   };

   String[][] createRequirements(Collection<String> var1);
}
