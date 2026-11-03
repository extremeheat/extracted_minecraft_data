package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.jspecify.annotations.Nullable;

public class DebugLevelSource extends ChunkGenerator {
   public static final MapCodec<DebugLevelSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryOps.retrieveElement(Biomes.PLAINS)).apply(i, i.stable(DebugLevelSource::new)));
   private static final int BLOCK_MARGIN = 2;
   private static final int ENTITY_SPACING = 8;
   public static final int HEIGHT = 70;
   private static final int BARRIER_HEIGHT = 60;
   private static final int MOB_HEIGHT = 70;
   private static final int MOB_BABY_HEIGHT = 76;
   private final List<BlockState> allBlocks;
   private final int gridWidth;
   private final int gridHeight;
   private final List<EntityType> allEntities;
   private final int entityGridWidth;
   private final int entityGridHeight;
   private final BlockState air;
   private final BlockState barrier;

   public DebugLevelSource(final Holder.Reference<Biome> plains) {
      super(new FixedBiomeSource(plains));
      this.allBlocks = (List)StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false).flatMap((b) -> b.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
      this.gridWidth = Mth.ceil(Mth.sqrt((float)this.allBlocks.size()));
      this.gridHeight = Mth.ceil((float)this.allBlocks.size() / (float)this.gridWidth);
      this.allEntities = (List)StreamSupport.stream(BuiltInRegistries.ENTITY_TYPE.spliterator(), false).collect(Collectors.toList());
      this.entityGridWidth = Mth.ceil(Mth.sqrt((float)this.allEntities.size()));
      this.entityGridHeight = Mth.ceil((float)this.allEntities.size() / (float)this.entityGridWidth);
      this.air = Blocks.AIR.defaultBlockState();
      this.barrier = Blocks.BARRIER.defaultBlockState();
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public void applyBiomeDecoration(final WorldGenLevel level, final ChunkAccess chunk, final StructureManager structureManager) {
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
      ChunkPos centerPos = chunk.getPos();
      int chunkX = centerPos.x();
      int chunkZ = centerPos.z();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int worldX = SectionPos.sectionToBlockCoord(chunkX, x);
            int worldZ = SectionPos.sectionToBlockCoord(chunkZ, z);
            level.setBlock(blockPos.set(worldX, 60, worldZ), this.barrier, 2);
            BlockState state = this.getBlockStateFor(worldX, worldZ);
            level.setBlock(blockPos.set(worldX, 70, worldZ), state, 2);
         }
      }

   }

   public CompletableFuture<ChunkAccess> buildTerrain(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final StructureManager structureManager, final @Nullable WorldGenRegion carverBiomeRegion, final Set<Holder<Biome>> possibleBiomes) {
      return CompletableFuture.completedFuture(chunk);
   }

   public NoiseColumn getBaseColumn(final int x, final int z, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      return new NoiseColumn(heightAccessor.getMinY(), 0);
   }

   public void addDebugScreenInfo(final BiConsumer<String, String> addFact, final RandomState randomState, final BlockPos feetPos, final SamplerContext samplerContext) {
   }

   private BlockState getBlockStateFor(int worldX, int worldZ) {
      BlockState state = this.air;
      if (worldX > 0 && worldZ > 0 && worldX % 2 != 0 && worldZ % 2 != 0) {
         worldX /= 2;
         worldZ /= 2;
         if (worldX < this.gridWidth && worldZ < this.gridHeight) {
            int index = Mth.abs(worldX * this.gridWidth + worldZ);
            if (index < this.allBlocks.size()) {
               state = (BlockState)this.allBlocks.get(index);
            }
         }
      }

      return state;
   }

   public void spawnOriginalMobs(final WorldGenRegion worldGenRegion) {
      ChunkPos centerPos = worldGenRegion.getCenter();
      int chunkX = centerPos.x();
      int chunkZ = centerPos.z();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int worldX = SectionPos.sectionToBlockCoord(chunkX, x);
            int worldZ = SectionPos.sectionToBlockCoord(chunkZ, z);
            Optional<EntityType> entityTypeRef = this.getEntityTypeFor(worldX, worldZ);
            if (!entityTypeRef.isEmpty()) {
               this.spawnEntity(worldGenRegion, (EntityType)entityTypeRef.get(), worldX, worldZ);
            }
         }
      }

   }

   private Optional<EntityType> getEntityTypeFor(int worldX, int worldZ) {
      Optional<EntityType> entityTypeRef = Optional.empty();
      worldX = -worldX;
      worldZ = -worldZ;
      if (worldX > 0 && worldZ > 0 && worldX % 8 == 0 && worldZ % 8 == 0) {
         worldX = (worldX - 1) / 8;
         worldZ = (worldZ - 1) / 8;
         if (worldX <= this.entityGridWidth && worldZ <= this.entityGridHeight) {
            int index = Mth.abs(worldX * this.entityGridWidth + worldZ);
            if (index < this.allEntities.size()) {
               entityTypeRef = Optional.of((EntityType)this.allEntities.get(index));
            }
         }
      }

      return entityTypeRef;
   }

   private void spawnEntity(final WorldGenRegion worldGenRegion, final EntityType entityType, final int worldX, final int worldZ) {
      Entity entity = entityType.create(worldGenRegion.getLevel(), (EntitySpawnReason)EntitySpawnReason.COMMAND);
      if (entity != null) {
         entity.setPos((double)worldX, 70.0, (double)worldZ);
         this.addEntity(worldGenRegion, entity);
         if (entity instanceof AgeableMob) {
            AgeableMob mob = (AgeableMob)entity;
            if (mob.canBeABaby()) {
               AgeableMob baby = (AgeableMob)entityType.create(worldGenRegion.getLevel(), (EntitySpawnReason)EntitySpawnReason.COMMAND);
               baby.setBaby(true);
               baby.setAgeLocked(true);
               baby.setPos((double)worldX, 76.0, (double)worldZ);
               this.addEntity(worldGenRegion, baby);
            }
         }
      }

   }

   private void addEntity(final WorldGenRegion worldGenRegion, final Entity entity) {
      entity.setYRot(0.0F);
      entity.setPermanentlyInvulnerable(true);
      entity.setCustomName(entity.getType().getDescription());
      if (entity instanceof Mob mob) {
         mob.setNoAi(true);
      }

      worldGenRegion.addFreshEntity(entity);
   }

   public int getMinY() {
      return 0;
   }

   public int getGenDepth() {
      return 384;
   }

   public int getSeaLevel() {
      return 63;
   }
}
