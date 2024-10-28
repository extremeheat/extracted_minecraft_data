package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt> {
   public LightningBoltRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(LightningBolt var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      float[] var7 = new float[8];
      float[] var8 = new float[8];
      float var9 = 0.0F;
      float var10 = 0.0F;
      RandomSource var11 = RandomSource.create(var1.seed);

      for(int var12 = 7; var12 >= 0; --var12) {
         var7[var12] = var9;
         var8[var12] = var10;
         var9 += (float)(var11.nextInt(11) - 5);
         var10 += (float)(var11.nextInt(11) - 5);
      }

      VertexConsumer var29 = var5.getBuffer(RenderType.lightning());
      Matrix4f var30 = var4.last().pose();

      for(int var13 = 0; var13 < 4; ++var13) {
         RandomSource var14 = RandomSource.create(var1.seed);

         for(int var15 = 0; var15 < 3; ++var15) {
            int var16 = 7;
            int var17 = 0;
            if (var15 > 0) {
               var16 = 7 - var15;
            }

            if (var15 > 0) {
               var17 = var16 - 2;
            }

            float var18 = var7[var16] - var9;
            float var19 = var8[var16] - var10;

            for(int var20 = var16; var20 >= var17; --var20) {
               float var21 = var18;
               float var22 = var19;
               if (var15 == 0) {
                  var18 += (float)(var14.nextInt(11) - 5);
                  var19 += (float)(var14.nextInt(11) - 5);
               } else {
                  var18 += (float)(var14.nextInt(31) - 15);
                  var19 += (float)(var14.nextInt(31) - 15);
               }

               float var23 = 0.5F;
               float var24 = 0.45F;
               float var25 = 0.45F;
               float var26 = 0.5F;
               float var27 = 0.1F + (float)var13 * 0.2F;
               if (var15 == 0) {
                  var27 *= (float)var20 * 0.1F + 1.0F;
               }

               float var28 = 0.1F + (float)var13 * 0.2F;
               if (var15 == 0) {
                  var28 *= ((float)var20 - 1.0F) * 0.1F + 1.0F;
               }

               quad(var30, var29, var18, var19, var20, var21, var22, 0.45F, 0.45F, 0.5F, var27, var28, false, false, true, false);
               quad(var30, var29, var18, var19, var20, var21, var22, 0.45F, 0.45F, 0.5F, var27, var28, true, false, true, true);
               quad(var30, var29, var18, var19, var20, var21, var22, 0.45F, 0.45F, 0.5F, var27, var28, true, true, false, true);
               quad(var30, var29, var18, var19, var20, var21, var22, 0.45F, 0.45F, 0.5F, var27, var28, false, true, false, false);
            }
         }
      }

   }

   private static void quad(Matrix4f var0, VertexConsumer var1, float var2, float var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, boolean var12, boolean var13, boolean var14, boolean var15) {
      var1.addVertex(var0, var2 + (var12 ? var11 : -var11), (float)(var4 * 16), var3 + (var13 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var5 + (var12 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var13 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var5 + (var14 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var15 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var2 + (var14 ? var11 : -var11), (float)(var4 * 16), var3 + (var15 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
   }

   public ResourceLocation getTextureLocation(LightningBolt var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
