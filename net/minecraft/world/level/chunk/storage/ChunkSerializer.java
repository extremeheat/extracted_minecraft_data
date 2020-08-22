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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
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

      ChunkBiomeContainer var9 = new ChunkBiomeContainer(var3, var6, var7.contains("Biomes", 11) ? var7.getIntArray("Biomes") : null);
      UpgradeData var10 = var7.contains("UpgradeData", 10) ? new UpgradeData(var7.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ProtoTickList var11 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x.defaultBlockState().isAir();
      }, var3, var7.getList("ToBeTicked", 9));
      ProtoTickList var12 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x == Fluids.EMPTY;
      }, var3, var7.getList("LiquidsToBeTicked", 9));
      boolean var13 = var7.getBoolean("isLightOn");
      ListTag var14 = var7.getList("Sections", 10);
      boolean var15 = true;
      LevelChunkSection[] var16 = new LevelChunkSection[16];
      boolean var17 = var0.getDimension().isHasSkyLight();
      ServerChunkCache var18 = var0.getChunkSource();
      LevelLightEngine var19 = var18.getLightEngine();
      if (var13) {
         var19.retainData(var3, true);
      }

      for(int var20 = 0; var20 < var14.size(); ++var20) {
         CompoundTag var21 = var14.getCompound(var20);
         byte var22 = var21.getByte("Y");
         if (var21.contains("Palette", 9) && var21.contains("BlockStates", 12)) {
            LevelChunkSection var23 = new LevelChunkSection(var22 << 4);
            var23.getStates().read(var21.getList("Palette", 10), var21.getLongArray("BlockStates"));
            var23.recalcBlockCounts();
            if (!var23.isEmpty()) {
               var16[var22] = var23;
            }

            var2.checkConsistencyWithBlocks(var3, var23);
         }

         if (var13) {
            if (var21.contains("BlockLight", 7)) {
               var19.queueSectionData(LightLayer.BLOCK, SectionPos.of(var3, var22), new DataLayer(var21.getByteArray("BlockLight")));
            }

            if (var17 && var21.contains("SkyLight", 7)) {
               var19.queueSectionData(LightLayer.SKY, SectionPos.of(var3, var22), new DataLayer(var21.getByteArray("SkyLight")));
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

         var38 = new LevelChunk(var0.getLevel(), var3, var9, var10, (TickList)var24, (TickList)var25, var36, var16, (var1x) -> {
            postLoadChunk(var7, var1x);
         });
      } else {
         ProtoChunk var39 = new ProtoChunk(var3, var10, var16, var11, var12);
         var39.setBiomes(var9);
         var38 = var39;
         var39.setInhabitedTime(var36);
         var39.setStatus(ChunkStatus.byName(var7.getString("Status")));
         if (var39.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
            var39.setLightEngine(var19);
         }

         if (!var13 && var39.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            Iterator var41 = BlockPos.betweenClosed(var3.getMinBlockX(), 0, var3.getMinBlockZ(), var3.getMaxBlockX(), 255, var3.getMaxBlockZ()).iterator();

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
      ((ChunkAccess)var38).setAllStarts(unpackStructureStart(var5, var1, var44));
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

      CompoundTag var15;
      for(int var10 = -1; var10 < 17; ++var10) {
         LevelChunkSection var12 = (LevelChunkSection)Arrays.stream(var6).filter((var1x) -> {
            return var1x != null && var1x.bottomBlockY() >> 4 == var10;
         }).findFirst().orElse(LevelChunk.EMPTY_SECTION);
         DataLayer var13 = var8.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var10));
         DataLayer var14 = var8.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var10));
         if (var12 != LevelChunk.EMPTY_SECTION || var13 != null || var14 != null) {
            var15 = new CompoundTag();
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

      ChunkBiomeContainer var19 = var1.getBiomes();
      if (var19 != null) {
         var4.putIntArray("Biomes", var19.writeBiomes());
      }

      ListTag var11 = new ListTag();
      Iterator var20 = var1.getBlockEntitiesPos().iterator();

      CompoundTag var25;
      while(var20.hasNext()) {
         BlockPos var22 = (BlockPos)var20.next();
         var25 = var1.getBlockEntityNbtForSaving(var22);
         if (var25 != null) {
            var11.add(var25);
         }
      }

      var4.put("TileEntities", var11);
      ListTag var21 = new ListTag();
      if (var1.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunk var23 = (LevelChunk)var1;
         var23.setLastSaveHadEntities(false);

         for(int var27 = 0; var27 < var23.getEntitySections().length; ++var27) {
            Iterator var28 = var23.getEntitySections()[var27].iterator();

            while(var28.hasNext()) {
               Entity var16 = (Entity)var28.next();
               CompoundTag var17 = new CompoundTag();
               if (var16.save(var17)) {
                  var23.setLastSaveHadEntities(true);
                  var21.add(var17);
               }
            }
         }
      } else {
         ProtoChunk var24 = (ProtoChunk)var1;
         var21.addAll(var24.getEntities());
         var4.put("Lights", packOffsets(var24.getPackedLights()));
         var25 = new CompoundTag();
         GenerationStep.Carving[] var29 = GenerationStep.Carving.values();
         int var31 = var29.length;

         for(int var33 = 0; var33 < var31; ++var33) {
            GenerationStep.Carving var18 = var29[var33];
            var25.putByteArray(var18.toString(), var1.getCarvingMask(var18).toByteArray());
         }

         var4.put("CarvingMasks", var25);
      }

      var4.put("Entities", var21);
      TickList var26 = var1.getBlockTicks();
      if (var26 instanceof ProtoTickList) {
         var4.put("ToBeTicked", ((ProtoTickList)var26).save());
      } else if (var26 instanceof ChunkTickList) {
         var4.put("TileTicks", ((ChunkTickList)var26).save(var0.getGameTime()));
      } else {
         var4.put("TileTicks", var0.getBlockTicks().save(var2));
      }

      TickList var30 = var1.getLiquidTicks();
      if (var30 instanceof ProtoTickList) {
         var4.put("LiquidsToBeTicked", ((ProtoTickList)var30).save());
      } else if (var30 instanceof ChunkTickList) {
         var4.put("LiquidTicks", ((ChunkTickList)var30).save(var0.getGameTime()));
      } else {
         var4.put("LiquidTicks", var0.getLiquidTicks().save(var2));
      }

      var4.put("PostProcessing", packOffsets(var1.getPostProcessing()));
      var15 = new CompoundTag();
      Iterator var32 = var1.getHeightmaps().iterator();

      while(var32.hasNext()) {
         Entry var34 = (Entry)var32.next();
         if (var1.getStatus().heightmapsAfter().contains(var34.getKey())) {
            var15.put(((Heightmap.Types)var34.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var34.getValue()).getRawData()));
         }
      }

      var4.put("Heightmaps", var15);
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

   private static void postLoadChunk(CompoundTag var0, LevelChunk var1) {
      ListTag var2 = var0.getList("Entities", 10);
      Level var3 = var1.getLevel();

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         CompoundTag var5 = var2.getCompound(var4);
         EntityType.loadEntityRecursive(var5, var3, (var1x) -> {
            var1.addEntity(var1x);
            return var1x;
         });
         var1.setLastSaveHadEntities(true);
      }

      ListTag var9 = var0.getList("TileEntities", 10);

      for(int var10 = 0; var10 < var9.size(); ++var10) {
         CompoundTag var6 = var9.getCompound(var10);
         boolean var7 = var6.getBoolean("keepPacked");
         if (var7) {
            var1.setBlockEntityNbt(var6);
         } else {
            BlockEntity var8 = BlockEntity.loadStatic(var6);
            if (var8 != null) {
               var1.addBlockEntity(var8);
            }
         }
      }

   }

   private static CompoundTag packStructureData(ChunkPos var0, Map var1, Map var2) {
      CompoundTag var3 = new CompoundTag();
      CompoundTag var4 = new CompoundTag();
      Iterator var5 = var1.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         var4.put((String)var6.getKey(), ((StructureStart)var6.getValue()).createTag(var0.x, var0.z));
      }

      var3.put("Starts", var4);
      CompoundTag var8 = new CompoundTag();
      Iterator var9 = var2.entrySet().iterator();

      while(var9.hasNext()) {
         Entry var7 = (Entry)var9.next();
         var8.put((String)var7.getKey(), new LongArrayTag((LongSet)var7.getValue()));
      }

      var3.put("References", var8);
      return var3;
   }

   private static Map unpackStructureStart(ChunkGenerator var0, StructureManager var1, CompoundTag var2) {
      HashMap var3 = Maps.newHashMap();
      CompoundTag var4 = var2.getCompound("Starts");
      Iterator var5 = var4.getAllKeys().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var3.put(var6, StructureFeatureIO.loadStaticStart(var0, var1, var4.getCompound(var6)));
      }

      return var3;
   }

   private static Map unpackStructureReferences(ChunkPos var0, CompoundTag var1) {
      HashMap var2 = Maps.newHashMap();
      CompoundTag var3 = var1.getCompound("References");
      Iterator var4 = var3.getAllKeys().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var2.put(var5, new LongOpenHashSet(Arrays.stream(var3.getLongArray(var5)).filter((var2x) -> {
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
