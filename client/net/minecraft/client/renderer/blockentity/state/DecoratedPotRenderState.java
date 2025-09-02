package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

public class DecoratedPotRenderState extends BlockEntityRenderState {
   public float yRot;
   @Nullable
   public DecoratedPotBlockEntity.WobbleStyle wobbleStyle;
   public float wobbleProgress;
   public PotDecorations decorations;
   public Direction direction;

   public DecoratedPotRenderState() {
      super();
      this.decorations = PotDecorations.EMPTY;
      this.direction = Direction.NORTH;
   }
}
