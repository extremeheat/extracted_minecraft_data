package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4300 extends NamespacedSchema {
   public V4300(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:llama", (var1x) -> entityWithInventory(var1));
      var1.register(var2, "minecraft:trader_llama", (var1x) -> entityWithInventory(var1));
      var1.register(var2, "minecraft:donkey", (var1x) -> entityWithInventory(var1));
      var1.register(var2, "minecraft:mule", (var1x) -> entityWithInventory(var1));
      var1.registerSimple(var2, "minecraft:horse");
      var1.registerSimple(var2, "minecraft:skeleton_horse");
      var1.registerSimple(var2, "minecraft:zombie_horse");
      return var2;
   }

   private static TypeTemplate entityWithInventory(Schema var0) {
      return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
   }
}
