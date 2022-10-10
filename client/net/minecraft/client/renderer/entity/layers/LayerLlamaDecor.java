package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLlama;
import net.minecraft.client.renderer.entity.model.ModelLlama;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.ResourceLocation;

public class LayerLlamaDecor implements LayerRenderer<EntityLlama> {
   private static final ResourceLocation[] field_191364_a = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
   private final RenderLlama field_191365_b;
   private final ModelLlama field_191366_c = new ModelLlama(0.5F);

   public LayerLlamaDecor(RenderLlama var1) {
      super();
      this.field_191365_b = var1;
   }

   public void func_177141_a(EntityLlama var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_190717_dN()) {
         this.field_191365_b.func_110776_a(field_191364_a[var1.func_190704_dO().func_196059_a()]);
         this.field_191366_c.func_178686_a(this.field_191365_b.func_177087_b());
         this.field_191366_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
