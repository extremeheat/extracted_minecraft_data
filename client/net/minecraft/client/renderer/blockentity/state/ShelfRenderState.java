package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jspecify.annotations.Nullable;

public class ShelfRenderState extends BlockEntityRenderState {
   public final @Nullable ItemStackRenderState[] items = new ItemStackRenderState[3];
   public boolean alignToBottom;

   public ShelfRenderState() {
      super();
   }
}
