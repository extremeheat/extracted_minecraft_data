package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapItemRenderer implements AutoCloseable {
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
      MapItemRenderer.Instance var2 = (MapItemRenderer.Instance)this.field_148252_c.get(var1.func_195925_e());
      if (var2 == null) {
         var2 = new MapItemRenderer.Instance(var1);
         this.field_148252_c.put(var1.func_195925_e(), var2);
      }

      return var2;
   }

   @Nullable
   public MapItemRenderer.Instance func_191205_a(String var1) {
      return (MapItemRenderer.Instance)this.field_148252_c.get(var1);
   }

   public void func_148249_a() {
      Iterator var1 = this.field_148252_c.values().iterator();

      while(var1.hasNext()) {
         MapItemRenderer.Instance var2 = (MapItemRenderer.Instance)var1.next();
         var2.close();
      }

      this.field_148252_c.clear();
   }

   @Nullable
   public MapData func_191207_a(@Nullable MapItemRenderer.Instance var1) {
      return var1 != null ? var1.field_148242_b : null;
   }

   public void close() {
      this.func_148249_a();
   }

   class Instance implements AutoCloseable {
      private final MapData field_148242_b;
      private final DynamicTexture field_148243_c;
      private final ResourceLocation field_148240_d;

      private Instance(MapData var2) {
         super();
         this.field_148242_b = var2;
         this.field_148243_c = new DynamicTexture(128, 128, true);
         this.field_148240_d = MapItemRenderer.this.field_148251_b.func_110578_a("map/" + var2.func_195925_e(), this.field_148243_c);
      }

      private void func_148236_a() {
         for(int var1 = 0; var1 < 128; ++var1) {
            for(int var2 = 0; var2 < 128; ++var2) {
               int var3 = var2 + var1 * 128;
               int var4 = this.field_148242_b.field_76198_e[var3] & 255;
               if (var4 / 4 == 0) {
                  this.field_148243_c.func_195414_e().func_195700_a(var2, var1, (var3 + var3 / 128 & 1) * 8 + 16 << 24);
               } else {
                  this.field_148243_c.func_195414_e().func_195700_a(var2, var1, MaterialColor.field_76281_a[var4 / 4].func_151643_b(var4 & 3));
               }
            }
         }

         this.field_148243_c.func_110564_a();
      }

      private void func_148237_a(boolean var1) {
         boolean var2 = false;
         boolean var3 = false;
         Tessellator var4 = Tessellator.func_178181_a();
         BufferBuilder var5 = var4.func_178180_c();
         float var6 = 0.0F;
         MapItemRenderer.this.field_148251_b.func_110577_a(this.field_148240_d);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.func_179118_c();
         var5.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var5.func_181662_b(0.0D, 128.0D, -0.009999999776482582D).func_187315_a(0.0D, 1.0D).func_181675_d();
         var5.func_181662_b(128.0D, 128.0D, -0.009999999776482582D).func_187315_a(1.0D, 1.0D).func_181675_d();
         var5.func_181662_b(128.0D, 0.0D, -0.009999999776482582D).func_187315_a(1.0D, 0.0D).func_181675_d();
         var5.func_181662_b(0.0D, 0.0D, -0.009999999776482582D).func_187315_a(0.0D, 0.0D).func_181675_d();
         var4.func_78381_a();
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
         int var7 = 0;
         Iterator var8 = this.field_148242_b.field_76203_h.values().iterator();

         while(true) {
            MapDecoration var9;
            do {
               if (!var8.hasNext()) {
                  GlStateManager.func_179094_E();
                  GlStateManager.func_179109_b(0.0F, 0.0F, -0.04F);
                  GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
                  GlStateManager.func_179121_F();
                  return;
               }

               var9 = (MapDecoration)var8.next();
            } while(var1 && !var9.func_191180_f());

            MapItemRenderer.this.field_148251_b.func_110577_a(MapItemRenderer.field_148253_a);
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0F + (float)var9.func_176112_b() / 2.0F + 64.0F, 0.0F + (float)var9.func_176113_c() / 2.0F + 64.0F, -0.02F);
            GlStateManager.func_179114_b((float)(var9.func_176111_d() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179152_a(4.0F, 4.0F, 3.0F);
            GlStateManager.func_179109_b(-0.125F, 0.125F, 0.0F);
            byte var10 = var9.func_176110_a();
            float var11 = (float)(var10 % 16 + 0) / 16.0F;
            float var12 = (float)(var10 / 16 + 0) / 16.0F;
            float var13 = (float)(var10 % 16 + 1) / 16.0F;
            float var14 = (float)(var10 / 16 + 1) / 16.0F;
            var5.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            float var15 = -0.001F;
            var5.func_181662_b(-1.0D, 1.0D, (double)((float)var7 * -0.001F)).func_187315_a((double)var11, (double)var12).func_181675_d();
            var5.func_181662_b(1.0D, 1.0D, (double)((float)var7 * -0.001F)).func_187315_a((double)var13, (double)var12).func_181675_d();
            var5.func_181662_b(1.0D, -1.0D, (double)((float)var7 * -0.001F)).func_187315_a((double)var13, (double)var14).func_181675_d();
            var5.func_181662_b(-1.0D, -1.0D, (double)((float)var7 * -0.001F)).func_187315_a((double)var11, (double)var14).func_181675_d();
            var4.func_78381_a();
            GlStateManager.func_179121_F();
            if (var9.func_204309_g() != null) {
               FontRenderer var16 = Minecraft.func_71410_x().field_71466_p;
               String var17 = var9.func_204309_g().func_150254_d();
               float var18 = (float)var16.func_78256_a(var17);
               float var19 = MathHelper.func_76131_a(25.0F / var18, 0.0F, 6.0F / (float)var16.field_78288_b);
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b(0.0F + (float)var9.func_176112_b() / 2.0F + 64.0F - var18 * var19 / 2.0F, 0.0F + (float)var9.func_176113_c() / 2.0F + 64.0F + 4.0F, -0.025F);
               GlStateManager.func_179152_a(var19, var19, 1.0F);
               GuiIngame.func_73734_a(-1, -1, (int)var18, var16.field_78288_b - 1, -2147483648);
               GlStateManager.func_179109_b(0.0F, 0.0F, -0.1F);
               var16.func_211126_b(var17, 0.0F, 0.0F, -1);
               GlStateManager.func_179121_F();
            }

            ++var7;
         }
      }

      public void close() {
         this.field_148243_c.close();
      }

      // $FF: synthetic method
      Instance(MapData var2, Object var3) {
         this(var2);
      }
   }
}
