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

   public static ProtoChunk read(ServerLevel var0, PoiManager var1, RegionStorageInfo var2, ChunkPos var3, CompoundTag var4) {
      ChunkPos var5 = new ChunkPos(var4.getInt("xPos"), var4.getInt("zPos"));
      if (!Objects.equals(var3, var5)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", new Object[]{var3, var3, var5});
         var0.getServer().reportMisplacedChunk(var5, var3, var2);
      }

      UpgradeData var6 = var4.contains("UpgradeData", 10) ? new UpgradeData(var4.getCompound("UpgradeData"), var0) : UpgradeData.EMPTY;
      boolean var7 = var4.getBoolean("isLightOn");
      ListTag var8 = var4.getList("sections", 10);
      int var9 = var0.getSectionsCount();
      LevelChunkSection[] var10 = new LevelChunkSection[var9];
      boolean var11 = var0.dimensionType().hasSkyLight();
      ServerChunkCache var12 = var0.getChunkSource();
      LevelLightEngine var13 = var12.getLightEngine();
      Registry var14 = var0.registryAccess().registryOrThrow(Registries.BIOME);
      Codec var15 = makeBiomeCodec(var14);
      boolean var16 = false;

      for (int var17 = 0; var17 < var8.size(); var17++) {
         CompoundTag var18 = var8.getCompound(var17);
         byte var19 = var18.getByte("Y");
         int var20 = var0.getSectionIndexFromSectionY(var19);
         if (var20 >= 0 && var20 < var10.length) {
            PalettedContainer var21;
            if (var18.contains("block_states", 10)) {
               var21 = (PalettedContainer)BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, var18.getCompound("block_states"))
                  .promotePartial(var2x -> logErrors(var3, var19, var2x))
                  .getOrThrow(ChunkSerializer.ChunkReadException::new);
            } else {
               var21 = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            }

            Object var22;
            if (var18.contains("biomes", 10)) {
               var22 = (PalettedContainerRO)var15.parse(NbtOps.INSTANCE, var18.getCompound("biomes"))
                  .promotePartial(var2x -> logErrors(var3, var19, var2x))
                  .getOrThrow(ChunkSerializer.ChunkReadException::new);
            } else {
               var22 = new PalettedContainer<>(var14.asHolderIdMap(), var14.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
            }

            LevelChunkSection var23 = new LevelChunkSection(var21, (PalettedContainerRO<Holder<Biome>>)var22);
            var10[var20] = var23;
            SectionPos var24 = SectionPos.of(var3, var19);
            var1.checkConsistencyWithBlocks(var24, var23);
         }

         boolean var36 = var18.contains("BlockLight", 7);
         boolean var38 = var11 && var18.contains("SkyLight", 7);
         if (var36 || var38) {
            if (!var16) {
               var13.retainData(var3, true);
               var16 = true;
            }

            if (var36) {
               var13.queueSectionData(LightLayer.BLOCK, SectionPos.of(var3, var19), new DataLayer(var18.getByteArray("BlockLight")));
            }

            if (var38) {
               var13.queueSectionData(LightLayer.SKY, SectionPos.of(var3, var19), new DataLayer(var18.getByteArray("SkyLight")));
            }
         }
      }

      long var33 = var4.getLong("InhabitedTime");
      ChunkType var34 = getChunkTypeFromTag(var4);
      BlendingData var35;
      if (var4.contains("blending_data", 10)) {
         var35 = (BlendingData)BlendingData.CODEC
            .parse(new Dynamic(NbtOps.INSTANCE, var4.getCompound("blending_data")))
            .resultOrPartial(LOGGER::error)
            .orElse(null);
      } else {
         var35 = null;
      }

      Object var37;
      if (var34 == ChunkType.LEVELCHUNK) {
         LevelChunkTicks var39 = LevelChunkTicks.load(
            var4.getList("block_ticks", 10), var0x -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(var0x)), var3
         );
         LevelChunkTicks var42 = LevelChunkTicks.load(
            var4.getList("fluid_ticks", 10), var0x -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(var0x)), var3
         );
         var37 = new LevelChunk(var0.getLevel(), var3, var6, var39, var42, var33, var10, postLoadChunk(var0, var4), var35);
      } else {
         ProtoChunkTicks var40 = ProtoChunkTicks.load(
            var4.getList("block_ticks", 10), var0x -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(var0x)), var3
         );
         ProtoChunkTicks var43 = ProtoChunkTicks.load(
            var4.getList("fluid_ticks", 10), var0x -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(var0x)), var3
         );
         ProtoChunk var45 = new ProtoChunk(var3, var6, var10, var40, var43, var0, var14, var35);
         var37 = var45;
         var45.setInhabitedTime(var33);
         if (var4.contains("below_zero_retrogen", 10)) {
            BelowZeroRetrogen.CODEC
               .parse(new Dynamic(NbtOps.INSTANCE, var4.getCompound("below_zero_retrogen")))
               .resultOrPartial(LOGGER::error)
               .ifPresent(var45::setBelowZeroRetrogen);
         }

         ChunkStatus var25 = ChunkStatus.byName(var4.getString("Status"));
         var45.setPersistedStatus(var25);
         if (var25.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
            var45.setLightEngine(var13);
         }
      }

      ((ChunkAccess)var37).setLightCorrect(var7);
      CompoundTag var41 = var4.getCompound("Heightmaps");
      EnumSet var44 = EnumSet.noneOf(Heightmap.Types.class);

      for (Heightmap.Types var48 : ((ChunkAccess)var37).getPersistedStatus().heightmapsAfter()) {
         String var26 = var48.getSerializationKey();
         if (var41.contains(var26, 12)) {
            ((ChunkAccess)var37).setHeightmap(var48, var41.getLongArray(var26));
         } else {
            var44.add(var48);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var37, var44);
      CompoundTag var47 = var4.getCompound("structures");
      ((ChunkAccess)var37).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(var0), var47, var0.getSeed()));
      ((ChunkAccess)var37).setAllReferences(unpackStructureReferences(var0.registryAccess(), var3, var47));
      if (var4.getBoolean("shouldSave")) {
         ((ChunkAccess)var37).setUnsaved(true);
      }

      ListTag var49 = var4.getList("PostProcessing", 9);

      for (int var50 = 0; var50 < var49.size(); var50++) {
         ListTag var27 = var49.getList(var50);

         for (int var28 = 0; var28 < var27.size(); var28++) {
            ((ChunkAccess)var37).addPackedPostProcess(var27.getShort(var28), var50);
         }
      }

      if (var34 == ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var37, false);
      } else {
         ProtoChunk var51 = (ProtoChunk)var37;
         ListTag var52 = var4.getList("entities", 10);

         for (int var53 = 0; var53 < var52.size(); var53++) {
            var51.addEntity(var52.getCompound(var53));
         }

         ListTag var54 = var4.getList("block_entities", 10);

         for (int var29 = 0; var29 < var54.size(); var29++) {
            CompoundTag var30 = var54.getCompound(var29);
            ((ChunkAccess)var37).setBlockEntityNbt(var30);
         }

         CompoundTag var55 = var4.getCompound("CarvingMasks");

         for (String var31 : var55.getAllKeys()) {
            GenerationStep.Carving var32 = GenerationStep.Carving.valueOf(var31);
            var51.setCarvingMask(var32, new CarvingMask(var55.getLongArray(var31), ((ChunkAccess)var37).getMinBuildHeight()));
         }

         return var51;
      }
   }

   private static void logErrors(ChunkPos var0, int var1, String var2) {
      LOGGER.error("Recoverable errors when loading section [{}, {}, {}]: {}", new Object[]{var0.x, var1, var0.z, var2});
   }

   private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> var0) {
      return PalettedContainer.codecRO(
         var0.asHolderIdMap(), var0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, var0.getHolderOrThrow(Biomes.PLAINS)
      );
   }

   public static CompoundTag write(ServerLevel var0, ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      CompoundTag var3 = NbtUtils.addCurrentDataVersion(new CompoundTag());
      var3.putInt("xPos", var2.x);
      var3.putInt("yPos", var1.getMinSection());
      var3.putInt("zPos", var2.z);
      var3.putLong("LastUpdate", var0.getGameTime());
      var3.putLong("InhabitedTime", var1.getInhabitedTime());
      var3.putString("Status", BuiltInRegistries.CHUNK_STATUS.getKey(var1.getPersistedStatus()).toString());
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
      Registry var10 = var0.registryAccess().registryOrThrow(Registries.BIOME);
      Codec var11 = makeBiomeCodec(var10);
      boolean var12 = var1.isLightCorrect();

      for (int var13 = var9.getMinLightSection(); var13 < var9.getMaxLightSection(); var13++) {
         int var14 = var1.getSectionIndexFromSectionY(var13);
         boolean var15 = var14 >= 0 && var14 < var7.length;
         DataLayer var16 = var9.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var13));
         DataLayer var17 = var9.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var13));
         if (var15 || var16 != null || var17 != null) {
            CompoundTag var18 = new CompoundTag();
            if (var15) {
               LevelChunkSection var19 = var7[var14];
               var18.put("block_states", (Tag)BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, var19.getStates()).getOrThrow());
               var18.put("biomes", (Tag)var11.encodeStart(NbtOps.INSTANCE, var19.getBiomes()).getOrThrow());
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

      for (BlockPos var26 : var1.getBlockEntitiesPos()) {
         CompoundTag var29 = var1.getBlockEntityNbtForSaving(var26, var0.registryAccess());
         if (var29 != null) {
            var22.add(var29);
         }
      }

      var3.put("block_entities", var22);
      if (var1.getPersistedStatus().getChunkType() == ChunkType.PROTOCHUNK) {
         ProtoChunk var24 = (ProtoChunk)var1;
         ListTag var27 = new ListTag();
         var27.addAll(var24.getEntities());
         var3.put("entities", var27);
         CompoundTag var30 = new CompoundTag();

         for (GenerationStep.Carving var20 : GenerationStep.Carving.values()) {
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

      for (Entry var31 : var1.getHeightmaps()) {
         if (var1.getPersistedStatus().heightmapsAfter().contains(var31.getKey())) {
            var25.put(((Heightmap.Types)var31.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var31.getValue()).getRawData()));
         }
      }

      var3.put("Heightmaps", var25);
      var3.put("structures", packStructureData(StructurePieceSerializationContext.fromLevel(var0), var2, var1.getAllStarts(), var1.getAllReferences()));
      return var3;
   }

   private static void saveTicks(ServerLevel var0, CompoundTag var1, ChunkAccess.TicksToSave var2) {
      long var3 = var0.getLevelData().getGameTime();
      var1.put("block_ticks", var2.blocks().save(var3, var0x -> BuiltInRegistries.BLOCK.getKey(var0x).toString()));
      var1.put("fluid_ticks", var2.fluids().save(var3, var0x -> BuiltInRegistries.FLUID.getKey(var0x).toString()));
   }

   public static ChunkType getChunkTypeFromTag(@Nullable CompoundTag var0) {
      return var0 != null ? ChunkStatus.byName(var0.getString("Status")).getChunkType() : ChunkType.PROTOCHUNK;
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
            for (int var4 = 0; var4 < var3.size(); var4++) {
               CompoundTag var5 = var3.getCompound(var4);
               boolean var6 = var5.getBoolean("keepPacked");
               if (var6) {
                  var3x.setBlockEntityNbt(var5);
               } else {
                  BlockPos var7 = BlockEntity.getPosFromTag(var5);
                  BlockEntity var8 = BlockEntity.loadStatic(var7, var3x.getBlockState(var7), var5, var0.registryAccess());
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
      Registry var6 = var0.registryAccess().registryOrThrow(Registries.STRUCTURE);

      for (Entry var8 : var2.entrySet()) {
         ResourceLocation var9 = var6.getKey((Structure)var8.getKey());
         var5.put(var9.toString(), ((StructureStart)var8.getValue()).createTag(var0, var1));
      }

      var4.put("starts", var5);
      CompoundTag var11 = new CompoundTag();

      for (Entry var13 : var3.entrySet()) {
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
      Registry var5 = var0.registryAccess().registryOrThrow(Registries.STRUCTURE);
      CompoundTag var6 = var1.getCompound("starts");

      for (String var8 : var6.getAllKeys()) {
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
      Registry var4 = var0.registryOrThrow(Registries.STRUCTURE);
      CompoundTag var5 = var2.getCompound("References");

      for (String var7 : var5.getAllKeys()) {
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

      for (ShortList var5 : var0) {
         ListTag var6 = new ListTag();
         if (var5 != null) {
            ShortListIterator var7 = var5.iterator();

            while (var7.hasNext()) {
               Short var8 = (Short)var7.next();
               var6.add(ShortTag.valueOf(var8));
            }
         }

         var1.add(var6);
      }

      return var1;
   }

   public static class ChunkReadException extends NbtException {
      public ChunkReadException(String var1) {
         super(var1);
      }
   }
}
