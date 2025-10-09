package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.item.ItemStack;

public class NautilusRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public ItemStack bodyArmorItem;

   public NautilusRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
      this.bodyArmorItem = ItemStack.EMPTY;
   }
}
