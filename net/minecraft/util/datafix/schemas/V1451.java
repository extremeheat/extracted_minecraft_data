package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class V1451 extends NamespacedSchema {
   public V1451(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:trapped_chest", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      return var2;
   }
}
