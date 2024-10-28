package net.minecraft.world.level.block.entity;

import net.minecraft.resources.ResourceLocation;

public record DecoratedPotPattern(ResourceLocation assetId) {
   public DecoratedPotPattern(ResourceLocation var1) {
      super();
      this.assetId = var1;
   }

   public ResourceLocation assetId() {
      return this.assetId;
   }
}
