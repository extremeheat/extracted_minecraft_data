package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class V1904 extends NamespacedSchema {
   public V1904(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerMob(Schema var0, Map var1, String var2) {
      var0.register(var1, var2, () -> {
         return V100.equipment(var0);
      });
   }

   public Map registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      registerMob(var1, var2, "minecraft:cat");
      return var2;
   }
}
