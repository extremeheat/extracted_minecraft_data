package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.dimension.DimensionType;

public class LightTexture implements AutoCloseable {
   private final DynamicTexture lightTexture;
   private final NativeImage lightPixels;
   private final ResourceLocation lightTextureLocation;
   private boolean updateLightTexture;
   private float blockLightRed;
   private float blockLightRedTotal;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer var1) {
      super();
      this.renderer = var1;
      this.minecraft = var1.getMinecraft();
      this.lightTexture = new DynamicTexture(16, 16, false);
      this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
      this.lightPixels = this.lightTexture.getPixels();
   }

   public void close() {
      this.lightTexture.close();
   }

   public void tick() {
      this.blockLightRedTotal = (float)((double)this.blockLightRedTotal + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.blockLightRedTotal = (float)((double)this.blockLightRedTotal * 0.9D);
      this.blockLightRed += this.blockLightRedTotal - this.blockLightRed;
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   public void turnOnLightLayer() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float var1 = 0.00390625F;
      GlStateManager.scalef(0.00390625F, 0.00390625F, 0.00390625F);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.minecraft.getTextureManager().bind(this.lightTextureLocation);
      GlStateManager.texParameter(3553, 10241, 9729);
      GlStateManager.texParameter(3553, 10240, 9729);
      GlStateManager.texParameter(3553, 10242, 10496);
      GlStateManager.texParameter(3553, 10243, 10496);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   public void updateLightTexture(float var1) {
      if (this.updateLightTexture) {
         this.minecraft.getProfiler().push("lightTex");
         MultiPlayerLevel var2 = this.minecraft.level;
         if (var2 != null) {
            float var3 = var2.getSkyDarken(1.0F);
            float var4 = var3 * 0.95F + 0.05F;
            float var6 = this.minecraft.player.getWaterVision();
            float var5;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               var5 = this.renderer.getNightVisionScale(this.minecraft.player, var1);
            } else if (var6 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               var5 = var6;
            } else {
               var5 = 0.0F;
            }

            for(int var7 = 0; var7 < 16; ++var7) {
               for(int var8 = 0; var8 < 16; ++var8) {
                  float var9 = var2.dimension.getBrightnessRamp()[var7] * var4;
                  float var10 = var2.dimension.getBrightnessRamp()[var8] * (this.blockLightRed * 0.1F + 1.5F);
                  if (var2.getSkyFlashTime() > 0) {
                     var9 = var2.dimension.getBrightnessRamp()[var7];
                  }

                  float var11 = var9 * (var3 * 0.65F + 0.35F);
                  float var12 = var9 * (var3 * 0.65F + 0.35F);
                  float var15 = var10 * ((var10 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float var16 = var10 * (var10 * var10 * 0.6F + 0.4F);
                  float var17 = var11 + var10;
                  float var18 = var12 + var15;
                  float var19 = var9 + var16;
                  var17 = var17 * 0.96F + 0.03F;
                  var18 = var18 * 0.96F + 0.03F;
                  var19 = var19 * 0.96F + 0.03F;
                  float var20;
                  if (this.renderer.getDarkenWorldAmount(var1) > 0.0F) {
                     var20 = this.renderer.getDarkenWorldAmount(var1);
                     var17 = var17 * (1.0F - var20) + var17 * 0.7F * var20;
                     var18 = var18 * (1.0F - var20) + var18 * 0.6F * var20;
                     var19 = var19 * (1.0F - var20) + var19 * 0.6F * var20;
                  }

                  if (var2.dimension.getType() == DimensionType.THE_END) {
                     var17 = 0.22F + var10 * 0.75F;
                     var18 = 0.28F + var15 * 0.75F;
                     var19 = 0.25F + var16 * 0.75F;
                  }

                  if (var5 > 0.0F) {
                     var20 = 1.0F / var17;
                     if (var20 > 1.0F / var18) {
                        var20 = 1.0F / var18;
                     }

                     if (var20 > 1.0F / var19) {
                        var20 = 1.0F / var19;
                     }

                     var17 = var17 * (1.0F - var5) + var17 * var20 * var5;
                     var18 = var18 * (1.0F - var5) + var18 * var20 * var5;
                     var19 = var19 * (1.0F - var5) + var19 * var20 * var5;
                  }

                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  if (var18 > 1.0F) {
                     var18 = 1.0F;
                  }

                  if (var19 > 1.0F) {
                     var19 = 1.0F;
                  }

                  var20 = (float)this.minecraft.options.gamma;
                  float var21 = 1.0F - var17;
                  float var22 = 1.0F - var18;
                  float var23 = 1.0F - var19;
                  var21 = 1.0F - var21 * var21 * var21 * var21;
                  var22 = 1.0F - var22 * var22 * var22 * var22;
                  var23 = 1.0F - var23 * var23 * var23 * var23;
                  var17 = var17 * (1.0F - var20) + var21 * var20;
                  var18 = var18 * (1.0F - var20) + var22 * var20;
                  var19 = var19 * (1.0F - var20) + var23 * var20;
                  var17 = var17 * 0.96F + 0.03F;
                  var18 = var18 * 0.96F + 0.03F;
                  var19 = var19 * 0.96F + 0.03F;
                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  if (var18 > 1.0F) {
                     var18 = 1.0F;
                  }

                  if (var19 > 1.0F) {
                     var19 = 1.0F;
                  }

                  if (var17 < 0.0F) {
                     var17 = 0.0F;
                  }

                  if (var18 < 0.0F) {
                     var18 = 0.0F;
                  }

                  if (var19 < 0.0F) {
                     var19 = 0.0F;
                  }

                  boolean var24 = true;
                  int var25 = (int)(var17 * 255.0F);
                  int var26 = (int)(var18 * 255.0F);
                  int var27 = (int)(var19 * 255.0F);
                  this.lightPixels.setPixelRGBA(var8, var7, -16777216 | var27 << 16 | var26 << 8 | var25);
               }
            }

            this.lightTexture.upload();
            this.updateLightTexture = false;
            this.minecraft.getProfiler().pop();
         }
      }
   }
}
