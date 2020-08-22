package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LevelChunkSection EMPTY_SECTION = null;
   private final LevelChunkSection[] sections;
   private ChunkBiomeContainer biomes;
   private final Map pendingBlockEntities;
   private boolean loaded;
   private final Level level;
   private final Map heightmaps;
   private final UpgradeData upgradeData;
   private final Map blockEntities;
   private final ClassInstanceMultiMap[] entitySections;
   private final Map structureStarts;
   private final Map structuresRefences;
   private final ShortList[] postProcessing;
   private TickList blockTicks;
   private TickList liquidTicks;
   private boolean lastSaveHadEntities;
   private long lastSaveTime;
   private volatile boolean unsaved;
   private long inhabitedTime;
   @Nullable
   private Supplier fullStatus;
   @Nullable
   private Consumer postLoad;
   private final ChunkPos chunkPos;
   private volatile boolean isLightCorrect;

   public LevelChunk(Level var1, ChunkPos var2, ChunkBiomeContainer var3) {
      this(var1, var2, var3, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, (LevelChunkSection[])null, (Consumer)null);
   }

   public LevelChunk(Level var1, ChunkPos var2, ChunkBiomeContainer var3, UpgradeData var4, TickList var5, TickList var6, long var7, @Nullable LevelChunkSection[] var9, @Nullable Consumer var10) {
      this.sections = new LevelChunkSection[16];
      this.pendingBlockEntities = Maps.newHashMap();
      this.heightmaps = Maps.newEnumMap(Heightmap.Types.class);
      this.blockEntities = Maps.newHashMap();
      this.structureStarts = Maps.newHashMap();
      this.structuresRefences = Maps.newHashMap();
      this.postProcessing = new ShortList[16];
      this.entitySections = (ClassInstanceMultiMap[])(new ClassInstanceMultiMap[16]);
      this.level = var1;
      this.chunkPos = var2;
      this.upgradeData = var4;
      Heightmap.Types[] var11 = Heightmap.Types.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         Heightmap.Types var14 = var11[var13];
         if (ChunkStatus.FULL.heightmapsAfter().contains(var14)) {
            this.heightmaps.put(var14, new Heightmap(this, var14));
         }
      }

      for(int var15 = 0; var15 < this.entitySections.length; ++var15) {
         this.entitySections[var15] = new ClassInstanceMultiMap(Entity.class);
      }

      this.biomes = var3;
      this.blockTicks = var5;
      this.liquidTicks = var6;
      this.inhabitedTime = var7;
      this.postLoad = var10;
      if (var9 != null) {
         if (this.sections.length == var9.length) {
            System.arraycopy(var9, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", var9.length, this.sections.length);
         }
      }

   }

   public LevelChunk(Level var1, ProtoChunk var2) {
      this(var1, var2.getPos(), var2.getBiomes(), var2.getUpgradeData(), var2.getBlockTicks(), var2.getLiquidTicks(), var2.getInhabitedTime(), var2.getSections(), (Consumer)null);
      Iterator var3 = var2.getEntities().iterator();

      while(var3.hasNext()) {
         CompoundTag var4 = (CompoundTag)var3.next();
         EntityType.loadEntityRecursive(var4, var1, (var1x) -> {
            this.addEntity(var1x);
            return var1x;
         });
      }

      var3 = var2.getBlockEntities().values().iterator();

      while(var3.hasNext()) {
         BlockEntity var6 = (BlockEntity)var3.next();
         this.addBlockEntity(var6);
      }

      this.pendingBlockEntities.putAll(var2.getBlockEntityNbts());

      for(int var5 = 0; var5 < var2.getPostProcessing().length; ++var5) {
         this.postProcessing[var5] = var2.getPostProcessing()[var5];
      }

      this.setAllStarts(var2.getAllStarts());
      this.setAllReferences(var2.getAllReferences());
      var3 = var2.getHeightmaps().iterator();

      while(var3.hasNext()) {
         Entry var7 = (Entry)var3.next();
         if (ChunkStatus.FULL.heightmapsAfter().contains(var7.getKey())) {
            this.getOrCreateHeightmapUnprimed((Heightmap.Types)var7.getKey()).setRawData(((Heightmap)var7.getValue()).getRawData());
         }
      }

      this.setLightCorrect(var2.isLightCorrect());
      this.unsaved = true;
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1) {
      return (Heightmap)this.heightmaps.computeIfAbsent(var1, (var1x) -> {
         return new Heightmap(this, var1x);
      });
   }

   public Set getBlockEntitiesPos() {
      HashSet var1 = Sets.newHashSet(this.pendingBlockEntities.keySet());
      var1.addAll(this.blockEntities.keySet());
      return var1;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      if (this.level.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         BlockState var9 = null;
         if (var3 == 60) {
            var9 = Blocks.BARRIER.defaultBlockState();
         }

         if (var3 == 70) {
            var9 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var9 == null ? Blocks.AIR.defaultBlockState() : var9;
      } else {
         try {
            if (var3 >= 0 && var3 >> 4 < this.sections.length) {
               LevelChunkSection var5 = this.sections[var3 >> 4];
               if (!LevelChunkSection.isEmpty(var5)) {
                  return var5.getBlockState(var2 & 15, var3 & 15, var4 & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(var2, var3, var4);
            });
            throw new ReportedException(var6);
         }
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getFluidState(var1.getX(), var1.getY(), var1.getZ());
   }

   public FluidState getFluidState(int var1, int var2, int var3) {
      try {
         if (var2 >= 0 && var2 >> 4 < this.sections.length) {
            LevelChunkSection var4 = this.sections[var2 >> 4];
            if (!LevelChunkSection.isEmpty(var4)) {
               return var4.getFluidState(var1 & 15, var2 & 15, var3 & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Getting fluid state");
         CrashReportCategory var6 = var5.addCategory("Block being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      int var4 = var1.getX() & 15;
      int var5 = var1.getY();
      int var6 = var1.getZ() & 15;
      LevelChunkSection var7 = this.sections[var5 >> 4];
      if (var7 == EMPTY_SECTION) {
         if (var2.isAir()) {
            return null;
         }

         var7 = new LevelChunkSection(var5 >> 4 << 4);
         this.sections[var5 >> 4] = var7;
      }

      boolean var8 = var7.isEmpty();
      BlockState var9 = var7.setBlockState(var4, var5 & 15, var6, var2);
      if (var9 == var2) {
         return null;
      } else {
         Block var10 = var2.getBlock();
         Block var11 = var9.getBlock();
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(var4, var5, var6, var2);
         boolean var12 = var7.isEmpty();
         if (var8 != var12) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(var1, var12);
         }

         if (!this.level.isClientSide) {
            var9.onRemove(this.level, var1, var2, var3);
         } else if (var11 != var10 && var11 instanceof EntityBlock) {
            this.level.removeBlockEntity(var1);
         }

         if (var7.getBlockState(var4, var5 & 15, var6).getBlock() != var10) {
            return null;
         } else {
            BlockEntity var13;
            if (var11 instanceof EntityBlock) {
               var13 = this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
               if (var13 != null) {
                  var13.clearCache();
               }
            }

            if (!this.level.isClientSide) {
               var2.onPlace(this.level, var1, var9, var3);
            }

            if (var10 instanceof EntityBlock) {
               var13 = this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
               if (var13 == null) {
                  var13 = ((EntityBlock)var10).newBlockEntity(this.level);
                  this.level.setBlockEntity(var1, var13);
               } else {
                  var13.clearCache();
               }
            }

            this.unsaved = true;
            return var9;
         }
      }
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.level.getChunkSource().getLightEngine();
   }

   public void addEntity(Entity var1) {
      this.lastSaveHadEntities = true;
      int var2 = Mth.floor(var1.getX() / 16.0D);
      int var3 = Mth.floor(var1.getZ() / 16.0D);
      if (var2 != this.chunkPos.x || var3 != this.chunkPos.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", var2, var3, this.chunkPos.x, this.chunkPos.z, var1);
         var1.removed = true;
      }

      int var4 = Mth.floor(var1.getY() / 16.0D);
      if (var4 < 0) {
         var4 = 0;
      }

      if (var4 >= this.entitySections.length) {
         var4 = this.entitySections.length - 1;
      }

      var1.inChunk = true;
      var1.xChunk = this.chunkPos.x;
      var1.yChunk = var4;
      var1.zChunk = this.chunkPos.z;
      this.entitySections[var4].add(var1);
   }

   public void setHeightmap(Heightmap.Types var1, long[] var2) {
      ((Heightmap)this.heightmaps.get(var1)).setRawData(var2);
   }

   public void removeEntity(Entity var1) {
      this.removeEntity(var1, var1.yChunk);
   }

   public void removeEntity(Entity var1, int var2) {
      if (var2 < 0) {
         var2 = 0;
      }

      if (var2 >= this.entitySections.length) {
         var2 = this.entitySections.length - 1;
      }

      this.entitySections[var2].remove(var1);
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return ((Heightmap)this.heightmaps.get(var1)).getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos var1) {
      BlockState var2 = this.getBlockState(var1);
      Block var3 = var2.getBlock();
      return !var3.isEntityBlock() ? null : ((EntityBlock)var3).newBlockEntity(this.level);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      BlockEntity var3 = (BlockEntity)this.blockEntities.get(var1);
      if (var3 == null) {
         CompoundTag var4 = (CompoundTag)this.pendingBlockEntities.remove(var1);
         if (var4 != null) {
            BlockEntity var5 = this.promotePendingBlockEntity(var1, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      if (var3 == null) {
         if (var2 == LevelChunk.EntityCreationType.IMMEDIATE) {
            var3 = this.createBlockEntity(var1);
            this.level.setBlockEntity(var1, var3);
         }
      } else if (var3.isRemoved()) {
         this.blockEntities.remove(var1);
         return null;
      }

      return var3;
   }

   public void addBlockEntity(BlockEntity var1) {
      this.setBlockEntity(var1.getBlockPos(), var1);
      if (this.loaded || this.level.isClientSide()) {
         this.level.setBlockEntity(var1.getBlockPos(), var1);
      }

   }

   public void setBlockEntity(BlockPos var1, BlockEntity var2) {
      if (this.getBlockState(var1).getBlock() instanceof EntityBlock) {
         var2.setLevelAndPosition(this.level, var1);
         var2.clearRemoved();
         BlockEntity var3 = (BlockEntity)this.blockEntities.put(var1.immutable(), var2);
         if (var3 != null && var3 != var2) {
            var3.setRemoved();
         }

      }
   }

   public void setBlockEntityNbt(CompoundTag var1) {
      this.pendingBlockEntities.put(new BlockPos(var1.getInt("x"), var1.getInt("y"), var1.getInt("z")), var1);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1) {
      BlockEntity var2 = this.getBlockEntity(var1);
      CompoundTag var3;
      if (var2 != null && !var2.isRemoved()) {
         var3 = var2.save(new CompoundTag());
         var3.putBoolean("keepPacked", false);
         return var3;
      } else {
         var3 = (CompoundTag)this.pendingBlockEntities.get(var1);
         if (var3 != null) {
            var3 = var3.copy();
            var3.putBoolean("keepPacked", true);
         }

         return var3;
      }
   }

   public void removeBlockEntity(BlockPos var1) {
      if (this.loaded || this.level.isClientSide()) {
         BlockEntity var2 = (BlockEntity)this.blockEntities.remove(var1);
         if (var2 != null) {
            var2.setRemoved();
         }
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.accept(this);
         this.postLoad = null;
      }

   }

   public void markUnsaved() {
      this.unsaved = true;
   }

   public void getEntities(@Nullable Entity var1, AABB var2, List var3, @Nullable Predicate var4) {
      int var5 = Mth.floor((var2.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      label68:
      for(int var7 = var5; var7 <= var6; ++var7) {
         if (!this.entitySections[var7].isEmpty()) {
            Iterator var8 = this.entitySections[var7].iterator();

            while(true) {
               Entity var9;
               do {
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label68;
                        }

                        var9 = (Entity)var8.next();
                     } while(!var9.getBoundingBox().intersects(var2));
                  } while(var9 == var1);

                  if (var4 == null || var4.test(var9)) {
                     var3.add(var9);
                  }
               } while(!(var9 instanceof EnderDragon));

               EnderDragonPart[] var10 = ((EnderDragon)var9).getSubEntities();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  EnderDragonPart var13 = var10[var12];
                  if (var13 != var1 && var13.getBoundingBox().intersects(var2) && (var4 == null || var4.test(var13))) {
                     var3.add(var13);
                  }
               }
            }
         }
      }

   }

   public void getEntities(@Nullable EntityType var1, AABB var2, List var3, Predicate var4) {
      int var5 = Mth.floor((var2.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      label33:
      for(int var7 = var5; var7 <= var6; ++var7) {
         Iterator var8 = this.entitySections[var7].find(Entity.class).iterator();

         while(true) {
            Entity var9;
            do {
               if (!var8.hasNext()) {
                  continue label33;
               }

               var9 = (Entity)var8.next();
            } while(var1 != null && var9.getType() != var1);

            if (var9.getBoundingBox().intersects(var2) && var4.test(var9)) {
               var3.add(var9);
            }
         }
      }

   }

   public void getEntitiesOfClass(Class var1, AABB var2, List var3, @Nullable Predicate var4) {
      int var5 = Mth.floor((var2.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      label33:
      for(int var7 = var5; var7 <= var6; ++var7) {
         Iterator var8 = this.entitySections[var7].find(var1).iterator();

         while(true) {
            Entity var9;
            do {
               do {
                  if (!var8.hasNext()) {
                     continue label33;
                  }

                  var9 = (Entity)var8.next();
               } while(!var9.getBoundingBox().intersects(var2));
            } while(var4 != null && !var4.test(var9));

            var3.add(var9);
         }
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void replaceWithPacketData(@Nullable ChunkBiomeContainer var1, FriendlyByteBuf var2, CompoundTag var3, int var4) {
      boolean var5 = var1 != null;
      Predicate var6 = var5 ? (var0) -> {
         return true;
      } : (var1x) -> {
         return (var4 & 1 << (var1x.getY() >> 4)) != 0;
      };
      Stream var10000 = Sets.newHashSet(this.blockEntities.keySet()).stream().filter(var6);
      Level var10001 = this.level;
      var10000.forEach(var10001::removeBlockEntity);

      for(int var7 = 0; var7 < this.sections.length; ++var7) {
         LevelChunkSection var8 = this.sections[var7];
         if ((var4 & 1 << var7) == 0) {
            if (var5 && var8 != EMPTY_SECTION) {
               this.sections[var7] = EMPTY_SECTION;
            }
         } else {
            if (var8 == EMPTY_SECTION) {
               var8 = new LevelChunkSection(var7 << 4);
               this.sections[var7] = var8;
            }

            var8.read(var2);
         }
      }

      if (var1 != null) {
         this.biomes = var1;
      }

      Heightmap.Types[] var12 = Heightmap.Types.values();
      int var14 = var12.length;

      for(int var9 = 0; var9 < var14; ++var9) {
         Heightmap.Types var10 = var12[var9];
         String var11 = var10.getSerializationKey();
         if (var3.contains(var11, 12)) {
            this.setHeightmap(var10, var3.getLongArray(var11));
         }
      }

      Iterator var13 = this.blockEntities.values().iterator();

      while(var13.hasNext()) {
         BlockEntity var15 = (BlockEntity)var13.next();
         var15.clearCache();
      }

   }

   public ChunkBiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setLoaded(boolean var1) {
      this.loaded = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public Collection getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Map getBlockEntities() {
      return this.blockEntities;
   }

   public ClassInstanceMultiMap[] getEntitySections() {
      return this.entitySections;
   }

   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return (CompoundTag)this.pendingBlockEntities.get(var1);
   }

   public Stream getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), 255, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((var1) -> {
         return this.getBlockState(var1).getLightEmission() != 0;
      });
   }

   public TickList getBlockTicks() {
      return this.blockTicks;
   }

   public TickList getLiquidTicks() {
      return this.liquidTicks;
   }

   public void setUnsaved(boolean var1) {
      this.unsaved = var1;
   }

   public boolean isUnsaved() {
      return this.unsaved || this.lastSaveHadEntities && this.level.getGameTime() != this.lastSaveTime;
   }

   public void setLastSaveHadEntities(boolean var1) {
      this.lastSaveHadEntities = var1;
   }

   public void setLastSaveTime(long var1) {
      this.lastSaveTime = var1;
   }

   @Nullable
   public StructureStart getStartForFeature(String var1) {
      return (StructureStart)this.structureStarts.get(var1);
   }

   public void setStartForFeature(String var1, StructureStart var2) {
      this.structureStarts.put(var1, var2);
   }

   public Map getAllStarts() {
      return this.structureStarts;
   }

   public void setAllStarts(Map var1) {
      this.structureStarts.clear();
      this.structureStarts.putAll(var1);
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
   }

   public Map getAllReferences() {
      return this.structuresRefences;
   }

   public void setAllReferences(Map var1) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(var1);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long var1) {
      this.inhabitedTime = var1;
   }

   public void postProcessGeneration() {
      ChunkPos var1 = this.getPos();

      for(int var2 = 0; var2 < this.postProcessing.length; ++var2) {
         if (this.postProcessing[var2] != null) {
            ShortListIterator var3 = this.postProcessing[var2].iterator();

            while(var3.hasNext()) {
               Short var4 = (Short)var3.next();
               BlockPos var5 = ProtoChunk.unpackOffsetCoordinates(var4, var2, var1);
               BlockState var6 = this.getBlockState(var5);
               BlockState var7 = Block.updateFromNeighbourShapes(var6, this.level, var5);
               this.level.setBlock(var5, var7, 20);
            }

            this.postProcessing[var2].clear();
         }
      }

      this.unpackTicks();
      Iterator var8 = Sets.newHashSet(this.pendingBlockEntities.keySet()).iterator();

      while(var8.hasNext()) {
         BlockPos var9 = (BlockPos)var8.next();
         this.getBlockEntity(var9);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity promotePendingBlockEntity(BlockPos var1, CompoundTag var2) {
      BlockEntity var3;
      if ("DUMMY".equals(var2.getString("id"))) {
         Block var4 = this.getBlockState(var1).getBlock();
         if (var4 instanceof EntityBlock) {
            var3 = ((EntityBlock)var4).newBlockEntity(this.level);
         } else {
            var3 = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", var1, this.getBlockState(var1));
         }
      } else {
         var3 = BlockEntity.loadStatic(var2);
      }

      if (var3 != null) {
         var3.setLevelAndPosition(this.level, var1);
         this.addBlockEntity(var3);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(var1), var1);
      }

      return var3;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void unpackTicks() {
      if (this.blockTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.blockTicks).copyOut(this.level.getBlockTicks(), (var1) -> {
            return this.getBlockState(var1).getBlock();
         });
         this.blockTicks = EmptyTickList.empty();
      } else if (this.blockTicks instanceof ChunkTickList) {
         this.level.getBlockTicks().addAll(((ChunkTickList)this.blockTicks).ticks());
         this.blockTicks = EmptyTickList.empty();
      }

      if (this.liquidTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks(), (var1) -> {
            return this.getFluidState(var1).getType();
         });
         this.liquidTicks = EmptyTickList.empty();
      } else if (this.liquidTicks instanceof ChunkTickList) {
         this.level.getLiquidTicks().addAll(((ChunkTickList)this.liquidTicks).ticks());
         this.liquidTicks = EmptyTickList.empty();
      }

   }

   public void packTicks(ServerLevel var1) {
      if (this.blockTicks == EmptyTickList.empty()) {
         this.blockTicks = new ChunkTickList(Registry.BLOCK::getKey, var1.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false));
         this.setUnsaved(true);
      }

      if (this.liquidTicks == EmptyTickList.empty()) {
         this.liquidTicks = new ChunkTickList(Registry.FLUID::getKey, var1.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false));
         this.setUnsaved(true);
      }

   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return this.fullStatus == null ? ChunkHolder.FullChunkStatus.BORDER : (ChunkHolder.FullChunkStatus)this.fullStatus.get();
   }

   public void setFullStatus(Supplier var1) {
      this.fullStatus = var1;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean var1) {
      this.isLightCorrect = var1;
      this.setUnsaved(true);
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
