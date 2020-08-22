package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class V808 extends NamespacedSchema {
   public V808(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerInventory(Schema var0, Map var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
      });
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      registerInventory(var1, var2, "minecraft:shulker_box");
      return var2;
   }
}
