package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2551 extends NamespacedSchema {
   public V2551(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.WORLD_GEN_SETTINGS, () -> DSL.fields("dimensions", DSL.compoundList(DSL.constType(namespacedString()), DSL.fields("generator", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL::remainder, "minecraft:flat", (Supplier)() -> DSL.optionalFields("settings", DSL.optionalFields("biome", References.BIOME.in(var1), "layers", DSL.list(DSL.optionalFields("block", References.BLOCK_NAME.in(var1))))), "minecraft:noise", (Supplier)() -> DSL.optionalFields("biome_source", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:fixed", (Supplier)() -> DSL.fields("biome", References.BIOME.in(var1)), "minecraft:multi_noise", (Supplier)() -> DSL.list(DSL.fields("biome", References.BIOME.in(var1))), "minecraft:checkerboard", (Supplier)() -> DSL.fields("biomes", DSL.list(References.BIOME.in(var1))), "minecraft:vanilla_layered", DSL::remainder, "minecraft:the_end", DSL::remainder)), "settings", DSL.or(DSL.constType(DSL.string()), DSL.optionalFields("default_block", References.BLOCK_NAME.in(var1), "default_fluid", References.BLOCK_NAME.in(var1))))))))));
   }
}
