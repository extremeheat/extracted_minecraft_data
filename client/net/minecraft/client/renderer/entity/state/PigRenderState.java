package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class PigRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public @Nullable PigVariant variant;

   public PigRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
   }
}
