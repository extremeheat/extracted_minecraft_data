package net.minecraft.client.renderer.entity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.jspecify.annotations.Nullable;

public class PaintingRenderState extends EntityRenderState {
   public Direction direction;
   public @Nullable PaintingVariant variant;
   public int[] lightCoordsPerBlock;

   public PaintingRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.lightCoordsPerBlock = new int[0];
   }
}
