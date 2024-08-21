package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;

public class LightTexture implements AutoCloseable {
   public static final int FULL_BRIGHT = 15728880;
   public static final int FULL_SKY = 15728640;
   public static final int FULL_BLOCK = 240;
   private static final int TEXTURE_SIZE = 16;
   private final TextureTarget target;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer var1, Minecraft var2) {
      super();
      this.renderer = var1;
      this.minecraft = var2;
      this.target = new TextureTarget(16, 16, false);
      this.target.setFilterMode(9729);
      this.target.setClearColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.target.clear();
   }

   @Override
   public void close() {
      this.target.destroyBuffers();
   }

   public void tick() {
      this.blockLightRedFlicker = this.blockLightRedFlicker + (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
      this.blockLightRedFlicker *= 0.9F;
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      RenderSystem.setShaderTexture(2, 0);
   }

   public void turnOnLightLayer() {
      RenderSystem.setShaderTexture(2, this.target.getColorTextureId());
   }

   private float getDarknessGamma(float var1) {
      MobEffectInstance var2 = this.minecraft.player.getEffect(MobEffects.DARKNESS);
      return var2 != null ? var2.getBlendFactor(this.minecraft.player, var1) : 0.0F;
   }

   private float calculateDarknessScale(LivingEntity var1, float var2, float var3) {
      float var4 = 0.45F * var2;
      return Math.max(0.0F, Mth.cos(((float)var1.tickCount - var3) * 3.1415927F * 0.025F) * var4);
   }

   public void updateLightTexture(float var1) {
      if (this.updateLightTexture) {
         this.updateLightTexture = false;
         this.minecraft.getProfiler().push("lightTex");
         ClientLevel var2 = this.minecraft.level;
         if (var2 != null) {
            float var3 = var2.getSkyDarken(1.0F);
            float var4;
            if (var2.getSkyFlashTime() > 0) {
               var4 = 1.0F;
            } else {
               var4 = var3 * 0.95F + 0.05F;
            }

            float var5 = this.minecraft.options.darknessEffectScale().get().floatValue();
            float var6 = this.getDarknessGamma(var1) * var5;
            float var7 = this.calculateDarknessScale(this.minecraft.player, var6, var1) * var5;
            float var9 = this.minecraft.player.getWaterVision();
            float var8;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               var8 = GameRenderer.getNightVisionScale(this.minecraft.player, var1);
            } else if (var9 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               var8 = var9;
            } else {
               var8 = 0.0F;
            }

            Vector3f var10 = new Vector3f(var3, var3, 1.0F).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float var11 = this.blockLightRedFlicker + 1.5F;
            float var12 = var2.dimensionType().ambientLight();
            boolean var13 = var2.effects().forceBrightLightmap();
            float var14 = this.minecraft.options.gamma().get().floatValue();
            CompiledShaderProgram var15 = Objects.requireNonNull(RenderSystem.setShader(CoreShaders.LIGHTMAP), "Lightmap shader not loaded");
            var15.safeGetUniform("AmbientLightFactor").set(var12);
            var15.safeGetUniform("SkyFactor").set(var4);
            var15.safeGetUniform("BlockFactor").set(var11);
            var15.safeGetUniform("UseBrightLightmap").set(var13 ? 1 : 0);
            var15.safeGetUniform("SkyLightColor").set(var10);
            var15.safeGetUniform("NightVisionFactor").set(var8);
            var15.safeGetUniform("DarknessScale").set(var7);
            var15.safeGetUniform("DarkenWorldFactor").set(this.renderer.getDarkenWorldAmount(var1));
            var15.safeGetUniform("BrightnessFactor").set(Math.max(0.0F, var14 - var6));
            this.target.bindWrite(true);
            BufferBuilder var16 = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
            var16.addVertex(0.0F, 0.0F, 0.0F);
            var16.addVertex(1.0F, 0.0F, 0.0F);
            var16.addVertex(1.0F, 1.0F, 0.0F);
            var16.addVertex(0.0F, 1.0F, 0.0F);
            BufferUploader.drawWithShader(var16.buildOrThrow());
            this.target.unbindWrite();
            this.minecraft.getProfiler().pop();
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
