package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldBorderRenderer {
   public static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
   private boolean needsRebuild = true;
   private double lastMinX;
   private double lastMinZ;
   private double lastBorderMinX;
   private double lastBorderMaxX;
   private double lastBorderMinZ;
   private double lastBorderMaxZ;
   private final GpuBuffer worldBorderBuffer;
   private final RenderSystem.AutoStorageIndexBuffer indices;

   public WorldBorderRenderer() {
      super();
      this.worldBorderBuffer = RenderSystem.getDevice().createBuffer(() -> "World border vertex buffer", 40, 16 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
      this.indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
   }

   private void rebuildWorldBorderBuffer(WorldBorder var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      try (ByteBufferBuilder var11 = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
         double var12 = var1.getMinX();
         double var14 = var1.getMaxX();
         double var16 = var1.getMinZ();
         double var18 = var1.getMaxZ();
         double var20 = Math.max((double)Mth.floor(var4 - var2), var16);
         double var22 = Math.min((double)Mth.ceil(var4 + var2), var18);
         float var24 = (float)(Mth.floor(var20) & 1) * 0.5F;
         float var25 = (float)(var22 - var20) / 2.0F;
         double var26 = Math.max((double)Mth.floor(var6 - var2), var12);
         double var28 = Math.min((double)Mth.ceil(var6 + var2), var14);
         float var30 = (float)(Mth.floor(var26) & 1) * 0.5F;
         float var31 = (float)(var28 - var26) / 2.0F;
         BufferBuilder var32 = new BufferBuilder(var11, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         var32.addVertex(0.0F, -var8, (float)(var18 - var20)).setUv(var30, var9);
         var32.addVertex((float)(var28 - var26), -var8, (float)(var18 - var20)).setUv(var31 + var30, var9);
         var32.addVertex((float)(var28 - var26), var8, (float)(var18 - var20)).setUv(var31 + var30, var10);
         var32.addVertex(0.0F, var8, (float)(var18 - var20)).setUv(var30, var10);
         var32.addVertex(0.0F, -var8, 0.0F).setUv(var24, var9);
         var32.addVertex(0.0F, -var8, (float)(var22 - var20)).setUv(var25 + var24, var9);
         var32.addVertex(0.0F, var8, (float)(var22 - var20)).setUv(var25 + var24, var10);
         var32.addVertex(0.0F, var8, 0.0F).setUv(var24, var10);
         var32.addVertex((float)(var28 - var26), -var8, 0.0F).setUv(var30, var9);
         var32.addVertex(0.0F, -var8, 0.0F).setUv(var31 + var30, var9);
         var32.addVertex(0.0F, var8, 0.0F).setUv(var31 + var30, var10);
         var32.addVertex((float)(var28 - var26), var8, 0.0F).setUv(var30, var10);
         var32.addVertex((float)(var14 - var26), -var8, (float)(var22 - var20)).setUv(var24, var9);
         var32.addVertex((float)(var14 - var26), -var8, 0.0F).setUv(var25 + var24, var9);
         var32.addVertex((float)(var14 - var26), var8, 0.0F).setUv(var25 + var24, var10);
         var32.addVertex((float)(var14 - var26), var8, (float)(var22 - var20)).setUv(var24, var10);

         try (MeshData var33 = var32.buildOrThrow()) {
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.worldBorderBuffer.slice(), var33.vertexBuffer());
         }

         this.lastBorderMinX = var12;
         this.lastBorderMaxX = var14;
         this.lastBorderMinZ = var16;
         this.lastBorderMaxZ = var18;
         this.lastMinX = var26;
         this.lastMinZ = var20;
         this.needsRebuild = false;
      }

   }

   public void render(WorldBorder var1, Vec3 var2, double var3, double var5) {
      double var7 = var1.getMinX();
      double var9 = var1.getMaxX();
      double var11 = var1.getMinZ();
      double var13 = var1.getMaxZ();
      if ((!(var2.x < var9 - var3) || !(var2.x > var7 + var3) || !(var2.z < var13 - var3) || !(var2.z > var11 + var3)) && !(var2.x < var7 - var3) && !(var2.x > var9 + var3) && !(var2.z < var11 - var3) && !(var2.z > var13 + var3)) {
         double var15 = 1.0 - var1.getDistanceToBorder(var2.x, var2.z) / var3;
         var15 = Math.pow(var15, 4.0);
         var15 = Mth.clamp(var15, 0.0, 1.0);
         double var17 = var2.x;
         double var19 = var2.z;
         float var21 = (float)var5;
         int var22 = var1.getStatus().getColor();
         float var23 = (float)ARGB.red(var22) / 255.0F;
         float var24 = (float)ARGB.green(var22) / 255.0F;
         float var25 = (float)ARGB.blue(var22) / 255.0F;
         float var26 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var27 = (float)(-Mth.frac(var2.y * 0.5));
         float var28 = var27 + var21;
         if (this.shouldRebuildWorldBorderBuffer(var1)) {
            this.rebuildWorldBorderBuffer(var1, var3, var19, var17, var21, var28, var27);
         }

         TextureManager var29 = Minecraft.getInstance().getTextureManager();
         AbstractTexture var30 = var29.getTexture(FORCEFIELD_LOCATION);
         var30.setUseMipmaps(false);
         RenderPipeline var31 = RenderPipelines.WORLD_BORDER;
         RenderTarget var32 = Minecraft.getInstance().getMainRenderTarget();
         RenderTarget var33 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
         GpuTexture var34;
         GpuTexture var35;
         if (var33 != null) {
            var34 = var33.getColorTexture();
            var35 = var33.getDepthTexture();
         } else {
            var34 = var32.getColorTexture();
            var35 = var32.getDepthTexture();
         }

         GpuBuffer var36 = this.indices.getBuffer(6);
         GpuBufferSlice var37 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(var23, var24, var25, (float)var15), new Vector3f((float)(this.lastMinX - var17), (float)(-var2.y), (float)(this.lastMinZ - var19)), (new Matrix4f()).translation(var26, var26, 0.0F), 0.0F);

         try (RenderPass var38 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var34, OptionalInt.empty(), var35, OptionalDouble.empty())) {
            var38.setPipeline(var31);
            RenderSystem.bindDefaultUniforms(var38);
            var38.setUniform("DynamicTransforms", var37);
            var38.setIndexBuffer(var36, this.indices.type());
            var38.bindSampler("Sampler0", var30.getTexture());
            var38.setVertexBuffer(0, this.worldBorderBuffer);
            ArrayList var39 = new ArrayList();

            for(WorldBorder.DistancePerDirection var41 : var1.closestBorder(var17, var19)) {
               if (var41.distance() < var3) {
                  int var42 = var41.direction().get2DDataValue();
                  var39.add(new RenderPass.Draw(0, this.worldBorderBuffer, var36, this.indices.type(), 6 * var42, 6));
               }
            }

            var38.drawMultipleIndexed(var39, (GpuBuffer)null, (VertexFormat.IndexType)null, Collections.emptyList());
         }

      }
   }

   public void invalidate() {
      this.needsRebuild = true;
   }

   private boolean shouldRebuildWorldBorderBuffer(WorldBorder var1) {
      return this.needsRebuild || var1.getMinX() != this.lastBorderMinX || var1.getMinZ() != this.lastBorderMinZ || var1.getMaxX() != this.lastBorderMaxX || var1.getMaxZ() != this.lastBorderMaxZ;
   }
}
