package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class V1486 extends NamespacedSchema {
   public V1486(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.put("minecraft:cod", var2.remove("minecraft:cod_mob"));
      var2.put("minecraft:salmon", var2.remove("minecraft:salmon_mob"));
      return var2;
   }
}
