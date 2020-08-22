package net.minecraft.world.level.dimension;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.phys.Vec3;

public abstract class Dimension {
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected final Level level;
   private final DimensionType type;
   protected boolean ultraWarm;
   protected boolean hasCeiling;
   protected final float[] brightnessRamp = new float[16];
   private final float[] sunriseCol = new float[4];

   public Dimension(Level var1, DimensionType var2, float var3) {
      this.level = var1;
      this.type = var2;

      for(int var4 = 0; var4 <= 15; ++var4) {
         float var5 = (float)var4 / 15.0F;
         float var6 = var5 / (4.0F - 3.0F * var5);
         this.brightnessRamp[var4] = Mth.lerp(var3, var6, 1.0F);
      }

   }

   public int getMoonPhase(long var1) {
      return (int)(var1 / 24000L % 8L + 8L) % 8;
   }

   @Nullable
   public float[] getSunriseColor(float var1, float var2) {
      float var3 = 0.4F;
      float var4 = Mth.cos(var1 * 6.2831855F) - 0.0F;
      float var5 = -0.0F;
      if (var4 >= -0.4F && var4 <= 0.4F) {
         float var6 = (var4 - -0.0F) / 0.4F * 0.5F + 0.5F;
         float var7 = 1.0F - (1.0F - Mth.sin(var6 * 3.1415927F)) * 0.99F;
         var7 *= var7;
         this.sunriseCol[0] = var6 * 0.3F + 0.7F;
         this.sunriseCol[1] = var6 * var6 * 0.7F + 0.2F;
         this.sunriseCol[2] = var6 * var6 * 0.0F + 0.2F;
         this.sunriseCol[3] = var7;
         return this.sunriseCol;
      } else {
         return null;
      }
   }

   public float getCloudHeight() {
      return 128.0F;
   }

   public boolean hasGround() {
      return true;
   }

   @Nullable
   public BlockPos getDimensionSpecificSpawn() {
      return null;
   }

   public double getClearColorScale() {
      return this.level.getLevelData().getGeneratorType() == LevelType.FLAT ? 1.0D : 0.03125D;
   }

   public boolean isUltraWarm() {
      return this.ultraWarm;
   }

   public boolean isHasSkyLight() {
      return this.type.hasSkyLight();
   }

   public boolean isHasCeiling() {
      return this.hasCeiling;
   }

   public float getBrightness(int var1) {
      return this.brightnessRamp[var1];
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder();
   }

   public void saveData() {
   }

   public void tick() {
   }

   public abstract ChunkGenerator createRandomLevelGenerator();

   @Nullable
   public abstract BlockPos getSpawnPosInChunk(ChunkPos var1, boolean var2);

   @Nullable
   public abstract BlockPos getValidSpawnPosition(int var1, int var2, boolean var3);

   public abstract float getTimeOfDay(long var1, float var3);

   public abstract boolean isNaturalDimension();

   public abstract Vec3 getFogColor(float var1, float var2);

   public abstract boolean mayRespawn();

   public abstract boolean isFoggyAt(int var1, int var2);

   public abstract DimensionType getType();
}
