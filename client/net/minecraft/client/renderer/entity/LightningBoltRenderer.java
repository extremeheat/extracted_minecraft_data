package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LightningBoltRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt, LightningBoltRenderState> {
   public LightningBoltRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(LightningBoltRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      float[] var5 = new float[8];
      float[] var6 = new float[8];
      float var7 = 0.0F;
      float var8 = 0.0F;
      RandomSource var9 = RandomSource.create(var1.seed);

      for (int var10 = 7; var10 >= 0; var10--) {
         var5[var10] = var7;
         var6[var10] = var8;
         var7 += (float)(var9.nextInt(11) - 5);
         var8 += (float)(var9.nextInt(11) - 5);
      }

      VertexConsumer var27 = var3.getBuffer(RenderType.lightning());
      Matrix4f var28 = var2.last().pose();

      for (int var11 = 0; var11 < 4; var11++) {
         RandomSource var12 = RandomSource.create(var1.seed);

         for (int var13 = 0; var13 < 3; var13++) {
            int var14 = 7;
            int var15 = 0;
            if (var13 > 0) {
               var14 = 7 - var13;
            }

            if (var13 > 0) {
               var15 = var14 - 2;
            }

            float var16 = var5[var14] - var7;
            float var17 = var6[var14] - var8;

            for (int var18 = var14; var18 >= var15; var18--) {
               float var19 = var16;
               float var20 = var17;
               if (var13 == 0) {
                  var16 += (float)(var12.nextInt(11) - 5);
                  var17 += (float)(var12.nextInt(11) - 5);
               } else {
                  var16 += (float)(var12.nextInt(31) - 15);
                  var17 += (float)(var12.nextInt(31) - 15);
               }

               float var21 = 0.5F;
               float var22 = 0.45F;
               float var23 = 0.45F;
               float var24 = 0.5F;
               float var25 = 0.1F + (float)var11 * 0.2F;
               if (var13 == 0) {
                  var25 *= (float)var18 * 0.1F + 1.0F;
               }

               float var26 = 0.1F + (float)var11 * 0.2F;
               if (var13 == 0) {
                  var26 *= ((float)var18 - 1.0F) * 0.1F + 1.0F;
               }

               quad(var28, var27, var16, var17, var18, var19, var20, 0.45F, 0.45F, 0.5F, var25, var26, false, false, true, false);
               quad(var28, var27, var16, var17, var18, var19, var20, 0.45F, 0.45F, 0.5F, var25, var26, true, false, true, true);
               quad(var28, var27, var16, var17, var18, var19, var20, 0.45F, 0.45F, 0.5F, var25, var26, true, true, false, true);
               quad(var28, var27, var16, var17, var18, var19, var20, 0.45F, 0.45F, 0.5F, var25, var26, false, true, false, false);
            }
         }
      }
   }

   private static void quad(
      Matrix4f var0,
      VertexConsumer var1,
      float var2,
      float var3,
      int var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15
   ) {
      var1.addVertex(var0, var2 + (var12 ? var11 : -var11), (float)(var4 * 16), var3 + (var13 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var5 + (var12 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var13 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var5 + (var14 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var15 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex(var0, var2 + (var14 ? var11 : -var11), (float)(var4 * 16), var3 + (var15 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
   }

   public ResourceLocation getTextureLocation(LightningBoltRenderState var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public LightningBoltRenderState createRenderState() {
      return new LightningBoltRenderState();
   }

   public void extractRenderState(LightningBolt var1, LightningBoltRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.seed = var1.seed;
   }

   protected boolean affectedByCulling(LightningBolt var1) {
      return false;
   }
}
