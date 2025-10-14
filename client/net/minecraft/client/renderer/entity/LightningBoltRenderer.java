package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LightningBoltRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt, LightningBoltRenderState> {
   public LightningBoltRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void submit(LightningBoltRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      float[] var5 = new float[8];
      float[] var6 = new float[8];
      float var7 = 0.0F;
      float var8 = 0.0F;
      RandomSource var9 = RandomSource.create(var1.seed);

      for(int var10 = 7; var10 >= 0; --var10) {
         var5[var10] = var7;
         var6[var10] = var8;
         var7 += (float)(var9.nextInt(11) - 5);
         var8 += (float)(var9.nextInt(11) - 5);
      }

      var3.submitCustomGeometry(var2, RenderType.lightning(), (var5x, var6x) -> {
         Matrix4f var7x = var5x.pose();

         for(int var8x = 0; var8x < 4; ++var8x) {
            RandomSource var9 = RandomSource.create(var1.seed);

            for(int var10 = 0; var10 < 3; ++var10) {
               int var11 = 7;
               int var12 = 0;
               if (var10 > 0) {
                  var11 = 7 - var10;
               }

               if (var10 > 0) {
                  var12 = var11 - 2;
               }

               float var13 = var5[var11] - var7;
               float var14 = var6[var11] - var8;

               for(int var15 = var11; var15 >= var12; --var15) {
                  float var16 = var13;
                  float var17 = var14;
                  if (var10 == 0) {
                     var13 += (float)(var9.nextInt(11) - 5);
                     var14 += (float)(var9.nextInt(11) - 5);
                  } else {
                     var13 += (float)(var9.nextInt(31) - 15);
                     var14 += (float)(var9.nextInt(31) - 15);
                  }

                  float var18 = 0.5F;
                  float var19 = 0.45F;
                  float var20 = 0.45F;
                  float var21 = 0.5F;
                  float var22 = 0.1F + (float)var8x * 0.2F;
                  if (var10 == 0) {
                     var22 *= (float)var15 * 0.1F + 1.0F;
                  }

                  float var23 = 0.1F + (float)var8x * 0.2F;
                  if (var10 == 0) {
                     var23 *= ((float)var15 - 1.0F) * 0.1F + 1.0F;
                  }

                  quad(var7x, var6x, var13, var14, var15, var16, var17, 0.45F, 0.45F, 0.5F, var22, var23, false, false, true, false);
                  quad(var7x, var6x, var13, var14, var15, var16, var17, 0.45F, 0.45F, 0.5F, var22, var23, true, false, true, true);
                  quad(var7x, var6x, var13, var14, var15, var16, var17, 0.45F, 0.45F, 0.5F, var22, var23, true, true, false, true);
                  quad(var7x, var6x, var13, var14, var15, var16, var17, 0.45F, 0.45F, 0.5F, var22, var23, false, true, false, false);
               }
            }
         }

      });
   }

   private static void quad(Matrix4f var0, VertexConsumer var1, float var2, float var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, boolean var12, boolean var13, boolean var14, boolean var15) {
      var1.addVertex((Matrix4fc)var0, var2 + (var12 ? var11 : -var11), (float)(var4 * 16), var3 + (var13 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex((Matrix4fc)var0, var5 + (var12 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var13 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex((Matrix4fc)var0, var5 + (var14 ? var10 : -var10), (float)((var4 + 1) * 16), var6 + (var15 ? var10 : -var10)).setColor(var7, var8, var9, 0.3F);
      var1.addVertex((Matrix4fc)var0, var2 + (var14 ? var11 : -var11), (float)(var4 * 16), var3 + (var15 ? var11 : -var11)).setColor(var7, var8, var9, 0.3F);
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

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected boolean affectedByCulling(final Entity var1) {
      return this.affectedByCulling((LightningBolt)var1);
   }
}
