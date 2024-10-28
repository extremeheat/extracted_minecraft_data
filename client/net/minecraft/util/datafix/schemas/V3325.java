package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3325 extends NamespacedSchema {
   public V3325(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:item_display", (var1x) -> {
         return DSL.optionalFields("item", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:block_display", (var1x) -> {
         return DSL.optionalFields("block_state", References.BLOCK_STATE.in(var1));
      });
      var1.registerSimple(var2, "minecraft:text_display");
      return var2;
   }
}
