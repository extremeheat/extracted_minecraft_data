package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public abstract class DimensionSpecialEffects {
   private static final Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), (var0) -> {
      OverworldEffects var1 = new OverworldEffects();
      var0.defaultReturnValue(var1);
      var0.put(BuiltinDimensionTypes.OVERWORLD_EFFECTS, var1);
      var0.put(BuiltinDimensionTypes.NETHER_EFFECTS, new NetherEffects());
      var0.put(BuiltinDimensionTypes.END_EFFECTS, new EndEffects());
   });
   private final float cloudLevel;
   private final boolean hasGround;
   private final SkyType skyType;
   private final boolean forceBrightLightmap;
   private final boolean constantAmbientLight;

   public DimensionSpecialEffects(float var1, boolean var2, SkyType var3, boolean var4, boolean var5) {
      super();
      this.cloudLevel = var1;
      this.hasGround = var2;
      this.skyType = var3;
      this.forceBrightLightmap = var4;
      this.constantAmbientLight = var5;
   }

   public static DimensionSpecialEffects forType(DimensionType var0) {
      return (DimensionSpecialEffects)EFFECTS.get(var0.effectsLocation());
   }

   public boolean isSunriseOrSunset(float var1) {
      return false;
   }

   public int getSunriseOrSunsetColor(float var1) {
      return 0;
   }

   public float getCloudHeight() {
      return this.cloudLevel;
   }

   public boolean hasGround() {
      return this.hasGround;
   }

   public abstract Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2);

   public abstract boolean isFoggyAt(int var1, int var2);

   public SkyType skyType() {
      return this.skyType;
   }

   public boolean forceBrightLightmap() {
      return this.forceBrightLightmap;
   }

   public boolean constantAmbientLight() {
      return this.constantAmbientLight;
   }

   public static enum SkyType {
      NONE,
      OVERWORLD,
      END;

      private SkyType() {
      }

      // $FF: synthetic method
      private static SkyType[] $values() {
         return new SkyType[]{NONE, OVERWORLD, END};
      }
   }

   public static class OverworldEffects extends DimensionSpecialEffects {
      public static final int CLOUD_LEVEL = 192;
      private static final float SUNRISE_AND_SUNSET_TIMESPAN = 0.4F;

      public OverworldEffects() {
         super(192.0F, true, DimensionSpecialEffects.SkyType.OVERWORLD, false, false);
      }

      public boolean isSunriseOrSunset(float var1) {
         float var2 = Mth.cos(var1 * 6.2831855F);
         return var2 >= -0.4F && var2 <= 0.4F;
      }

      public int getSunriseOrSunsetColor(float var1) {
         float var2 = Mth.cos(var1 * 6.2831855F);
         float var3 = var2 / 0.4F * 0.5F + 0.5F;
         float var4 = Mth.square(1.0F - (1.0F - Mth.sin(var3 * 3.1415927F)) * 0.99F);
         return ARGB.colorFromFloat(var4, var3 * 0.3F + 0.7F, var3 * var3 * 0.7F + 0.2F, 0.2F);
      }

      public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
         return var1.multiply((double)(var2 * 0.94F + 0.06F), (double)(var2 * 0.94F + 0.06F), (double)(var2 * 0.91F + 0.09F));
      }

      public boolean isFoggyAt(int var1, int var2) {
         return false;
      }
   }

   public static class NetherEffects extends DimensionSpecialEffects {
      public NetherEffects() {
         super(0.0F / 0.0F, true, DimensionSpecialEffects.SkyType.NONE, false, true);
      }

      public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
         return var1;
      }

      public boolean isFoggyAt(int var1, int var2) {
         return true;
      }
   }

   public static class EndEffects extends DimensionSpecialEffects {
      public EndEffects() {
         super(0.0F / 0.0F, false, DimensionSpecialEffects.SkyType.END, true, false);
      }

      public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
         return var1.scale(0.15000000596046448);
      }

      public boolean isFoggyAt(int var1, int var2) {
         return false;
      }
   }
}
