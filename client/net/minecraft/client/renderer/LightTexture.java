package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;

public class LightTexture implements AutoCloseable {
   public static final int FULL_BRIGHT = 15728880;
   public static final int FULL_SKY = 15728640;
   public static final int FULL_BLOCK = 240;
   private static final int TEXTURE_SIZE = 16;
   private final GpuTexture texture;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer var1, Minecraft var2) {
      super();
      this.renderer = var1;
      this.minecraft = var2;
      GpuDevice var3 = RenderSystem.getDevice();
      this.texture = var3.createTexture("Light Texture", TextureFormat.RGBA8, 16, 16, 1);
      this.texture.setTextureFilter(FilterMode.LINEAR, false);
      var3.createCommandEncoder().clearColorTexture(this.texture, -1);
   }

   public GpuTexture getTarget() {
      return this.texture;
   }

   public void close() {
      this.texture.close();
   }

   public void tick() {
      this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
      this.blockLightRedFlicker *= 0.9F;
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      RenderSystem.setShaderTexture(2, (GpuTexture)null);
   }

   public void turnOnLightLayer() {
      RenderSystem.setShaderTexture(2, this.texture);
   }

   private float calculateDarknessScale(LivingEntity var1, float var2, float var3) {
      float var4 = 0.45F * var2;
      return Math.max(0.0F, Mth.cos(((float)var1.tickCount - var3) * 3.1415927F * 0.025F) * var4);
   }

   public void updateLightTexture(float var1) {
      if (this.updateLightTexture) {
         this.updateLightTexture = false;
         ProfilerFiller var2 = Profiler.get();
         var2.push("lightTex");
         ClientLevel var3 = this.minecraft.level;
         if (var3 != null) {
            float var4 = var3.getSkyDarken(1.0F);
            float var5;
            if (var3.getSkyFlashTime() > 0) {
               var5 = 1.0F;
            } else {
               var5 = var4 * 0.95F + 0.05F;
            }

            float var6 = ((Double)this.minecraft.options.darknessEffectScale().get()).floatValue();
            float var7 = this.minecraft.player.getEffectBlendFactor(MobEffects.DARKNESS, var1) * var6;
            float var8 = this.calculateDarknessScale(this.minecraft.player, var7, var1) * var6;
            float var10 = this.minecraft.player.getWaterVision();
            float var9;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               var9 = GameRenderer.getNightVisionScale(this.minecraft.player, var1);
            } else if (var10 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               var9 = var10;
            } else {
               var9 = 0.0F;
            }

            Vector3f var11 = (new Vector3f(var4, var4, 1.0F)).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float var12 = this.blockLightRedFlicker + 1.5F;
            float var13 = var3.dimensionType().ambientLight();
            boolean var14 = var3.effects().forceBrightLightmap();
            float var15 = ((Double)this.minecraft.options.gamma().get()).floatValue();

            try (RenderPass var16 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.texture, OptionalInt.empty())) {
               var16.setPipeline(RenderPipelines.LIGHTMAP);
               var16.setUniform("AmbientLightFactor", var13);
               var16.setUniform("SkyFactor", var5);
               var16.setUniform("BlockFactor", var12);
               var16.setUniform("UseBrightLightmap", var14 ? 1 : 0);
               var16.setUniform("SkyLightColor", var11.x, var11.y, var11.z);
               var16.setUniform("NightVisionFactor", var9);
               var16.setUniform("DarknessScale", var8);
               var16.setUniform("DarkenWorldFactor", this.renderer.getDarkenWorldAmount(var1));
               var16.setUniform("BrightnessFactor", Math.max(0.0F, var15 - var7));
               var16.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
               RenderSystem.AutoStorageIndexBuffer var17 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
               var16.setIndexBuffer(var17.getBuffer(6), var17.type());
               var16.drawIndexed(0, 6);
            }

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
