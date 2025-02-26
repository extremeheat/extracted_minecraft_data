package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ShortTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public record SerializableChunkData(Registry<Biome> biomeRegistry, ChunkPos chunkPos, int minSectionY, long lastUpdateTime, long inhabitedTime, ChunkStatus chunkStatus, @Nullable BlendingData.Packed blendingData, @Nullable BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, @Nullable long[] carvingMask, Map<Heightmap.Types, long[]> heightmaps, ChunkAccess.PackedTicks packedTicks, ShortList[] postProcessingSections, boolean lightCorrect, List<SectionData> sectionData, List<CompoundTag> entities, List<CompoundTag> blockEntities, CompoundTag structureData) {
   private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC;
   private static final Codec<List<SavedTick<Block>>> BLOCK_TICKS_CODEC;
   private static final Codec<List<SavedTick<Fluid>>> FLUID_TICKS_CODEC;
   private static final Logger LOGGER;
   private static final String TAG_UPGRADE_DATA = "UpgradeData";
   private static final String BLOCK_TICKS_TAG = "block_ticks";
   private static final String FLUID_TICKS_TAG = "fluid_ticks";
   public static final String X_POS_TAG = "xPos";
   public static final String Z_POS_TAG = "zPos";
   public static final String HEIGHTMAPS_TAG = "Heightmaps";
   public static final String IS_LIGHT_ON_TAG = "isLightOn";
   public static final String SECTIONS_TAG = "sections";
   public static final String BLOCK_LIGHT_TAG = "BlockLight";
   public static final String SKY_LIGHT_TAG = "SkyLight";

   public SerializableChunkData(Registry<Biome> var1, ChunkPos var2, int var3, long var4, long var6, ChunkStatus var8, @Nullable BlendingData.Packed var9, @Nullable BelowZeroRetrogen var10, UpgradeData var11, @Nullable long[] var12, Map<Heightmap.Types, long[]> var13, ChunkAccess.PackedTicks var14, ShortList[] var15, boolean var16, List<SectionData> var17, List<CompoundTag> var18, List<CompoundTag> var19, CompoundTag var20) {
      super();
      this.biomeRegistry = var1;
      this.chunkPos = var2;
      this.minSectionY = var3;
      this.lastUpdateTime = var4;
      this.inhabitedTime = var6;
      this.chunkStatus = var8;
      this.blendingData = var9;
      this.belowZeroRetrogen = var10;
      this.upgradeData = var11;
      this.carvingMask = var12;
      this.heightmaps = var13;
      this.packedTicks = var14;
      this.postProcessingSections = var15;
      this.lightCorrect = var16;
      this.sectionData = var17;
      this.entities = var18;
      this.blockEntities = var19;
      this.structureData = var20;
   }

   @Nullable
   public static SerializableChunkData parse(LevelHeightAccessor var0, RegistryAccess var1, CompoundTag var2) {
      if (var2.getString("Status").isEmpty()) {
         return null;
      } else {
         ChunkPos var3 = new ChunkPos(var2.getIntOr("xPos", 0), var2.getIntOr("zPos", 0));
         long var4 = var2.getLongOr("LastUpdate", 0L);
         long var6 = var2.getLongOr("InhabitedTime", 0L);
         ChunkStatus var8 = (ChunkStatus)var2.read("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY);
         UpgradeData var9 = (UpgradeData)var2.getCompound("UpgradeData").map((var1x) -> new UpgradeData(var1x, var0)).orElse(UpgradeData.EMPTY);
         boolean var10 = var2.getBooleanOr("isLightOn", false);
         BlendingData.Packed var11 = (BlendingData.Packed)var2.read("blending_data", BlendingData.Packed.CODEC).orElse((Object)null);
         BelowZeroRetrogen var12 = (BelowZeroRetrogen)var2.read("below_zero_retrogen", BelowZeroRetrogen.CODEC).orElse((Object)null);
         long[] var13 = (long[])var2.getLongArray("carving_mask").orElse((Object)null);
         EnumMap var14 = new EnumMap(Heightmap.Types.class);
         var2.getCompound("Heightmaps").ifPresent((var2x) -> {
            for(Heightmap.Types var4 : var8.heightmapsAfter()) {
               var2x.getLongArray(var4.getSerializationKey()).ifPresent((var2) -> var14.put(var4, var2));
            }

         });
         List var15 = SavedTick.filterTickListForChunk((List)var2.read("block_ticks", BLOCK_TICKS_CODEC).orElse(List.of()), var3);
         List var16 = SavedTick.filterTickListForChunk((List)var2.read("fluid_ticks", FLUID_TICKS_CODEC).orElse(List.of()), var3);
         ChunkAccess.PackedTicks var17 = new ChunkAccess.PackedTicks(var15, var16);
         ListTag var18 = var2.getListOrEmpty("PostProcessing");
         ShortList[] var19 = new ShortList[var18.size()];

         for(int var20 = 0; var20 < var18.size(); ++var20) {
            ListTag var21 = var18.getListOrEmpty(var20);
            ShortArrayList var22 = new ShortArrayList(var21.size());

            for(int var23 = 0; var23 < var21.size(); ++var23) {
               var22.add(var21.getShortOr(var23, (short)0));
            }

            var19[var20] = var22;
         }

         List var34 = var2.getList("entities").stream().flatMap(ListTag::compoundStream).toList();
         List var35 = var2.getList("block_entities").stream().flatMap(ListTag::compoundStream).toList();
         CompoundTag var36 = var2.getCompoundOrEmpty("structures");
         ListTag var37 = var2.getListOrEmpty("sections");
         ArrayList var24 = new ArrayList(var37.size());
         Registry var25 = var1.lookupOrThrow(Registries.BIOME);
         Codec var26 = makeBiomeCodec(var25);

         for(int var27 = 0; var27 < var37.size(); ++var27) {
            Optional var28 = var37.getCompound(var27);
            if (!var28.isEmpty()) {
               CompoundTag var29 = (CompoundTag)var28.get();
               byte var30 = var29.getByteOr("Y", (byte)0);
               LevelChunkSection var31;
               if (var30 >= var0.getMinSectionY() && var30 <= var0.getMaxSectionY()) {
                  PalettedContainer var32 = (PalettedContainer)var29.getCompound("block_states").map((var2x) -> (PalettedContainer)BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var2x).promotePartial((var2) -> logErrors(var3, var30, var2)).getOrThrow(ChunkReadException::new)).orElseGet(() -> new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES));
                  PalettedContainerRO var33 = (PalettedContainerRO)var29.getCompound("biomes").map((var3x) -> (PalettedContainerRO)var26.parse(NbtOps.INSTANCE, var3x).promotePartial((var2) -> logErrors(var3, var30, var2)).getOrThrow(ChunkReadException::new)).orElseGet(() -> new PalettedContainer(var25.asHolderIdMap(), var25.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES));
                  var31 = new LevelChunkSection(var32, var33);
               } else {
                  var31 = null;
               }

               DataLayer var38 = (DataLayer)var29.getByteArray("BlockLight").map(DataLayer::new).orElse((Object)null);
               DataLayer var39 = (DataLayer)var29.getByteArray("SkyLight").map(DataLayer::new).orElse((Object)null);
               var24.add(new SectionData(var30, var31, var38, var39));
            }
         }

         return new SerializableChunkData(var25, var3, var0.getMinSectionY(), var4, var6, var8, var11, var12, var9, var13, var14, var17, var19, var10, var24, var34, var35, var36);
      }
   }

   public ProtoChunk read(ServerLevel var1, PoiManager var2, RegionStorageInfo var3, ChunkPos var4) {
      if (!Objects.equals(var4, this.chunkPos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", new Object[]{var4, var4, this.chunkPos});
         var1.getServer().reportMisplacedChunk(this.chunkPos, var4, var3);
      }

      int var5 = var1.getSectionsCount();
      LevelChunkSection[] var6 = new LevelChunkSection[var5];
      boolean var7 = var1.dimensionType().hasSkyLight();
      ServerChunkCache var8 = var1.getChunkSource();
      LevelLightEngine var9 = ((ChunkSource)var8).getLightEngine();
      Registry var10 = var1.registryAccess().lookupOrThrow(Registries.BIOME);
      boolean var11 = false;

      for(SectionData var13 : this.sectionData) {
         SectionPos var14 = SectionPos.of(var4, var13.y);
         if (var13.chunkSection != null) {
            var6[var1.getSectionIndexFromSectionY(var13.y)] = var13.chunkSection;
            var2.checkConsistencyWithBlocks(var14, var13.chunkSection);
         }

         boolean var15 = var13.blockLight != null;
         boolean var16 = var7 && var13.skyLight != null;
         if (var15 || var16) {
            if (!var11) {
               var9.retainData(var4, true);
               var11 = true;
            }

            if (var15) {
               var9.queueSectionData(LightLayer.BLOCK, var14, var13.blockLight);
            }

            if (var16) {
               var9.queueSectionData(LightLayer.SKY, var14, var13.skyLight);
            }
         }
      }

      ChunkType var18 = this.chunkStatus.getChunkType();
      Object var19;
      if (var18 == ChunkType.LEVELCHUNK) {
         LevelChunkTicks var20 = new LevelChunkTicks(this.packedTicks.blocks());
         LevelChunkTicks var23 = new LevelChunkTicks(this.packedTicks.fluids());
         var19 = new LevelChunk(var1.getLevel(), var4, this.upgradeData, var20, var23, this.inhabitedTime, var6, postLoadChunk(var1, this.entities, this.blockEntities), BlendingData.unpack(this.blendingData));
      } else {
         ProtoChunkTicks var21 = ProtoChunkTicks.load(this.packedTicks.blocks());
         ProtoChunkTicks var24 = ProtoChunkTicks.load(this.packedTicks.fluids());
         ProtoChunk var28 = new ProtoChunk(var4, this.upgradeData, var6, var21, var24, var1, var10, BlendingData.unpack(this.blendingData));
         var19 = var28;
         ((ChunkAccess)var28).setInhabitedTime(this.inhabitedTime);
         if (this.belowZeroRetrogen != null) {
            var28.setBelowZeroRetrogen(this.belowZeroRetrogen);
         }

         var28.setPersistedStatus(this.chunkStatus);
         if (this.chunkStatus.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
            var28.setLightEngine(var9);
         }
      }

      ((ChunkAccess)var19).setLightCorrect(this.lightCorrect);
      EnumSet var22 = EnumSet.noneOf(Heightmap.Types.class);

      for(Heightmap.Types var29 : ((ChunkAccess)var19).getPersistedStatus().heightmapsAfter()) {
         long[] var17 = (long[])this.heightmaps.get(var29);
         if (var17 != null) {
            ((ChunkAccess)var19).setHeightmap(var29, var17);
         } else {
            var22.add(var29);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var19, var22);
      ((ChunkAccess)var19).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var1), this.structureData, var1.getSeed()));
      ((ChunkAccess)var19).setAllReferences(unpackStructureReferences(var1.registryAccess(), var4, this.structureData));

      for(int var26 = 0; var26 < this.postProcessingSections.length; ++var26) {
         ((ChunkAccess)var19).addPackedPostProcess(this.postProcessingSections[var26], var26);
      }

      if (var18 == ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var19, false);
      } else {
         ProtoChunk var27 = (ProtoChunk)var19;

         for(CompoundTag var32 : this.entities) {
            var27.addEntity(var32);
         }

         for(CompoundTag var33 : this.blockEntities) {
            var27.setBlockEntityNbt(var33);
         }

         if (this.carvingMask != null) {
            var27.setCarvingMask(new CarvingMask(this.carvingMask, ((ChunkAccess)var19).getMinY()));
         }

         return var27;
      }
   }

   private static void logErrors(ChunkPos var0, int var1, String var2) {
      LOGGER.error("Recoverable errors when loading section [{}, {}, {}]: {}", new Object[]{var0.x, var1, var0.z, var2});
   }

   private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> var0) {
      return PalettedContainer.codecRO(var0.asHolderIdMap(), var0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, var0.getOrThrow(Biomes.PLAINS));
   }

   public static SerializableChunkData copyOf(ServerLevel var0, ChunkAccess var1) {
      if (!var1.canBeSerialized()) {
         throw new IllegalArgumentException("Chunk can't be serialized: " + String.valueOf(var1));
      } else {
         ChunkPos var2 = var1.getPos();
         ArrayList var3 = new ArrayList();
         LevelChunkSection[] var4 = var1.getSections();
         ThreadedLevelLightEngine var5 = var0.getChunkSource().getLightEngine();

         for(int var6 = ((LevelLightEngine)var5).getMinLightSection(); var6 < ((LevelLightEngine)var5).getMaxLightSection(); ++var6) {
            int var7 = var1.getSectionIndexFromSectionY(var6);
            boolean var8 = var7 >= 0 && var7 < var4.length;
            DataLayer var9 = ((LevelLightEngine)var5).getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var6));
            DataLayer var10 = ((LevelLightEngine)var5).getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var6));
            DataLayer var11 = var9 != null && !var9.isEmpty() ? var9.copy() : null;
            DataLayer var12 = var10 != null && !var10.isEmpty() ? var10.copy() : null;
            if (var8 || var11 != null || var12 != null) {
               LevelChunkSection var13 = var8 ? var4[var7].copy() : null;
               var3.add(new SectionData(var6, var13, var11, var12));
            }
         }

         ArrayList var14 = new ArrayList(var1.getBlockEntitiesPos().size());

         for(BlockPos var17 : var1.getBlockEntitiesPos()) {
            CompoundTag var19 = var1.getBlockEntityNbtForSaving(var17, var0.registryAccess());
            if (var19 != null) {
               var14.add(var19);
            }
         }

         ArrayList var16 = new ArrayList();
         long[] var18 = null;
         if (var1.getPersistedStatus().getChunkType() == ChunkType.PROTOCHUNK) {
            ProtoChunk var20 = (ProtoChunk)var1;
            var16.addAll(var20.getEntities());
            CarvingMask var22 = var20.getCarvingMask();
            if (var22 != null) {
               var18 = var22.toArray();
            }
         }

         EnumMap var21 = new EnumMap(Heightmap.Types.class);

         for(Map.Entry var25 : var1.getHeightmaps()) {
            if (var1.getPersistedStatus().heightmapsAfter().contains(var25.getKey())) {
               long[] var27 = ((Heightmap)var25.getValue()).getRawData();
               var21.put((Heightmap.Types)var25.getKey(), (long[])(([J)var27).clone());
            }
         }

         ChunkAccess.PackedTicks var24 = var1.getTicksForSerialization(var0.getGameTime());
         ShortList[] var26 = (ShortList[])Arrays.stream(var1.getPostProcessing()).map((var0x) -> var0x != null ? new ShortArrayList(var0x) : null).toArray((var0x) -> new ShortList[var0x]);
         CompoundTag var28 = packStructureData(StructurePieceSerializationContext.fromLevel(var0), var2, var1.getAllStarts(), var1.getAllReferences());
         return new SerializableChunkData(var0.registryAccess().lookupOrThrow(Registries.BIOME), var2, var1.getMinSectionY(), var0.getGameTime(), var1.getInhabitedTime(), var1.getPersistedStatus(), (BlendingData.Packed)Optionull.map(var1.getBlendingData(), BlendingData::pack), var1.getBelowZeroRetrogen(), var1.getUpgradeData().copy(), var18, var21, var24, var26, var1.isLightCorrect(), var3, var16, var14, var28);
      }
   }

   public CompoundTag write() {
      CompoundTag var1 = NbtUtils.addCurrentDataVersion(new CompoundTag());
      var1.putInt("xPos", this.chunkPos.x);
      var1.putInt("yPos", this.minSectionY);
      var1.putInt("zPos", this.chunkPos.z);
      var1.putLong("LastUpdate", this.lastUpdateTime);
      var1.putLong("InhabitedTime", this.inhabitedTime);
      var1.putString("Status", BuiltInRegistries.CHUNK_STATUS.getKey(this.chunkStatus).toString());
      var1.storeNullable("blending_data", BlendingData.Packed.CODEC, this.blendingData);
      var1.storeNullable("below_zero_retrogen", BelowZeroRetrogen.CODEC, this.belowZeroRetrogen);
      if (!this.upgradeData.isEmpty()) {
         var1.put("UpgradeData", this.upgradeData.write());
      }

      ListTag var2 = new ListTag();
      Codec var3 = makeBiomeCodec(this.biomeRegistry);

      for(SectionData var5 : this.sectionData) {
         CompoundTag var6 = new CompoundTag();
         LevelChunkSection var7 = var5.chunkSection;
         if (var7 != null) {
            var6.store("block_states", BLOCK_STATE_CODEC, var7.getStates());
            var6.store("biomes", var3, var7.getBiomes());
         }

         if (var5.blockLight != null) {
            var6.putByteArray("BlockLight", var5.blockLight.getData());
         }

         if (var5.skyLight != null) {
            var6.putByteArray("SkyLight", var5.skyLight.getData());
         }

         if (!var6.isEmpty()) {
            var6.putByte("Y", (byte)var5.y);
            var2.add(var6);
         }
      }

      var1.put("sections", var2);
      if (this.lightCorrect) {
         var1.putBoolean("isLightOn", true);
      }

      ListTag var8 = new ListTag();
      var8.addAll(this.blockEntities);
      var1.put("block_entities", var8);
      if (this.chunkStatus.getChunkType() == ChunkType.PROTOCHUNK) {
         ListTag var9 = new ListTag();
         var9.addAll(this.entities);
         var1.put("entities", var9);
         if (this.carvingMask != null) {
            var1.putLongArray("carving_mask", this.carvingMask);
         }
      }

      saveTicks(var1, this.packedTicks);
      var1.put("PostProcessing", packOffsets(this.postProcessingSections));
      CompoundTag var10 = new CompoundTag();
      this.heightmaps.forEach((var1x, var2x) -> var10.put(var1x.getSerializationKey(), new LongArrayTag(var2x)));
      var1.put("Heightmaps", var10);
      var1.put("structures", this.structureData);
      return var1;
   }

   private static void saveTicks(CompoundTag var0, ChunkAccess.PackedTicks var1) {
      var0.store("block_ticks", BLOCK_TICKS_CODEC, var1.blocks());
      var0.store("fluid_ticks", FLUID_TICKS_CODEC, var1.fluids());
   }

   public static ChunkStatus getChunkStatusFromTag(@Nullable CompoundTag var0) {
      return var0 != null ? (ChunkStatus)var0.read("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY) : ChunkStatus.EMPTY;
   }

   @Nullable
   private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel var0, List<CompoundTag> var1, List<CompoundTag> var2) {
      return var1.isEmpty() && var2.isEmpty() ? null : (var3) -> {
         if (!var1.isEmpty()) {
            var0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive(var1, var0, EntitySpawnReason.LOAD));
         }

         for(CompoundTag var5 : var2) {
            boolean var6 = var5.getBooleanOr("keepPacked", false);
            if (var6) {
               var3.setBlockEntityNbt(var5);
            } else {
               BlockPos var7 = BlockEntity.getPosFromTag(var5);
               BlockEntity var8 = BlockEntity.loadStatic(var7, var3.getBlockState(var7), var5, var0.registryAccess());
               if (var8 != null) {
                  var3.setBlockEntity(var8);
               }
            }
         }

      };
   }

   private static CompoundTag packStructureData(StructurePieceSerializationContext var0, ChunkPos var1, Map<Structure, StructureStart> var2, Map<Structure, LongSet> var3) {
      CompoundTag var4 = new CompoundTag();
      CompoundTag var5 = new CompoundTag();
      Registry var6 = var0.registryAccess().lookupOrThrow(Registries.STRUCTURE);

      for(Map.Entry var8 : var2.entrySet()) {
         ResourceLocation var9 = var6.getKey((Structure)var8.getKey());
         var5.put(var9.toString(), ((StructureStart)var8.getValue()).createTag(var0, var1));
      }

      var4.put("starts", var5);
      CompoundTag var11 = new CompoundTag();

      for(Map.Entry var13 : var3.entrySet()) {
         if (!((LongSet)var13.getValue()).isEmpty()) {
            ResourceLocation var10 = var6.getKey((Structure)var13.getKey());
            var11.putLongArray(var10.toString(), ((LongSet)var13.getValue()).toLongArray());
         }
      }

      var4.put("References", var11);
      return var4;
   }

   private static Map<Structure, StructureStart> unpackStructureStart(StructurePieceSerializationContext var0, CompoundTag var1, long var2) {
      HashMap var4 = Maps.newHashMap();
      Registry var5 = var0.registryAccess().lookupOrThrow(Registries.STRUCTURE);
      CompoundTag var6 = var1.getCompoundOrEmpty("starts");

      for(String var8 : var6.keySet()) {
         ResourceLocation var9 = ResourceLocation.tryParse(var8);
         Structure var10 = (Structure)var5.getValue(var9);
         if (var10 == null) {
            LOGGER.error("Unknown structure start: {}", var9);
         } else {
            StructureStart var11 = StructureStart.loadStaticStart(var0, var6.getCompoundOrEmpty(var8), var2);
            if (var11 != null) {
               var4.put(var10, var11);
            }
         }
      }

      return var4;
   }

   private static Map<Structure, LongSet> unpackStructureReferences(RegistryAccess var0, ChunkPos var1, CompoundTag var2) {
      HashMap var3 = Maps.newHashMap();
      Registry var4 = var0.lookupOrThrow(Registries.STRUCTURE);
      CompoundTag var5 = var2.getCompoundOrEmpty("References");
      var5.forEach((var3x, var4x) -> {
         ResourceLocation var5 = ResourceLocation.tryParse(var3x);
         Structure var6 = (Structure)var4.getValue(var5);
         if (var6 == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", var5, var1);
         } else {
            Optional var7 = var4x.asLongArray();
            if (!var7.isEmpty()) {
               var3.put(var6, new LongOpenHashSet(Arrays.stream((long[])var7.get()).filter((var2) -> {
                  ChunkPos var4 = new ChunkPos(var2);
                  if (var4.getChessboardDistance(var1) > 8) {
                     LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", new Object[]{var5, var4, var1});
                     return false;
                  } else {
                     return true;
                  }
               }).toArray()));
            }
         }
      });
      return var3;
   }

   private static ListTag packOffsets(ShortList[] var0) {
      ListTag var1 = new ListTag();

      for(ShortList var5 : var0) {
         ListTag var6 = new ListTag();
         if (var5 != null) {
            for(int var7 = 0; var7 < var5.size(); ++var7) {
               var6.add(ShortTag.valueOf(var5.getShort(var7)));
            }
         }

         var1.add(var6);
      }

      return var1;
   }

   static {
      BLOCK_STATE_CODEC = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
      BLOCK_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.BLOCK.byNameCodec()).listOf();
      FLUID_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.FLUID.byNameCodec()).listOf();
      LOGGER = LogUtils.getLogger();
   }

   public static record SectionData(int y, @Nullable LevelChunkSection chunkSection, @Nullable DataLayer blockLight, @Nullable DataLayer skyLight) {
      final int y;
      @Nullable
      final LevelChunkSection chunkSection;
      @Nullable
      final DataLayer blockLight;
      @Nullable
      final DataLayer skyLight;

      public SectionData(int var1, @Nullable LevelChunkSection var2, @Nullable DataLayer var3, @Nullable DataLayer var4) {
         super();
         this.y = var1;
         this.chunkSection = var2;
         this.blockLight = var3;
         this.skyLight = var4;
      }
   }

   public static class ChunkReadException extends NbtException {
      public ChunkReadException(String var1) {
         super(var1);
      }
   }
}
