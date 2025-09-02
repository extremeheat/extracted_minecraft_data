package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ShelfRenderState extends BlockEntityRenderState {
   public ItemStackRenderState[] items = new ItemStackRenderState[3];
   public boolean alignToBottom;

   public ShelfRenderState() {
      super();
   }
}
