package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2832 extends NamespacedSchema {
   public V2832(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(var1), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1))), "Sections", DSL.list(DSL.optionalFields("biomes", DSL.optionalFields("palette", DSL.list(References.BIOME.in(var1))), "block_states", DSL.optionalFields("palette", DSL.list(References.BLOCK_STATE.in(var1))))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1)))));
      });
      var1.registerType(false, References.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.WORLD_GEN_SETTINGS, () -> {
         return DSL.fields("dimensions", DSL.compoundList(DSL.constType(namespacedString()), DSL.fields("generator", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL::remainder, "minecraft:flat", () -> {
            return DSL.optionalFields("settings", DSL.optionalFields("biome", References.BIOME.in(var1), "layers", DSL.list(DSL.optionalFields("block", References.BLOCK_NAME.in(var1)))));
         }, "minecraft:noise", () -> {
            return DSL.optionalFields("biome_source", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:fixed", () -> {
               return DSL.fields("biome", References.BIOME.in(var1));
            }, "minecraft:multi_noise", () -> {
               return DSL.or(DSL.fields("preset", References.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST.in(var1)), DSL.list(DSL.fields("biome", References.BIOME.in(var1))));
            }, "minecraft:checkerboard", () -> {
               return DSL.fields("biomes", DSL.list(References.BIOME.in(var1)));
            }, "minecraft:the_end", DSL::remainder)), "settings", DSL.or(DSL.constType(DSL.string()), DSL.optionalFields("default_block", References.BLOCK_NAME.in(var1), "default_fluid", References.BLOCK_NAME.in(var1))));
         })))));
      });
   }
}
