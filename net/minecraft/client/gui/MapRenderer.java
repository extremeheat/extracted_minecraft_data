package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapRenderer implements AutoCloseable {
   private static final ResourceLocation MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
   private static final RenderType MAP_ICONS;
   private final TextureManager textureManager;
   private final Map maps = Maps.newHashMap();

   public MapRenderer(TextureManager var1) {
      this.textureManager = var1;
   }

   public void update(MapItemSavedData var1) {
      this.getMapInstance(var1).updateTexture();
   }

   public void render(PoseStack var1, MultiBufferSource var2, MapItemSavedData var3, boolean var4, int var5) {
      this.getMapInstance(var3).draw(var1, var2, var4, var5);
   }

   private MapRenderer.MapInstance getMapInstance(MapItemSavedData var1) {
      MapRenderer.MapInstance var2 = (MapRenderer.MapInstance)this.maps.get(var1.getId());
      if (var2 == null) {
         var2 = new MapRenderer.MapInstance(var1);
         this.maps.put(var1.getId(), var2);
      }

      return var2;
   }

   @Nullable
   public MapRenderer.MapInstance getMapInstanceIfExists(String var1) {
      return (MapRenderer.MapInstance)this.maps.get(var1);
   }

   public void resetData() {
      Iterator var1 = this.maps.values().iterator();

      while(var1.hasNext()) {
         MapRenderer.MapInstance var2 = (MapRenderer.MapInstance)var1.next();
         var2.close();
      }

      this.maps.clear();
   }

   @Nullable
   public MapItemSavedData getData(@Nullable MapRenderer.MapInstance var1) {
      return var1 != null ? var1.data : null;
   }

   public void close() {
      this.resetData();
   }

   static {
      MAP_ICONS = RenderType.text(MAP_ICONS_LOCATION);
   }

   class MapInstance implements AutoCloseable {
      private final MapItemSavedData data;
      private final DynamicTexture texture;
      private final RenderType renderType;

      private MapInstance(MapItemSavedData var2) {
         this.data = var2;
         this.texture = new DynamicTexture(128, 128, true);
         ResourceLocation var3 = MapRenderer.this.textureManager.register("map/" + var2.getId(), this.texture);
         this.renderType = RenderType.text(var3);
      }

      private void updateTexture() {
         for(int var1 = 0; var1 < 128; ++var1) {
            for(int var2 = 0; var2 < 128; ++var2) {
               int var3 = var2 + var1 * 128;
               int var4 = this.data.colors[var3] & 255;
               if (var4 / 4 == 0) {
                  this.texture.getPixels().setPixelRGBA(var2, var1, 0);
               } else {
                  this.texture.getPixels().setPixelRGBA(var2, var1, MaterialColor.MATERIAL_COLORS[var4 / 4].calculateRGBColor(var4 & 3));
               }
            }
         }

         this.texture.upload();
      }

      private void draw(PoseStack var1, MultiBufferSource var2, boolean var3, int var4) {
         boolean var5 = false;
         boolean var6 = false;
         float var7 = 0.0F;
         Matrix4f var8 = var1.last().pose();
         VertexConsumer var9 = var2.getBuffer(this.renderType);
         var9.vertex(var8, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(var4).endVertex();
         var9.vertex(var8, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(var4).endVertex();
         var9.vertex(var8, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(var4).endVertex();
         var9.vertex(var8, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(var4).endVertex();
         int var10 = 0;
         Iterator var11 = this.data.decorations.values().iterator();

         while(true) {
            MapDecoration var12;
            do {
               if (!var11.hasNext()) {
                  return;
               }

               var12 = (MapDecoration)var11.next();
            } while(var3 && !var12.renderOnFrame());

            var1.pushPose();
            var1.translate((double)(0.0F + (float)var12.getX() / 2.0F + 64.0F), (double)(0.0F + (float)var12.getY() / 2.0F + 64.0F), -0.019999999552965164D);
            var1.mulPose(Vector3f.ZP.rotationDegrees((float)(var12.getRot() * 360) / 16.0F));
            var1.scale(4.0F, 4.0F, 3.0F);
            var1.translate(-0.125D, 0.125D, 0.0D);
            byte var13 = var12.getImage();
            float var14 = (float)(var13 % 16 + 0) / 16.0F;
            float var15 = (float)(var13 / 16 + 0) / 16.0F;
            float var16 = (float)(var13 % 16 + 1) / 16.0F;
            float var17 = (float)(var13 / 16 + 1) / 16.0F;
            Matrix4f var18 = var1.last().pose();
            float var19 = -0.001F;
            VertexConsumer var20 = var2.getBuffer(MapRenderer.MAP_ICONS);
            var20.vertex(var18, -1.0F, 1.0F, (float)var10 * -0.001F).color(255, 255, 255, 255).uv(var14, var15).uv2(var4).endVertex();
            var20.vertex(var18, 1.0F, 1.0F, (float)var10 * -0.001F).color(255, 255, 255, 255).uv(var16, var15).uv2(var4).endVertex();
            var20.vertex(var18, 1.0F, -1.0F, (float)var10 * -0.001F).color(255, 255, 255, 255).uv(var16, var17).uv2(var4).endVertex();
            var20.vertex(var18, -1.0F, -1.0F, (float)var10 * -0.001F).color(255, 255, 255, 255).uv(var14, var17).uv2(var4).endVertex();
            var1.popPose();
            if (var12.getName() != null) {
               Font var21 = Minecraft.getInstance().font;
               String var22 = var12.getName().getColoredString();
               float var23 = (float)var21.width(var22);
               float var10000 = 25.0F / var23;
               var21.getClass();
               float var24 = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
               var1.pushPose();
               var1.translate((double)(0.0F + (float)var12.getX() / 2.0F + 64.0F - var23 * var24 / 2.0F), (double)(0.0F + (float)var12.getY() / 2.0F + 64.0F + 4.0F), -0.02500000037252903D);
               var1.scale(var24, var24, 1.0F);
               var1.translate(0.0D, 0.0D, -0.10000000149011612D);
               var21.drawInBatch(var22, 0.0F, 0.0F, -1, false, var1.last().pose(), var2, false, Integer.MIN_VALUE, var4);
               var1.popPose();
            }

            ++var10;
         }
      }

      public void close() {
         this.texture.close();
      }

      // $FF: synthetic method
      MapInstance(MapItemSavedData var2, Object var3) {
         this(var2);
      }
   }
}
