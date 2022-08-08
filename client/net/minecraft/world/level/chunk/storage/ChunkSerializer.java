package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import org.slf4j.Logger;

public class ChunkSerializer {
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

   public ChunkSerializer() {
      super();
   }

   public static ProtoChunk read(ServerLevel var0, PoiManager var1, ChunkPos var2, CompoundTag var3) {
      ChunkPos var4 = new ChunkPos(var3.getInt("xPos"), var3.getInt("zPos"));
      if (!Objects.equals(var2, var4)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", new Object[]{var2, var2, var4});
      }

      UpgradeData var5 = var3.contains("UpgradeData", 10) ? new UpgradeData(var3.getCompound("UpgradeData"), var0) : UpgradeData.EMPTY;
      boolean var6 = var3.getBoolean("isLightOn");
      ListTag var7 = var3.getList("sections", 10);
      int var8 = var0.getSectionsCount();
      LevelChunkSection[] var9 = new LevelChunkSection[var8];
      boolean var10 = var0.dimensionType().hasSkyLight();
      ServerChunkCache var11 = var0.getChunkSource();
      LevelLightEngine var12 = var11.getLightEngine();
      Registry var13 = var0.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
      Codec var14 = makeBiomeCodec(var13);
      boolean var15 = false;

      DataResult var10000;
      for(int var16 = 0; var16 < var7.size(); ++var16) {
         CompoundTag var17 = var7.getCompound(var16);
         byte var18 = var17.getByte("Y");
         int var19 = var0.getSectionIndexFromSectionY(var18);
         if (var19 >= 0 && var19 < var9.length) {
            Logger var10002;
            PalettedContainer var20;
            if (var17.contains("block_states", 10)) {
               var10000 = BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var17.getCompound("block_states")).promotePartial((var2x) -> {
                  logErrors(var2, var18, var2x);
               });
               var10002 = LOGGER;
               Objects.requireNonNull(var10002);
               var20 = (PalettedContainer)var10000.getOrThrow(false, var10002::error);
            } else {
               var20 = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            }

            Object var21;
            if (var17.contains("biomes", 10)) {
               var10000 = var14.parse(NbtOps.INSTANCE, var17.getCompound("biomes")).promotePartial((var2x) -> {
                  logErrors(var2, var18, var2x);
               });
               var10002 = LOGGER;
               Objects.requireNonNull(var10002);
               var21 = (PalettedContainerRO)var10000.getOrThrow(false, var10002::error);
            } else {
               var21 = new PalettedContainer(var13.asHolderIdMap(), var13.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
            }

            LevelChunkSection var22 = new LevelChunkSection(var18, var20, (PalettedContainerRO)var21);
            var9[var19] = var22;
            var1.checkConsistencyWithBlocks(var2, var22);
         }

         boolean var37 = var17.contains("BlockLight", 7);
         boolean var39 = var10 && var17.contains("SkyLight", 7);
         if (var37 || var39) {
            if (!var15) {
               var12.retainData(var2, true);
               var15 = true;
            }

            if (var37) {
               var12.queueSectionData(LightLayer.BLOCK, SectionPos.of(var2, var18), new DataLayer(var17.getByteArray("BlockLight")), true);
            }

            if (var39) {
               var12.queueSectionData(LightLayer.SKY, SectionPos.of(var2, var18), new DataLayer(var17.getByteArray("SkyLight")), true);
            }
         }
      }

      long var33 = var3.getLong("InhabitedTime");
      ChunkStatus.ChunkType var35 = getChunkTypeFromTag(var3);
      Logger var10001;
      BlendingData var36;
      if (var3.contains("blending_data", 10)) {
         var10000 = BlendingData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("blending_data")));
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var36 = (BlendingData)var10000.resultOrPartial(var10001::error).orElse((Object)null);
      } else {
         var36 = null;
      }

      Object var38;
      if (var35 == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunkTicks var40 = LevelChunkTicks.load(var3.getList("block_ticks", 10), (var0x) -> {
            return Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         LevelChunkTicks var41 = LevelChunkTicks.load(var3.getList("fluid_ticks", 10), (var0x) -> {
            return Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         var38 = new LevelChunk(var0.getLevel(), var2, var5, var40, var41, var33, var9, postLoadChunk(var0, var3), var36);
      } else {
         ProtoChunkTicks var42 = ProtoChunkTicks.load(var3.getList("block_ticks", 10), (var0x) -> {
            return Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         ProtoChunkTicks var43 = ProtoChunkTicks.load(var3.getList("fluid_ticks", 10), (var0x) -> {
            return Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         ProtoChunk var23 = new ProtoChunk(var2, var5, var9, var42, var43, var0, var13, var36);
         var38 = var23;
         var23.setInhabitedTime(var33);
         if (var3.contains("below_zero_retrogen", 10)) {
            var10000 = BelowZeroRetrogen.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("below_zero_retrogen")));
            var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            Optional var34 = var10000.resultOrPartial(var10001::error);
            Objects.requireNonNull(var23);
            var34.ifPresent(var23::setBelowZeroRetrogen);
         }

         ChunkStatus var24 = ChunkStatus.byName(var3.getString("Status"));
         var23.setStatus(var24);
         if (var24.isOrAfter(ChunkStatus.FEATURES)) {
            var23.setLightEngine(var12);
         }

         BelowZeroRetrogen var25 = var23.getBelowZeroRetrogen();
         boolean var26 = var24.isOrAfter(ChunkStatus.LIGHT) || var25 != null && var25.targetStatus().isOrAfter(ChunkStatus.LIGHT);
         if (!var6 && var26) {
            Iterator var27 = BlockPos.betweenClosed(var2.getMinBlockX(), var0.getMinBuildHeight(), var2.getMinBlockZ(), var2.getMaxBlockX(), var0.getMaxBuildHeight() - 1, var2.getMaxBlockZ()).iterator();

            while(var27.hasNext()) {
               BlockPos var28 = (BlockPos)var27.next();
               if (((ChunkAccess)var38).getBlockState(var28).getLightEmission() != 0) {
                  var23.addLight(var28);
               }
            }
         }
      }

      ((ChunkAccess)var38).setLightCorrect(var6);
      CompoundTag var44 = var3.getCompound("Heightmaps");
      EnumSet var45 = EnumSet.noneOf(Heightmap.Types.class);
      Iterator var46 = ((ChunkAccess)var38).getStatus().heightmapsAfter().iterator();

      while(var46.hasNext()) {
         Heightmap.Types var48 = (Heightmap.Types)var46.next();
         String var50 = var48.getSerializationKey();
         if (var44.contains(var50, 12)) {
            ((ChunkAccess)var38).setHeightmap(var48, var44.getLongArray(var50));
         } else {
            var45.add(var48);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var38, var45);
      CompoundTag var47 = var3.getCompound("structures");
      ((ChunkAccess)var38).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var0), var47, var0.getSeed()));
      ((ChunkAccess)var38).setAllReferences(unpackStructureReferences(var0.registryAccess(), var2, var47));
      if (var3.getBoolean("shouldSave")) {
         ((ChunkAccess)var38).setUnsaved(true);
      }

      ListTag var49 = var3.getList("PostProcessing", 9);

      ListTag var52;
      int var54;
      for(int var51 = 0; var51 < var49.size(); ++var51) {
         var52 = var49.getList(var51);

         for(var54 = 0; var54 < var52.size(); ++var54) {
            ((ChunkAccess)var38).addPackedPostProcess(var52.getShort(var54), var51);
         }
      }

      if (var35 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var38, false);
      } else {
         ProtoChunk var53 = (ProtoChunk)var38;
         var52 = var3.getList("entities", 10);

         for(var54 = 0; var54 < var52.size(); ++var54) {
            var53.addEntity(var52.getCompound(var54));
         }

         ListTag var56 = var3.getList("block_entities", 10);

         CompoundTag var29;
         for(int var55 = 0; var55 < var56.size(); ++var55) {
            var29 = var56.getCompound(var55);
            ((ChunkAccess)var38).setBlockEntityNbt(var29);
         }

         ListTag var57 = var3.getList("Lights", 9);

         for(int var58 = 0; var58 < var57.size(); ++var58) {
            ListTag var30 = var57.getList(var58);

            for(int var31 = 0; var31 < var30.size(); ++var31) {
               var53.addLight(var30.getShort(var31), var58);
            }
         }

         var29 = var3.getCompound("CarvingMasks");
         Iterator var59 = var29.getAllKeys().iterator();

         while(var59.hasNext()) {
            String var60 = (String)var59.next();
            GenerationStep.Carving var32 = GenerationStep.Carving.valueOf(var60);
            var53.setCarvingMask(var32, new CarvingMask(var29.getLongArray(var60), ((ChunkAccess)var38).getMinBuildHeight()));
         }

         return var53;
      }
   }

   private static void logErrors(ChunkPos var0, int var1, String var2) {
      LOGGER.error("Recoverable errors when loading section [" + var0.x + ", " + var1 + ", " + var0.z + "]: " + var2);
   }

   private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> var0) {
      return PalettedContainer.codecRO(var0.asHolderIdMap(), var0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, var0.getHolderOrThrow(Biomes.PLAINS));
   }

   public static CompoundTag write(ServerLevel var0, ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      CompoundTag var3 = new CompoundTag();
      var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.putInt("xPos", var2.x);
      var3.putInt("yPos", var1.getMinSection());
      var3.putInt("zPos", var2.z);
      var3.putLong("LastUpdate", var0.getGameTime());
      var3.putLong("InhabitedTime", var1.getInhabitedTime());
      var3.putString("Status", var1.getStatus().getName());
      BlendingData var4 = var1.getBlendingData();
      DataResult var10000;
      Logger var10001;
      if (var4 != null) {
         var10000 = BlendingData.CODEC.encodeStart(NbtOps.INSTANCE, var4);
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            var3.put("blending_data", var1x);
         });
      }

      BelowZeroRetrogen var5 = var1.getBelowZeroRetrogen();
      if (var5 != null) {
         var10000 = BelowZeroRetrogen.CODEC.encodeStart(NbtOps.INSTANCE, var5);
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            var3.put("below_zero_retrogen", var1x);
         });
      }

      UpgradeData var6 = var1.getUpgradeData();
      if (!var6.isEmpty()) {
         var3.put("UpgradeData", var6.write());
      }

      LevelChunkSection[] var7 = var1.getSections();
      ListTag var8 = new ListTag();
      ThreadedLevelLightEngine var9 = var0.getChunkSource().getLightEngine();
      Registry var10 = var0.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
      Codec var11 = makeBiomeCodec(var10);
      boolean var12 = var1.isLightCorrect();

      for(int var13 = var9.getMinLightSection(); var13 < var9.getMaxLightSection(); ++var13) {
         int var14 = var1.getSectionIndexFromSectionY(var13);
         boolean var15 = var14 >= 0 && var14 < var7.length;
         DataLayer var16 = var9.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var13));
         DataLayer var17 = var9.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var13));
         if (var15 || var16 != null || var17 != null) {
            CompoundTag var18 = new CompoundTag();
            if (var15) {
               LevelChunkSection var19 = var7[var14];
               DataResult var10002 = BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, var19.getStates());
               Logger var10004 = LOGGER;
               Objects.requireNonNull(var10004);
               var18.put("block_states", (Tag)var10002.getOrThrow(false, var10004::error));
               var10002 = var11.encodeStart(NbtOps.INSTANCE, var19.getBiomes());
               var10004 = LOGGER;
               Objects.requireNonNull(var10004);
               var18.put("biomes", (Tag)var10002.getOrThrow(false, var10004::error));
            }

            if (var16 != null && !var16.isEmpty()) {
               var18.putByteArray("BlockLight", var16.getData());
            }

            if (var17 != null && !var17.isEmpty()) {
               var18.putByteArray("SkyLight", var17.getData());
            }

            if (!var18.isEmpty()) {
               var18.putByte("Y", (byte)var13);
               var8.add(var18);
            }
         }
      }

      var3.put("sections", var8);
      if (var12) {
         var3.putBoolean("isLightOn", true);
      }

      ListTag var22 = new ListTag();
      Iterator var23 = var1.getBlockEntitiesPos().iterator();

      CompoundTag var29;
      while(var23.hasNext()) {
         BlockPos var26 = (BlockPos)var23.next();
         var29 = var1.getBlockEntityNbtForSaving(var26);
         if (var29 != null) {
            var22.add(var29);
         }
      }

      var3.put("block_entities", var22);
      if (var1.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
         ProtoChunk var24 = (ProtoChunk)var1;
         ListTag var27 = new ListTag();
         var27.addAll(var24.getEntities());
         var3.put("entities", var27);
         var3.put("Lights", packOffsets(var24.getPackedLights()));
         var29 = new CompoundTag();
         GenerationStep.Carving[] var31 = GenerationStep.Carving.values();
         int var32 = var31.length;

         for(int var33 = 0; var33 < var32; ++var33) {
            GenerationStep.Carving var20 = var31[var33];
            CarvingMask var21 = var24.getCarvingMask(var20);
            if (var21 != null) {
               var29.putLongArray(var20.toString(), var21.toArray());
            }
         }

         var3.put("CarvingMasks", var29);
      }

      saveTicks(var0, var3, var1.getTicksForSerialization());
      var3.put("PostProcessing", packOffsets(var1.getPostProcessing()));
      CompoundTag var25 = new CompoundTag();
      Iterator var28 = var1.getHeightmaps().iterator();

      while(var28.hasNext()) {
         Map.Entry var30 = (Map.Entry)var28.next();
         if (var1.getStatus().heightmapsAfter().contains(var30.getKey())) {
            var25.put(((Heightmap.Types)var30.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var30.getValue()).getRawData()));
         }
      }

      var3.put("Heightmaps", var25);
      var3.put("structures", packStructureData(StructurePieceSerializationContext.fromLevel(var0), var2, var1.getAllStarts(), var1.getAllReferences()));
      return var3;
   }

   private static void saveTicks(ServerLevel var0, CompoundTag var1, ChunkAccess.TicksToSave var2) {
      long var3 = var0.getLevelData().getGameTime();
      var1.put("block_ticks", var2.blocks().save(var3, (var0x) -> {
         return Registry.BLOCK.getKey(var0x).toString();
      }));
      var1.put("fluid_ticks", var2.fluids().save(var3, (var0x) -> {
         return Registry.FLUID.getKey(var0x).toString();
      }));
   }

   public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag var0) {
      return var0 != null ? ChunkStatus.byName(var0.getString("Status")).getChunkType() : ChunkStatus.ChunkType.PROTOCHUNK;
   }

   @Nullable
   private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel var0, CompoundTag var1) {
      ListTag var2 = getListOfCompoundsOrNull(var1, "entities");
      ListTag var3 = getListOfCompoundsOrNull(var1, "block_entities");
      return var2 == null && var3 == null ? null : (var3x) -> {
         if (var2 != null) {
            var0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive(var2, var0));
         }

         if (var3 != null) {
            for(int var4 = 0; var4 < var3.size(); ++var4) {
               CompoundTag var5 = var3.getCompound(var4);
               boolean var6 = var5.getBoolean("keepPacked");
               if (var6) {
                  var3x.setBlockEntityNbt(var5);
               } else {
                  BlockPos var7 = BlockEntity.getPosFromTag(var5);
                  BlockEntity var8 = BlockEntity.loadStatic(var7, var3x.getBlockState(var7), var5);
                  if (var8 != null) {
                     var3x.setBlockEntity(var8);
                  }
               }
            }
         }

      };
   }

   @Nullable
   private static ListTag getListOfCompoundsOrNull(CompoundTag var0, String var1) {
      ListTag var2 = var0.getList(var1, 10);
      return var2.isEmpty() ? null : var2;
   }

   private static CompoundTag packStructureData(StructurePieceSerializationContext var0, ChunkPos var1, Map<Structure, StructureStart> var2, Map<Structure, LongSet> var3) {
      CompoundTag var4 = new CompoundTag();
      CompoundTag var5 = new CompoundTag();
      Registry var6 = var0.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
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
      Registry var5 = var0.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
      CompoundTag var6 = var1.getCompound("starts");
      Iterator var7 = var6.getAllKeys().iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         ResourceLocation var9 = ResourceLocation.tryParse(var8);
         Structure var10 = (Structure)var5.get(var9);
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
      Registry var4 = var0.registryOrThrow(Registry.STRUCTURE_REGISTRY);
      CompoundTag var5 = var2.getCompound("References");
      Iterator var6 = var5.getAllKeys().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         ResourceLocation var8 = ResourceLocation.tryParse(var7);
         Structure var9 = (Structure)var4.get(var8);
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

   public static ListTag packOffsets(ShortList[] var0) {
      ListTag var1 = new ListTag();
      ShortList[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ShortList var5 = var2[var4];
         ListTag var6 = new ListTag();
         if (var5 != null) {
            ShortListIterator var7 = var5.iterator();

            while(var7.hasNext()) {
               Short var8 = (Short)var7.next();
               var6.add(ShortTag.valueOf(var8));
            }
         }

         var1.add(var6);
      }

      return var1;
   }

   static {
      BLOCK_STATE_CODEC = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
      LOGGER = LogUtils.getLogger();
   }
}
