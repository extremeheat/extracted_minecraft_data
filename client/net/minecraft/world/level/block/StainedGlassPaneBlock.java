package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassPaneBlock extends IronBarsBlock implements BeaconBeamBlock {
   private final DyeColor color;

   public StainedGlassPaneBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   public DyeColor getColor() {
      return this.color;
   }
}
