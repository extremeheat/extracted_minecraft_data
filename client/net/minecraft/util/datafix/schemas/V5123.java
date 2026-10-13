package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V5123 extends NamespacedSchema {
   public V5123(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> entityTypes, final Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      schema.registerType(false, References.FLUID_NAME, () -> DSL.constType(namespacedString()));
      schema.registerType(false, References.CHUNK, () -> DSL.optionalFields(new Pair[]{Pair.of("entities", DSL.list(References.ENTITY_TREE.in(schema))), Pair.of("block_entities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(schema), DSL.remainder()))), Pair.of("block_ticks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(schema)))), Pair.of("fluid_ticks", DSL.list(DSL.fields("i", References.FLUID_NAME.in(schema)))), Pair.of("UpgradeData", DSL.optionalFields("neighbor_block_ticks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(schema))), "neighbor_fluid_ticks", DSL.list(DSL.fields("i", References.FLUID_NAME.in(schema))))), Pair.of("sections", DSL.list(DSL.optionalFields("biomes", DSL.optionalFields("palette", DSL.list(References.BIOME.in(schema))), "noise_biomes", DSL.optionalFields("palette", DSL.list(References.BIOME.in(schema))), "block_states", DSL.optionalFields("palette", DSL.list(References.BLOCK_STATE.in(schema)))))), Pair.of("structures", DSL.optionalFields("starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(schema))))}));
   }
}
