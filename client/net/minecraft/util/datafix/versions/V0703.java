package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0703 extends Schema {
   public V0703(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.remove("EntityHorse");
      var1.register(var2, "Horse", () -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.field_211295_k.in(var1), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.register(var2, "Donkey", () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.register(var2, "Mule", () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.register(var2, "ZombieHorse", () -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.register(var2, "SkeletonHorse", () -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      return var2;
   }
}
