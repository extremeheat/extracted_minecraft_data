package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1466 extends NamespacedSchema {
   public V1466(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, TypeReferences.field_211287_c, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.field_211298_n.in(var1)), "TileEntities", DSL.list(TypeReferences.field_211294_j.in(var1)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.field_211300_p.in(var1))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.field_211296_l.in(var1)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(TypeReferences.field_211303_s.in(var1)))));
      });
      var1.registerType(false, TypeReferences.field_211303_s, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.field_211296_l.in(var1), "CB", TypeReferences.field_211296_l.in(var1), "CC", TypeReferences.field_211296_l.in(var1), "CD", TypeReferences.field_211296_l.in(var1))), "biome", TypeReferences.field_211305_u.in(var1));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var2.put("DUMMY", DSL::remainder);
      return var2;
   }
}
