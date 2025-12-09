package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.item.ItemStack;

public class LlamaRenderState extends LivingEntityRenderState {
   public Llama.Variant variant;
   public boolean hasChest;
   public ItemStack bodyItem;
   public boolean isTraderLlama;

   public LlamaRenderState() {
      super();
      this.variant = Llama.Variant.DEFAULT;
      this.bodyItem = ItemStack.EMPTY;
   }
}
