package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

public class ShulkerBoxRenderState extends BlockEntityRenderState {
   public Direction direction;
   @Nullable
   public DyeColor color;
   public float progress;

   public ShulkerBoxRenderState() {
      super();
      this.direction = Direction.NORTH;
   }
}
