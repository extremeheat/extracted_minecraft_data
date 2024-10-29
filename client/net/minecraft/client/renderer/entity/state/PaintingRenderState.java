package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingRenderState extends EntityRenderState {
   public Direction direction;
   @Nullable
   public PaintingVariant variant;
   public int[] lightCoords;

   public PaintingRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.lightCoords = new int[0];
   }
}
