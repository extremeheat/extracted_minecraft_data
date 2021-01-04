package net.minecraft.world.level.dimension.end;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.TheEndBiomeSourceSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.phys.Vec3;

public class TheEndDimension extends Dimension {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   private final EndDragonFight dragonFight;

   public TheEndDimension(Level var1, DimensionType var2) {
      super(var1, var2);
      CompoundTag var3 = var1.getLevelData().getDimensionData(DimensionType.THE_END);
      this.dragonFight = var1 instanceof ServerLevel ? new EndDragonFight((ServerLevel)var1, var3.getCompound("DragonFight")) : null;
   }

   public ChunkGenerator<?> createRandomLevelGenerator() {
      TheEndGeneratorSettings var1 = (TheEndGeneratorSettings)ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
      var1.setDefaultBlock(Blocks.END_STONE.defaultBlockState());
      var1.setDefaultFluid(Blocks.AIR.defaultBlockState());
      var1.setSpawnPosition(this.getDimensionSpecificSpawn());
      return ChunkGeneratorType.FLOATING_ISLANDS.create(this.level, BiomeSourceType.THE_END.create(((TheEndBiomeSourceSettings)BiomeSourceType.THE_END.createSettings()).setSeed(this.level.getSeed())), var1);
   }

   public float getTimeOfDay(long var1, float var3) {
      return 0.0F;
   }

   @Nullable
   public float[] getSunriseColor(float var1, float var2) {
      return null;
   }

   public Vec3 getFogColor(float var1, float var2) {
      int var3 = 10518688;
      float var4 = Mth.cos(var1 * 6.2831855F) * 2.0F + 0.5F;
      var4 = Mth.clamp(var4, 0.0F, 1.0F);
      float var5 = 0.627451F;
      float var6 = 0.5019608F;
      float var7 = 0.627451F;
      var5 *= var4 * 0.0F + 0.15F;
      var6 *= var4 * 0.0F + 0.15F;
      var7 *= var4 * 0.0F + 0.15F;
      return new Vec3((double)var5, (double)var6, (double)var7);
   }

   public boolean hasGround() {
      return false;
   }

   public boolean mayRespawn() {
      return false;
   }

   public boolean isNaturalDimension() {
      return false;
   }

   public float getCloudHeight() {
      return 8.0F;
   }

   @Nullable
   public BlockPos getSpawnPosInChunk(ChunkPos var1, boolean var2) {
      Random var3 = new Random(this.level.getSeed());
      BlockPos var4 = new BlockPos(var1.getMinBlockX() + var3.nextInt(15), 0, var1.getMaxBlockZ() + var3.nextInt(15));
      return this.level.getTopBlockState(var4).getMaterial().blocksMotion() ? var4 : null;
   }

   public BlockPos getDimensionSpecificSpawn() {
      return END_SPAWN_POINT;
   }

   @Nullable
   public BlockPos getValidSpawnPosition(int var1, int var2, boolean var3) {
      return this.getSpawnPosInChunk(new ChunkPos(var1 >> 4, var2 >> 4), var3);
   }

   public boolean isFoggyAt(int var1, int var2) {
      return false;
   }

   public DimensionType getType() {
      return DimensionType.THE_END;
   }

   public void saveData() {
      CompoundTag var1 = new CompoundTag();
      if (this.dragonFight != null) {
         var1.put("DragonFight", this.dragonFight.saveData());
      }

      this.level.getLevelData().setDimensionData(DimensionType.THE_END, var1);
   }

   public void tick() {
      if (this.dragonFight != null) {
         this.dragonFight.tick();
      }

   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }
}
