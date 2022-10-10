package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;

public class LayerIronGolemFlower implements LayerRenderer<EntityIronGolem> {
   private final RenderIronGolem field_177154_a;

   public LayerIronGolemFlower(RenderIronGolem var1) {
      super();
      this.field_177154_a = var1;
   }

   public void func_177141_a(EntityIronGolem var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_70853_p() != 0) {
         BlockRendererDispatcher var9 = Minecraft.func_71410_x().func_175602_ab();
         GlStateManager.func_179091_B();
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(5.0F + 180.0F * ((ModelIronGolem)this.field_177154_a.func_177087_b()).func_205071_a().field_78795_f / 3.1415927F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179109_b(-0.9375F, -0.625F, -0.9375F);
         float var10 = 0.5F;
         GlStateManager.func_179152_a(0.5F, -0.5F, 0.5F);
         int var11 = var1.func_70070_b();
         int var12 = var11 % 65536;
         int var13 = var11 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var12, (float)var13);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_177154_a.func_110776_a(TextureMap.field_110575_b);
         var9.func_175016_a(Blocks.field_196606_bd.func_176223_P(), 1.0F);
         GlStateManager.func_179121_F();
         GlStateManager.func_179101_C();
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
