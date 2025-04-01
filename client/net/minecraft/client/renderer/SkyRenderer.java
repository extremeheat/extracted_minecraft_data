package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.dimension.DimensionSpecialEffects;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class SkyRenderer implements AutoCloseable {
   private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
   public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   public static final ResourceLocation HUB_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/block/bedrock.png");
   private static final ResourceLocation PANORAMA_BASE = ResourceLocation.withDefaultNamespace("textures/environment/skynorama/panorama");
   private static final List<ResourceLocation> PANORAMA_SIDES = IntStream.range(0, 6).mapToObj((var0) -> PANORAMA_BASE.withSuffix("_" + var0 + ".png")).toList();
   private static final float SKY_DISC_RADIUS = 512.0F;
   private static final int SKY_VERTICES = 10;
   private static final int STAR_COUNT = 1500;
   private static final int END_SKY_QUAD_COUNT = 6;
   private static final int CODE_SKY_QUAD_COUNT = 4;
   public static final ResourceLocation THE_CODE = ResourceLocation.withDefaultNamespace("textures/font/code.png");
   private final GpuBuffer starBuffer;
   private final RenderSystem.AutoStorageIndexBuffer starIndices;
   private final GpuBuffer topSkyBuffer;
   private final GpuBuffer bottomSkyBuffer;
   private final GpuBuffer endSkyBuffer;
   private final GpuBuffer panoramaSkyBuffer;
   private final GpuBuffer codeSkyBuffer;
   private final GpuBuffer codeTitleBuffer;
   @Nullable
   private CachedCubeSky cubeSkyCache;
   private int starIndexCount;

   public SkyRenderer() {
      super();
      this.starIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      this.panoramaSkyBuffer = CubeMap.initializeVertices();
      this.starBuffer = this.buildStars();
      this.endSkyBuffer = buildEndSky();
      this.codeSkyBuffer = buildCodeSky();
      this.codeTitleBuffer = buildCodeTitle();

      try (ByteBufferBuilder var1 = new ByteBufferBuilder(10 * DefaultVertexFormat.POSITION.getVertexSize())) {
         BufferBuilder var2 = new BufferBuilder(var1, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
         this.buildSkyDisc(var2, 16.0F);

         try (MeshData var3 = var2.buildOrThrow()) {
            this.topSkyBuffer = RenderSystem.getDevice().createBuffer(() -> "Top sky vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var3.vertexBuffer());
         }

         var2 = new BufferBuilder(var1, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
         this.buildSkyDisc(var2, -16.0F);

         try (MeshData var13 = var2.buildOrThrow()) {
            this.bottomSkyBuffer = RenderSystem.getDevice().createBuffer(() -> "Bottom sky vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var13.vertexBuffer());
         }
      }

   }

   private GpuBuffer buildStars() {
      RandomSource var1 = RandomSource.create(10842L);
      float var2 = 100.0F;

      GpuBuffer var19;
      try (ByteBufferBuilder var3 = new ByteBufferBuilder(DefaultVertexFormat.POSITION.getVertexSize() * 1500 * 4)) {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

         for(int var5 = 0; var5 < 1500; ++var5) {
            float var6 = var1.nextFloat() * 2.0F - 1.0F;
            float var7 = var1.nextFloat() * 2.0F - 1.0F;
            float var8 = var1.nextFloat() * 2.0F - 1.0F;
            float var9 = 0.15F + var1.nextFloat() * 0.1F;
            float var10 = Mth.lengthSquared(var6, var7, var8);
            if (!(var10 <= 0.010000001F) && !(var10 >= 1.0F)) {
               Vector3f var11 = (new Vector3f(var6, var7, var8)).normalize(100.0F);
               float var12 = (float)(var1.nextDouble() * 3.1415927410125732 * 2.0);
               Matrix3f var13 = (new Matrix3f()).rotateTowards((new Vector3f(var11)).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-var12);
               var4.addVertex((new Vector3f(var9, -var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(var9, var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(-var9, var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(-var9, -var9, 0.0F)).mul(var13).add(var11));
            }
         }

         try (MeshData var18 = var4.buildOrThrow()) {
            this.starIndexCount = var18.drawState().indexCount();
            var19 = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var18.vertexBuffer());
         }
      }

      return var19;
   }

   private void buildSkyDisc(VertexConsumer var1, float var2) {
      float var3 = Math.signum(var2) * 512.0F;
      var1.addVertex(0.0F, var2, 0.0F);

      for(int var4 = -180; var4 <= 180; var4 += 45) {
         var1.addVertex(var3 * Mth.cos((float)var4 * 0.017453292F), var2, 512.0F * Mth.sin((float)var4 * 0.017453292F));
      }

   }

   public void renderSkyDisc(RenderTarget var1, float var2, float var3, float var4) {
      RenderSystem.setShaderColor(var2, var3, var4, 1.0F);
      GpuTexture var5 = var1.getColorTexture();
      GpuTexture var6 = var1.getDepthTexture();

      try (RenderPass var7 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var5, OptionalInt.empty(), var6, OptionalDouble.empty())) {
         var7.setPipeline(RenderPipelines.SKY);
         var7.setVertexBuffer(0, this.topSkyBuffer);
         var7.draw(0, 10);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void renderDarkDisc(RenderTarget var1) {
      RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
      Matrix4fStack var2 = RenderSystem.getModelViewStack();
      var2.pushMatrix();
      var2.translate(0.0F, 12.0F, 0.0F);
      GpuTexture var3 = var1.getColorTexture();
      GpuTexture var4 = var1.getDepthTexture();

      try (RenderPass var5 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var3, OptionalInt.empty(), var4, OptionalDouble.empty())) {
         var5.setPipeline(RenderPipelines.SKY);
         var5.setVertexBuffer(0, this.bottomSkyBuffer);
         var5.draw(0, 10);
      }

      var2.popMatrix();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void renderSunMoonAndStars(RenderTarget var1, PoseStack var2, MultiBufferSource.BufferSource var3, float var4, int var5, float var6, float var7, FogParameters var8) {
      var2.pushPose();
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0F));
      var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(var4 * 360.0F));
      this.renderSun(var6, var3, var2);
      this.renderMoon(var5, var6, var3, var2);
      var3.endBatch();
      if (var7 > 0.0F) {
         this.renderStars(var1, var8, var7, var2);
      }

      var2.popPose();
   }

   private void renderSun(float var1, MultiBufferSource var2, PoseStack var3) {
      float var4 = 30.0F;
      float var5 = 100.0F;
      VertexConsumer var6 = var2.getBuffer(RenderType.celestial(SUN_LOCATION));
      int var7 = ARGB.white(var1);
      Matrix4f var8 = var3.last().pose();
      var6.addVertex(var8, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F).setColor(var7);
      var6.addVertex(var8, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F).setColor(var7);
      var6.addVertex(var8, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F).setColor(var7);
      var6.addVertex(var8, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F).setColor(var7);
   }

   private void renderMoon(int var1, float var2, MultiBufferSource var3, PoseStack var4) {
      float var5 = 20.0F;
      int var6 = var1 % 4;
      int var7 = var1 / 4 % 2;
      float var8 = (float)(var6 + 0) / 4.0F;
      float var9 = (float)(var7 + 0) / 2.0F;
      float var10 = (float)(var6 + 1) / 4.0F;
      float var11 = (float)(var7 + 1) / 2.0F;
      float var12 = 100.0F;
      VertexConsumer var13 = var3.getBuffer(RenderType.celestial(MOON_LOCATION));
      int var14 = ARGB.white(var2);
      Matrix4f var15 = var4.last().pose();
      var13.addVertex(var15, -20.0F, -100.0F, 20.0F).setUv(var10, var11).setColor(var14);
      var13.addVertex(var15, 20.0F, -100.0F, 20.0F).setUv(var8, var11).setColor(var14);
      var13.addVertex(var15, 20.0F, -100.0F, -20.0F).setUv(var8, var9).setColor(var14);
      var13.addVertex(var15, -20.0F, -100.0F, -20.0F).setUv(var10, var9).setColor(var14);
   }

   private void renderStars(RenderTarget var1, FogParameters var2, float var3, PoseStack var4) {
      Matrix4fStack var5 = RenderSystem.getModelViewStack();
      var5.pushMatrix();
      var5.mul(var4.last().pose());
      RenderSystem.setShaderColor(var3, var3, var3, var3);
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
      RenderPipeline var6 = RenderPipelines.STARS;
      GpuTexture var7 = var1.getColorTexture();
      GpuTexture var8 = var1.getDepthTexture();
      GpuBuffer var9 = this.starIndices.getBuffer(this.starIndexCount);

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var7, OptionalInt.empty(), var8, OptionalDouble.empty())) {
         var10.setPipeline(var6);
         var10.setVertexBuffer(0, this.starBuffer);
         var10.setIndexBuffer(var9, this.starIndices.type());
         var10.drawIndexed(0, this.starIndexCount);
      }

      RenderSystem.setShaderFog(var2);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      var5.popMatrix();
   }

   public void renderSunriseAndSunset(PoseStack var1, MultiBufferSource.BufferSource var2, float var3, int var4) {
      var1.pushPose();
      var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
      float var5 = Mth.sin(var3) < 0.0F ? 180.0F : 0.0F;
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var5));
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0F));
      Matrix4f var6 = var1.last().pose();
      VertexConsumer var7 = var2.getBuffer(RenderType.sunriseSunset());
      float var8 = ARGB.alphaFloat(var4);
      var7.addVertex(var6, 0.0F, 100.0F, 0.0F).setColor(var4);
      int var9 = ARGB.transparent(var4);
      boolean var10 = true;

      for(int var11 = 0; var11 <= 16; ++var11) {
         float var12 = (float)var11 * 6.2831855F / 16.0F;
         float var13 = Mth.sin(var12);
         float var14 = Mth.cos(var12);
         var7.addVertex(var6, var13 * 120.0F, var14 * 120.0F, -var14 * 40.0F * var8).setColor(var9);
      }

      var1.popPose();
   }

   private static GpuBuffer buildEndSky() {
      GpuBuffer var10;
      try (ByteBufferBuilder var0 = new ByteBufferBuilder(24 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize())) {
         BufferBuilder var1 = new BufferBuilder(var0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for(int var2 = 0; var2 < 6; ++var2) {
            Matrix4f var3 = new Matrix4f();
            switch (var2) {
               case 1 -> var3.rotationX(1.5707964F);
               case 2 -> var3.rotationX(-1.5707964F);
               case 3 -> var3.rotationX(3.1415927F);
               case 4 -> var3.rotationZ(1.5707964F);
               case 5 -> var3.rotationZ(-1.5707964F);
            }

            var1.addVertex(var3, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
            var1.addVertex(var3, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
            var1.addVertex(var3, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
            var1.addVertex(var3, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
         }

         try (MeshData var9 = var1.buildOrThrow()) {
            var10 = RenderSystem.getDevice().createBuffer(() -> "End sky vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var9.vertexBuffer());
         }
      }

      return var10;
   }

   public void renderEndSky(RenderTarget var1) {
      TextureManager var2 = Minecraft.getInstance().getTextureManager();
      AbstractTexture var3 = var2.getTexture(END_SKY_LOCATION);
      var3.setFilter(TriState.FALSE, false);
      RenderSystem.AutoStorageIndexBuffer var4 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var5 = var4.getBuffer(36);
      GpuTexture var6 = var1.getColorTexture();
      GpuTexture var7 = var1.getDepthTexture();

      try (RenderPass var8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var6, OptionalInt.empty(), var7, OptionalDouble.empty())) {
         var8.setPipeline(RenderPipelines.END_SKY);
         var8.bindSampler("Sampler0", var3.getTexture());
         var8.setVertexBuffer(0, this.endSkyBuffer);
         var8.setIndexBuffer(var5, var4.type());
         var8.drawIndexed(0, 36);
      }

   }

   private static GpuBuffer buildHubSky(DimensionSpecialEffects.CubeSky var0) {
      GpuBuffer var12;
      try (ByteBufferBuilder var1 = new ByteBufferBuilder(24 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize())) {
         BufferBuilder var2 = new BufferBuilder(var1, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for(int var3 = 0; var3 < 6; ++var3) {
            Matrix4f var4 = new Matrix4f();
            switch (var3) {
               case 1 -> var4.rotationX(1.5707964F);
               case 2 -> var4.rotationX(-1.5707964F);
               case 3 -> var4.rotationX(3.1415927F);
               case 4 -> var4.rotationZ(1.5707964F);
               case 5 -> var4.rotationZ(-1.5707964F);
            }

            float var5 = var0.size();
            float var6 = (float)var0.repeats();
            var2.addVertex(var4, -var5, -var5, -var5).setUv(0.0F, 0.0F).setColor(-1);
            var2.addVertex(var4, -var5, -var5, var5).setUv(0.0F, var6).setColor(-1);
            var2.addVertex(var4, var5, -var5, var5).setUv(var6, var6).setColor(-1);
            var2.addVertex(var4, var5, -var5, -var5).setUv(var6, 0.0F).setColor(-1);
         }

         try (MeshData var11 = var2.buildOrThrow()) {
            var12 = RenderSystem.getDevice().createBuffer(() -> "Hub sky vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var11.vertexBuffer());
         }
      }

      return var12;
   }

   public void renderCubeSky(DimensionSpecialEffects.CubeSky var1, RenderTarget var2) {
      if (this.cubeSkyCache != null && !this.cubeSkyCache.dasObjekt().equals(var1)) {
         this.cubeSkyCache.close();
         this.cubeSkyCache = null;
      }

      if (this.cubeSkyCache == null) {
         GpuBuffer var3 = buildHubSky(var1);
         this.cubeSkyCache = new CachedCubeSky(var1, var1.textureId().withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png")), var3);
      }

      TextureManager var14 = Minecraft.getInstance().getTextureManager();
      AbstractTexture var4 = var14.getTexture(this.cubeSkyCache.textureId());
      var4.setFilter(TriState.FALSE, false);
      RenderSystem.AutoStorageIndexBuffer var5 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var6 = var5.getBuffer(36);
      GpuTexture var7 = var2.getColorTexture();
      GpuTexture var8 = var2.getDepthTexture();

      try (RenderPass var9 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var7, OptionalInt.empty(), var8, OptionalDouble.empty())) {
         var9.setPipeline(RenderPipelines.HUB_SKY);
         var9.bindSampler("Sampler0", var4.getTexture());
         var9.setVertexBuffer(0, this.cubeSkyCache.buffer());
         var9.setIndexBuffer(var6, var5.type());
         var9.drawIndexed(0, 36);
      }

   }

   public void renderPanorama(RenderTarget var1) {
      RenderPipeline var2 = RenderPipelines.PANORAMA;
      GpuTexture var3 = var1.getColorTexture();
      GpuTexture var4 = var1.getDepthTexture();
      RenderSystem.AutoStorageIndexBuffer var5 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var6 = var5.getBuffer(36);
      TextureManager var7 = Minecraft.getInstance().getTextureManager();
      Stream var10000 = PANORAMA_SIDES.stream();
      Objects.requireNonNull(var7);
      List var8 = var10000.map(var7::getTexture).toList();
      Matrix4fStack var9 = RenderSystem.getModelViewStack();
      var9.pushMatrix();
      var9.rotateX(3.1415927F);

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var3, OptionalInt.empty(), var4, OptionalDouble.empty())) {
         var10.setPipeline(var2);
         var10.setVertexBuffer(0, this.panoramaSkyBuffer);
         var10.setIndexBuffer(var6, var5.type());

         for(int var11 = 0; var11 < 6; ++var11) {
            var10.bindSampler("Sampler0", ((AbstractTexture)var8.get(var11)).getTexture());
            var10.drawIndexed(6 * var11, 6);
         }
      }

      var9.popMatrix();
   }

   private static GpuBuffer buildCodeSky() {
      GpuBuffer var11;
      try (ByteBufferBuilder var0 = new ByteBufferBuilder(16 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
         BufferBuilder var1 = new BufferBuilder(var0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

         for(int var2 = 0; var2 < 4; ++var2) {
            Matrix4f var3 = new Matrix4f();
            switch (var2) {
               case 1 -> var3.rotationY(-1.5707964F);
               case 2 -> var3.rotationY(1.5707964F);
               case 3 -> var3.rotationY(3.1415927F);
            }

            float var4 = 100.0F;
            float var5 = 400.0F;
            var1.addVertex(var3, 100.0F, 400.0F, -100.0F).setUv(1.0F, 1.0F);
            var1.addVertex(var3, -100.0F, 400.0F, -100.0F).setUv(0.0F, 1.0F);
            var1.addVertex(var3, -100.0F, -400.0F, -100.0F).setUv(0.0F, 0.0F);
            var1.addVertex(var3, 100.0F, -400.0F, -100.0F).setUv(1.0F, 0.0F);
         }

         try (MeshData var10 = var1.buildOrThrow()) {
            var11 = RenderSystem.getDevice().createBuffer(() -> "Code sky vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var10.vertexBuffer());
         }
      }

      return var11;
   }

   public void renderCodeSky(RenderTarget var1) {
      TextureManager var2 = Minecraft.getInstance().getTextureManager();
      AbstractTexture var3 = var2.getTexture(THE_CODE);
      RenderSystem.AutoStorageIndexBuffer var4 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var5 = var4.getBuffer(24);
      GpuTexture var6 = var1.getColorTexture();
      GpuTexture var7 = var1.getDepthTexture();

      try (RenderPass var8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var6, OptionalInt.empty(), var7, OptionalDouble.empty())) {
         var8.setPipeline(RenderPipelines.CODE_SKY);
         var8.bindSampler("Sampler0", var3.getTexture());
         var8.setVertexBuffer(0, this.codeSkyBuffer);
         var8.setIndexBuffer(var5, var4.type());
         var8.drawIndexed(0, 24);
      }

   }

   private static GpuBuffer buildCodeTitle() {
      GpuBuffer var3;
      try (ByteBufferBuilder var0 = new ByteBufferBuilder(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
         BufferBuilder var1 = new BufferBuilder(var0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         var1.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 1.0F);
         var1.addVertex(-1.0F, 1.0F, 0.0F).setUv(0.0F, 0.0F);
         var1.addVertex(1.0F, 1.0F, 0.0F).setUv(1.0F, 0.0F);
         var1.addVertex(1.0F, -1.0F, 0.0F).setUv(1.0F, 1.0F);

         try (MeshData var2 = var1.buildOrThrow()) {
            var3 = RenderSystem.getDevice().createBuffer(() -> "Code title vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var2.vertexBuffer());
         }
      }

      return var3;
   }

   public void renderCodeSkyForTitle(Minecraft var1, float var2, float var3) {
      RenderSystem.setShaderGameTime(var1.tickCount() + 1000L, var3);
      AbstractTexture var4 = var1.getTextureManager().getTexture(THE_CODE);
      RenderSystem.AutoStorageIndexBuffer var5 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var6 = var5.getBuffer(6);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(new Matrix4f(), ProjectionType.ORTHOGRAPHIC);
      Matrix4fStack var7 = RenderSystem.getModelViewStack();
      var7.pushMatrix();
      var7.rotationX(3.1415927F);
      RenderTarget var8 = var1.getMainRenderTarget();
      GpuTexture var9 = var8.getColorTexture();
      GpuTexture var10 = var8.getDepthTexture();

      try (RenderPass var11 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var9, OptionalInt.empty(), var10, OptionalDouble.empty())) {
         var11.setPipeline(RenderPipelines.CODE_PANORAMA);
         var11.bindSampler("Sampler0", var4.getTexture());
         var11.setVertexBuffer(0, this.codeTitleBuffer);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var2);
         var11.setIndexBuffer(var6, var5.type());
         boolean var12 = false;
         var11.drawIndexed(0, 6);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.restoreProjectionMatrix();
      var7.popMatrix();
   }

   public void close() {
      this.starBuffer.close();
      this.topSkyBuffer.close();
      this.bottomSkyBuffer.close();
      this.endSkyBuffer.close();
      if (this.cubeSkyCache != null) {
         this.cubeSkyCache.close();
      }

      this.panoramaSkyBuffer.close();
      this.codeSkyBuffer.close();
      this.codeTitleBuffer.close();
   }

   static record CachedCubeSky(DimensionSpecialEffects.CubeSky dasObjekt, ResourceLocation textureId, GpuBuffer buffer) implements AutoCloseable {
      CachedCubeSky(DimensionSpecialEffects.CubeSky var1, ResourceLocation var2, GpuBuffer var3) {
         super();
         this.dasObjekt = var1;
         this.textureId = var2;
         this.buffer = var3;
      }

      public void close() {
         this.buffer.close();
      }
   }
}
