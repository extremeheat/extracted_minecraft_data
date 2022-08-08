package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class FlatLevelGeneratorPresetTags {
   public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = create("visible");

   private FlatLevelGeneratorPresetTags() {
      super();
   }

   private static TagKey<FlatLevelGeneratorPreset> create(String var0) {
      return TagKey.create(Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY, new ResourceLocation(var0));
   }
}
