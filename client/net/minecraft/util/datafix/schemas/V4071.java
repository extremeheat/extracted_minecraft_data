package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;

public class V4071 extends NamespacedSchema {
   public V4071(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:creaking", () -> {
         return V100.equipment(var1);
      });
      var1.register(var2, "minecraft:creaking_transient", () -> {
         return V100.equipment(var1);
      });
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:creaking_heart", () -> {
         return DSL.optionalFields(new Pair[0]);
      });
      return var2;
   }
}
