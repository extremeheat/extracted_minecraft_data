package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunk extends ChunkAccess {
   @Nullable
   private volatile LevelLightEngine lightEngine;
   private volatile ChunkStatus status;
   private final List<CompoundTag> entities;
   private final Map<GenerationStep.Carving, CarvingMask> carvingMasks;
   @Nullable
   private BelowZeroRetrogen belowZeroRetrogen;
   private final ProtoChunkTicks<Block> blockTicks;
   private final ProtoChunkTicks<Fluid> fluidTicks;

   public ProtoChunk(ChunkPos var1, UpgradeData var2, LevelHeightAccessor var3, Registry<Biome> var4, @Nullable BlendingData var5) {
      this(var1, var2, (LevelChunkSection[])null, new ProtoChunkTicks(), new ProtoChunkTicks(), var3, var4, var5);
   }

   public ProtoChunk(ChunkPos var1, UpgradeData var2, @Nullable LevelChunkSection[] var3, ProtoChunkTicks<Block> var4, ProtoChunkTicks<Fluid> var5, LevelHeightAccessor var6, Registry<Biome> var7, @Nullable BlendingData var8) {
      super(var1, var2, var6, var7, 0L, var3, var8);
      this.status = ChunkStatus.EMPTY;
      this.entities = Lists.newArrayList();
      this.carvingMasks = new Object2ObjectArrayMap();
      this.blockTicks = var4;
      this.fluidTicks = var5;
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public ChunkAccess.TicksToSave getTicksForSerialization() {
      return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getY();
      if (this.isOutsideBuildHeight(var2)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunkSection var3 = this.getSection(this.getSectionIndex(var2));
         return var3.hasOnlyAir() ? Blocks.AIR.defaultBlockState() : var3.getBlockState(var1.getX() & 15, var2 & 15, var1.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      int var2 = var1.getY();
      if (this.isOutsideBuildHeight(var2)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunkSection var3 = this.getSection(this.getSectionIndex(var2));
         return var3.hasOnlyAir() ? Fluids.EMPTY.defaultFluidState() : var3.getFluidState(var1.getX() & 15, var2 & 15, var1.getZ() & 15);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      if (var5 >= this.getMinBuildHeight() && var5 < this.getMaxBuildHeight()) {
         int var7 = this.getSectionIndex(var5);
         LevelChunkSection var8 = this.getSection(var7);
         boolean var9 = var8.hasOnlyAir();
         if (var9 && var2.is(Blocks.AIR)) {
            return var2;
         } else {
            int var10 = SectionPos.sectionRelative(var4);
            int var11 = SectionPos.sectionRelative(var5);
            int var12 = SectionPos.sectionRelative(var6);
            BlockState var13 = var8.setBlockState(var10, var11, var12, var2);
            if (this.status.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
               boolean var14 = var8.hasOnlyAir();
               if (var14 != var9) {
                  this.lightEngine.updateSectionStatus(var1, var14);
               }

               if (LightEngine.hasDifferentLightProperties(this, var1, var13, var2)) {
                  this.skyLightSources.update(this, var10, var5, var12);
                  this.lightEngine.checkBlock(var1);
               }
            }

            EnumSet var19 = this.getPersistedStatus().heightmapsAfter();
            EnumSet var15 = null;
            Iterator var16 = var19.iterator();

            Heightmap.Types var17;
            while(var16.hasNext()) {
               var17 = (Heightmap.Types)var16.next();
               Heightmap var18 = (Heightmap)this.heightmaps.get(var17);
               if (var18 == null) {
                  if (var15 == null) {
                     var15 = EnumSet.noneOf(Heightmap.Types.class);
                  }

                  var15.add(var17);
               }
            }

            if (var15 != null) {
               Heightmap.primeHeightmaps(this, var15);
            }

            var16 = var19.iterator();

            while(var16.hasNext()) {
               var17 = (Heightmap.Types)var16.next();
               ((Heightmap)this.heightmaps.get(var17)).update(var10, var5, var12, var2);
            }

            return var13;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public void setBlockEntity(BlockEntity var1) {
      this.blockEntities.put(var1.getBlockPos(), var1);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return (BlockEntity)this.blockEntities.get(var1);
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public void addEntity(CompoundTag var1) {
      this.entities.add(var1);
   }

   public void addEntity(Entity var1) {
      if (!var1.isPassenger()) {
         CompoundTag var2 = new CompoundTag();
         var1.save(var2);
         this.addEntity(var2);
      }
   }

   public void setStartForStructure(Structure var1, StructureStart var2) {
      BelowZeroRetrogen var3 = this.getBelowZeroRetrogen();
      if (var3 != null && var2.isValid()) {
         BoundingBox var4 = var2.getBoundingBox();
         LevelHeightAccessor var5 = this.getHeightAccessorForGeneration();
         if (var4.minY() < var5.getMinBuildHeight() || var4.maxY() >= var5.getMaxBuildHeight()) {
            return;
         }
      }

      super.setStartForStructure(var1, var2);
   }

   public List<CompoundTag> getEntities() {
      return this.entities;
   }

   public ChunkStatus getPersistedStatus() {
      return this.status;
   }

   public void setPersistedStatus(ChunkStatus var1) {
      this.status = var1;
      if (this.belowZeroRetrogen != null && var1.isOrAfter(this.belowZeroRetrogen.targetStatus())) {
         this.setBelowZeroRetrogen((BelowZeroRetrogen)null);
      }

      this.setUnsaved(true);
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      if (this.getHighestGeneratedStatus().isOrAfter(ChunkStatus.BIOMES)) {
         return super.getNoiseBiome(var1, var2, var3);
      } else {
         throw new IllegalStateException("Asking for biomes before we have biomes");
      }
   }

   public static short packOffsetCoordinates(BlockPos var0) {
      int var1 = var0.getX();
      int var2 = var0.getY();
      int var3 = var0.getZ();
      int var4 = var1 & 15;
      int var5 = var2 & 15;
      int var6 = var3 & 15;
      return (short)(var4 | var5 << 4 | var6 << 8);
   }

   public static BlockPos unpackOffsetCoordinates(short var0, int var1, ChunkPos var2) {
      int var3 = SectionPos.sectionToBlockCoord(var2.x, var0 & 15);
      int var4 = SectionPos.sectionToBlockCoord(var1, var0 >>> 4 & 15);
      int var5 = SectionPos.sectionToBlockCoord(var2.z, var0 >>> 8 & 15);
      return new BlockPos(var3, var4, var5);
   }

   public void markPosForPostprocessing(BlockPos var1) {
      if (!this.isOutsideBuildHeight(var1)) {
         ChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex(var1.getY())).add(packOffsetCoordinates(var1));
      }

   }

   public void addPackedPostProcess(short var1, int var2) {
      ChunkAccess.getOrCreateOffsetList(this.postProcessing, var2).add(var1);
   }

   public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.pendingBlockEntities);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1, HolderLookup.Provider var2) {
      BlockEntity var3 = this.getBlockEntity(var1);
      return var3 != null ? var3.saveWithFullMetadata(var2) : (CompoundTag)this.pendingBlockEntities.get(var1);
   }

   public void removeBlockEntity(BlockPos var1) {
      this.blockEntities.remove(var1);
      this.pendingBlockEntities.remove(var1);
   }

   @Nullable
   public CarvingMask getCarvingMask(GenerationStep.Carving var1) {
      return (CarvingMask)this.carvingMasks.get(var1);
   }

   public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving var1) {
      return (CarvingMask)this.carvingMasks.computeIfAbsent(var1, (var1x) -> {
         return new CarvingMask(this.getHeight(), this.getMinBuildHeight());
      });
   }

   public void setCarvingMask(GenerationStep.Carving var1, CarvingMask var2) {
      this.carvingMasks.put(var1, var2);
   }

   public void setLightEngine(LevelLightEngine var1) {
      this.lightEngine = var1;
   }

   public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen var1) {
      this.belowZeroRetrogen = var1;
   }

   @Nullable
   public BelowZeroRetrogen getBelowZeroRetrogen() {
      return this.belowZeroRetrogen;
   }

   private static <T> LevelChunkTicks<T> unpackTicks(ProtoChunkTicks<T> var0) {
      return new LevelChunkTicks(var0.scheduledTicks());
   }

   public LevelChunkTicks<Block> unpackBlockTicks() {
      return unpackTicks(this.blockTicks);
   }

   public LevelChunkTicks<Fluid> unpackFluidTicks() {
      return unpackTicks(this.fluidTicks);
   }

   public LevelHeightAccessor getHeightAccessorForGeneration() {
      return (LevelHeightAccessor)(this.isUpgrading() ? BelowZeroRetrogen.UPGRADE_HEIGHT_ACCESSOR : this);
   }
}
