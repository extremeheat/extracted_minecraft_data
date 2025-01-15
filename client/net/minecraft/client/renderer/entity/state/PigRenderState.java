package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.item.ItemStack;

public class PigRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   @Nullable
   public PigVariant variant;

   public PigRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
   }
}
