package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

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

   public void close() {
      this.lightTexture.close();
   }

   public void tick() {
      this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1D);
      this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker * 0.9D);
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
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

            float var6 = this.minecraft.player.getWaterVision();
            float var5;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               var5 = GameRenderer.getNightVisionScale(this.minecraft.player, var1);
            } else if (var6 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               var5 = var6;
            } else {
               var5 = 0.0F;
            }

            Vector3f var7 = new Vector3f(var3, var3, 1.0F);
            var7.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float var8 = this.blockLightRedFlicker + 1.5F;
            Vector3f var9 = new Vector3f();

            for(int var10 = 0; var10 < 16; ++var10) {
               for(int var11 = 0; var11 < 16; ++var11) {
                  float var12 = this.getBrightness(var2, var10) * var4;
                  float var13 = this.getBrightness(var2, var11) * var8;
                  float var15 = var13 * ((var13 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float var16 = var13 * (var13 * var13 * 0.6F + 0.4F);
                  var9.set(var13, var15, var16);
                  float var18;
                  Vector3f var19;
                  if (var2.effects().forceBrightLightmap()) {
                     var9.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                  } else {
                     Vector3f var17 = var7.copy();
                     var17.mul(var12);
                     var9.add(var17);
                     var9.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                     if (this.renderer.getDarkenWorldAmount(var1) > 0.0F) {
                        var18 = this.renderer.getDarkenWorldAmount(var1);
                        var19 = var9.copy();
                        var19.mul(0.7F, 0.6F, 0.6F);
                        var9.lerp(var19, var18);
                     }
                  }

                  var9.clamp(0.0F, 1.0F);
                  float var23;
                  if (var5 > 0.0F) {
                     var23 = Math.max(var9.method_82(), Math.max(var9.method_83(), var9.method_84()));
                     if (var23 < 1.0F) {
                        var18 = 1.0F / var23;
                        var19 = var9.copy();
                        var19.mul(var18);
                        var9.lerp(var19, var5);
                     }
                  }

                  var23 = (float)this.minecraft.options.gamma;
                  Vector3f var24 = var9.copy();
                  var24.map(this::notGamma);
                  var9.lerp(var24, var23);
                  var9.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                  var9.clamp(0.0F, 1.0F);
                  var9.mul(255.0F);
                  boolean var25 = true;
                  int var20 = (int)var9.method_82();
                  int var21 = (int)var9.method_83();
                  int var22 = (int)var9.method_84();
                  this.lightPixels.setPixelRGBA(var11, var10, -16777216 | var22 << 16 | var21 << 8 | var20);
               }
            }

            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();
         }
      }
   }

   private float notGamma(float var1) {
      float var2 = 1.0F - var1;
      return 1.0F - var2 * var2 * var2 * var2;
   }

   private float getBrightness(Level var1, int var2) {
      return var1.dimensionType().brightness(var2);
   }

   public static int pack(int var0, int var1) {
      return var0 << 4 | var1 << 20;
   }

   public static int block(int var0) {
      return var0 >> 4 & '\uffff';
   }

   public static int sky(int var0) {
      return var0 >> 20 & '\uffff';
   }
}
