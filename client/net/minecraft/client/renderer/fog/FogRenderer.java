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
   private static final List<FogEnvironment> FOG_ENVIRONMENTS = Lists.newArrayList(new FogEnvironment[]{new LavaFogEnvironment(), new PowderedSnowFogEnvironment(), new BlindnessFogEnvironment(), new DarknessFogEnvironment(), new WaterFogEnvironment(), new AtmosphericFogEnvironment()});
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
         return this.emptyBuffer.slice(0L, (long)FOG_UBO_SIZE);
      } else {
         GpuBufferSlice var10000;
         switch (var1.ordinal()) {
            case 0 -> var10000 = this.emptyBuffer.slice(0L, (long)FOG_UBO_SIZE);
            case 1 -> var10000 = this.regularBuffer.currentBuffer().slice(0L, (long)FOG_UBO_SIZE);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private Vector4f computeFogColor(Camera var1, float var2, ClientLevel var3, int var4, float var5) {
      FogType var6 = this.getFogType(var1);
      Entity var7 = var1.entity();
      FogEnvironment var8 = null;
      FogEnvironment var9 = null;

      for(FogEnvironment var11 : FOG_ENVIRONMENTS) {
         if (var11.isApplicable(var6, var7)) {
            if (var8 == null && var11.providesColor()) {
               var8 = var11;
            }

            if (var9 == null && var11.modifiesDarkness()) {
               var9 = var11;
            }
         }
      }

      if (var8 == null) {
         throw new IllegalStateException("No color source environment found");
      } else {
         int var18 = var8.getBaseColor(var3, var1, var4, var2);
         float var19 = var3.getLevelData().voidDarknessOnsetRange();
         float var12 = Mth.clamp((var19 + (float)var3.getMinY() - (float)var1.position().y) / var19, 0.0F, 1.0F);
         if (var9 != null) {
            LivingEntity var13 = (LivingEntity)var7;
            var12 = var9.getModifiedDarkness(var13, var12, var2);
         }

         float var20 = ARGB.redFloat(var18);
         float var14 = ARGB.greenFloat(var18);
         float var15 = ARGB.blueFloat(var18);
         if (var12 > 0.0F && var6 != FogType.LAVA && var6 != FogType.POWDER_SNOW) {
            float var16 = Mth.square(1.0F - var12);
            var20 *= var16;
            var14 *= var16;
            var15 *= var16;
         }

         if (var5 > 0.0F) {
            var20 = Mth.lerp(var5, var20, var20 * 0.7F);
            var14 = Mth.lerp(var5, var14, var14 * 0.6F);
            var15 = Mth.lerp(var5, var15, var15 * 0.6F);
         }

         float var21;
         if (var6 == FogType.WATER) {
            if (var7 instanceof LocalPlayer) {
               var21 = ((LocalPlayer)var7).getWaterVision();
            } else {
               var21 = 1.0F;
            }
         } else {
            label57: {
               if (var7 instanceof LivingEntity) {
                  LivingEntity var17 = (LivingEntity)var7;
                  if (var17.hasEffect(MobEffects.NIGHT_VISION) && !var17.hasEffect(MobEffects.DARKNESS)) {
                     var21 = GameRenderer.getNightVisionScale(var17, var2);
                     break label57;
                  }
               }

               var21 = 0.0F;
            }
         }

         if (var20 != 0.0F && var14 != 0.0F && var15 != 0.0F) {
            float var22 = 1.0F / Math.max(var20, Math.max(var14, var15));
            var20 = Mth.lerp(var21, var20, var20 * var22);
            var14 = Mth.lerp(var21, var14, var14 * var22);
            var15 = Mth.lerp(var21, var15, var15 * var22);
         }

         return new Vector4f(var20, var14, var15, 1.0F);
      }
   }

   public static boolean toggleFog() {
      return fogEnabled = !fogEnabled;
   }

   public Vector4f setupFog(Camera var1, int var2, DeltaTracker var3, float var4, ClientLevel var5) {
      float var6 = var3.getGameTimeDeltaPartialTick(false);
      Vector4f var7 = this.computeFogColor(var1, var6, var5, var2, var4);
      float var8 = (float)(var2 * 16);
      FogType var9 = this.getFogType(var1);
      Entity var10 = var1.entity();
      FogData var11 = new FogData();

      for(FogEnvironment var13 : FOG_ENVIRONMENTS) {
         if (var13.isApplicable(var9, var10)) {
            var13.setupFog(var11, var1, var5, var8, var3);
            break;
         }
      }

      float var18 = Mth.clamp(var8 / 10.0F, 4.0F, 64.0F);
      var11.renderDistanceStart = var8 - var18;
      var11.renderDistanceEnd = var8;

      try (GpuBuffer.MappedView var19 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.regularBuffer.currentBuffer(), false, true)) {
         this.updateBuffer(var19.data(), 0, var7, var11.environmentalStart, var11.environmentalEnd, var11.renderDistanceStart, var11.renderDistanceEnd, var11.skyEnd, var11.cloudEnd);
      }

      return var7;
   }

   private FogType getFogType(Camera var1) {
      FogType var2 = var1.getFluidInCamera();
      return var2 == FogType.NONE ? FogType.ATMOSPHERIC : var2;
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
