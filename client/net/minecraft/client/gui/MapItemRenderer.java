package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer {
   private static final ResourceLocation field_148253_a = new ResourceLocation("textures/map/map_icons.png");
   private final TextureManager field_148251_b;
   private final Map<String, MapItemRenderer.Instance> field_148252_c = Maps.newHashMap();

   public MapItemRenderer(TextureManager var1) {
      super();
      this.field_148251_b = var1;
   }

   public void func_148246_a(MapData var1) {
      this.func_148248_b(var1).func_148236_a();
   }

   public void func_148250_a(MapData var1, boolean var2) {
      this.func_148248_b(var1).func_148237_a(var2);
   }

   private MapItemRenderer.Instance func_148248_b(MapData var1) {
      MapItemRenderer.Instance var2 = (MapItemRenderer.Instance)this.field_148252_c.get(var1.field_76190_i);
      if (var2 == null) {
         var2 = new MapItemRenderer.Instance(var1);
         this.field_148252_c.put(var1.field_76190_i, var2);
      }

      return var2;
   }

   public void func_148249_a() {
      Iterator var1 = this.field_148252_c.values().iterator();

      while(var1.hasNext()) {
         MapItemRenderer.Instance var2 = (MapItemRenderer.Instance)var1.next();
         this.field_148251_b.func_147645_c(var2.field_148240_d);
      }

      this.field_148252_c.clear();
   }

   class Instance {
      private final MapData field_148242_b;
      private final DynamicTexture field_148243_c;
      private final ResourceLocation field_148240_d;
      private final int[] field_148241_e;

      private Instance(MapData var2) {
         super();
         this.field_148242_b = var2;
         this.field_148243_c = new DynamicTexture(128, 128);
         this.field_148241_e = this.field_148243_c.func_110565_c();
         this.field_148240_d = MapItemRenderer.this.field_148251_b.func_110578_a("map/" + var2.field_76190_i, this.field_148243_c);

         for(int var3 = 0; var3 < this.field_148241_e.length; ++var3) {
            this.field_148241_e[var3] = 0;
         }

      }

      private void func_148236_a() {
         for(int var1 = 0; var1 < 16384; ++var1) {
            int var2 = this.field_148242_b.field_76198_e[var1] & 255;
            if (var2 / 4 == 0) {
               this.field_148241_e[var1] = (var1 + var1 / 128 & 1) * 8 + 16 << 24;
            } else {
               this.field_148241_e[var1] = MapColor.field_76281_a[var2 / 4].func_151643_b(var2 & 3);
            }
         }

         this.field_148243_c.func_110564_a();
      }

      private void func_148237_a(boolean var1) {
         byte var2 = 0;
         byte var3 = 0;
         Tessellator var4 = Tessellator.func_178181_a();
         WorldRenderer var5 = var4.func_178180_c();
         float var6 = 0.0F;
         MapItemRenderer.this.field_148251_b.func_110577_a(this.field_148240_d);
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(1, 771, 0, 1);
         GlStateManager.func_179118_c();
         var5.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var5.func_181662_b((double)((float)(var2 + 0) + var6), (double)((float)(var3 + 128) - var6), -0.009999999776482582D).func_181673_a(0.0D, 1.0D).func_181675_d();
         var5.func_181662_b((double)((float)(var2 + 128) - var6), (double)((float)(var3 + 128) - var6), -0.009999999776482582D).func_181673_a(1.0D, 1.0D).func_181675_d();
         var5.func_181662_b((double)((float)(var2 + 128) - var6), (double)((float)(var3 + 0) + var6), -0.009999999776482582D).func_181673_a(1.0D, 0.0D).func_181675_d();
         var5.func_181662_b((double)((float)(var2 + 0) + var6), (double)((float)(var3 + 0) + var6), -0.009999999776482582D).func_181673_a(0.0D, 0.0D).func_181675_d();
         var4.func_78381_a();
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
         MapItemRenderer.this.field_148251_b.func_110577_a(MapItemRenderer.field_148253_a);
         int var7 = 0;
         Iterator var8 = this.field_148242_b.field_76203_h.values().iterator();

         while(true) {
            Vec4b var9;
            do {
               if (!var8.hasNext()) {
                  GlStateManager.func_179094_E();
                  GlStateManager.func_179109_b(0.0F, 0.0F, -0.04F);
                  GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
                  GlStateManager.func_179121_F();
                  return;
               }

               var9 = (Vec4b)var8.next();
            } while(var1 && var9.func_176110_a() != 1);

            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)var2 + (float)var9.func_176112_b() / 2.0F + 64.0F, (float)var3 + (float)var9.func_176113_c() / 2.0F + 64.0F, -0.02F);
            GlStateManager.func_179114_b((float)(var9.func_176111_d() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179152_a(4.0F, 4.0F, 3.0F);
            GlStateManager.func_179109_b(-0.125F, 0.125F, 0.0F);
            byte var10 = var9.func_176110_a();
            float var11 = (float)(var10 % 4 + 0) / 4.0F;
            float var12 = (float)(var10 / 4 + 0) / 4.0F;
            float var13 = (float)(var10 % 4 + 1) / 4.0F;
            float var14 = (float)(var10 / 4 + 1) / 4.0F;
            var5.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            float var15 = -0.001F;
            var5.func_181662_b(-1.0D, 1.0D, (double)((float)var7 * -0.001F)).func_181673_a((double)var11, (double)var12).func_181675_d();
            var5.func_181662_b(1.0D, 1.0D, (double)((float)var7 * -0.001F)).func_181673_a((double)var13, (double)var12).func_181675_d();
            var5.func_181662_b(1.0D, -1.0D, (double)((float)var7 * -0.001F)).func_181673_a((double)var13, (double)var14).func_181675_d();
            var5.func_181662_b(-1.0D, -1.0D, (double)((float)var7 * -0.001F)).func_181673_a((double)var11, (double)var14).func_181675_d();
            var4.func_78381_a();
            GlStateManager.func_179121_F();
            ++var7;
         }
      }

      // $FF: synthetic method
      Instance(MapData var2, Object var3) {
         this(var2);
      }
   }
}
