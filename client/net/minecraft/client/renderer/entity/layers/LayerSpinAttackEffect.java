package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class LayerSpinAttackEffect implements LayerRenderer<AbstractClientPlayer> {
   public static final ResourceLocation field_204836_a = new ResourceLocation("textures/entity/trident_riptide.png");
   private final RenderPlayer field_204837_b;
   private final LayerSpinAttackEffect.Model field_204838_c;

   public LayerSpinAttackEffect(RenderPlayer var1) {
      super();
      this.field_204837_b = var1;
      this.field_204838_c = new LayerSpinAttackEffect.Model();
   }

   public void func_177141_a(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_204805_cN()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_204837_b.func_110776_a(field_204836_a);

         for(int var9 = 0; var9 < 3; ++var9) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(var5 * (float)(-(45 + var9 * 5)), 0.0F, 1.0F, 0.0F);
            float var10 = 0.75F * (float)var9;
            GlStateManager.func_179152_a(var10, var10, var10);
            GlStateManager.func_179109_b(0.0F, -0.2F + 0.6F * (float)var9, 0.0F);
            this.field_204838_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
            GlStateManager.func_179121_F();
         }

      }
   }

   public boolean func_177142_b() {
      return false;
   }

   static class Model extends ModelBase {
      private final ModelRenderer field_204834_a;

      public Model() {
         super();
         this.field_78090_t = 64;
         this.field_78089_u = 64;
         this.field_204834_a = new ModelRenderer(this, 0, 0);
         this.field_204834_a.func_78789_a(-8.0F, -16.0F, -8.0F, 16, 32, 16);
      }

      public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.field_204834_a.func_78785_a(var7);
      }
   }
}
