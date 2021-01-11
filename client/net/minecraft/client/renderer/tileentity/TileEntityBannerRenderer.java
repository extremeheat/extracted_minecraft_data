package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner> {
   private static final Map<String, TileEntityBannerRenderer.TimedBannerTexture> field_178466_c = Maps.newHashMap();
   private static final ResourceLocation field_178464_d = new ResourceLocation("textures/entity/banner_base.png");
   private ModelBanner field_178465_e = new ModelBanner();

   public TileEntityBannerRenderer() {
      super();
   }

   public void func_180535_a(TileEntityBanner var1, double var2, double var4, double var6, float var8, int var9) {
      boolean var10 = var1.func_145831_w() != null;
      boolean var11 = !var10 || var1.func_145838_q() == Blocks.field_180393_cK;
      int var12 = var10 ? var1.func_145832_p() : 0;
      long var13 = var10 ? var1.func_145831_w().func_82737_E() : 0L;
      GlStateManager.func_179094_E();
      float var15 = 0.6666667F;
      float var17;
      if (var11) {
         GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 + 0.75F * var15, (float)var6 + 0.5F);
         float var16 = (float)(var12 * 360) / 16.0F;
         GlStateManager.func_179114_b(-var16, 0.0F, 1.0F, 0.0F);
         this.field_178465_e.field_178688_b.field_78806_j = true;
      } else {
         var17 = 0.0F;
         if (var12 == 2) {
            var17 = 180.0F;
         }

         if (var12 == 4) {
            var17 = 90.0F;
         }

         if (var12 == 5) {
            var17 = -90.0F;
         }

         GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 - 0.25F * var15, (float)var6 + 0.5F);
         GlStateManager.func_179114_b(-var17, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.3125F, -0.4375F);
         this.field_178465_e.field_178688_b.field_78806_j = false;
      }

      BlockPos var19 = var1.func_174877_v();
      var17 = (float)(var19.func_177958_n() * 7 + var19.func_177956_o() * 9 + var19.func_177952_p() * 13) + (float)var13 + var8;
      this.field_178465_e.field_178690_a.field_78795_f = (-0.0125F + 0.01F * MathHelper.func_76134_b(var17 * 3.1415927F * 0.02F)) * 3.1415927F;
      GlStateManager.func_179091_B();
      ResourceLocation var18 = this.func_178463_a(var1);
      if (var18 != null) {
         this.func_147499_a(var18);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(var15, -var15, -var15);
         this.field_178465_e.func_178687_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
   }

   private ResourceLocation func_178463_a(TileEntityBanner var1) {
      String var2 = var1.func_175116_e();
      if (var2.isEmpty()) {
         return null;
      } else {
         TileEntityBannerRenderer.TimedBannerTexture var3 = (TileEntityBannerRenderer.TimedBannerTexture)field_178466_c.get(var2);
         if (var3 == null) {
            if (field_178466_c.size() >= 256) {
               long var4 = System.currentTimeMillis();
               Iterator var6 = field_178466_c.keySet().iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  TileEntityBannerRenderer.TimedBannerTexture var8 = (TileEntityBannerRenderer.TimedBannerTexture)field_178466_c.get(var7);
                  if (var4 - var8.field_178472_a > 60000L) {
                     Minecraft.func_71410_x().func_110434_K().func_147645_c(var8.field_178471_b);
                     var6.remove();
                  }
               }

               if (field_178466_c.size() >= 256) {
                  return null;
               }
            }

            List var9 = var1.func_175114_c();
            List var5 = var1.func_175110_d();
            ArrayList var10 = Lists.newArrayList();
            Iterator var11 = var9.iterator();

            while(var11.hasNext()) {
               TileEntityBanner.EnumBannerPattern var12 = (TileEntityBanner.EnumBannerPattern)var11.next();
               var10.add("textures/entity/banner/" + var12.func_177271_a() + ".png");
            }

            var3 = new TileEntityBannerRenderer.TimedBannerTexture();
            var3.field_178471_b = new ResourceLocation(var2);
            Minecraft.func_71410_x().func_110434_K().func_110579_a(var3.field_178471_b, new LayeredColorMaskTexture(field_178464_d, var10, var5));
            field_178466_c.put(var2, var3);
         }

         var3.field_178472_a = System.currentTimeMillis();
         return var3.field_178471_b;
      }
   }

   static class TimedBannerTexture {
      public long field_178472_a;
      public ResourceLocation field_178471_b;

      private TimedBannerTexture() {
         super();
      }

      // $FF: synthetic method
      TimedBannerTexture(Object var1) {
         this();
      }
   }
}
