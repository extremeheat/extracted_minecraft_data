package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;

public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation field_110789_a = new ResourceLocation("textures/map/map_background.png");
   private static final ModelResourceLocation field_209585_f = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation field_209586_g = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft field_147917_g = Minecraft.func_71410_x();
   private final ItemRenderer field_177074_h;

   public RenderItemFrame(RenderManager var1, ItemRenderer var2) {
      super(var1);
      this.field_177074_h = var2;
   }

   public void func_76986_a(EntityItemFrame var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      BlockPos var10 = var1.func_174857_n();
      double var11 = (double)var10.func_177958_n() - var1.field_70165_t + var2;
      double var13 = (double)var10.func_177956_o() - var1.field_70163_u + var4;
      double var15 = (double)var10.func_177952_p() - var1.field_70161_v + var6;
      GlStateManager.func_179137_b(var11 + 0.5D, var13 + 0.5D, var15 + 0.5D);
      GlStateManager.func_179114_b(var1.field_70125_A, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F - var1.field_70177_z, 0.0F, 1.0F, 0.0F);
      this.field_76990_c.field_78724_e.func_110577_a(TextureMap.field_110575_b);
      BlockRendererDispatcher var17 = this.field_147917_g.func_175602_ab();
      ModelManager var18 = var17.func_175023_a().func_178126_b();
      ModelResourceLocation var19 = var1.func_82335_i().func_77973_b() == Items.field_151098_aY ? field_209586_g : field_209585_f;
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      var17.func_175019_b().func_178262_a(var18.func_174953_a(var19), 1.0F, 1.0F, 1.0F, 1.0F);
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179145_e();
      if (var1.func_82335_i().func_77973_b() == Items.field_151098_aY) {
         GlStateManager.func_179123_a();
         RenderHelper.func_74519_b();
      }

      GlStateManager.func_179109_b(0.0F, 0.0F, 0.4375F);
      this.func_82402_b(var1);
      if (var1.func_82335_i().func_77973_b() == Items.field_151098_aY) {
         RenderHelper.func_74518_a();
         GlStateManager.func_179099_b();
      }

      GlStateManager.func_179145_e();
      GlStateManager.func_179121_F();
      this.func_177067_a(var1, var2 + (double)((float)var1.field_174860_b.func_82601_c() * 0.3F), var4 - 0.25D, var6 + (double)((float)var1.field_174860_b.func_82599_e() * 0.3F));
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityItemFrame var1) {
      return null;
   }

   private void func_82402_b(EntityItemFrame var1) {
      ItemStack var2 = var1.func_82335_i();
      if (!var2.func_190926_b()) {
         GlStateManager.func_179094_E();
         boolean var3 = var2.func_77973_b() == Items.field_151098_aY;
         int var4 = var3 ? var1.func_82333_j() % 4 * 2 : var1.func_82333_j();
         GlStateManager.func_179114_b((float)var4 * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (var3) {
            GlStateManager.func_179140_f();
            this.field_76990_c.field_78724_e.func_110577_a(field_110789_a);
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
            float var5 = 0.0078125F;
            GlStateManager.func_179152_a(0.0078125F, 0.0078125F, 0.0078125F);
            GlStateManager.func_179109_b(-64.0F, -64.0F, 0.0F);
            MapData var6 = ItemMap.func_195950_a(var2, var1.field_70170_p);
            GlStateManager.func_179109_b(0.0F, 0.0F, -1.0F);
            if (var6 != null) {
               this.field_147917_g.field_71460_t.func_147701_i().func_148250_a(var6, true);
            }
         } else {
            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
            this.field_177074_h.func_181564_a(var2, ItemCameraTransforms.TransformType.FIXED);
         }

         GlStateManager.func_179121_F();
      }
   }

   protected void func_177067_a(EntityItemFrame var1, double var2, double var4, double var6) {
      if (Minecraft.func_71382_s() && !var1.func_82335_i().func_190926_b() && var1.func_82335_i().func_82837_s() && this.field_76990_c.field_147941_i == var1) {
         double var8 = var1.func_70068_e(this.field_76990_c.field_78734_h);
         float var10 = var1.func_70093_af() ? 32.0F : 64.0F;
         if (var8 < (double)(var10 * var10)) {
            String var11 = var1.func_82335_i().func_200301_q().func_150254_d();
            this.func_147906_a(var1, var11, var2, var4, var6, 64);
         }
      }
   }
}
