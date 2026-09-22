package net.minecraft.world.level.block;

import net.minecraft.util.CommonColors;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassBlock extends TransparentBlock implements BeaconBeamBlock {
   private static final ColorCollection<Integer> BEAM_DYE_COLORS;
   private final DyeColor color;

   public StainedGlassBlock(final DyeColor color, final BlockBehaviour.Properties properties) {
      super(properties);
      this.color = color;
   }

   public int getColor() {
      return (Integer)BEAM_DYE_COLORS.pick(this.color);
   }

   static {
      BEAM_DYE_COLORS = CommonColors.TEXTURE_TINT_COLORS;
   }
}
