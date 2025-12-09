package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.item.ItemStack;

public class EquineRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public ItemStack bodyArmorItem;
   public boolean isRidden;
   public boolean animateTail;
   public float eatAnimation;
   public float standAnimation;
   public float feedingAnimation;

   public EquineRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
      this.bodyArmorItem = ItemStack.EMPTY;
   }
}
