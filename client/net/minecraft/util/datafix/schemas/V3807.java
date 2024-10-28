package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3807 extends NamespacedSchema {
   public V3807(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:vault", () -> {
         return DSL.optionalFields("config", DSL.optionalFields("key_item", References.ITEM_STACK.in(var1)), "server_data", DSL.optionalFields("items_to_eject", DSL.list(References.ITEM_STACK.in(var1))), "shared_data", DSL.optionalFields("display_item", References.ITEM_STACK.in(var1)));
      });
      return var2;
   }
}
