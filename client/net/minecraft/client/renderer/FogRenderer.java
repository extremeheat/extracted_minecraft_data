package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class FogRenderer {
   private final FloatBuffer blackBuffer = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer colorBuffer = MemoryTracker.createFloatBuffer(16);
   private float fogRed;
   private float fogGreen;
   private float fogBlue;
   private float oldRed = -1.0F;
   private float oldGreen = -1.0F;
   private float oldBlue = -1.0F;
   private int targetBiomeFog = -1;
   private int previousBiomeFog = -1;
   private long biomeChangedTime = -1L;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public FogRenderer(GameRenderer var1) {
      super();
      this.renderer = var1;
      this.minecraft = var1.getMinecraft();
      this.blackBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
   }

   public void setupClearColor(Camera var1, float var2) {
      MultiPlayerLevel var3 = this.minecraft.level;
      FluidState var4 = var1.getFluidInCamera();
      if (var4.is(FluidTags.WATER)) {
         this.setWaterFogColor(var1, var3);
      } else if (var4.is(FluidTags.LAVA)) {
         this.fogRed = 0.6F;
         this.fogGreen = 0.1F;
         this.fogBlue = 0.0F;
         this.biomeChangedTime = -1L;
      } else {
         this.setLandFogColor(var1, var3, var2);
         this.biomeChangedTime = -1L;
      }

      double var5 = var1.getPosition().y * var3.dimension.getClearColorScale();
      if (var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         int var7 = ((LivingEntity)var1.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (var7 < 20) {
            var5 *= (double)(1.0F - (float)var7 / 20.0F);
         } else {
            var5 = 0.0D;
         }
      }

      if (var5 < 1.0D) {
         if (var5 < 0.0D) {
            var5 = 0.0D;
         }

         var5 *= var5;
         this.fogRed = (float)((double)this.fogRed * var5);
         this.fogGreen = (float)((double)this.fogGreen * var5);
         this.fogBlue = (float)((double)this.fogBlue * var5);
      }

      float var9;
      if (this.renderer.getDarkenWorldAmount(var2) > 0.0F) {
         var9 = this.renderer.getDarkenWorldAmount(var2);
         this.fogRed = this.fogRed * (1.0F - var9) + this.fogRed * 0.7F * var9;
         this.fogGreen = this.fogGreen * (1.0F - var9) + this.fogGreen * 0.6F * var9;
         this.fogBlue = this.fogBlue * (1.0F - var9) + this.fogBlue * 0.6F * var9;
      }

      float var10;
      if (var4.is(FluidTags.WATER)) {
         var9 = 0.0F;
         if (var1.getEntity() instanceof LocalPlayer) {
            LocalPlayer var8 = (LocalPlayer)var1.getEntity();
            var9 = var8.getWaterVision();
         }

         var10 = 1.0F / this.fogRed;
         if (var10 > 1.0F / this.fogGreen) {
            var10 = 1.0F / this.fogGreen;
         }

         if (var10 > 1.0F / this.fogBlue) {
            var10 = 1.0F / this.fogBlue;
         }

         this.fogRed = this.fogRed * (1.0F - var9) + this.fogRed * var10 * var9;
         this.fogGreen = this.fogGreen * (1.0F - var9) + this.fogGreen * var10 * var9;
         this.fogBlue = this.fogBlue * (1.0F - var9) + this.fogBlue * var10 * var9;
      } else if (var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).hasEffect(MobEffects.NIGHT_VISION)) {
         var9 = this.renderer.getNightVisionScale((LivingEntity)var1.getEntity(), var2);
         var10 = 1.0F / this.fogRed;
         if (var10 > 1.0F / this.fogGreen) {
            var10 = 1.0F / this.fogGreen;
         }

         if (var10 > 1.0F / this.fogBlue) {
            var10 = 1.0F / this.fogBlue;
         }

         this.fogRed = this.fogRed * (1.0F - var9) + this.fogRed * var10 * var9;
         this.fogGreen = this.fogGreen * (1.0F - var9) + this.fogGreen * var10 * var9;
         this.fogBlue = this.fogBlue * (1.0F - var9) + this.fogBlue * var10 * var9;
      }

      GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0F);
   }

   private void setLandFogColor(Camera var1, Level var2, float var3) {
      float var4 = 0.25F + 0.75F * (float)this.minecraft.options.renderDistance / 32.0F;
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25D);
      Vec3 var5 = var2.getSkyColor(var1.getBlockPosition(), var3);
      float var6 = (float)var5.x;
      float var7 = (float)var5.y;
      float var8 = (float)var5.z;
      Vec3 var9 = var2.getFogColor(var3);
      this.fogRed = (float)var9.x;
      this.fogGreen = (float)var9.y;
      this.fogBlue = (float)var9.z;
      if (this.minecraft.options.renderDistance >= 4) {
         double var10 = Mth.sin(var2.getSunAngle(var3)) > 0.0F ? -1.0D : 1.0D;
         Vec3 var12 = new Vec3(var10, 0.0D, 0.0D);
         float var13 = (float)var1.getLookVector().dot(var12);
         if (var13 < 0.0F) {
            var13 = 0.0F;
         }

         if (var13 > 0.0F) {
            float[] var14 = var2.dimension.getSunriseColor(var2.getTimeOfDay(var3), var3);
            if (var14 != null) {
               var13 *= var14[3];
               this.fogRed = this.fogRed * (1.0F - var13) + var14[0] * var13;
               this.fogGreen = this.fogGreen * (1.0F - var13) + var14[1] * var13;
               this.fogBlue = this.fogBlue * (1.0F - var13) + var14[2] * var13;
            }
         }
      }

      this.fogRed += (var6 - this.fogRed) * var4;
      this.fogGreen += (var7 - this.fogGreen) * var4;
      this.fogBlue += (var8 - this.fogBlue) * var4;
      float var15 = var2.getRainLevel(var3);
      float var11;
      float var16;
      if (var15 > 0.0F) {
         var11 = 1.0F - var15 * 0.5F;
         var16 = 1.0F - var15 * 0.4F;
         this.fogRed *= var11;
         this.fogGreen *= var11;
         this.fogBlue *= var16;
      }

      var11 = var2.getThunderLevel(var3);
      if (var11 > 0.0F) {
         var16 = 1.0F - var11 * 0.5F;
         this.fogRed *= var16;
         this.fogGreen *= var16;
         this.fogBlue *= var16;
      }

   }

   private void setWaterFogColor(Camera var1, LevelReader var2) {
      long var3 = Util.getMillis();
      int var5 = var2.getBiome(new BlockPos(var1.getPosition())).getWaterFogColor();
      if (this.biomeChangedTime < 0L) {
         this.targetBiomeFog = var5;
         this.previousBiomeFog = var5;
         this.biomeChangedTime = var3;
      }

      int var6 = this.targetBiomeFog >> 16 & 255;
      int var7 = this.targetBiomeFog >> 8 & 255;
      int var8 = this.targetBiomeFog & 255;
      int var9 = this.previousBiomeFog >> 16 & 255;
      int var10 = this.previousBiomeFog >> 8 & 255;
      int var11 = this.previousBiomeFog & 255;
      float var12 = Mth.clamp((float)(var3 - this.biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
      float var13 = Mth.lerp(var12, (float)var9, (float)var6);
      float var14 = Mth.lerp(var12, (float)var10, (float)var7);
      float var15 = Mth.lerp(var12, (float)var11, (float)var8);
      this.fogRed = var13 / 255.0F;
      this.fogGreen = var14 / 255.0F;
      this.fogBlue = var15 / 255.0F;
      if (this.targetBiomeFog != var5) {
         this.targetBiomeFog = var5;
         this.previousBiomeFog = Mth.floor(var13) << 16 | Mth.floor(var14) << 8 | Mth.floor(var15);
         this.biomeChangedTime = var3;
      }

   }

   public void setupFog(Camera var1, int var2) {
      this.resetFogColor(false);
      GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      FluidState var3 = var1.getFluidInCamera();
      float var7;
      if (var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         var7 = 5.0F;
         int var8 = ((LivingEntity)var1.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (var8 < 20) {
            var7 = Mth.lerp(1.0F - (float)var8 / 20.0F, 5.0F, this.renderer.getRenderDistance());
         }

         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if (var2 == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var7 * 0.8F);
         } else {
            GlStateManager.fogStart(var7 * 0.25F);
            GlStateManager.fogEnd(var7);
         }

         GLX.setupNvFogDistance();
      } else if (var3.is(FluidTags.WATER)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
         if (var1.getEntity() instanceof LivingEntity) {
            if (var1.getEntity() instanceof LocalPlayer) {
               LocalPlayer var4 = (LocalPlayer)var1.getEntity();
               float var5 = 0.05F - var4.getWaterVision() * var4.getWaterVision() * 0.03F;
               Biome var6 = var4.level.getBiome(new BlockPos(var4));
               if (var6 == Biomes.SWAMP || var6 == Biomes.SWAMP_HILLS) {
                  var5 += 0.005F;
               }

               GlStateManager.fogDensity(var5);
            } else {
               GlStateManager.fogDensity(0.05F);
            }
         } else {
            GlStateManager.fogDensity(0.1F);
         }
      } else if (var3.is(FluidTags.LAVA)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP);
         GlStateManager.fogDensity(2.0F);
      } else {
         var7 = this.renderer.getRenderDistance();
         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if (var2 == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var7);
         } else {
            GlStateManager.fogStart(var7 * 0.75F);
            GlStateManager.fogEnd(var7);
         }

         GLX.setupNvFogDistance();
         if (this.minecraft.level.dimension.isFoggyAt(Mth.floor(var1.getPosition().x), Mth.floor(var1.getPosition().z)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
            GlStateManager.fogStart(var7 * 0.05F);
            GlStateManager.fogEnd(Math.min(var7, 192.0F) * 0.5F);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   public void resetFogColor(boolean var1) {
      if (var1) {
         GlStateManager.fog(2918, this.blackBuffer);
      } else {
         GlStateManager.fog(2918, this.updateColorBuffer());
      }

   }

   private FloatBuffer updateColorBuffer() {
      if (this.oldRed != this.fogRed || this.oldGreen != this.fogGreen || this.oldBlue != this.fogBlue) {
         this.colorBuffer.clear();
         this.colorBuffer.put(this.fogRed).put(this.fogGreen).put(this.fogBlue).put(1.0F);
         this.colorBuffer.flip();
         this.oldRed = this.fogRed;
         this.oldGreen = this.fogGreen;
         this.oldBlue = this.fogBlue;
      }

      return this.colorBuffer;
   }
}
