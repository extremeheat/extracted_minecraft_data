package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public record SerializableChunkData(Registry<Biome> biomeRegistry, ChunkPos chunkPos, int minSectionY, long lastUpdateTime, long inhabitedTime, ChunkStatus chunkStatus, @Nullable BlendingData.Packed blendingData, @Nullable BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, @Nullable long[] carvingMask, Map<Heightmap.Types, long[]> heightmaps, ChunkAccess.PackedTicks packedTicks, ShortList[] postProcessingSections, boolean lightCorrect, List<SectionData> sectionData, List<CompoundTag> entities, List<CompoundTag> blockEntities, CompoundTag structureData) {
   private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC;
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
      if (!var2.contains("Status", 8)) {
         return null;
      } else {
         ChunkPos var3 = new ChunkPos(var2.getInt("xPos"), var2.getInt("zPos"));
         long var4 = var2.getLong("LastUpdate");
         long var6 = var2.getLong("InhabitedTime");
         ChunkStatus var8 = ChunkStatus.byName(var2.getString("Status"));
         UpgradeData var9 = var2.contains("UpgradeData", 10) ? new UpgradeData(var2.getCompound("UpgradeData"), var0) : UpgradeData.EMPTY;
         boolean var10 = var2.getBoolean("isLightOn");
         BlendingData.Packed var11;
         DataResult var10000;
         Logger var10001;
         if (var2.contains("blending_data", 10)) {
            var10000 = BlendingData.Packed.CODEC.parse(NbtOps.INSTANCE, var2.getCompound("blending_data"));
            var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var11 = (BlendingData.Packed)var10000.resultOrPartial(var10001::error).orElse((Object)null);
         } else {
            var11 = null;
         }

         BelowZeroRetrogen var12;
         if (var2.contains("below_zero_retrogen", 10)) {
            var10000 = BelowZeroRetrogen.CODEC.parse(NbtOps.INSTANCE, var2.getCompound("below_zero_retrogen"));
            var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var12 = (BelowZeroRetrogen)var10000.resultOrPartial(var10001::error).orElse((Object)null);
         } else {
            var12 = null;
         }

         long[] var13;
         if (var2.contains("carving_mask", 12)) {
            var13 = var2.getLongArray("carving_mask");
         } else {
            var13 = null;
         }

         CompoundTag var14 = var2.getCompound("Heightmaps");
         EnumMap var15 = new EnumMap(Heightmap.Types.class);
         Iterator var16 = var8.heightmapsAfter().iterator();

         while(var16.hasNext()) {
            Heightmap.Types var17 = (Heightmap.Types)var16.next();
            String var18 = var17.getSerializationKey();
            if (var14.contains(var18, 12)) {
               var15.put(var17, var14.getLongArray(var18));
            }
         }

         List var34 = SavedTick.loadTickList(var2.getList("block_ticks", 10), (var0x) -> {
            return BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(var0x));
         }, var3);
         List var35 = SavedTick.loadTickList(var2.getList("fluid_ticks", 10), (var0x) -> {
            return BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(var0x));
         }, var3);
         ChunkAccess.PackedTicks var36 = new ChunkAccess.PackedTicks(var34, var35);
         ListTag var19 = var2.getList("PostProcessing", 9);
         ShortList[] var20 = new ShortList[var19.size()];

         for(int var21 = 0; var21 < var19.size(); ++var21) {
            ListTag var22 = var19.getList(var21);
            ShortArrayList var23 = new ShortArrayList(var22.size());

            for(int var24 = 0; var24 < var22.size(); ++var24) {
               var23.add(var22.getShort(var24));
            }

            var20[var21] = var23;
         }

         List var37 = Lists.transform(var2.getList("entities", 10), (var0x) -> {
            return (CompoundTag)var0x;
         });
         List var38 = Lists.transform(var2.getList("block_entities", 10), (var0x) -> {
            return (CompoundTag)var0x;
         });
         CompoundTag var39 = var2.getCompound("structures");
         ListTag var40 = var2.getList("sections", 10);
         ArrayList var25 = new ArrayList(var40.size());
         Registry var26 = var1.lookupOrThrow(Registries.BIOME);
         Codec var27 = makeBiomeCodec(var26);

         for(int var28 = 0; var28 < var40.size(); ++var28) {
            CompoundTag var29 = var40.getCompound(var28);
            byte var30 = var29.getByte("Y");
            LevelChunkSection var31;
            if (var30 >= var0.getMinSectionY() && var30 <= var0.getMaxSectionY()) {
               PalettedContainer var32;
               if (var29.contains("block_states", 10)) {
                  var32 = (PalettedContainer)BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var29.getCompound("block_states")).promotePartial((var2x) -> {
                     logErrors(var3, var30, var2x);
                  }).getOrThrow(ChunkReadException::new);
               } else {
                  var32 = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
               }

               Object var33;
               if (var29.contains("biomes", 10)) {
                  var33 = (PalettedContainerRO)var27.parse(NbtOps.INSTANCE, var29.getCompound("biomes")).promotePartial((var2x) -> {
                     logErrors(var3, var30, var2x);
                  }).getOrThrow(ChunkReadException::new);
               } else {
                  var33 = new PalettedContainer(var26.asHolderIdMap(), var26.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
               }

               var31 = new LevelChunkSection(var32, (PalettedContainerRO)var33);
            } else {
               var31 = null;
            }

            DataLayer var41 = var29.contains("BlockLight", 7) ? new DataLayer(var29.getByteArray("BlockLight")) : null;
            DataLayer var42 = var29.contains("SkyLight", 7) ? new DataLayer(var29.getByteArray("SkyLight")) : null;
            var25.add(new SectionData(var30, var31, var41, var42));
         }

         return new SerializableChunkData(var26, var3, var0.getMinSectionY(), var4, var6, var8, var11, var12, var9, var13, var15, var36, var20, var10, var25, var37, var38, var39);
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
      Iterator var12 = this.sectionData.iterator();

      while(true) {
         SectionData var13;
         SectionPos var14;
         boolean var15;
         boolean var16;
         do {
            if (!var12.hasNext()) {
               ChunkType var18 = this.chunkStatus.getChunkType();
               Object var19;
               if (var18 == ChunkType.LEVELCHUNK) {
                  LevelChunkTicks var20 = new LevelChunkTicks(this.packedTicks.blocks());
                  LevelChunkTicks var23 = new LevelChunkTicks(this.packedTicks.fluids());
                  var19 = new LevelChunk(var1.getLevel(), var4, this.upgradeData, var20, var23, this.inhabitedTime, var6, postLoadChunk(var1, this.entities, this.blockEntities), BlendingData.unpack(this.blendingData));
               } else {
                  ProtoChunkTicks var21 = ProtoChunkTicks.load(this.packedTicks.blocks());
                  ProtoChunkTicks var24 = ProtoChunkTicks.load(this.packedTicks.fluids());
                  ProtoChunk var26 = new ProtoChunk(var4, this.upgradeData, var6, var21, var24, var1, var10, BlendingData.unpack(this.blendingData));
                  var19 = var26;
                  ((ChunkAccess)var26).setInhabitedTime(this.inhabitedTime);
                  if (this.belowZeroRetrogen != null) {
                     var26.setBelowZeroRetrogen(this.belowZeroRetrogen);
                  }

                  var26.setPersistedStatus(this.chunkStatus);
                  if (this.chunkStatus.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
                     var26.setLightEngine(var9);
                  }
               }

               ((ChunkAccess)var19).setLightCorrect(this.lightCorrect);
               EnumSet var22 = EnumSet.noneOf(Heightmap.Types.class);
               Iterator var25 = ((ChunkAccess)var19).getPersistedStatus().heightmapsAfter().iterator();

               while(var25.hasNext()) {
                  Heightmap.Types var28 = (Heightmap.Types)var25.next();
                  long[] var17 = (long[])this.heightmaps.get(var28);
                  if (var17 != null) {
                     ((ChunkAccess)var19).setHeightmap(var28, var17);
                  } else {
                     var22.add(var28);
                  }
               }

               Heightmap.primeHeightmaps((ChunkAccess)var19, var22);
               ((ChunkAccess)var19).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var1), this.structureData, var1.getSeed()));
               ((ChunkAccess)var19).setAllReferences(unpackStructureReferences(var1.registryAccess(), var4, this.structureData));

               for(int var27 = 0; var27 < this.postProcessingSections.length; ++var27) {
                  ((ChunkAccess)var19).addPackedPostProcess(this.postProcessingSections[var27], var27);
               }

               if (var18 == ChunkType.LEVELCHUNK) {
                  return new ImposterProtoChunk((LevelChunk)var19, false);
               }

               ProtoChunk var29 = (ProtoChunk)var19;
               Iterator var30 = this.entities.iterator();

               CompoundTag var31;
               while(var30.hasNext()) {
                  var31 = (CompoundTag)var30.next();
                  var29.addEntity(var31);
               }

               var30 = this.blockEntities.iterator();

               while(var30.hasNext()) {
                  var31 = (CompoundTag)var30.next();
                  var29.setBlockEntityNbt(var31);
               }

               if (this.carvingMask != null) {
                  var29.setCarvingMask(new CarvingMask(this.carvingMask, ((ChunkAccess)var19).getMinY()));
               }

               return var29;
            }

            var13 = (SectionData)var12.next();
            var14 = SectionPos.of(var4, var13.y);
            if (var13.chunkSection != null) {
               var6[var1.getSectionIndexFromSectionY(var13.y)] = var13.chunkSection;
               var2.checkConsistencyWithBlocks(var14, var13.chunkSection);
            }

            var15 = var13.blockLight != null;
            var16 = var7 && var13.skyLight != null;
         } while(!var15 && !var16);

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
         Iterator var15 = var1.getBlockEntitiesPos().iterator();

         while(var15.hasNext()) {
            BlockPos var17 = (BlockPos)var15.next();
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
         Iterator var23 = var1.getHeightmaps().iterator();

         while(var23.hasNext()) {
            Map.Entry var25 = (Map.Entry)var23.next();
            if (var1.getPersistedStatus().heightmapsAfter().contains(var25.getKey())) {
               long[] var27 = ((Heightmap)var25.getValue()).getRawData();
               var21.put((Heightmap.Types)var25.getKey(), (long[])(([J)var27).clone());
            }
         }

         ChunkAccess.PackedTicks var24 = var1.getTicksForSerialization(var0.getGameTime());
         ShortList[] var26 = (ShortList[])Arrays.stream(var1.getPostProcessing()).map((var0x) -> {
            return var0x != null ? new ShortArrayList(var0x) : null;
         }).toArray((var0x) -> {
            return new ShortList[var0x];
         });
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
      DataResult var10000;
      Logger var10001;
      if (this.blendingData != null) {
         var10000 = BlendingData.Packed.CODEC.encodeStart(NbtOps.INSTANCE, this.blendingData);
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            var1.put("blending_data", var1x);
         });
      }

      if (this.belowZeroRetrogen != null) {
         var10000 = BelowZeroRetrogen.CODEC.encodeStart(NbtOps.INSTANCE, this.belowZeroRetrogen);
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            var1.put("below_zero_retrogen", var1x);
         });
      }

      if (!this.upgradeData.isEmpty()) {
         var1.put("UpgradeData", this.upgradeData.write());
      }

      ListTag var2 = new ListTag();
      Codec var3 = makeBiomeCodec(this.biomeRegistry);
      Iterator var4 = this.sectionData.iterator();

      while(var4.hasNext()) {
         SectionData var5 = (SectionData)var4.next();
         CompoundTag var6 = new CompoundTag();
         LevelChunkSection var7 = var5.chunkSection;
         if (var7 != null) {
            var6.put("block_states", (Tag)BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, var7.getStates()).getOrThrow());
            var6.put("biomes", (Tag)var3.encodeStart(NbtOps.INSTANCE, var7.getBiomes()).getOrThrow());
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
      this.heightmaps.forEach((var1x, var2x) -> {
         var10.put(var1x.getSerializationKey(), new LongArrayTag(var2x));
      });
      var1.put("Heightmaps", var10);
      var1.put("structures", this.structureData);
      return var1;
   }

   private static void saveTicks(CompoundTag var0, ChunkAccess.PackedTicks var1) {
      ListTag var2 = new ListTag();
      Iterator var3 = var1.blocks().iterator();

      while(var3.hasNext()) {
         SavedTick var4 = (SavedTick)var3.next();
         var2.add(var4.save((var0x) -> {
            return BuiltInRegistries.BLOCK.getKey(var0x).toString();
         }));
      }

      var0.put("block_ticks", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = var1.fluids().iterator();

      while(var7.hasNext()) {
         SavedTick var5 = (SavedTick)var7.next();
         var6.add(var5.save((var0x) -> {
            return BuiltInRegistries.FLUID.getKey(var0x).toString();
         }));
      }

      var0.put("fluid_ticks", var6);
   }

   public static ChunkType getChunkTypeFromTag(@Nullable CompoundTag var0) {
      return var0 != null ? ChunkStatus.byName(var0.getString("Status")).getChunkType() : ChunkType.PROTOCHUNK;
   }

   @Nullable
   private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel var0, List<CompoundTag> var1, List<CompoundTag> var2) {
      return var1.isEmpty() && var2.isEmpty() ? null : (var3) -> {
         if (!var1.isEmpty()) {
            var0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive(var1, var0, EntitySpawnReason.LOAD));
         }

         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            CompoundTag var5 = (CompoundTag)var4.next();
            boolean var6 = var5.getBoolean("keepPacked");
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
      Iterator var7 = var2.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry var8 = (Map.Entry)var7.next();
         ResourceLocation var9 = var6.getKey((Structure)var8.getKey());
         var5.put(var9.toString(), ((StructureStart)var8.getValue()).createTag(var0, var1));
      }

      var4.put("starts", var5);
      CompoundTag var11 = new CompoundTag();
      Iterator var12 = var3.entrySet().iterator();

      while(var12.hasNext()) {
         Map.Entry var13 = (Map.Entry)var12.next();
         if (!((LongSet)var13.getValue()).isEmpty()) {
            ResourceLocation var10 = var6.getKey((Structure)var13.getKey());
            var11.put(var10.toString(), new LongArrayTag((LongSet)var13.getValue()));
         }
      }

      var4.put("References", var11);
      return var4;
   }

   private static Map<Structure, StructureStart> unpackStructureStart(StructurePieceSerializationContext var0, CompoundTag var1, long var2) {
      HashMap var4 = Maps.newHashMap();
      Registry var5 = var0.registryAccess().lookupOrThrow(Registries.STRUCTURE);
      CompoundTag var6 = var1.getCompound("starts");
      Iterator var7 = var6.getAllKeys().iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         ResourceLocation var9 = ResourceLocation.tryParse(var8);
         Structure var10 = (Structure)var5.getValue(var9);
         if (var10 == null) {
            LOGGER.error("Unknown structure start: {}", var9);
         } else {
            StructureStart var11 = StructureStart.loadStaticStart(var0, var6.getCompound(var8), var2);
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
      CompoundTag var5 = var2.getCompound("References");
      Iterator var6 = var5.getAllKeys().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         ResourceLocation var8 = ResourceLocation.tryParse(var7);
         Structure var9 = (Structure)var4.getValue(var8);
         if (var9 == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", var8, var1);
         } else {
            long[] var10 = var5.getLongArray(var7);
            if (var10.length != 0) {
               var3.put(var9, new LongOpenHashSet(Arrays.stream(var10).filter((var2x) -> {
                  ChunkPos var4 = new ChunkPos(var2x);
                  if (var4.getChessboardDistance(var1) > 8) {
                     LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", new Object[]{var8, var4, var1});
                     return false;
                  } else {
                     return true;
                  }
               }).toArray()));
            }
         }
      }

      return var3;
   }

   private static ListTag packOffsets(ShortList[] var0) {
      ListTag var1 = new ListTag();
      ShortList[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ShortList var5 = var2[var4];
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

   public Registry<Biome> biomeRegistry() {
      return this.biomeRegistry;
   }

   public ChunkPos chunkPos() {
      return this.chunkPos;
   }

   public int minSectionY() {
      return this.minSectionY;
   }

   public long lastUpdateTime() {
      return this.lastUpdateTime;
   }

   public long inhabitedTime() {
      return this.inhabitedTime;
   }

   public ChunkStatus chunkStatus() {
      return this.chunkStatus;
   }

   @Nullable
   public BlendingData.Packed blendingData() {
      return this.blendingData;
   }

   @Nullable
   public BelowZeroRetrogen belowZeroRetrogen() {
      return this.belowZeroRetrogen;
   }

   public UpgradeData upgradeData() {
      return this.upgradeData;
   }

   @Nullable
   public long[] carvingMask() {
      return this.carvingMask;
   }

   public Map<Heightmap.Types, long[]> heightmaps() {
      return this.heightmaps;
   }

   public ChunkAccess.PackedTicks packedTicks() {
      return this.packedTicks;
   }

   public ShortList[] postProcessingSections() {
      return this.postProcessingSections;
   }

   public boolean lightCorrect() {
      return this.lightCorrect;
   }

   public List<SectionData> sectionData() {
      return this.sectionData;
   }

   public List<CompoundTag> entities() {
      return this.entities;
   }

   public List<CompoundTag> blockEntities() {
      return this.blockEntities;
   }

   public CompoundTag structureData() {
      return this.structureData;
   }

   static {
      BLOCK_STATE_CODEC = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
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

      public int y() {
         return this.y;
      }

      @Nullable
      public LevelChunkSection chunkSection() {
         return this.chunkSection;
      }

      @Nullable
      public DataLayer blockLight() {
         return this.blockLight;
      }

      @Nullable
      public DataLayer skyLight() {
         return this.skyLight;
      }
   }

   public static class ChunkReadException extends NbtException {
      public ChunkReadException(String var1) {
         super(var1);
      }
   }
}
