package net.minecraft.world.level.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.CheckerboardBiomeSourceSettings;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.phys.Vec3;

public class NormalDimension extends Dimension {
   public NormalDimension(Level var1, DimensionType var2) {
      super(var1, var2, 0.0F);
   }

   public DimensionType getType() {
      return DimensionType.OVERWORLD;
   }

   public ChunkGenerator createRandomLevelGenerator() {
      LevelType var1 = this.level.getLevelData().getGeneratorType();
      ChunkGeneratorType var2 = ChunkGeneratorType.FLAT;
      ChunkGeneratorType var3 = ChunkGeneratorType.DEBUG;
      ChunkGeneratorType var4 = ChunkGeneratorType.CAVES;
      ChunkGeneratorType var5 = ChunkGeneratorType.FLOATING_ISLANDS;
      ChunkGeneratorType var6 = ChunkGeneratorType.SURFACE;
      BiomeSourceType var7 = BiomeSourceType.FIXED;
      BiomeSourceType var8 = BiomeSourceType.VANILLA_LAYERED;
      BiomeSourceType var9 = BiomeSourceType.CHECKERBOARD;
      if (var1 == LevelType.FLAT) {
         FlatLevelGeneratorSettings var23 = FlatLevelGeneratorSettings.fromObject(new Dynamic(NbtOps.INSTANCE, this.level.getLevelData().getGeneratorOptions()));
         FixedBiomeSourceSettings var20 = ((FixedBiomeSourceSettings)var7.createSettings(this.level.getLevelData())).setBiome(var23.getBiome());
         return var2.create(this.level, var7.create(var20), var23);
      } else if (var1 == LevelType.DEBUG_ALL_BLOCK_STATES) {
         FixedBiomeSourceSettings var22 = ((FixedBiomeSourceSettings)var7.createSettings(this.level.getLevelData())).setBiome(Biomes.PLAINS);
         return var3.create(this.level, var7.create(var22), var3.createSettings());
      } else if (var1 != LevelType.BUFFET) {
         OverworldGeneratorSettings var21 = (OverworldGeneratorSettings)var6.createSettings();
         OverworldBiomeSourceSettings var19 = ((OverworldBiomeSourceSettings)var8.createSettings(this.level.getLevelData())).setGeneratorSettings(var21);
         return var6.create(this.level, var8.create(var19), var21);
      } else {
         BiomeSource var10 = null;
         JsonElement var11 = (JsonElement)Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.level.getLevelData().getGeneratorOptions());
         JsonObject var12 = var11.getAsJsonObject();
         JsonObject var13 = var12.getAsJsonObject("biome_source");
         if (var13 != null && var13.has("type") && var13.has("options")) {
            BiomeSourceType var14 = (BiomeSourceType)Registry.BIOME_SOURCE_TYPE.get(new ResourceLocation(var13.getAsJsonPrimitive("type").getAsString()));
            JsonObject var15 = var13.getAsJsonObject("options");
            Biome[] var16 = new Biome[]{Biomes.OCEAN};
            if (var15.has("biomes")) {
               JsonArray var17 = var15.getAsJsonArray("biomes");
               var16 = var17.size() > 0 ? new Biome[var17.size()] : new Biome[]{Biomes.OCEAN};

               for(int var18 = 0; var18 < var17.size(); ++var18) {
                  var16[var18] = (Biome)Registry.BIOME.getOptional(new ResourceLocation(var17.get(var18).getAsString())).orElse(Biomes.OCEAN);
               }
            }

            if (BiomeSourceType.FIXED == var14) {
               FixedBiomeSourceSettings var27 = ((FixedBiomeSourceSettings)var7.createSettings(this.level.getLevelData())).setBiome(var16[0]);
               var10 = var7.create(var27);
            }

            if (BiomeSourceType.CHECKERBOARD == var14) {
               int var28 = var15.has("size") ? var15.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeSourceSettings var31 = ((CheckerboardBiomeSourceSettings)var9.createSettings(this.level.getLevelData())).setAllowedBiomes(var16).setSize(var28);
               var10 = var9.create(var31);
            }

            if (BiomeSourceType.VANILLA_LAYERED == var14) {
               OverworldBiomeSourceSettings var29 = (OverworldBiomeSourceSettings)var8.createSettings(this.level.getLevelData());
               var10 = var8.create(var29);
            }
         }

         if (var10 == null) {
            var10 = var7.create(((FixedBiomeSourceSettings)var7.createSettings(this.level.getLevelData())).setBiome(Biomes.OCEAN));
         }

         BlockState var24 = Blocks.STONE.defaultBlockState();
         BlockState var25 = Blocks.WATER.defaultBlockState();
         JsonObject var26 = var12.getAsJsonObject("chunk_generator");
         if (var26 != null && var26.has("options")) {
            JsonObject var30 = var26.getAsJsonObject("options");
            String var33;
            if (var30.has("default_block")) {
               var33 = var30.getAsJsonPrimitive("default_block").getAsString();
               var24 = ((Block)Registry.BLOCK.get(new ResourceLocation(var33))).defaultBlockState();
            }

            if (var30.has("default_fluid")) {
               var33 = var30.getAsJsonPrimitive("default_fluid").getAsString();
               var25 = ((Block)Registry.BLOCK.get(new ResourceLocation(var33))).defaultBlockState();
            }
         }

         if (var26 != null && var26.has("type")) {
            ChunkGeneratorType var32 = (ChunkGeneratorType)Registry.CHUNK_GENERATOR_TYPE.get(new ResourceLocation(var26.getAsJsonPrimitive("type").getAsString()));
            if (ChunkGeneratorType.CAVES == var32) {
               NetherGeneratorSettings var36 = (NetherGeneratorSettings)var4.createSettings();
               var36.setDefaultBlock(var24);
               var36.setDefaultFluid(var25);
               return var4.create(this.level, var10, var36);
            }

            if (ChunkGeneratorType.FLOATING_ISLANDS == var32) {
               TheEndGeneratorSettings var35 = (TheEndGeneratorSettings)var5.createSettings();
               var35.setSpawnPosition(new BlockPos(0, 64, 0));
               var35.setDefaultBlock(var24);
               var35.setDefaultFluid(var25);
               return var5.create(this.level, var10, var35);
            }
         }

         OverworldGeneratorSettings var34 = (OverworldGeneratorSettings)var6.createSettings();
         var34.setDefaultBlock(var24);
         var34.setDefaultFluid(var25);
         return var6.create(this.level, var10, var34);
      }
   }

   @Nullable
   public BlockPos getSpawnPosInChunk(ChunkPos var1, boolean var2) {
      for(int var3 = var1.getMinBlockX(); var3 <= var1.getMaxBlockX(); ++var3) {
         for(int var4 = var1.getMinBlockZ(); var4 <= var1.getMaxBlockZ(); ++var4) {
            BlockPos var5 = this.getValidSpawnPosition(var3, var4, var2);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos getValidSpawnPosition(int var1, int var2, boolean var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(var1, 0, var2);
      Biome var5 = this.level.getBiome(var4);
      BlockState var6 = var5.getSurfaceBuilderConfig().getTopMaterial();
      if (var3 && !var6.getBlock().is(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         LevelChunk var7 = this.level.getChunk(var1 >> 4, var2 >> 4);
         int var8 = var7.getHeight(Heightmap.Types.MOTION_BLOCKING, var1 & 15, var2 & 15);
         if (var8 < 0) {
            return null;
         } else if (var7.getHeight(Heightmap.Types.WORLD_SURFACE, var1 & 15, var2 & 15) > var7.getHeight(Heightmap.Types.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
            return null;
         } else {
            for(int var9 = var8 + 1; var9 >= 0; --var9) {
               var4.set(var1, var9, var2);
               BlockState var10 = this.level.getBlockState(var4);
               if (!var10.getFluidState().isEmpty()) {
                  break;
               }

               if (var10.equals(var6)) {
                  return var4.above().immutable();
               }
            }

            return null;
         }
      }
   }

   public float getTimeOfDay(long var1, float var3) {
      double var4 = Mth.frac((double)var1 / 24000.0D - 0.25D);
      double var6 = 0.5D - Math.cos(var4 * 3.141592653589793D) / 2.0D;
      return (float)(var4 * 2.0D + var6) / 3.0F;
   }

   public boolean isNaturalDimension() {
      return true;
   }

   public Vec3 getFogColor(float var1, float var2) {
      float var3 = Mth.cos(var1 * 6.2831855F) * 2.0F + 0.5F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public boolean mayRespawn() {
      return true;
   }

   public boolean isFoggyAt(int var1, int var2) {
      return false;
   }
}
