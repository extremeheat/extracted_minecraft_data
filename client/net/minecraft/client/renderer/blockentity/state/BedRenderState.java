package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

public class BedRenderState extends BlockEntityRenderState {
   public DyeColor color;
   public Direction facing;
   public boolean isHead;

   public BedRenderState() {
      super();
      this.color = DyeColor.WHITE;
      this.facing = Direction.NORTH;
   }
}
