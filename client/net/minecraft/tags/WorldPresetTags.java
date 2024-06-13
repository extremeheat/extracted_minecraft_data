package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresetTags {
   public static final TagKey<WorldPreset> NORMAL = create("normal");
   public static final TagKey<WorldPreset> EXTENDED = create("extended");

   private WorldPresetTags() {
      super();
   }

   private static TagKey<WorldPreset> create(String var0) {
      return TagKey.create(Registries.WORLD_PRESET, ResourceLocation.withDefaultNamespace(var0));
   }
}
