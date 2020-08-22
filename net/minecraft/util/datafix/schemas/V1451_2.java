package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class V1451_2 extends NamespacedSchema {
   public V1451_2(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:piston", (var1x) -> {
         return DSL.optionalFields("blockState", References.BLOCK_STATE.in(var1));
      });
      return var2;
   }
}
