package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

public class ShulkerBoxRenderState extends BlockEntityRenderState {
   public Direction direction;
   public @Nullable DyeColor color;
   public float progress;

   public ShulkerBoxRenderState() {
      super();
      this.direction = Direction.NORTH;
   }
}
