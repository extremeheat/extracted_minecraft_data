package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1928 extends NamespacedSchema {
   public V1928(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate equipment(Schema var0) {
      return DSL.optionalFields("ArmorItems", DSL.list(References.ITEM_STACK.in(var0)), "HandItems", DSL.list(References.ITEM_STACK.in(var0)));
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return equipment(var0);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.remove("minecraft:illager_beast");
      registerMob(var1, var2, "minecraft:ravager");
      return var2;
   }
}
