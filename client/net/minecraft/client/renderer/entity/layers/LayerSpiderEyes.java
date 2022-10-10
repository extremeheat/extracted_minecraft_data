package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class LayerSpiderEyes<T extends EntitySpider> implements LayerRenderer<T> {
   private static final ResourceLocation field_177150_a = new ResourceLocation("textures/entity/spider_eyes.png");
   private final RenderSpider<T> field_177149_b;

   public LayerSpiderEyes(RenderSpider<T> var1) {
      super();
      this.field_177149_b = var1;
   }

   public void func_177141_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.field_177149_b.func_110776_a(field_177150_a);
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      if (var1.func_82150_aj()) {
         GlStateManager.func_179132_a(false);
      } else {
         GlStateManager.func_179132_a(true);
      }

      char var9 = '\uf0f0';
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var10, (float)var11);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.func_71410_x().field_71460_t.func_191514_d(true);
      this.field_177149_b.func_177087_b().func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      Minecraft.func_71410_x().field_71460_t.func_191514_d(false);
      int var12 = var1.func_70070_b();
      var10 = var12 % 65536;
      var11 = var12 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var10, (float)var11);
      this.field_177149_b.func_177105_a(var1);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
   }

   public boolean func_177142_b() {
      return false;
   }
}
