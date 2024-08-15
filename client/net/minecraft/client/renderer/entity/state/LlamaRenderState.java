package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;

public class LlamaRenderState extends LivingEntityRenderState {
   public Llama.Variant variant = Llama.Variant.CREAMY;
   public boolean hasChest;
   @Nullable
   public DyeColor decorColor;
   public boolean isTraderLlama;

   public LlamaRenderState() {
      super();
   }
}
