package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation field_110789_a = new ResourceLocation("textures/map/map_background.png");
   private final Minecraft field_147917_g = Minecraft.func_71410_x();
   private final ModelResourceLocation field_177072_f = new ModelResourceLocation("item_frame", "normal");
   private final ModelResourceLocation field_177073_g = new ModelResourceLocation("item_frame", "map");
   private RenderItem field_177074_h;

   public RenderItemFrame(RenderManager var1, RenderItem var2) {
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
      GlStateManager.func_179114_b(180.0F - var1.field_70177_z, 0.0F, 1.0F, 0.0F);
      this.field_76990_c.field_78724_e.func_110577_a(TextureMap.field_110575_b);
      BlockRendererDispatcher var17 = this.field_147917_g.func_175602_ab();
      ModelManager var18 = var17.func_175023_a().func_178126_b();
      IBakedModel var19;
      if (var1.func_82335_i() != null && var1.func_82335_i().func_77973_b() == Items.field_151098_aY) {
         var19 = var18.func_174953_a(this.field_177073_g);
      } else {
         var19 = var18.func_174953_a(this.field_177072_f);
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
      var17.func_175019_b().func_178262_a(var19, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
      GlStateManager.func_179109_b(0.0F, 0.0F, 0.4375F);
      this.func_82402_b(var1);
      GlStateManager.func_179121_F();
      this.func_177067_a(var1, var2 + (double)((float)var1.field_174860_b.func_82601_c() * 0.3F), var4 - 0.25D, var6 + (double)((float)var1.field_174860_b.func_82599_e() * 0.3F));
   }

   protected ResourceLocation func_110775_a(EntityItemFrame var1) {
      return null;
   }

   private void func_82402_b(EntityItemFrame var1) {
      ItemStack var2 = var1.func_82335_i();
      if (var2 != null) {
         EntityItem var3 = new EntityItem(var1.field_70170_p, 0.0D, 0.0D, 0.0D, var2);
         Item var4 = var3.func_92059_d().func_77973_b();
         var3.func_92059_d().field_77994_a = 1;
         var3.field_70290_d = 0.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179140_f();
         int var5 = var1.func_82333_j();
         if (var4 == Items.field_151098_aY) {
            var5 = var5 % 4 * 2;
         }

         GlStateManager.func_179114_b((float)var5 * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (var4 == Items.field_151098_aY) {
            this.field_76990_c.field_78724_e.func_110577_a(field_110789_a);
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
            float var6 = 0.0078125F;
            GlStateManager.func_179152_a(var6, var6, var6);
            GlStateManager.func_179109_b(-64.0F, -64.0F, 0.0F);
            MapData var7 = Items.field_151098_aY.func_77873_a(var3.func_92059_d(), var1.field_70170_p);
            GlStateManager.func_179109_b(0.0F, 0.0F, -1.0F);
            if (var7 != null) {
               this.field_147917_g.field_71460_t.func_147701_i().func_148250_a(var7, true);
            }
         } else {
            TextureAtlasSprite var12 = null;
            if (var4 == Items.field_151111_aL) {
               var12 = this.field_147917_g.func_147117_R().func_110572_b(TextureCompass.field_176608_l);
               this.field_147917_g.func_110434_K().func_110577_a(TextureMap.field_110575_b);
               if (var12 instanceof TextureCompass) {
                  TextureCompass var13 = (TextureCompass)var12;
                  double var8 = var13.field_94244_i;
                  double var10 = var13.field_94242_j;
                  var13.field_94244_i = 0.0D;
                  var13.field_94242_j = 0.0D;
                  var13.func_94241_a(var1.field_70170_p, var1.field_70165_t, var1.field_70161_v, (double)MathHelper.func_76142_g((float)(180 + var1.field_174860_b.func_176736_b() * 90)), false, true);
                  var13.field_94244_i = var8;
                  var13.field_94242_j = var10;
               } else {
                  var12 = null;
               }
            }

            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
            if (!this.field_177074_h.func_175050_a(var3.func_92059_d()) || var4 instanceof ItemSkull) {
               GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179123_a();
            RenderHelper.func_74519_b();
            this.field_177074_h.func_181564_a(var3.func_92059_d(), ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.func_74518_a();
            GlStateManager.func_179099_b();
            if (var12 != null && var12.func_110970_k() > 0) {
               var12.func_94219_l();
            }
         }

         GlStateManager.func_179145_e();
         GlStateManager.func_179121_F();
      }
   }

   protected void func_177067_a(EntityItemFrame var1, double var2, double var4, double var6) {
      if (Minecraft.func_71382_s() && var1.func_82335_i() != null && var1.func_82335_i().func_82837_s() && this.field_76990_c.field_147941_i == var1) {
         float var8 = 1.6F;
         float var9 = 0.016666668F * var8;
         double var10 = var1.func_70068_e(this.field_76990_c.field_78734_h);
         float var12 = var1.func_70093_af() ? 32.0F : 64.0F;
         if (var10 < (double)(var12 * var12)) {
            String var13 = var1.func_82335_i().func_82833_r();
            if (var1.func_70093_af()) {
               FontRenderer var14 = this.func_76983_a();
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)var2 + 0.0F, (float)var4 + var1.field_70131_O + 0.5F, (float)var6);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179152_a(-var9, -var9, var9);
               GlStateManager.func_179140_f();
               GlStateManager.func_179109_b(0.0F, 0.25F / var9, 0.0F);
               GlStateManager.func_179132_a(false);
               GlStateManager.func_179147_l();
               GlStateManager.func_179112_b(770, 771);
               Tessellator var15 = Tessellator.func_178181_a();
               WorldRenderer var16 = var15.func_178180_c();
               int var17 = var14.func_78256_a(var13) / 2;
               GlStateManager.func_179090_x();
               var16.func_181668_a(7, DefaultVertexFormats.field_181706_f);
               var16.func_181662_b((double)(-var17 - 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(-var17 - 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(var17 + 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(var17 + 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var15.func_78381_a();
               GlStateManager.func_179098_w();
               GlStateManager.func_179132_a(true);
               var14.func_78276_b(var13, -var14.func_78256_a(var13) / 2, 0, 553648127);
               GlStateManager.func_179145_e();
               GlStateManager.func_179084_k();
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.func_179121_F();
            } else {
               this.func_147906_a(var1, var13, var2, var4, var6, 64);
            }
         }
      }

   }
}
