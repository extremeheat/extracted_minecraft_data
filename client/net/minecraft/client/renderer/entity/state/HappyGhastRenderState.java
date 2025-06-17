package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.item.ItemStack;

public class HappyGhastRenderState extends LivingEntityRenderState {
   public ItemStack bodyItem;
   public boolean isRidden;
   public boolean isLeashHolder;

   public HappyGhastRenderState() {
      super();
      this.bodyItem = ItemStack.EMPTY;
   }
}
