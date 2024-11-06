package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ItemDisplayEntityRenderState extends DisplayEntityRenderState {
   public final ItemStackRenderState item = new ItemStackRenderState();

   public ItemDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return !this.item.isEmpty();
   }
}
