package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class BrushableBlockRenderState extends BlockEntityRenderState {
   public ItemStackRenderState itemState = new ItemStackRenderState();
   public int dustProgress;
   @Nullable
   public Direction hitDirection;

   public BrushableBlockRenderState() {
      super();
   }
}
