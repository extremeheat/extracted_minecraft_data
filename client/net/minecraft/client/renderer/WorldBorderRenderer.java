package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
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
import net.minecraft.client.renderer.state.WorldBorderRenderState;
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

   private void rebuildWorldBorderBuffer(WorldBorderRenderState var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      try (ByteBufferBuilder var11 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4 * 4)) {
         double var12 = var1.minX;
         double var14 = var1.maxX;
         double var16 = var1.minZ;
         double var18 = var1.maxZ;
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

   public void extract(WorldBorder var1, Vec3 var2, double var3, WorldBorderRenderState var5) {
      var5.minX = var1.getMinX();
      var5.maxX = var1.getMaxX();
      var5.minZ = var1.getMinZ();
      var5.maxZ = var1.getMaxZ();
      if ((!(var2.x < var5.maxX - var3) || !(var2.x > var5.minX + var3) || !(var2.z < var5.maxZ - var3) || !(var2.z > var5.minZ + var3)) && !(var2.x < var5.minX - var3) && !(var2.x > var5.maxX + var3) && !(var2.z < var5.minZ - var3) && !(var2.z > var5.maxZ + var3)) {
         var5.alpha = 1.0 - var1.getDistanceToBorder(var2.x, var2.z) / var3;
         var5.alpha = Math.pow(var5.alpha, 4.0);
         var5.alpha = Mth.clamp(var5.alpha, 0.0, 1.0);
         var5.tint = var1.getStatus().getColor();
      } else {
         var5.alpha = 0.0;
      }
   }

   public void render(WorldBorderRenderState var1, Vec3 var2, double var3, double var5) {
      if (!(var1.alpha <= 0.0)) {
         double var7 = var2.x;
         double var9 = var2.z;
         float var11 = (float)var5;
         float var12 = (float)ARGB.red(var1.tint) / 255.0F;
         float var13 = (float)ARGB.green(var1.tint) / 255.0F;
         float var14 = (float)ARGB.blue(var1.tint) / 255.0F;
         float var15 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var16 = (float)(-Mth.frac(var2.y * 0.5));
         float var17 = var16 + var11;
         if (this.shouldRebuildWorldBorderBuffer(var1)) {
            this.rebuildWorldBorderBuffer(var1, var3, var9, var7, var11, var17, var16);
         }

         TextureManager var18 = Minecraft.getInstance().getTextureManager();
         AbstractTexture var19 = var18.getTexture(FORCEFIELD_LOCATION);
         var19.setUseMipmaps(false);
         RenderPipeline var20 = RenderPipelines.WORLD_BORDER;
         RenderTarget var21 = Minecraft.getInstance().getMainRenderTarget();
         RenderTarget var22 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
         GpuTextureView var23;
         GpuTextureView var24;
         if (var22 != null) {
            var23 = var22.getColorTextureView();
            var24 = var22.getDepthTextureView();
         } else {
            var23 = var21.getColorTextureView();
            var24 = var21.getDepthTextureView();
         }

         GpuBuffer var25 = this.indices.getBuffer(6);
         GpuBufferSlice var26 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(var12, var13, var14, (float)var1.alpha), new Vector3f((float)(this.lastMinX - var7), (float)(-var2.y), (float)(this.lastMinZ - var9)), (new Matrix4f()).translation(var15, var15, 0.0F), 0.0F);

         try (RenderPass var27 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "World border", var23, OptionalInt.empty(), var24, OptionalDouble.empty())) {
            var27.setPipeline(var20);
            RenderSystem.bindDefaultUniforms(var27);
            var27.setUniform("DynamicTransforms", var26);
            var27.setIndexBuffer(var25, this.indices.type());
            var27.bindSampler("Sampler0", var19.getTextureView());
            var27.setVertexBuffer(0, this.worldBorderBuffer);
            ArrayList var28 = new ArrayList();

            for(WorldBorderRenderState.DistancePerDirection var30 : var1.closestBorder(var7, var9)) {
               if (var30.distance() < var3) {
                  int var31 = var30.direction().get2DDataValue();
                  var28.add(new RenderPass.Draw(0, this.worldBorderBuffer, var25, this.indices.type(), 6 * var31, 6));
               }
            }

            var27.drawMultipleIndexed(var28, (GpuBuffer)null, (VertexFormat.IndexType)null, Collections.emptyList(), this);
         }

      }
   }

   public void invalidate() {
      this.needsRebuild = true;
   }

   private boolean shouldRebuildWorldBorderBuffer(WorldBorderRenderState var1) {
      return this.needsRebuild || var1.minX != this.lastBorderMinX || var1.minZ != this.lastBorderMinZ || var1.maxX != this.lastBorderMaxX || var1.maxZ != this.lastBorderMaxZ;
   }
}
