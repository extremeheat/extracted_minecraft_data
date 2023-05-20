package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
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
   private final DynamicTexture lightTexture;
   private final NativeImage lightPixels;
   private final ResourceLocation lightTextureLocation;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer var1, Minecraft var2) {
      super();
      this.renderer = var1;
      this.minecraft = var2;
      this.lightTexture = new DynamicTexture(16, 16, false);
      this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
      this.lightPixels = this.lightTexture.getPixels();

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            this.lightPixels.setPixelRGBA(var4, var3, -1);
         }
      }

      this.lightTexture.upload();
   }

   @Override
   public void close() {
      this.lightTexture.close();
   }

   public void tick() {
      this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
      this.blockLightRedFlicker *= 0.9F;
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      RenderSystem.setShaderTexture(2, 0);
   }

   public void turnOnLightLayer() {
      RenderSystem.setShaderTexture(2, this.lightTextureLocation);
      this.minecraft.getTextureManager().bindForSetup(this.lightTextureLocation);
      RenderSystem.texParameter(3553, 10241, 9729);
      RenderSystem.texParameter(3553, 10240, 9729);
   }

   private float getDarknessGamma(float var1) {
      if (this.minecraft.player.hasEffect(MobEffects.DARKNESS)) {
         MobEffectInstance var2 = this.minecraft.player.getEffect(MobEffects.DARKNESS);
         if (var2 != null && var2.getFactorData().isPresent()) {
            return var2.getFactorData().get().getFactor(this.minecraft.player, var1);
         }
      }

      return 0.0F;
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
            Vector3f var12 = new Vector3f();

            for(int var13 = 0; var13 < 16; ++var13) {
               for(int var14 = 0; var14 < 16; ++var14) {
                  float var15 = getBrightness(var2.dimensionType(), var13) * var4;
                  float var16 = getBrightness(var2.dimensionType(), var14) * var11;
                  float var18 = var16 * ((var16 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float var19 = var16 * (var16 * var16 * 0.6F + 0.4F);
                  var12.set(var16, var18, var19);
                  boolean var20 = var2.effects().forceBrightLightmap();
                  if (var20) {
                     var12.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                     clampColor(var12);
                  } else {
                     Vector3f var21 = new Vector3f(var10).mul(var15);
                     var12.add(var21);
                     var12.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                     if (this.renderer.getDarkenWorldAmount(var1) > 0.0F) {
                        float var22 = this.renderer.getDarkenWorldAmount(var1);
                        Vector3f var23 = new Vector3f(var12).mul(0.7F, 0.6F, 0.6F);
                        var12.lerp(var23, var22);
                     }
                  }

                  if (var8 > 0.0F) {
                     float var27 = Math.max(var12.x(), Math.max(var12.y(), var12.z()));
                     if (var27 < 1.0F) {
                        float var29 = 1.0F / var27;
                        Vector3f var31 = new Vector3f(var12).mul(var29);
                        var12.lerp(var31, var8);
                     }
                  }

                  if (!var20) {
                     if (var7 > 0.0F) {
                        var12.add(-var7, -var7, -var7);
                     }

                     clampColor(var12);
                  }

                  float var28 = this.minecraft.options.gamma().get().floatValue();
                  Vector3f var30 = new Vector3f(this.notGamma(var12.x), this.notGamma(var12.y), this.notGamma(var12.z));
                  var12.lerp(var30, Math.max(0.0F, var28 - var6));
                  var12.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                  clampColor(var12);
                  var12.mul(255.0F);
                  boolean var32 = true;
                  int var24 = (int)var12.x();
                  int var25 = (int)var12.y();
                  int var26 = (int)var12.z();
                  this.lightPixels.setPixelRGBA(var14, var13, 0xFF000000 | var26 << 16 | var25 << 8 | var24);
               }
            }

            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();
         }
      }
   }

   private static void clampColor(Vector3f var0) {
      var0.set(Mth.clamp(var0.x, 0.0F, 1.0F), Mth.clamp(var0.y, 0.0F, 1.0F), Mth.clamp(var0.z, 0.0F, 1.0F));
   }

   private float notGamma(float var1) {
      float var2 = 1.0F - var1;
      return 1.0F - var2 * var2 * var2 * var2;
   }

   public static float getBrightness(DimensionType var0, int var1) {
      float var2 = (float)var1 / 15.0F;
      float var3 = var2 / (4.0F - 3.0F * var2);
      return Mth.lerp(var0.ambientLight(), var3, 1.0F);
   }

   public static int pack(int var0, int var1) {
      return var0 << 4 | var1 << 20;
   }

   public static int block(int var0) {
      return var0 >> 4 & 65535;
   }

   public static int sky(int var0) {
      return var0 >> 20 & 65535;
   }
}
