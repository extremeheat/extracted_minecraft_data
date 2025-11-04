package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.OptionalInt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;

public class LightTexture implements AutoCloseable {
   public static final int FULL_BRIGHT = 15728880;
   public static final int FULL_SKY = 15728640;
   public static final int FULL_BLOCK = 240;
   private static final int TEXTURE_SIZE = 16;
   private static final int LIGHTMAP_UBO_SIZE = (new Std140SizeCalculator()).putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putVec3().putVec3().get();
   private final GpuTexture texture;
   private final GpuTextureView textureView;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;
   private final MappableRingBuffer ubo;

   public LightTexture(GameRenderer var1, Minecraft var2) {
      super();
      this.renderer = var1;
      this.minecraft = var2;
      GpuDevice var3 = RenderSystem.getDevice();
      this.texture = var3.createTexture("Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);
      this.textureView = var3.createTextureView(this.texture);
      var3.createCommandEncoder().clearColorTexture(this.texture, -1);
      this.ubo = new MappableRingBuffer(() -> "Lightmap UBO", 130, LIGHTMAP_UBO_SIZE);
   }

   public GpuTextureView getTextureView() {
      return this.textureView;
   }

   public void close() {
      this.texture.close();
      this.textureView.close();
      this.ubo.close();
   }

   public void tick() {
      this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
      this.blockLightRedFlicker *= 0.9F;
      this.updateLightTexture = true;
   }

   private float calculateDarknessScale(LivingEntity var1, float var2, float var3) {
      float var4 = 0.45F * var2;
      return Math.max(0.0F, Mth.cos((double)(((float)var1.tickCount - var3) * 3.1415927F * 0.025F)) * var4);
   }

   public void updateLightTexture(float var1) {
      if (this.updateLightTexture) {
         this.updateLightTexture = false;
         ProfilerFiller var2 = Profiler.get();
         var2.push("lightTex");
         ClientLevel var3 = this.minecraft.level;
         if (var3 != null) {
            Camera var4 = this.minecraft.gameRenderer.getMainCamera();
            int var5 = (Integer)var4.attributeProbe().getValue(EnvironmentAttributes.SKY_LIGHT_COLOR, var1);
            float var6 = var3.dimensionType().ambientLight();
            float var8 = (Float)var4.attributeProbe().getValue(EnvironmentAttributes.SKY_LIGHT_FACTOR, var1);
            EndFlashState var9 = var3.endFlashState();
            Vector3f var7;
            if (var9 != null) {
               var7 = new Vector3f(0.99F, 1.12F, 1.0F);
               if (!(Boolean)this.minecraft.options.hideLightningFlash().get()) {
                  float var10 = var9.getIntensity(var1);
                  if (this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
                     var8 += var10 / 3.0F;
                  } else {
                     var8 += var10;
                  }
               }
            } else {
               var7 = new Vector3f(1.0F, 1.0F, 1.0F);
            }

            float var25 = ((Double)this.minecraft.options.darknessEffectScale().get()).floatValue();
            float var11 = this.minecraft.player.getEffectBlendFactor(MobEffects.DARKNESS, var1) * var25;
            float var12 = this.calculateDarknessScale(this.minecraft.player, var11, var1) * var25;
            float var14 = this.minecraft.player.getWaterVision();
            float var13;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               var13 = GameRenderer.getNightVisionScale(this.minecraft.player, var1);
            } else if (var14 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               var13 = var14;
            } else {
               var13 = 0.0F;
            }

            float var15 = this.blockLightRedFlicker + 1.5F;
            float var16 = ((Double)this.minecraft.options.gamma().get()).floatValue();
            CommandEncoder var17 = RenderSystem.getDevice().createCommandEncoder();

            try (GpuBuffer.MappedView var18 = var17.mapBuffer(this.ubo.currentBuffer(), false, true)) {
               Std140Builder.intoBuffer(var18.data()).putFloat(var6).putFloat(var8).putFloat(var15).putFloat(var13).putFloat(var12).putFloat(this.renderer.getDarkenWorldAmount(var1)).putFloat(Math.max(0.0F, var16 - var11)).putVec3(ARGB.vector3fFromRGB24(var5)).putVec3(var7);
            }

            try (RenderPass var26 = var17.createRenderPass(() -> "Update light", this.textureView, OptionalInt.empty())) {
               var26.setPipeline(RenderPipelines.LIGHTMAP);
               RenderSystem.bindDefaultUniforms(var26);
               var26.setUniform("LightmapInfo", this.ubo.currentBuffer());
               var26.draw(0, 3);
            }

            this.ubo.rotate();
            var2.pop();
         }
      }
   }

   public static float getBrightness(DimensionType var0, int var1) {
      return getBrightness(var0.ambientLight(), var1);
   }

   public static float getBrightness(float var0, int var1) {
      float var2 = (float)var1 / 15.0F;
      float var3 = var2 / (4.0F - 3.0F * var2);
      return Mth.lerp(var0, var3, 1.0F);
   }

   public static int pack(int var0, int var1) {
      return var0 << 4 | var1 << 20;
   }

   public static int block(int var0) {
      return var0 >>> 4 & 15;
   }

   public static int sky(int var0) {
      return var0 >>> 20 & 15;
   }

   public static int lightCoordsWithEmission(int var0, int var1) {
      if (var1 == 0) {
         return var0;
      } else {
         int var2 = Math.max(sky(var0), var1);
         int var3 = Math.max(block(var0), var1);
         return pack(var3, var2);
      }
   }
}
