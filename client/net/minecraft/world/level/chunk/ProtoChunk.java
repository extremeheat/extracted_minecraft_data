package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos chunkPos;
   private volatile boolean isDirty;
   private Biome[] biomes;
   @Nullable
   private volatile LevelLightEngine lightEngine;
   private final Map<Heightmap.Types, Heightmap> heightmaps;
   private volatile ChunkStatus status;
   private final Map<BlockPos, BlockEntity> blockEntities;
   private final Map<BlockPos, CompoundTag> blockEntityNbts;
   private final LevelChunkSection[] sections;
   private final List<CompoundTag> entities;
   private final List<BlockPos> lights;
   private final ShortList[] postProcessing;
   private final Map<String, StructureStart> structureStarts;
   private final Map<String, LongSet> structuresRefences;
   private final UpgradeData upgradeData;
   private final ProtoTickList<Block> blockTicks;
   private final ProtoTickList<Fluid> liquidTicks;
   private long inhabitedTime;
   private final Map<GenerationStep.Carving, BitSet> carvingMasks;
   private volatile boolean isLightCorrect;

   public ProtoChunk(ChunkPos var1, UpgradeData var2) {
      this(var1, var2, (LevelChunkSection[])null, new ProtoTickList((var0) -> {
         return var0 == null || var0.defaultBlockState().isAir();
      }, var1), new ProtoTickList((var0) -> {
         return var0 == null || var0 == Fluids.EMPTY;
      }, var1));
   }

   public ProtoChunk(ChunkPos var1, UpgradeData var2, @Nullable LevelChunkSection[] var3, ProtoTickList<Block> var4, ProtoTickList<Fluid> var5) {
      super();
      this.heightmaps = Maps.newEnumMap(Heightmap.Types.class);
      this.status = ChunkStatus.EMPTY;
      this.blockEntities = Maps.newHashMap();
      this.blockEntityNbts = Maps.newHashMap();
      this.sections = new LevelChunkSection[16];
      this.entities = Lists.newArrayList();
      this.lights = Lists.newArrayList();
      this.postProcessing = new ShortList[16];
      this.structureStarts = Maps.newHashMap();
      this.structuresRefences = Maps.newHashMap();
      this.carvingMasks = Maps.newHashMap();
      this.chunkPos = var1;
      this.upgradeData = var2;
      this.blockTicks = var4;
      this.liquidTicks = var5;
      if (var3 != null) {
         if (this.sections.length == var3.length) {
            System.arraycopy(var3, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", var3.length, this.sections.length);
         }
      }

   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getY();
      if (Level.isOutsideBuildHeight(var2)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunkSection var3 = this.getSections()[var2 >> 4];
         return LevelChunkSection.isEmpty(var3) ? Blocks.AIR.defaultBlockState() : var3.getBlockState(var1.getX() & 15, var2 & 15, var1.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      int var2 = var1.getY();
      if (Level.isOutsideBuildHeight(var2)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunkSection var3 = this.getSections()[var2 >> 4];
         return LevelChunkSection.isEmpty(var3) ? Fluids.EMPTY.defaultFluidState() : var3.getFluidState(var1.getX() & 15, var2 & 15, var1.getZ() & 15);
      }
   }

   public Stream<BlockPos> getLights() {
      return this.lights.stream();
   }

   public ShortList[] getPackedLights() {
      ShortList[] var1 = new ShortList[16];
      Iterator var2 = this.lights.iterator();

      while(var2.hasNext()) {
         BlockPos var3 = (BlockPos)var2.next();
         ChunkAccess.getOrCreateOffsetList(var1, var3.getY() >> 4).add(packOffsetCoordinates(var3));
      }

      return var1;
   }

   public void addLight(short var1, int var2) {
      this.addLight(unpackOffsetCoordinates(var1, var2, this.chunkPos));
   }

   public void addLight(BlockPos var1) {
      this.lights.add(var1.immutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      if (var5 >= 0 && var5 < 256) {
         if (this.sections[var5 >> 4] == LevelChunk.EMPTY_SECTION && var2.getBlock() == Blocks.AIR) {
            return var2;
         } else {
            if (var2.getLightEmission() > 0) {
               this.lights.add(new BlockPos((var4 & 15) + this.getPos().getMinBlockX(), var5, (var6 & 15) + this.getPos().getMinBlockZ()));
            }

            LevelChunkSection var7 = this.getOrCreateSection(var5 >> 4);
            BlockState var8 = var7.setBlockState(var4 & 15, var5 & 15, var6 & 15, var2);
            if (this.status.isOrAfter(ChunkStatus.FEATURES) && var2 != var8 && (var2.getLightBlock(this, var1) != var8.getLightBlock(this, var1) || var2.getLightEmission() != var8.getLightEmission() || var2.useShapeForLightOcclusion() || var8.useShapeForLightOcclusion())) {
               LevelLightEngine var9 = this.getLightEngine();
               var9.checkBlock(var1);
            }

            EnumSet var14 = this.getStatus().heightmapsAfter();
            EnumSet var10 = null;
            Iterator var11 = var14.iterator();

            Heightmap.Types var12;
            while(var11.hasNext()) {
               var12 = (Heightmap.Types)var11.next();
               Heightmap var13 = (Heightmap)this.heightmaps.get(var12);
               if (var13 == null) {
                  if (var10 == null) {
                     var10 = EnumSet.noneOf(Heightmap.Types.class);
                  }

                  var10.add(var12);
               }
            }

            if (var10 != null) {
               Heightmap.primeHeightmaps(this, var10);
            }

            var11 = var14.iterator();

            while(var11.hasNext()) {
               var12 = (Heightmap.Types)var11.next();
               ((Heightmap)this.heightmaps.get(var12)).update(var4 & 15, var5, var6 & 15, var2);
            }

            return var8;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public LevelChunkSection getOrCreateSection(int var1) {
      if (this.sections[var1] == LevelChunk.EMPTY_SECTION) {
         this.sections[var1] = new LevelChunkSection(var1 << 4);
      }

      return this.sections[var1];
   }

   public void setBlockEntity(BlockPos var1, BlockEntity var2) {
      var2.setPosition(var1);
      this.blockEntities.put(var1, var2);
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      HashSet var1 = Sets.newHashSet(this.blockEntityNbts.keySet());
      var1.addAll(this.blockEntities.keySet());
      return var1;
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
      CompoundTag var2 = new CompoundTag();
      var1.save(var2);
      this.addEntity(var2);
   }

   public List<CompoundTag> getEntities() {
      return this.entities;
   }

   public void setBiomes(Biome[] var1) {
      this.biomes = var1;
   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public void setUnsaved(boolean var1) {
      this.isDirty = var1;
   }

   public boolean isUnsaved() {
      return this.isDirty;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus var1) {
      this.status = var1;
      this.setUnsaved(true);
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   public Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Types var1, long[] var2) {
      this.getOrCreateHeightmapUnprimed(var1).setRawData(var2);
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1) {
      return (Heightmap)this.heightmaps.computeIfAbsent(var1, (var1x) -> {
         return new Heightmap(this, var1x);
      });
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      Heightmap var4 = (Heightmap)this.heightmaps.get(var1);
      if (var4 == null) {
         Heightmap.primeHeightmaps(this, EnumSet.of(var1));
         var4 = (Heightmap)this.heightmaps.get(var1);
      }

      return var4.getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void setLastSaveTime(long var1) {
   }

   @Nullable
   public StructureStart getStartForFeature(String var1) {
      return (StructureStart)this.structureStarts.get(var1);
   }

   public void setStartForFeature(String var1, StructureStart var2) {
      this.structureStarts.put(var1, var2);
      this.isDirty = true;
   }

   public Map<String, StructureStart> getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map<String, StructureStart> var1) {
      this.structureStarts.clear();
      this.structureStarts.putAll(var1);
      this.isDirty = true;
   }

   public LongSet getReferencesForFeature(String var1) {
      return (LongSet)this.structuresRefences.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(String var1, long var2) {
      ((LongSet)this.structuresRefences.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      })).add(var2);
      this.isDirty = true;
   }

   public Map<String, LongSet> getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map<String, LongSet> var1) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(var1);
      this.isDirty = true;
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
      int var3 = (var0 & 15) + (var2.x << 4);
      int var4 = (var0 >>> 4 & 15) + (var1 << 4);
      int var5 = (var0 >>> 8 & 15) + (var2.z << 4);
      return new BlockPos(var3, var4, var5);
   }

   public void markPosForPostprocessing(BlockPos var1) {
      if (!Level.isOutsideBuildHeight(var1)) {
         ChunkAccess.getOrCreateOffsetList(this.postProcessing, var1.getY() >> 4).add(packOffsetCoordinates(var1));
      }

   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short var1, int var2) {
      ChunkAccess.getOrCreateOffsetList(this.postProcessing, var2).add(var1);
   }

   public ProtoTickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ProtoTickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long var1) {
      this.inhabitedTime = var1;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setBlockEntityNbt(CompoundTag var1) {
      this.blockEntityNbts.put(new BlockPos(var1.getInt("x"), var1.getInt("y"), var1.getInt("z")), var1);
   }

   public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.blockEntityNbts);
   }

   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return (CompoundTag)this.blockEntityNbts.get(var1);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1) {
      BlockEntity var2 = this.getBlockEntity(var1);
      return var2 != null ? var2.save(new CompoundTag()) : (CompoundTag)this.blockEntityNbts.get(var1);
   }

   public void removeBlockEntity(BlockPos var1) {
      this.blockEntities.remove(var1);
      this.blockEntityNbts.remove(var1);
   }

   public BitSet getCarvingMask(GenerationStep.Carving var1) {
      return (BitSet)this.carvingMasks.computeIfAbsent(var1, (var0) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStep.Carving var1, BitSet var2) {
      this.carvingMasks.put(var1, var2);
   }

   public void setLightEngine(LevelLightEngine var1) {
      this.lightEngine = var1;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean var1) {
      this.isLightCorrect = var1;
      this.setUnsaved(true);
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
