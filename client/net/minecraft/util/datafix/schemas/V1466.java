package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1466 extends NamespacedSchema {
   public V1466(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)), "TileEntities", DSL.list(References.BLOCK_ENTITY.in(var1)), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(References.BLOCK_STATE.in(var1)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1)))));
      });
      var1.registerType(false, References.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", References.BLOCK_STATE.in(var1), "CB", References.BLOCK_STATE.in(var1), "CC", References.BLOCK_STATE.in(var1), "CD", References.BLOCK_STATE.in(var1))), "biome", References.BIOME.in(var1));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var2.put("DUMMY", DSL::remainder);
      return var2;
   }
}
