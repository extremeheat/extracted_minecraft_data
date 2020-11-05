package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ProtoChunk read(ServerLevel var0, StructureManager var1, PoiManager var2, ChunkPos var3, CompoundTag var4) {
      ChunkGenerator var5 = var0.getChunkSource().getGenerator();
      BiomeSource var6 = var5.getBiomeSource();
      CompoundTag var7 = var4.getCompound("Level");
      ChunkPos var8 = new ChunkPos(var7.getInt("xPos"), var7.getInt("zPos"));
      if (!Objects.equals(var3, var8)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", var3, var3, var8);
      }

      ChunkBiomeContainer var9 = new ChunkBiomeContainer(var0.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), var3, var6, var7.contains("Biomes", 11) ? var7.getIntArray("Biomes") : null);
      UpgradeData var10 = var7.contains("UpgradeData", 10) ? new UpgradeData(var7.getCompound("UpgradeData"), var0) : UpgradeData.EMPTY;
      ProtoTickList var11 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x.defaultBlockState().isAir();
      }, var3, var7.getList("ToBeTicked", 9), var0);
      ProtoTickList var12 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x == Fluids.EMPTY;
      }, var3, var7.getList("LiquidsToBeTicked", 9), var0);
      boolean var13 = var7.getBoolean("isLightOn");
      ListTag var14 = var7.getList("Sections", 10);
      int var15 = var0.getSectionsCount();
      LevelChunkSection[] var16 = new LevelChunkSection[var15];
      boolean var17 = var0.dimensionType().hasSkyLight();
      ServerChunkCache var18 = var0.getChunkSource();
      LevelLightEngine var19 = var18.getLightEngine();
      if (var13) {
         var19.retainData(var3, true);
      }

      for(int var20 = 0; var20 < var14.size(); ++var20) {
         CompoundTag var21 = var14.getCompound(var20);
         byte var22 = var21.getByte("Y");
         if (var21.contains("Palette", 9) && var21.contains("BlockStates", 12)) {
            LevelChunkSection var23 = new LevelChunkSection(var22);
            var23.getStates().read(var21.getList("Palette", 10), var21.getLongArray("BlockStates"));
            var23.recalcBlockCounts();
            if (!var23.isEmpty()) {
               var16[var0.getSectionIndexFromSectionY(var22)] = var23;
            }

            var2.checkConsistencyWithBlocks(var3, var23);
         }

         if (var13) {
            if (var21.contains("BlockLight", 7)) {
               var19.queueSectionData(LightLayer.BLOCK, SectionPos.of(var3, var22), new DataLayer(var21.getByteArray("BlockLight")), true);
            }

            if (var17 && var21.contains("SkyLight", 7)) {
               var19.queueSectionData(LightLayer.SKY, SectionPos.of(var3, var22), new DataLayer(var21.getByteArray("SkyLight")), true);
            }
         }
      }

      long var36 = var7.getLong("InhabitedTime");
      ChunkStatus.ChunkType var37 = getChunkTypeFromTag(var4);
      Object var38;
      if (var37 == ChunkStatus.ChunkType.LEVELCHUNK) {
         ListTag var10000;
         Function var10001;
         DefaultedRegistry var10002;
         Object var24;
         if (var7.contains("TileTicks", 9)) {
            var10000 = var7.getList("TileTicks", 10);
            var10001 = Registry.BLOCK::getKey;
            var10002 = Registry.BLOCK;
            var10002.getClass();
            var24 = ChunkTickList.create(var10000, var10001, var10002::get);
         } else {
            var24 = var11;
         }

         Object var25;
         if (var7.contains("LiquidTicks", 9)) {
            var10000 = var7.getList("LiquidTicks", 10);
            var10001 = Registry.FLUID::getKey;
            var10002 = Registry.FLUID;
            var10002.getClass();
            var25 = ChunkTickList.create(var10000, var10001, var10002::get);
         } else {
            var25 = var12;
         }

         var38 = new LevelChunk(var0.getLevel(), var3, var9, var10, (TickList)var24, (TickList)var25, var36, var16, (var2x) -> {
            postLoadChunk(var0, var7, var2x);
         });
      } else {
         ProtoChunk var39 = new ProtoChunk(var3, var10, var16, var11, var12, var0);
         var39.setBiomes(var9);
         var38 = var39;
         var39.setInhabitedTime(var36);
         var39.setStatus(ChunkStatus.byName(var7.getString("Status")));
         if (var39.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
            var39.setLightEngine(var19);
         }

         if (!var13 && var39.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            Iterator var41 = BlockPos.betweenClosed(var3.getMinBlockX(), var0.getMinBuildHeight(), var3.getMinBlockZ(), var3.getMaxBlockX(), var0.getMaxBuildHeight() - 1, var3.getMaxBlockZ()).iterator();

            while(var41.hasNext()) {
               BlockPos var26 = (BlockPos)var41.next();
               if (((ChunkAccess)var38).getBlockState(var26).getLightEmission() != 0) {
                  var39.addLight(var26);
               }
            }
         }
      }

      ((ChunkAccess)var38).setLightCorrect(var13);
      CompoundTag var40 = var7.getCompound("Heightmaps");
      EnumSet var42 = EnumSet.noneOf(Heightmap.Types.class);
      Iterator var43 = ((ChunkAccess)var38).getStatus().heightmapsAfter().iterator();

      while(var43.hasNext()) {
         Heightmap.Types var27 = (Heightmap.Types)var43.next();
         String var28 = var27.getSerializationKey();
         if (var40.contains(var28, 12)) {
            ((ChunkAccess)var38).setHeightmap(var27, var40.getLongArray(var28));
         } else {
            var42.add(var27);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var38, var42);
      CompoundTag var44 = var7.getCompound("Structures");
      ((ChunkAccess)var38).setAllStarts(unpackStructureStart(var1, var44, var0.getSeed()));
      ((ChunkAccess)var38).setAllReferences(unpackStructureReferences(var3, var44));
      if (var7.getBoolean("shouldSave")) {
         ((ChunkAccess)var38).setUnsaved(true);
      }

      ListTag var45 = var7.getList("PostProcessing", 9);

      ListTag var29;
      int var30;
      for(int var46 = 0; var46 < var45.size(); ++var46) {
         var29 = var45.getList(var46);

         for(var30 = 0; var30 < var29.size(); ++var30) {
            ((ChunkAccess)var38).addPackedPostProcess(var29.getShort(var30), var46);
         }
      }

      if (var37 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var38);
      } else {
         ProtoChunk var47 = (ProtoChunk)var38;
         var29 = var7.getList("Entities", 10);

         for(var30 = 0; var30 < var29.size(); ++var30) {
            var47.addEntity(var29.getCompound(var30));
         }

         ListTag var48 = var7.getList("TileEntities", 10);

         CompoundTag var32;
         for(int var31 = 0; var31 < var48.size(); ++var31) {
            var32 = var48.getCompound(var31);
            ((ChunkAccess)var38).setBlockEntityNbt(var32);
         }

         ListTag var49 = var7.getList("Lights", 9);

         for(int var50 = 0; var50 < var49.size(); ++var50) {
            ListTag var33 = var49.getList(var50);

            for(int var34 = 0; var34 < var33.size(); ++var34) {
               var47.addLight(var33.getShort(var34), var50);
            }
         }

         var32 = var7.getCompound("CarvingMasks");
         Iterator var51 = var32.getAllKeys().iterator();

         while(var51.hasNext()) {
            String var52 = (String)var51.next();
            GenerationStep.Carving var35 = GenerationStep.Carving.valueOf(var52);
            var47.setCarvingMask(var35, BitSet.valueOf(var32.getByteArray(var52)));
         }

         return var47;
      }
   }

   public static CompoundTag write(ServerLevel var0, ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      CompoundTag var3 = new CompoundTag();
      CompoundTag var4 = new CompoundTag();
      var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.put("Level", var4);
      var4.putInt("xPos", var2.x);
      var4.putInt("zPos", var2.z);
      var4.putLong("LastUpdate", var0.getGameTime());
      var4.putLong("InhabitedTime", var1.getInhabitedTime());
      var4.putString("Status", var1.getStatus().getName());
      UpgradeData var5 = var1.getUpgradeData();
      if (!var5.isEmpty()) {
         var4.put("UpgradeData", var5.write());
      }

      LevelChunkSection[] var6 = var1.getSections();
      ListTag var7 = new ListTag();
      ThreadedLevelLightEngine var8 = var0.getChunkSource().getLightEngine();
      boolean var9 = var1.isLightCorrect();

      for(int var10 = var8.getMinLightSection(); var10 < var8.getMaxLightSection(); ++var10) {
         LevelChunkSection var12 = (LevelChunkSection)Arrays.stream(var6).filter((var1x) -> {
            return var1x != null && SectionPos.blockToSectionCoord(var1x.bottomBlockY()) == var10;
         }).findFirst().orElse(LevelChunk.EMPTY_SECTION);
         DataLayer var13 = var8.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var10));
         DataLayer var14 = var8.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var10));
         if (var12 != LevelChunk.EMPTY_SECTION || var13 != null || var14 != null) {
            CompoundTag var15 = new CompoundTag();
            var15.putByte("Y", (byte)(var10 & 255));
            if (var12 != LevelChunk.EMPTY_SECTION) {
               var12.getStates().write(var15, "Palette", "BlockStates");
            }

            if (var13 != null && !var13.isEmpty()) {
               var15.putByteArray("BlockLight", var13.getData());
            }

            if (var14 != null && !var14.isEmpty()) {
               var15.putByteArray("SkyLight", var14.getData());
            }

            var7.add(var15);
         }
      }

      var4.put("Sections", var7);
      if (var9) {
         var4.putBoolean("isLightOn", true);
      }

      ChunkBiomeContainer var20 = var1.getBiomes();
      if (var20 != null) {
         var4.putIntArray("Biomes", var20.writeBiomes());
      }

      ListTag var11 = new ListTag();
      Iterator var21 = var1.getBlockEntitiesPos().iterator();

      CompoundTag var27;
      while(var21.hasNext()) {
         BlockPos var24 = (BlockPos)var21.next();
         var27 = var1.getBlockEntityNbtForSaving(var24);
         if (var27 != null) {
            var11.add(var27);
         }
      }

      var4.put("TileEntities", var11);
      if (var1.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
         ProtoChunk var22 = (ProtoChunk)var1;
         ListTag var25 = new ListTag();
         var25.addAll(var22.getEntities());
         var4.put("Entities", var25);
         var4.put("Lights", packOffsets(var22.getPackedLights()));
         var27 = new CompoundTag();
         GenerationStep.Carving[] var28 = GenerationStep.Carving.values();
         int var16 = var28.length;

         for(int var17 = 0; var17 < var16; ++var17) {
            GenerationStep.Carving var18 = var28[var17];
            BitSet var19 = var22.getCarvingMask(var18);
            if (var19 != null) {
               var27.putByteArray(var18.toString(), var19.toByteArray());
            }
         }

         var4.put("CarvingMasks", var27);
      }

      TickList var23 = var1.getBlockTicks();
      if (var23 instanceof ProtoTickList) {
         var4.put("ToBeTicked", ((ProtoTickList)var23).save());
      } else if (var23 instanceof ChunkTickList) {
         var4.put("TileTicks", ((ChunkTickList)var23).save());
      } else {
         var4.put("TileTicks", var0.getBlockTicks().save(var2));
      }

      TickList var26 = var1.getLiquidTicks();
      if (var26 instanceof ProtoTickList) {
         var4.put("LiquidsToBeTicked", ((ProtoTickList)var26).save());
      } else if (var26 instanceof ChunkTickList) {
         var4.put("LiquidTicks", ((ChunkTickList)var26).save());
      } else {
         var4.put("LiquidTicks", var0.getLiquidTicks().save(var2));
      }

      var4.put("PostProcessing", packOffsets(var1.getPostProcessing()));
      var27 = new CompoundTag();
      Iterator var29 = var1.getHeightmaps().iterator();

      while(var29.hasNext()) {
         Entry var30 = (Entry)var29.next();
         if (var1.getStatus().heightmapsAfter().contains(var30.getKey())) {
            var27.put(((Heightmap.Types)var30.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var30.getValue()).getRawData()));
         }
      }

      var4.put("Heightmaps", var27);
      var4.put("Structures", packStructureData(var2, var1.getAllStarts(), var1.getAllReferences()));
      return var3;
   }

   public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag var0) {
      if (var0 != null) {
         ChunkStatus var1 = ChunkStatus.byName(var0.getCompound("Level").getString("Status"));
         if (var1 != null) {
            return var1.getChunkType();
         }
      }

      return ChunkStatus.ChunkType.PROTOCHUNK;
   }

   private static void postLoadChunk(ServerLevel var0, CompoundTag var1, LevelChunk var2) {
      ListTag var3;
      if (var1.contains("Entities", 9)) {
         var3 = var1.getList("Entities", 10);
         if (!var3.isEmpty()) {
            var0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive(var3, var0));
         }
      }

      var3 = var1.getList("TileEntities", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         CompoundTag var5 = var3.getCompound(var4);
         boolean var6 = var5.getBoolean("keepPacked");
         if (var6) {
            var2.setBlockEntityNbt(var5);
         } else {
            BlockPos var7 = new BlockPos(var5.getInt("x"), var5.getInt("y"), var5.getInt("z"));
            BlockEntity var8 = BlockEntity.loadStatic(var7, var2.getBlockState(var7), var5);
            if (var8 != null) {
               var2.setBlockEntity(var8);
            }
         }
      }

   }

   private static CompoundTag packStructureData(ChunkPos var0, Map<StructureFeature<?>, StructureStart<?>> var1, Map<StructureFeature<?>, LongSet> var2) {
      CompoundTag var3 = new CompoundTag();
      CompoundTag var4 = new CompoundTag();
      Iterator var5 = var1.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         var4.put(((StructureFeature)var6.getKey()).getFeatureName(), ((StructureStart)var6.getValue()).createTag(var0.x, var0.z));
      }

      var3.put("Starts", var4);
      CompoundTag var8 = new CompoundTag();
      Iterator var9 = var2.entrySet().iterator();

      while(var9.hasNext()) {
         Entry var7 = (Entry)var9.next();
         var8.put(((StructureFeature)var7.getKey()).getFeatureName(), new LongArrayTag((LongSet)var7.getValue()));
      }

      var3.put("References", var8);
      return var3;
   }

   private static Map<StructureFeature<?>, StructureStart<?>> unpackStructureStart(StructureManager var0, CompoundTag var1, long var2) {
      HashMap var4 = Maps.newHashMap();
      CompoundTag var5 = var1.getCompound("Starts");
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
         var2.put(StructureFeature.STRUCTURES_REGISTRY.get(var5.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(var3.getLongArray(var5)).filter((var2x) -> {
            ChunkPos var4 = new ChunkPos(var2x);
            if (var4.getChessboardDistance(var0) > 8) {
               LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", var5, var4, var0);
               return false;
            } else {
               return true;
            }
         }).toArray()));
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
}
