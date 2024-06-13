package net.minecraft.world.level.block.entity;

import net.minecraft.resources.ResourceLocation;

public record DecoratedPotPattern(ResourceLocation assetId) {
   public DecoratedPotPattern(ResourceLocation assetId) {
      super();
      this.assetId = assetId;
   }
}
