package net.minecraft.world.level.block.entity;

import net.minecraft.resources.Identifier;

public record DecoratedPotPattern(Identifier assetId) {
   public DecoratedPotPattern(Identifier var1) {
      super();
      this.assetId = var1;
   }
}
