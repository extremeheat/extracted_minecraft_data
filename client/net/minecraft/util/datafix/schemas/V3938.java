package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3938 extends NamespacedSchema {
   public V3938(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate abstractArrow(Schema var0) {
      return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var0), "item", References.ITEM_STACK.in(var0), "weapon", References.ITEM_STACK.in(var0));
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:spectral_arrow", () -> {
         return abstractArrow(var1);
      });
      var1.register(var2, "minecraft:arrow", () -> {
         return abstractArrow(var1);
      });
      return var2;
   }
}
