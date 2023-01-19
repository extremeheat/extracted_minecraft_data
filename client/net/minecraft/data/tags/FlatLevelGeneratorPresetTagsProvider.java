package net.minecraft.data.tags;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPresets;

public class FlatLevelGeneratorPresetTagsProvider extends TagsProvider<FlatLevelGeneratorPreset> {
   public FlatLevelGeneratorPresetTagsProvider(DataGenerator var1) {
      super(var1, BuiltinRegistries.FLAT_LEVEL_GENERATOR_PRESET);
   }

   @Override
   protected void addTags() {
      this.tag(FlatLevelGeneratorPresetTags.VISIBLE)
         .add(FlatLevelGeneratorPresets.CLASSIC_FLAT)
         .add(FlatLevelGeneratorPresets.TUNNELERS_DREAM)
         .add(FlatLevelGeneratorPresets.WATER_WORLD)
         .add(FlatLevelGeneratorPresets.OVERWORLD)
         .add(FlatLevelGeneratorPresets.SNOWY_KINGDOM)
         .add(FlatLevelGeneratorPresets.BOTTOMLESS_PIT)
         .add(FlatLevelGeneratorPresets.DESERT)
         .add(FlatLevelGeneratorPresets.REDSTONE_READY)
         .add(FlatLevelGeneratorPresets.THE_VOID);
   }
}
