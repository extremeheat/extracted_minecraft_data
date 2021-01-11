package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;

public class LayerMooshroomMushroom implements LayerRenderer<EntityMooshroom> {
   private final RenderMooshroom field_177205_a;

   public LayerMooshroomMushroom(RenderMooshroom var1) {
      super();
      this.field_177205_a = var1;
   }

   public void func_177141_a(EntityMooshroom var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.func_70631_g_() && !var1.func_82150_aj()) {
         BlockRendererDispatcher var9 = Minecraft.func_71410_x().func_175602_ab();
         this.field_177205_a.func_110776_a(TextureMap.field_110575_b);
         GlStateManager.func_179089_o();
         GlStateManager.func_179107_e(1028);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F, -1.0F, 1.0F);
         GlStateManager.func_179109_b(0.2F, 0.35F, 0.5F);
         GlStateManager.func_179114_b(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(-0.5F, -0.5F, 0.5F);
         var9.func_175016_a(Blocks.field_150337_Q.func_176223_P(), 1.0F);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.1F, 0.0F, -0.6F);
         GlStateManager.func_179114_b(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(-0.5F, -0.5F, 0.5F);
         var9.func_175016_a(Blocks.field_150337_Q.func_176223_P(), 1.0F);
         GlStateManager.func_179121_F();
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         ((ModelQuadruped)this.field_177205_a.func_177087_b()).field_78150_a.func_78794_c(0.0625F);
         GlStateManager.func_179152_a(1.0F, -1.0F, 1.0F);
         GlStateManager.func_179109_b(0.0F, 0.7F, -0.2F);
         GlStateManager.func_179114_b(12.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(-0.5F, -0.5F, 0.5F);
         var9.func_175016_a(Blocks.field_150337_Q.func_176223_P(), 1.0F);
         GlStateManager.func_179121_F();
         GlStateManager.func_179107_e(1029);
         GlStateManager.func_179129_p();
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
