package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class UpdateOneTwentyBiomeTagsProvider extends TagsProvider<Biome> {
   public UpdateOneTwentyBiomeTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.BIOME, var2);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.tag(BiomeTags.IS_MOUNTAIN).add(Biomes.CHERRY_GROVE);
   }
}
