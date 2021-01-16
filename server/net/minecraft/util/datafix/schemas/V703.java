package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V703 extends Schema {
   public V703(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.remove("EntityHorse");
      var1.register(var2, "Horse", () -> {
         return DSL.optionalFields("ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.register(var2, "Donkey", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.register(var2, "Mule", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.register(var2, "ZombieHorse", () -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.register(var2, "SkeletonHorse", () -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      return var2;
   }
}
