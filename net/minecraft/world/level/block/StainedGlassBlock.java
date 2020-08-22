package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements BeaconBeamBlock {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor var1, Block.Properties var2) {
      super(var2);
      this.color = var1;
   }

   public DyeColor getColor() {
      return this.color;
   }
}
