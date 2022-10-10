package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1451_7 extends NamespacedSchema {
   public V1451_7(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, TypeReferences.field_211303_s, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.field_211296_l.in(var1), "CB", TypeReferences.field_211296_l.in(var1), "CC", TypeReferences.field_211296_l.in(var1), "CD", TypeReferences.field_211296_l.in(var1))));
      });
   }
}
