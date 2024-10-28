package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2688 extends NamespacedSchema {
   public V2688(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:glow_squid", () -> {
         return V100.equipment(var1);
      });
      var1.register(var2, "minecraft:glow_item_frame", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      return var2;
   }
}
