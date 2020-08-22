package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class V2100 extends NamespacedSchema {
   public V2100(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerMob(Schema var0, Map var1, String var2) {
      var0.register(var1, var2, () -> {
         return V100.equipment(var0);
      });
   }

   public Map registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      registerMob(var1, var2, "minecraft:bee");
      registerMob(var1, var2, "minecraft:bee_stinger");
      return var2;
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:beehive", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "Bees", DSL.list(DSL.optionalFields("EntityData", References.ENTITY_TREE.in(var1))));
      });
      return var2;
   }
}
