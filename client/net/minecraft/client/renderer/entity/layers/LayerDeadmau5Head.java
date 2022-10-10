package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer field_177208_a;

   public LayerDeadmau5Head(RenderPlayer var1) {
      super();
      this.field_177208_a = var1;
   }

   public void func_177141_a(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if ("deadmau5".equals(var1.func_200200_C_().getString()) && var1.func_152123_o() && !var1.func_82150_aj()) {
         this.field_177208_a.func_110776_a(var1.func_110306_p());

         for(int var9 = 0; var9 < 2; ++var9) {
            float var10 = var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var4 - (var1.field_70760_ar + (var1.field_70761_aq - var1.field_70760_ar) * var4);
            float var11 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var4;
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(var10, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.375F * (float)(var9 * 2 - 1), 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, -0.375F, 0.0F);
            GlStateManager.func_179114_b(-var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-var10, 0.0F, 1.0F, 0.0F);
            float var12 = 1.3333334F;
            GlStateManager.func_179152_a(1.3333334F, 1.3333334F, 1.3333334F);
            this.field_177208_a.func_177087_b().func_178727_b(0.0625F);
            GlStateManager.func_179121_F();
         }

      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
