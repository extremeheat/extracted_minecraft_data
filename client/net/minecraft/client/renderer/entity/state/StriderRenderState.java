package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.item.ItemStack;

public class StriderRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public boolean isSuffocating;
   public boolean isRidden;

   public StriderRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
   }
}
