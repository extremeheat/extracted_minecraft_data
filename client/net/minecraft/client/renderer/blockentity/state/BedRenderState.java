package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedRenderState extends BlockEntityRenderState {
   public DyeColor color;
   public Direction facing;
   public BedPart part;

   public BedRenderState() {
      super();
      this.color = DyeColor.WHITE;
      this.facing = Direction.NORTH;
   }
}
