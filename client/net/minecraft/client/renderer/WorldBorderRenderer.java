package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.state.level.WorldBorderRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MipmappedTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class WorldBorderRenderer implements AutoCloseable {
   public static final Identifier FORCEFIELD_LOCATION = Identifier.withDefaultNamespace("textures/misc/forcefield.png");
   private static final int FORCEFIELD_MIP_LEVEL = 4;
   private static final double RENDERING_OFFSET = 0.01;
   private boolean needsRebuild = true;
   private double lastMinX;
   private double lastMinZ;
   private double lastBorderMinX;
   private double lastBorderMaxX;
   private double lastBorderMinZ;
   private double lastBorderMaxZ;
   private @Nullable AbstractTexture texture;
   private final GpuBuffer worldBorderBuffer;
   private final RenderSystem.AutoStorageIndexBuffer indices;
   private final TextureManager textureManager;

   public WorldBorderRenderer() {
      super();
      this.worldBorderBuffer = RenderSystem.getDevice().createBuffer(() -> "World border vertex buffer", 40, 16L * (long)DefaultVertexFormat.POSITION_TEX.getVertexSize());
      this.indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
      Minecraft minecraft = Minecraft.getInstance();
      this.textureManager = minecraft.getTextureManager();
      this.textureManager.register(FORCEFIELD_LOCATION, new MipmappedTexture(FORCEFIELD_LOCATION, 4));
   }

   public void close() {
      this.worldBorderBuffer.close();
   }

   private void rebuildWorldBorderBuffer(final WorldBorderRenderState state, final double renderDistance, final double cameraZ, final double cameraX, final float halfHeightY, final float v1, final float v0) {
      try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4 * 4)) {
         double borderMinX = state.minX + 0.01;
         double borderMaxX = state.maxX - 0.01;
         double borderMinZ = state.minZ + 0.01;
         double borderMaxZ = state.maxZ - 0.01;
         double minZ = Math.max((double)Mth.floor(cameraZ - renderDistance), borderMinZ);
         double maxZ = Math.min((double)Mth.ceil(cameraZ + renderDistance), borderMaxZ);
         float u0z = (float)(Mth.floor(minZ) & 1) * 0.5F;
         float u1z = (float)(maxZ - minZ) / 2.0F;
         double minX = Math.max((double)Mth.floor(cameraX - renderDistance), borderMinX);
         double maxX = Math.min((double)Mth.ceil(cameraX + renderDistance), borderMaxX);
         float u0x = (float)(Mth.floor(minX) & 1) * 0.5F;
         float u1x = (float)(maxX - minX) / 2.0F;
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(0.0F, -halfHeightY, (float)(borderMaxZ - minZ)).setUv(u0x, v1);
         bufferBuilder.addVertex((float)(maxX - minX), -halfHeightY, (float)(borderMaxZ - minZ)).setUv(u1x + u0x, v1);
         bufferBuilder.addVertex((float)(maxX - minX), halfHeightY, (float)(borderMaxZ - minZ)).setUv(u1x + u0x, v0);
         bufferBuilder.addVertex(0.0F, halfHeightY, (float)(borderMaxZ - minZ)).setUv(u0x, v0);
         bufferBuilder.addVertex(0.0F, -halfHeightY, 0.0F).setUv(u0z, v1);
         bufferBuilder.addVertex(0.0F, -halfHeightY, (float)(maxZ - minZ)).setUv(u1z + u0z, v1);
         bufferBuilder.addVertex(0.0F, halfHeightY, (float)(maxZ - minZ)).setUv(u1z + u0z, v0);
         bufferBuilder.addVertex(0.0F, halfHeightY, 0.0F).setUv(u0z, v0);
         bufferBuilder.addVertex((float)(maxX - minX), -halfHeightY, 0.0F).setUv(u0x, v1);
         bufferBuilder.addVertex(0.0F, -halfHeightY, 0.0F).setUv(u1x + u0x, v1);
         bufferBuilder.addVertex(0.0F, halfHeightY, 0.0F).setUv(u1x + u0x, v0);
         bufferBuilder.addVertex((float)(maxX - minX), halfHeightY, 0.0F).setUv(u0x, v0);
         bufferBuilder.addVertex((float)(borderMaxX - minX), -halfHeightY, (float)(maxZ - minZ)).setUv(u0z, v1);
         bufferBuilder.addVertex((float)(borderMaxX - minX), -halfHeightY, 0.0F).setUv(u1z + u0z, v1);
         bufferBuilder.addVertex((float)(borderMaxX - minX), halfHeightY, 0.0F).setUv(u1z + u0z, v0);
         bufferBuilder.addVertex((float)(borderMaxX - minX), halfHeightY, (float)(maxZ - minZ)).setUv(u0z, v0);

         try (MeshData meshData = bufferBuilder.buildOrThrow()) {
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.worldBorderBuffer.slice(), meshData.vertexBuffer());
         }

         this.lastBorderMinX = state.minX;
         this.lastBorderMaxX = state.maxX;
         this.lastBorderMinZ = state.minZ;
         this.lastBorderMaxZ = state.maxZ;
         this.lastMinX = minX;
         this.lastMinZ = minZ;
         this.needsRebuild = false;
      }

   }

   public void extract(final WorldBorder border, final float deltaPartialTick, final Vec3 cameraPos, final double renderDistance, final WorldBorderRenderState state) {
      state.minX = border.getMinX(deltaPartialTick);
      state.maxX = border.getMaxX(deltaPartialTick);
      state.minZ = border.getMinZ(deltaPartialTick);
      state.maxZ = border.getMaxZ(deltaPartialTick);
      if ((!(cameraPos.x < state.maxX - renderDistance) || !(cameraPos.x > state.minX + renderDistance) || !(cameraPos.z < state.maxZ - renderDistance) || !(cameraPos.z > state.minZ + renderDistance)) && !(cameraPos.x < state.minX - renderDistance) && !(cameraPos.x > state.maxX + renderDistance) && !(cameraPos.z < state.minZ - renderDistance) && !(cameraPos.z > state.maxZ + renderDistance)) {
         state.alpha = 1.0 - border.getDistanceToBorder(cameraPos.x, cameraPos.z) / renderDistance;
         state.alpha = Math.pow(state.alpha, 4.0);
         state.alpha = Mth.clamp(state.alpha, 0.0, 1.0);
         state.tint = border.getStatus().getColor();
      } else {
         state.alpha = 0.0;
      }
   }

   public void prepare(final WorldBorderRenderState state, final Vec3 cameraPos, final double renderDistance, final double depthFar) {
      if (!(state.alpha <= 0.0)) {
         double cameraX = cameraPos.x;
         double cameraZ = cameraPos.z;
         float halfHeightY = (float)depthFar;
         float v0 = (float)(-Mth.frac(cameraPos.y * 0.5));
         float v1 = v0 + halfHeightY;
         if (this.shouldRebuildWorldBorderBuffer(state)) {
            this.rebuildWorldBorderBuffer(state, renderDistance, cameraZ, cameraX, halfHeightY, v1, v0);
         }

         this.indices.requestIndexCount(6);
         this.texture = this.textureManager.getTexture(FORCEFIELD_LOCATION);
      }
   }

   public void render(final WorldBorderRenderState state, final RenderPass renderPass, final Vec3 cameraPos, final double renderDistance) {
      List<WorldBorderRenderState.DistancePerDirection> distancesPerDirection = state.closestBorder(cameraPos.x, cameraPos.z);
      if (!(((WorldBorderRenderState.DistancePerDirection)distancesPerDirection.getFirst()).distance() >= renderDistance)) {
         RenderPipeline renderPipeline = RenderPipelines.WORLD_BORDER;
         GpuBuffer indexBuffer = this.indices.getBuffer();
         GpuBufferSlice dynamicTransforms = this.prepareDynamicTransforms(state, cameraPos);
         renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipeline));
         this.prepareRenderPass(renderPass, dynamicTransforms, indexBuffer, this.texture);
         this.draw(distancesPerDirection, renderDistance, cameraPos.x, cameraPos.z, indexBuffer, renderPass);
      }
   }

   public void renderOit(final WorldBorderRenderState state, final Vec3 cameraPos, final double renderDistance, final OitStage stage, final RenderPass renderPass) {
      List<WorldBorderRenderState.DistancePerDirection> distancesPerDirection = state.closestBorder(cameraPos.x, cameraPos.z);
      if (!(((WorldBorderRenderState.DistancePerDirection)distancesPerDirection.getFirst()).distance() >= renderDistance)) {
         GpuBuffer indexBuffer = this.indices.getBuffer();
         GpuBufferSlice dynamicTransforms = this.prepareDynamicTransforms(state, cameraPos);
         RenderPipeline renderPipeline = RenderPipelines.OIT_WORLD_BORDER.getPipeline(stage);
         renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipeline));
         this.prepareRenderPass(renderPass, dynamicTransforms, indexBuffer, this.texture);
         this.draw(distancesPerDirection, renderDistance, cameraPos.x, cameraPos.z, indexBuffer, renderPass);
      }
   }

   private GpuBufferSlice prepareDynamicTransforms(final WorldBorderRenderState state, final Vec3 cameraPos) {
      float red = (float)ARGB.red(state.tint) / 255.0F;
      float green = (float)ARGB.green(state.tint) / 255.0F;
      float blue = (float)ARGB.blue(state.tint) / 255.0F;
      float offset = (float)(Util.getMillis() % 3000L) / 3000.0F;
      GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f(red, green, blue, (float)state.alpha), new Vector3f((float)(this.lastMinX - cameraPos.x), (float)(-cameraPos.y), (float)(this.lastMinZ - cameraPos.z)), (new Matrix4f()).translation(offset, offset, 0.0F));
      return dynamicTransforms;
   }

   private void prepareRenderPass(final RenderPass renderPass, final GpuBufferSlice dynamicTransforms, final GpuBuffer indexBuffer, final AbstractTexture abstractTexture) {
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.setUniform("DynamicTransforms", dynamicTransforms);
      renderPass.setIndexBuffer(indexBuffer, this.indices.type());
      renderPass.setUniform("Sampler0", abstractTexture.getTextureView(), abstractTexture.getSampler());
      renderPass.setVertexBuffer(0, this.worldBorderBuffer.slice());
   }

   private void draw(final List<WorldBorderRenderState.DistancePerDirection> distancesPerDirection, final double renderDistance, final double cameraX, final double cameraZ, final GpuBuffer indexBuffer, final RenderPass renderPass) {
      renderPass.pushDebugGroup(() -> "World Border");
      ArrayList<RenderPass.Draw<WorldBorderRenderer>> draws = new ArrayList();

      for(WorldBorderRenderState.DistancePerDirection distancePerDirection : distancesPerDirection) {
         if (distancePerDirection.distance() < renderDistance) {
            int sideIndex = distancePerDirection.direction().get2DDataValue();
            draws.add(new RenderPass.Draw(0, this.worldBorderBuffer, indexBuffer, this.indices.type(), 6 * sideIndex, 6, 0));
         }
      }

      renderPass.drawMultipleIndexed(draws, (GpuBuffer)null, (IndexType)null, Collections.emptyList(), this);
      renderPass.popDebugGroup();
   }

   public void invalidate() {
      this.needsRebuild = true;
   }

   private boolean shouldRebuildWorldBorderBuffer(final WorldBorderRenderState state) {
      return this.needsRebuild || state.minX != this.lastBorderMinX || state.minZ != this.lastBorderMinZ || state.maxX != this.lastBorderMaxX || state.maxZ != this.lastBorderMaxZ;
   }
}
