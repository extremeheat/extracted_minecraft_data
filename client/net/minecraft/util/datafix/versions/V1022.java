package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V1022 extends Schema {
   public V1022(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, TypeReferences.field_211304_t, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211286_b, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.field_211298_n.in(var1)), "Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "EnderItems", DSL.list(TypeReferences.field_211295_k.in(var1)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.field_211298_n.in(var1), "ShoulderEntityRight", TypeReferences.field_211298_n.in(var1), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.field_211304_t.in(var1)), "toBeDisplayed", DSL.list(TypeReferences.field_211304_t.in(var1)))));
      });
      var1.registerType(false, TypeReferences.field_211288_d, () -> {
         return DSL.compoundList(DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
   }
}
