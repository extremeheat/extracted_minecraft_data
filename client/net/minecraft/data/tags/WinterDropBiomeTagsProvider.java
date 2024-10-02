package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.WinterDropBiomes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class WinterDropBiomeTagsProvider extends TagsProvider<Biome> {
   public WinterDropBiomeTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, CompletableFuture<TagsProvider.TagLookup<Biome>> var3) {
      super(var1, Registries.BIOME, var2, var3);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.tag(BiomeTags.IS_FOREST).add(WinterDropBiomes.PALE_GARDEN);
      this.tag(BiomeTags.STRONGHOLD_BIASED_TO).add(WinterDropBiomes.PALE_GARDEN);
   }
}
