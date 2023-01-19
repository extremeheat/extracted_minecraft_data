package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
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
   private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW(
      Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState()
   );
   private static final Logger LOGGER = LogUtils.getLogger();
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

      for(int var16 = 0; var16 < var7.size(); ++var16) {
         CompoundTag var17 = var7.getCompound(var16);
         byte var18 = var17.getByte("Y");
         int var19 = var0.getSectionIndexFromSectionY(var18);
         if (var19 >= 0 && var19 < var9.length) {
            PalettedContainer var20;
            if (var17.contains("block_states", 10)) {
               var20 = (PalettedContainer)BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var17.getCompound("block_states"))
                  .promotePartial(var2x -> logErrors(var2, var18, var2x))
                  .getOrThrow(false, LOGGER::error);
            } else {
               var20 = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            }

            Object var21;
            if (var17.contains("biomes", 10)) {
               var21 = (PalettedContainerRO)var14.parse(NbtOps.INSTANCE, var17.getCompound("biomes"))
                  .promotePartial(var2x -> logErrors(var2, var18, var2x))
                  .getOrThrow(false, LOGGER::error);
            } else {
               var21 = new PalettedContainer<>(var13.asHolderIdMap(), var13.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
            }

            LevelChunkSection var22 = new LevelChunkSection(var18, var20, (PalettedContainerRO<Holder<Biome>>)var21);
            var9[var19] = var22;
            var1.checkConsistencyWithBlocks(var2, var22);
         }

         boolean var36 = var17.contains("BlockLight", 7);
         boolean var38 = var10 && var17.contains("SkyLight", 7);
         if (var36 || var38) {
            if (!var15) {
               var12.retainData(var2, true);
               var15 = true;
            }

            if (var36) {
               var12.queueSectionData(LightLayer.BLOCK, SectionPos.of(var2, var18), new DataLayer(var17.getByteArray("BlockLight")), true);
            }

            if (var38) {
               var12.queueSectionData(LightLayer.SKY, SectionPos.of(var2, var18), new DataLayer(var17.getByteArray("SkyLight")), true);
            }
         }
      }

      long var33 = var3.getLong("InhabitedTime");
      ChunkStatus.ChunkType var34 = getChunkTypeFromTag(var3);
      BlendingData var35;
      if (var3.contains("blending_data", 10)) {
         var35 = (BlendingData)BlendingData.CODEC
            .parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("blending_data")))
            .resultOrPartial(LOGGER::error)
            .orElse(null);
      } else {
         var35 = null;
      }

      Object var37;
      if (var34 == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunkTicks var39 = LevelChunkTicks.load(
            var3.getList("block_ticks", 10), var0x -> Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x)), var2
         );
         LevelChunkTicks var42 = LevelChunkTicks.load(
            var3.getList("fluid_ticks", 10), var0x -> Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x)), var2
         );
         var37 = new LevelChunk(var0.getLevel(), var2, var5, var39, var42, var33, var9, postLoadChunk(var0, var3), var35);
      } else {
         ProtoChunkTicks var40 = ProtoChunkTicks.load(
            var3.getList("block_ticks", 10), var0x -> Registry.BLOCK.getOptional(ResourceLocation.tryParse(var0x)), var2
         );
         ProtoChunkTicks var43 = ProtoChunkTicks.load(
            var3.getList("fluid_ticks", 10), var0x -> Registry.FLUID.getOptional(ResourceLocation.tryParse(var0x)), var2
         );
         ProtoChunk var23 = new ProtoChunk(var2, var5, var9, var40, var43, var0, var13, var35);
         var37 = var23;
         var23.setInhabitedTime(var33);
         if (var3.contains("below_zero_retrogen", 10)) {
            BelowZeroRetrogen.CODEC
               .parse(new Dynamic(NbtOps.INSTANCE, var3.getCompound("below_zero_retrogen")))
               .resultOrPartial(LOGGER::error)
               .ifPresent(var23::setBelowZeroRetrogen);
         }

         ChunkStatus var24 = ChunkStatus.byName(var3.getString("Status"));
         var23.setStatus(var24);
         if (var24.isOrAfter(ChunkStatus.FEATURES)) {
            var23.setLightEngine(var12);
         }

         BelowZeroRetrogen var25 = var23.getBelowZeroRetrogen();
         boolean var26 = var24.isOrAfter(ChunkStatus.LIGHT) || var25 != null && var25.targetStatus().isOrAfter(ChunkStatus.LIGHT);
         if (!var6 && var26) {
            for(BlockPos var28 : BlockPos.betweenClosed(
               var2.getMinBlockX(), var0.getMinBuildHeight(), var2.getMinBlockZ(), var2.getMaxBlockX(), var0.getMaxBuildHeight() - 1, var2.getMaxBlockZ()
            )) {
               if (((ChunkAccess)var37).getBlockState(var28).getLightEmission() != 0) {
                  var23.addLight(var28);
               }
            }
         }
      }

      ((ChunkAccess)var37).setLightCorrect(var6);
      CompoundTag var41 = var3.getCompound("Heightmaps");
      EnumSet var44 = EnumSet.noneOf(Heightmap.Types.class);

      for(Heightmap.Types var47 : ((ChunkAccess)var37).getStatus().heightmapsAfter()) {
         String var49 = var47.getSerializationKey();
         if (var41.contains(var49, 12)) {
            ((ChunkAccess)var37).setHeightmap(var47, var41.getLongArray(var49));
         } else {
            var44.add(var47);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var37, var44);
      CompoundTag var46 = var3.getCompound("structures");
      ((ChunkAccess)var37).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var0), var46, var0.getSeed()));
      ((ChunkAccess)var37).setAllReferences(unpackStructureReferences(var0.registryAccess(), var2, var46));
      if (var3.getBoolean("shouldSave")) {
         ((ChunkAccess)var37).setUnsaved(true);
      }

      ListTag var48 = var3.getList("PostProcessing", 9);

      for(int var50 = 0; var50 < var48.size(); ++var50) {
         ListTag var52 = var48.getList(var50);

         for(int var54 = 0; var54 < var52.size(); ++var54) {
            ((ChunkAccess)var37).addPackedPostProcess(var52.getShort(var54), var50);
         }
      }

      if (var34 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var37, false);
      } else {
         ProtoChunk var51 = (ProtoChunk)var37;
         ListTag var53 = var3.getList("entities", 10);

         for(int var55 = 0; var55 < var53.size(); ++var55) {
            var51.addEntity(var53.getCompound(var55));
         }

         ListTag var56 = var3.getList("block_entities", 10);

         for(int var57 = 0; var57 < var56.size(); ++var57) {
            CompoundTag var29 = var56.getCompound(var57);
            ((ChunkAccess)var37).setBlockEntityNbt(var29);
         }

         ListTag var58 = var3.getList("Lights", 9);

         for(int var59 = 0; var59 < var58.size(); ++var59) {
            ListTag var30 = var58.getList(var59);

            for(int var31 = 0; var31 < var30.size(); ++var31) {
               var51.addLight(var30.getShort(var31), var59);
            }
         }

         CompoundTag var60 = var3.getCompound("CarvingMasks");

         for(String var62 : var60.getAllKeys()) {
            GenerationStep.Carving var32 = GenerationStep.Carving.valueOf(var62);
            var51.setCarvingMask(var32, new CarvingMask(var60.getLongArray(var62), ((ChunkAccess)var37).getMinBuildHeight()));
         }

         return var51;
      }
   }

   private static void logErrors(ChunkPos var0, int var1, String var2) {
      LOGGER.error("Recoverable errors when loading section [" + var0.x + ", " + var1 + ", " + var0.z + "]: " + var2);
   }

   private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> var0) {
      return PalettedContainer.codecRO(
         var0.asHolderIdMap(), var0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, var0.getHolderOrThrow(Biomes.PLAINS)
      );
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
      if (var4 != null) {
         BlendingData.CODEC.encodeStart(NbtOps.INSTANCE, var4).resultOrPartial(LOGGER::error).ifPresent(var1x -> var3.put("blending_data", var1x));
      }

      BelowZeroRetrogen var5 = var1.getBelowZeroRetrogen();
      if (var5 != null) {
         BelowZeroRetrogen.CODEC.encodeStart(NbtOps.INSTANCE, var5).resultOrPartial(LOGGER::error).ifPresent(var1x -> var3.put("below_zero_retrogen", var1x));
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
               var18.put("block_states", (Tag)BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, var19.getStates()).getOrThrow(false, LOGGER::error));
               var18.put("biomes", (Tag)var11.encodeStart(NbtOps.INSTANCE, var19.getBiomes()).getOrThrow(false, LOGGER::error));
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

      for(BlockPos var26 : var1.getBlockEntitiesPos()) {
         CompoundTag var29 = var1.getBlockEntityNbtForSaving(var26);
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
         CompoundTag var30 = new CompoundTag();

         for(GenerationStep.Carving var20 : GenerationStep.Carving.values()) {
            CarvingMask var21 = var24.getCarvingMask(var20);
            if (var21 != null) {
               var30.putLongArray(var20.toString(), var21.toArray());
            }
         }

         var3.put("CarvingMasks", var30);
      }

      saveTicks(var0, var3, var1.getTicksForSerialization());
      var3.put("PostProcessing", packOffsets(var1.getPostProcessing()));
      CompoundTag var25 = new CompoundTag();

      for(Entry var31 : var1.getHeightmaps()) {
         if (var1.getStatus().heightmapsAfter().contains(var31.getKey())) {
            var25.put(((Heightmap.Types)var31.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var31.getValue()).getRawData()));
         }
      }

      var3.put("Heightmaps", var25);
      var3.put("structures", packStructureData(StructurePieceSerializationContext.fromLevel(var0), var2, var1.getAllStarts(), var1.getAllReferences()));
      return var3;
   }

   private static void saveTicks(ServerLevel var0, CompoundTag var1, ChunkAccess.TicksToSave var2) {
      long var3 = var0.getLevelData().getGameTime();
      var1.put("block_ticks", var2.blocks().save(var3, var0x -> Registry.BLOCK.getKey(var0x).toString()));
      var1.put("fluid_ticks", var2.fluids().save(var3, var0x -> Registry.FLUID.getKey(var0x).toString()));
   }

   public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag var0) {
      return var0 != null ? ChunkStatus.byName(var0.getString("Status")).getChunkType() : ChunkStatus.ChunkType.PROTOCHUNK;
   }

   @Nullable
   private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel var0, CompoundTag var1) {
      ListTag var2 = getListOfCompoundsOrNull(var1, "entities");
      ListTag var3 = getListOfCompoundsOrNull(var1, "block_entities");
      return var2 == null && var3 == null ? null : var3x -> {
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

   private static CompoundTag packStructureData(
      StructurePieceSerializationContext var0, ChunkPos var1, Map<Structure, StructureStart> var2, Map<Structure, LongSet> var3
   ) {
      CompoundTag var4 = new CompoundTag();
      CompoundTag var5 = new CompoundTag();
      Registry var6 = var0.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);

      for(Entry var8 : var2.entrySet()) {
         ResourceLocation var9 = var6.getKey((Structure)var8.getKey());
         var5.put(var9.toString(), ((StructureStart)var8.getValue()).createTag(var0, var1));
      }

      var4.put("starts", var5);
      CompoundTag var11 = new CompoundTag();

      for(Entry var13 : var3.entrySet()) {
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

      for(String var8 : var6.getAllKeys()) {
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

      for(String var7 : var5.getAllKeys()) {
         ResourceLocation var8 = ResourceLocation.tryParse(var7);
         Structure var9 = (Structure)var4.get(var8);
         if (var9 == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", var8, var1);
         } else {
            long[] var10 = var5.getLongArray(var7);
            if (var10.length != 0) {
               var3.put(var9, new LongOpenHashSet(Arrays.stream(var10).filter(var2x -> {
                  ChunkPos var4x = new ChunkPos(var2x);
                  if (var4x.getChessboardDistance(var1) > 8) {
                     LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", new Object[]{var8, var4x, var1});
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

      for(ShortList var5 : var0) {
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
}