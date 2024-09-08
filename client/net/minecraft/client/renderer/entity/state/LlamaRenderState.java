package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.ItemStack;

public class LlamaRenderState extends LivingEntityRenderState {
   public Llama.Variant variant = Llama.Variant.CREAMY;
   public boolean hasChest;
   public ItemStack bodyItem = ItemStack.EMPTY;
   public boolean isTraderLlama;

   public LlamaRenderState() {
      super();
   }
}
