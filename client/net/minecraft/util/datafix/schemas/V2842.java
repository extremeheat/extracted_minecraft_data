package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2842 extends NamespacedSchema {
   public V2842(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.CHUNK, () -> {
         return DSL.optionalFields("entities", DSL.list(References.ENTITY_TREE.in(var1)), "block_entities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(var1), DSL.remainder())), "block_ticks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1))), "sections", DSL.list(DSL.optionalFields("biomes", DSL.optionalFields("palette", DSL.list(References.BIOME.in(var1))), "block_states", DSL.optionalFields("palette", DSL.list(References.BLOCK_STATE.in(var1))))), "structures", DSL.optionalFields("starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1))));
      });
   }
}
