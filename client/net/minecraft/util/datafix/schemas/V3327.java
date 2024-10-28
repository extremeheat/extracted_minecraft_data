package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3327 extends NamespacedSchema {
   public V3327(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:decorated_pot", () -> {
         return DSL.optionalFields("shards", DSL.list(References.ITEM_NAME.in(var1)), "item", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:suspicious_sand", () -> {
         return DSL.optionalFields("item", References.ITEM_STACK.in(var1));
      });
      return var2;
   }
}
