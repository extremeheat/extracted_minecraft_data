package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class UpdateOneTwentyOneBannerPatternTagsProvider extends TagsProvider<BannerPattern> {
   public UpdateOneTwentyOneBannerPatternTagsProvider(
      PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, CompletableFuture<TagsProvider.TagLookup<BannerPattern>> var3
   ) {
      super(var1, Registries.BANNER_PATTERN, var2, var3);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.tag(BannerPatternTags.PATTERN_ITEM_FLOW).add(BannerPatterns.FLOW);
      this.tag(BannerPatternTags.PATTERN_ITEM_GUSTER).add(BannerPatterns.GUSTER);
   }
}
