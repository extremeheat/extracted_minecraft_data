package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.jspecify.annotations.Nullable;

public class DecoratedPotRenderState extends BlockEntityRenderState {
   public float yRot;
   public DecoratedPotBlockEntity.@Nullable WobbleStyle wobbleStyle;
   public float wobbleProgress;
   public PotDecorations decorations;
   public Direction direction;

   public DecoratedPotRenderState() {
      super();
      this.decorations = PotDecorations.EMPTY;
      this.direction = Direction.NORTH;
   }
}
