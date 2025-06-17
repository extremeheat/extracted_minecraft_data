package net.minecraft.client.renderer.fog;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DimensionOrBossFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.client.renderer.fog.environment.PowderedSnowFogEnvironment;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class FogRenderer implements AutoCloseable {
   public static final int FOG_UBO_SIZE = (new Std140SizeCalculator()).putVec4().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().get();
   private static final List<FogEnvironment> FOG_ENVIRONMENTS = Lists.newArrayList(new FogEnvironment[]{new LavaFogEnvironment(), new PowderedSnowFogEnvironment(), new WaterFogEnvironment(), new BlindnessFogEnvironment(), new DarknessFogEnvironment(), new DimensionOrBossFogEnvironment(), new AtmosphericFogEnvironment()});
   private static boolean fogEnabled = true;
   private final GpuBuffer emptyBuffer;
   private final MappableRingBuffer regularBuffer;

   public FogRenderer() {
      super();
      GpuDevice var1 = RenderSystem.getDevice();
      this.regularBuffer = new MappableRingBuffer(() -> "Fog UBO", 130, FOG_UBO_SIZE);
      MemoryStack var2 = MemoryStack.stackPush();

      try {
         ByteBuffer var3 = var2.malloc(FOG_UBO_SIZE);
         this.updateBuffer(var3, 0, new Vector4f(0.0F), 3.4028235E38F, 3.4028235E38F, 3.4028235E38F, 3.4028235E38F, 3.4028235E38F, 3.4028235E38F);
         this.emptyBuffer = var1.createBuffer(() -> "Empty fog", 128, var3.flip());
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

      RenderSystem.setShaderFog(this.getBuffer(FogRenderer.FogMode.NONE));
   }

   public void close() {
      this.emptyBuffer.close();
      this.regularBuffer.close();
   }

   public void endFrame() {
      this.regularBuffer.rotate();
   }

   public GpuBufferSlice getBuffer(FogMode var1) {
      if (!fogEnabled) {
         return this.emptyBuffer.slice(0, FOG_UBO_SIZE);
      } else {
         GpuBufferSlice var10000;
         switch (var1.ordinal()) {
            case 0 -> var10000 = this.emptyBuffer.slice(0, FOG_UBO_SIZE);
            case 1 -> var10000 = this.regularBuffer.currentBuffer().slice(0, FOG_UBO_SIZE);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private Vector4f computeFogColor(Camera var1, float var2, ClientLevel var3, int var4, float var5, boolean var6) {
      FogType var7 = this.getFogType(var1, var6);
      Entity var8 = var1.getEntity();
      FogEnvironment var9 = null;
      FogEnvironment var10 = null;

      for(FogEnvironment var12 : FOG_ENVIRONMENTS) {
         if (var12.isApplicable(var7, var8)) {
            if (var9 == null && var12.providesColor()) {
               var9 = var12;
            }

            if (var10 == null && var12.modifiesDarkness()) {
               var10 = var12;
            }
         } else {
            var12.onNotApplicable();
         }
      }

      if (var9 == null) {
         throw new IllegalStateException("No color source environment found");
      } else {
         int var19 = var9.getBaseColor(var3, var1, var4, var5);
         float var20 = var3.getLevelData().voidDarknessOnsetRange();
         float var13 = Mth.clamp((var20 + (float)var3.getMinY() - (float)var1.getPosition().y) / var20, 0.0F, 1.0F);
         if (var10 != null) {
            LivingEntity var14 = (LivingEntity)var8;
            var13 = var10.getModifiedDarkness(var14, var13, var2);
         }

         float var21 = ARGB.redFloat(var19);
         float var15 = ARGB.greenFloat(var19);
         float var16 = ARGB.blueFloat(var19);
         if (var13 > 0.0F && var7 != FogType.LAVA && var7 != FogType.POWDER_SNOW) {
            float var17 = Mth.square(1.0F - var13);
            var21 *= var17;
            var15 *= var17;
            var16 *= var17;
         }

         if (var5 > 0.0F) {
            var21 = Mth.lerp(var5, var21, var21 * 0.7F);
            var15 = Mth.lerp(var5, var15, var15 * 0.6F);
            var16 = Mth.lerp(var5, var16, var16 * 0.6F);
         }

         float var22;
         if (var7 == FogType.WATER) {
            if (var8 instanceof LocalPlayer) {
               var22 = ((LocalPlayer)var8).getWaterVision();
            } else {
               var22 = 1.0F;
            }
         } else {
            label58: {
               if (var8 instanceof LivingEntity) {
                  LivingEntity var18 = (LivingEntity)var8;
                  if (var18.hasEffect(MobEffects.NIGHT_VISION) && !var18.hasEffect(MobEffects.DARKNESS)) {
                     var22 = GameRenderer.getNightVisionScale(var18, var2);
                     break label58;
                  }
               }

               var22 = 0.0F;
            }
         }

         if (var21 != 0.0F && var15 != 0.0F && var16 != 0.0F) {
            float var23 = 1.0F / Math.max(var21, Math.max(var15, var16));
            var21 = Mth.lerp(var22, var21, var21 * var23);
            var15 = Mth.lerp(var22, var15, var15 * var23);
            var16 = Mth.lerp(var22, var16, var16 * var23);
         }

         return new Vector4f(var21, var15, var16, 1.0F);
      }
   }

   public static boolean toggleFog() {
      return fogEnabled = !fogEnabled;
   }

   public Vector4f setupFog(Camera var1, int var2, boolean var3, DeltaTracker var4, float var5, ClientLevel var6) {
      float var7 = var4.getGameTimeDeltaPartialTick(false);
      Vector4f var8 = this.computeFogColor(var1, var7, var6, var2, var5, var3);
      float var9 = (float)(var2 * 16);
      FogType var10 = this.getFogType(var1, var3);
      Entity var11 = var1.getEntity();
      FogData var12 = new FogData();

      for(FogEnvironment var14 : FOG_ENVIRONMENTS) {
         if (var14.isApplicable(var10, var11)) {
            var14.setupFog(var12, var11, var1.getBlockPosition(), var6, var9, var4);
            break;
         }
      }

      float var19 = Mth.clamp(var9 / 10.0F, 4.0F, 64.0F);
      var12.renderDistanceStart = var9 - var19;
      var12.renderDistanceEnd = var9;

      try (GpuBuffer.MappedView var20 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.regularBuffer.currentBuffer(), false, true)) {
         this.updateBuffer(var20.data(), 0, var8, var12.environmentalStart, var12.environmentalEnd, var12.renderDistanceStart, var12.renderDistanceEnd, var12.skyEnd, var12.cloudEnd);
      }

      return var8;
   }

   private FogType getFogType(Camera var1, boolean var2) {
      FogType var3 = var1.getFluidInCamera();
      if (var3 == FogType.NONE) {
         return var2 ? FogType.DIMENSION_OR_BOSS : FogType.ATMOSPHERIC;
      } else {
         return var3;
      }
   }

   private void updateBuffer(ByteBuffer var1, int var2, Vector4f var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      var1.position(var2);
      Std140Builder.intoBuffer(var1).putVec4(var3).putFloat(var4).putFloat(var5).putFloat(var6).putFloat(var7).putFloat(var8).putFloat(var9);
   }

   public static enum FogMode {
      NONE,
      WORLD;

      private FogMode() {
      }

      // $FF: synthetic method
      private static FogMode[] $values() {
         return new FogMode[]{NONE, WORLD};
      }
   }
}
