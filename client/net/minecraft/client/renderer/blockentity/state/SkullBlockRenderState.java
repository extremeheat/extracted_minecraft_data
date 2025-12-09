package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;

public class SkullBlockRenderState extends BlockEntityRenderState {
   public float animationProgress;
   public Direction direction;
   public float rotationDegrees;
   public SkullBlock.Type skullType;
   public RenderType renderType;

   public SkullBlockRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.skullType = SkullBlock.Types.ZOMBIE;
   }
}
