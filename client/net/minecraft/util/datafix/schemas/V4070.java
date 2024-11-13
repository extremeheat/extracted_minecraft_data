package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4070 extends NamespacedSchema {
   public V4070(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.registerSimple(var2, "minecraft:pale_oak_boat");
      var1.register(var2, "minecraft:pale_oak_chest_boat", (var1x) -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1))));
      return var2;
   }
}
