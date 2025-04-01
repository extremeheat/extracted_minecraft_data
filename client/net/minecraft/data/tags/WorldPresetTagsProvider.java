package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldPresetTagsProvider extends TagsProvider<WorldPreset> {
   public WorldPresetTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.WORLD_PRESET, var2);
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(WorldPresetTags.NORMAL).add(WorldPresets.NORMAL).add(WorldPresets.FLAT);
      this.tag(WorldPresetTags.EXTENDED).addTag(WorldPresetTags.NORMAL);
   }
}
