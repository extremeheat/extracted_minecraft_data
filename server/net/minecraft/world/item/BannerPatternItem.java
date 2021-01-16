package net.minecraft.world.item;

import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerPatternItem extends Item {
   private final BannerPattern bannerPattern;

   public BannerPatternItem(BannerPattern var1, Item.Properties var2) {
      super(var2);
      this.bannerPattern = var1;
   }

   public BannerPattern getBannerPattern() {
      return this.bannerPattern;
   }
}
