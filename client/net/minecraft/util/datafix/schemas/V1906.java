package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1906 extends NamespacedSchema {
   public V1906(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      registerInventory(var1, var2, "minecraft:barrel");
      registerInventory(var1, var2, "minecraft:smoker");
      registerInventory(var1, var2, "minecraft:blast_furnace");
      var1.register(var2, "minecraft:lectern", (var1x) -> {
         return DSL.optionalFields("Book", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "minecraft:bell");
      return var2;
   }

   protected static void registerInventory(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
      });
   }
}
