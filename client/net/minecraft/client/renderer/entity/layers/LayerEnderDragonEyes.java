package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.ResourceLocation;

public class LayerEnderDragonEyes implements LayerRenderer<EntityDragon> {
   private static final ResourceLocation field_177212_a = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private final RenderDragon field_177211_b;

   public LayerEnderDragonEyes(RenderDragon var1) {
      super();
      this.field_177211_b = var1;
   }

   public void func_177141_a(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.field_177211_b.func_110776_a(field_177212_a);
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      GlStateManager.func_179140_f();
      GlStateManager.func_179143_c(514);
      char var9 = '\uf0f0';
      char var10 = '\uf0f0';
      boolean var11 = false;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 61680.0F, 0.0F);
      GlStateManager.func_179145_e();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.func_71410_x().field_71460_t.func_191514_d(true);
      this.field_177211_b.func_177087_b().func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      Minecraft.func_71410_x().field_71460_t.func_191514_d(false);
      this.field_177211_b.func_177105_a(var1);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179143_c(515);
   }

   public boolean func_177142_b() {
      return false;
   }
}
