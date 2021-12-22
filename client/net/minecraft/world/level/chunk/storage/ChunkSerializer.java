package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC;
   private static final Logger LOGGER;
   private static final String TAG_UPGRADE_DATA = "UpgradeData";
   private static final String BLOCK_TICKS_TAG = "block_ticks";
   private static final String FLUID_TICKS_TAG = "fluid_ticks";

   public ChunkSerializer() {
      super();
   }

   public static ProtoChunk read(ServerLevel var0, PoiManager var1, ChunkPos var2, CompoundTag var3) {
      ChunkPos var4 = new ChunkPos(var3.getInt("xPos"), var3.getInt("zPos"));
      if (!Objects.equals(var2, var4)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", var2, var2, var4);
      }

      UpgradeData var5 = var3.contains("UpgradeData", 10) ? new UpgradeData(var3.getCompound("UpgradeData"), var0) : UpgradeData.EMPTY;
      boolean var6 = var3.getBoolean("isLightOn");
      ListTag var7 = var3.getList("sections", 10);
      int var8 = var0.getSectionsCount();
      LevelChunkSection[] var9 = new LevelChunkSection[var8];
      boolean var10 = var0.dimensionType().hasSkyLight();
      ServerChunkCache var11 = var0.getChunkSource();
      LevelLightEngine var12 = var11.getLightEngine();
      if (var6) {
         var12.retainData(var2, true);
      }

      Registry var13 = var0.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
      Codec var14 = makeBiomeCodec(var13);

      DataResult var10000;
      for(int var15 = 0; var15 < var7.size(); ++var15) {
         CompoundTag var16 = var7.getCompound(var15);
         byte var17 = var16.getByte("Y");
         int var18 = var0.getSectionIndexFromSectionY(var17);
         if (var18 >= 0 && var18 < var9.length) {
            Logger var10002;
            PalettedContainer var19;
            if (var16.contains("block_states", 10)) {
               var10000 = BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var16.getCompound("block_states")).promotePartial((var2x) -> {
                  logErrors(var2, var17, var2x);
               });
               var10002 = LOGGER;
               Objects.requireNonNull(var10002);
               var19 = (PalettedContainer)var10000.getOrThrow(false, var10002::error);
            } else {
               var19 = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            }

            PalettedContainer var20;
            if (var16.contains("biomes", 10)) {
               var10000 = var14.parse(NbtOps.INSTANCE, var16.getCompound("biomes")).promotePartial((var2x) -> {
                  logErrors(var2, var17, var2x);
               });
               var10002 = LOGGER;
               Objects.requireNonNull(var10002);
               var20 = (PalettedContainer)var10000.getOrThrow(false, var10002::error);
            } else {
               var20 = new PalettedContainer(var13, (Biome)var13.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
            }

            LevelChunkSection var21 = new LevelChunkSection(var17, var19, var20);
            var9[var18] = var21;
            var1.checkConsistencyWithBlocks(var2, var21);
         }

         if (var6) {
            if (var16.contains("BlockLight", 7)) {
               var12.queueSectionData(LightLayer.BLOCK, SectionPos.method_72(var2, var17), new DataLayer(var16.getByteArray("BlockLight")), true);
            }

            if (var10 && var16.contains("SkyLight", 7)) {
               var12.queueSectionData(LightLayer.SKY, SectionPos.method_72(var2, var17), new DataLayer(var16.getByteArray("SkyLight")), true);
            }
         }
      }

      long var32 = var3.getLong("InhabitedTime");
      ChunkStatus.ChunkType var33 = getChunkTypeFromTag(var3);
      Logger var10001;
      BlendingData var35;
      if (var3.contains("blending_data", 10)) {
         var10000 = BlendingData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("blending_data")));
         var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var35 = (BlendingData)var10000.resultOrPartial(var10001::error).orElse((Object)null);
      } else {
         var35 = null;
      }

      Object var36;
      if (var33 == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunkTicks var37 = LevelChunkTicks.load(var3.getList("block_ticks", 10), (var0x) -> {
            return Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         LevelChunkTicks var39 = LevelChunkTicks.load(var3.getList("fluid_ticks", 10), (var0x) -> {
            return Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         var36 = new LevelChunk(var0.getLevel(), var2, var5, var37, var39, var32, var9, postLoadChunk(var0, var3), var35);
      } else {
         ProtoChunkTicks var38 = ProtoChunkTicks.load(var3.getList("block_ticks", 10), (var0x) -> {
            return Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         ProtoChunkTicks var40 = ProtoChunkTicks.load(var3.getList("fluid_ticks", 10), (var0x) -> {
            return Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x));
         }, var2);
         ProtoChunk var22 = new ProtoChunk(var2, var5, var9, var38, var40, var0, var13, var35);
         var36 = var22;
         var22.setInhabitedTime(var32);
         if (var3.contains("below_zero_retrogen", 10)) {
            var10000 = BelowZeroRetrogen.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("below_zero_retrogen")));
            var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            Optional var34 = var10000.resultOrPartial(var10001::error);
            Objects.requireNonNull(var22);
            var34.ifPresent(var22::setBelowZeroRetrogen);
         }

         ChunkStatus var23 = ChunkStatus.byName(var3.getString("Status"));
         var22.setStatus(var23);
         if (var23.isOrAfter(ChunkStatus.FEATURES)) {
            var22.setLightEngine(var12);
         }

         BelowZeroRetrogen var24 = var22.getBelowZeroRetrogen();
         boolean var25 = var23.isOrAfter(ChunkStatus.LIGHT) || var24 != null && var24.targetStatus().isOrAfter(ChunkStatus.LIGHT);
         if (!var6 && var25) {
            Iterator var26 = BlockPos.betweenClosed(var2.getMinBlockX(), var0.getMinBuildHeight(), var2.getMinBlockZ(), var2.getMaxBlockX(), var0.getMaxBuildHeight() - 1, var2.getMaxBlockZ()).iterator();

            while(var26.hasNext()) {
               BlockPos var27 = (BlockPos)var26.next();
               if (((ChunkAccess)var36).getBlockState(var27).getLightEmission() != 0) {
                  var22.addLight(var27);
               }
            }
         }
      }

      ((ChunkAccess)var36).setLightCorrect(var6);
      CompoundTag var41 = var3.getCompound("Heightmaps");
      EnumSet var42 = EnumSet.noneOf(Heightmap.Types.class);
      Iterator var43 = ((ChunkAccess)var36).getStatus().heightmapsAfter().iterator();

      while(var43.hasNext()) {
         Heightmap.Types var45 = (Heightmap.Types)var43.next();
         String var47 = var45.getSerializationKey();
         if (var41.contains(var47, 12)) {
            ((ChunkAccess)var36).setHeightmap(var45, var41.getLongArray(var47));
         } else {
            var42.add(var45);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var36, var42);
      CompoundTag var44 = var3.getCompound("structures");
      ((ChunkAccess)var36).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var0), var44, var0.getSeed()));
      ((ChunkAccess)var36).setAllReferences(unpackStructureReferences(var2, var44));
      if (var3.getBoolean("shouldSave")) {
         ((ChunkAccess)var36).setUnsaved(true);
      }

      ListTag var46 = var3.getList("PostProcessing", 9);

      ListTag var49;
      int var51;
      for(int var48 = 0; var48 < var46.size(); ++var48) {
         var49 = var46.getList(var48);

         for(var51 = 0; var51 < var49.size(); ++var51) {
            ((ChunkAccess)var36).addPackedPostProcess(var49.getShort(var51), var48);
         }
      }

      if (var33 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var36, false);
      } else {
         ProtoChunk var50 = (ProtoChunk)var36;
         var49 = var3.getList("entities", 10);

         for(var51 = 0; var51 < var49.size(); ++var51) {
            var50.addEntity(var49.getCompound(var51));
         }

         ListTag var53 = var3.getList("block_entities", 10);

         CompoundTag var28;
         for(int var52 = 0; var52 < var53.size(); ++var52) {
            var28 = var53.getCompound(var52);
            ((ChunkAccess)var36).setBlockEntityNbt(var28);
         }

         ListTag var54 = var3.getList("Lights", 9);

         for(int var55 = 0; var55 < var54.size(); ++var55) {
            ListTag var29 = var54.getList(var55);

            for(int var30 = 0; var30 < var29.size(); ++var30) {
               var50.addLight(var29.getShort(var30), var55);
            }
         }

         var28 = var3.getCompound("CarvingMasks");
         Iterator var56 = var28.getAllKeys().iterator();

         while(var56.hasNext()) {
            String var57 = (String)var56.next();
            GenerationStep.Carving var31 = GenerationStep.Carving.valueOf(var57);
            var50.setCarvingMask(var31, new CarvingMask(var28.getLongArray(var57), ((ChunkAccess)var36).getMinBuildHeight()));
         }

         return var50;
      }
   }

   private static void logErrors(ChunkPos var0, int var1, String var2) {
      LOGGER.error("Recoverable errors when loading section [" + var0.field_504 + ", " + var1 + ", " + var0.field_505 + "]: " + var2);
   }

   private static Codec<PalettedContainer<Biome>> makeBiomeCodec(Registry<Biome> var0) {
      return PalettedContainer.codec(var0, var0.byNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, (Biome)var0.getOrThrow(Biomes.PLAINS));
   }

   public static CompoundTag write(ServerLevel var0, ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      CompoundTag var3 = new CompoundTag();
      var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.putInt("xPos", var2.field_504);
      var3.putInt("yPos", var1.getMinSection());
      var3.putInt("zPos", var2.field_505);
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
         DataLayer var16 = var9.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.method_72(var2, var13));
         DataLayer var17 = var9.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.method_72(var2, var13));
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
         Entry var30 = (Entry)var28.next();
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

   private static CompoundTag packStructureData(StructurePieceSerializationContext var0, ChunkPos var1, Map<StructureFeature<?>, StructureStart<?>> var2, Map<StructureFeature<?>, LongSet> var3) {
      CompoundTag var4 = new CompoundTag();
      CompoundTag var5 = new CompoundTag();
      Iterator var6 = var2.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         var5.put(((StructureFeature)var7.getKey()).getFeatureName(), ((StructureStart)var7.getValue()).createTag(var0, var1));
      }

      var4.put("starts", var5);
      CompoundTag var9 = new CompoundTag();
      Iterator var10 = var3.entrySet().iterator();

      while(var10.hasNext()) {
         Entry var8 = (Entry)var10.next();
         var9.put(((StructureFeature)var8.getKey()).getFeatureName(), new LongArrayTag((LongSet)var8.getValue()));
      }

      var4.put("References", var9);
      return var4;
   }

   private static Map<StructureFeature<?>, StructureStart<?>> unpackStructureStart(StructurePieceSerializationContext var0, CompoundTag var1, long var2) {
      HashMap var4 = Maps.newHashMap();
      CompoundTag var5 = var1.getCompound("starts");
      Iterator var6 = var5.getAllKeys().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         String var8 = var7.toLowerCase(Locale.ROOT);
         StructureFeature var9 = (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get(var8);
         if (var9 == null) {
            LOGGER.error("Unknown structure start: {}", var8);
         } else {
            StructureStart var10 = StructureFeature.loadStaticStart(var0, var5.getCompound(var7), var2);
            if (var10 != null) {
               var4.put(var9, var10);
            }
         }
      }

      return var4;
   }

   private static Map<StructureFeature<?>, LongSet> unpackStructureReferences(ChunkPos var0, CompoundTag var1) {
      HashMap var2 = Maps.newHashMap();
      CompoundTag var3 = var1.getCompound("References");
      Iterator var4 = var3.getAllKeys().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String var6 = var5.toLowerCase(Locale.ROOT);
         StructureFeature var7 = (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get(var6);
         if (var7 == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", var6, var0);
         } else {
            var2.put(var7, new LongOpenHashSet(Arrays.stream(var3.getLongArray(var5)).filter((var2x) -> {
               ChunkPos var4 = new ChunkPos(var2x);
               if (var4.getChessboardDistance(var0) > 8) {
                  LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", var6, var4, var0);
                  return false;
               } else {
                  return true;
               }
            }).toArray()));
         }
      }

      return var2;
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
      BLOCK_STATE_CODEC = PalettedContainer.codec(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
      LOGGER = LogManager.getLogger();
   }
}
