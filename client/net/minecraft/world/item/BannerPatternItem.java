package net.minecraft.world.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerPatternItem extends Item {
   private final TagKey<BannerPattern> bannerPattern;

   public BannerPatternItem(TagKey<BannerPattern> var1, Item.Properties var2) {
      super(var2);
      this.bannerPattern = var1;
   }

   public TagKey<BannerPattern> getBannerPattern() {
      return this.bannerPattern;
   }
}
