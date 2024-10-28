package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1929 extends NamespacedSchema {
   public V1929(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:wandering_trader", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), V100.equipment(var1));
      });
      var1.register(var2, "minecraft:trader_llama", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), "DecorItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      return var2;
   }
}
