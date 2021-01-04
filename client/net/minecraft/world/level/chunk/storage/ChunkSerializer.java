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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
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

      Biome[] var9 = new Biome[256];
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      if (var7.contains("Biomes", 11)) {
         int[] var11 = var7.getIntArray("Biomes");

         for(int var12 = 0; var12 < var11.length; ++var12) {
            var9[var12] = (Biome)Registry.BIOME.byId(var11[var12]);
            if (var9[var12] == null) {
               var9[var12] = var6.getBiome(var10.set((var12 & 15) + var3.getMinBlockX(), 0, (var12 >> 4 & 15) + var3.getMinBlockZ()));
            }
         }
      } else {
         for(int var37 = 0; var37 < var9.length; ++var37) {
            var9[var37] = var6.getBiome(var10.set((var37 & 15) + var3.getMinBlockX(), 0, (var37 >> 4 & 15) + var3.getMinBlockZ()));
         }
      }

      UpgradeData var38 = var7.contains("UpgradeData", 10) ? new UpgradeData(var7.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ProtoTickList var39 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x.defaultBlockState().isAir();
      }, var3, var7.getList("ToBeTicked", 9));
      ProtoTickList var13 = new ProtoTickList((var0x) -> {
         return var0x == null || var0x == Fluids.EMPTY;
      }, var3, var7.getList("LiquidsToBeTicked", 9));
      boolean var14 = var7.getBoolean("isLightOn");
      ListTag var15 = var7.getList("Sections", 10);
      boolean var16 = true;
      LevelChunkSection[] var17 = new LevelChunkSection[16];
      boolean var18 = var0.getDimension().isHasSkyLight();
      ServerChunkCache var19 = var0.getChunkSource();
      LevelLightEngine var20 = var19.getLightEngine();
      if (var14) {
         var20.retainData(var3, true);
      }

      for(int var21 = 0; var21 < var15.size(); ++var21) {
         CompoundTag var22 = var15.getCompound(var21);
         byte var23 = var22.getByte("Y");
         if (var22.contains("Palette", 9) && var22.contains("BlockStates", 12)) {
            LevelChunkSection var24 = new LevelChunkSection(var23 << 4);
            var24.getStates().read(var22.getList("Palette", 10), var22.getLongArray("BlockStates"));
            var24.recalcBlockCounts();
            if (!var24.isEmpty()) {
               var17[var23] = var24;
            }

            var2.checkConsistencyWithBlocks(var3, var24);
         }

         if (var14) {
            if (var22.contains("BlockLight", 7)) {
               var20.queueSectionData(LightLayer.BLOCK, SectionPos.of(var3, var23), new DataLayer(var22.getByteArray("BlockLight")));
            }

            if (var18 && var22.contains("SkyLight", 7)) {
               var20.queueSectionData(LightLayer.SKY, SectionPos.of(var3, var23), new DataLayer(var22.getByteArray("SkyLight")));
            }
         }
      }

      long var40 = var7.getLong("InhabitedTime");
      ChunkStatus.ChunkType var41 = getChunkTypeFromTag(var4);
      Object var42;
      if (var41 == ChunkStatus.ChunkType.LEVELCHUNK) {
         ListTag var10000;
         Function var10001;
         DefaultedRegistry var10002;
         Object var25;
         if (var7.contains("TileTicks", 9)) {
            var10000 = var7.getList("TileTicks", 10);
            var10001 = Registry.BLOCK::getKey;
            var10002 = Registry.BLOCK;
            var10002.getClass();
            var25 = ChunkTickList.create(var10000, var10001, var10002::get);
         } else {
            var25 = var39;
         }

         Object var26;
         if (var7.contains("LiquidTicks", 9)) {
            var10000 = var7.getList("LiquidTicks", 10);
            var10001 = Registry.FLUID::getKey;
            var10002 = Registry.FLUID;
            var10002.getClass();
            var26 = ChunkTickList.create(var10000, var10001, var10002::get);
         } else {
            var26 = var13;
         }

         var42 = new LevelChunk(var0.getLevel(), var3, var9, var38, (TickList)var25, (TickList)var26, var40, var17, (var1x) -> {
            postLoadChunk(var7, var1x);
         });
      } else {
         ProtoChunk var43 = new ProtoChunk(var3, var38, var17, var39, var13);
         var42 = var43;
         var43.setBiomes(var9);
         var43.setInhabitedTime(var40);
         var43.setStatus(ChunkStatus.byName(var7.getString("Status")));
         if (var43.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
            var43.setLightEngine(var20);
         }

         if (!var14 && var43.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            Iterator var45 = BlockPos.betweenClosed(var3.getMinBlockX(), 0, var3.getMinBlockZ(), var3.getMaxBlockX(), 255, var3.getMaxBlockZ()).iterator();

            while(var45.hasNext()) {
               BlockPos var27 = (BlockPos)var45.next();
               if (((ChunkAccess)var42).getBlockState(var27).getLightEmission() != 0) {
                  var43.addLight(var27);
               }
            }
         }
      }

      ((ChunkAccess)var42).setLightCorrect(var14);
      CompoundTag var44 = var7.getCompound("Heightmaps");
      EnumSet var46 = EnumSet.noneOf(Heightmap.Types.class);
      Iterator var47 = ((ChunkAccess)var42).getStatus().heightmapsAfter().iterator();

      while(var47.hasNext()) {
         Heightmap.Types var28 = (Heightmap.Types)var47.next();
         String var29 = var28.getSerializationKey();
         if (var44.contains(var29, 12)) {
            ((ChunkAccess)var42).setHeightmap(var28, var44.getLongArray(var29));
         } else {
            var46.add(var28);
         }
      }

      Heightmap.primeHeightmaps((ChunkAccess)var42, var46);
      CompoundTag var48 = var7.getCompound("Structures");
      ((ChunkAccess)var42).setAllStarts(unpackStructureStart(var5, var1, var6, var48));
      ((ChunkAccess)var42).setAllReferences(unpackStructureReferences(var48));
      if (var7.getBoolean("shouldSave")) {
         ((ChunkAccess)var42).setUnsaved(true);
      }

      ListTag var49 = var7.getList("PostProcessing", 9);

      ListTag var30;
      int var31;
      for(int var50 = 0; var50 < var49.size(); ++var50) {
         var30 = var49.getList(var50);

         for(var31 = 0; var31 < var30.size(); ++var31) {
            ((ChunkAccess)var42).addPackedPostProcess(var30.getShort(var31), var50);
         }
      }

      if (var41 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var42);
      } else {
         ProtoChunk var51 = (ProtoChunk)var42;
         var30 = var7.getList("Entities", 10);

         for(var31 = 0; var31 < var30.size(); ++var31) {
            var51.addEntity(var30.getCompound(var31));
         }

         ListTag var52 = var7.getList("TileEntities", 10);

         CompoundTag var33;
         for(int var32 = 0; var32 < var52.size(); ++var32) {
            var33 = var52.getCompound(var32);
            ((ChunkAccess)var42).setBlockEntityNbt(var33);
         }

         ListTag var53 = var7.getList("Lights", 9);

         for(int var54 = 0; var54 < var53.size(); ++var54) {
            ListTag var34 = var53.getList(var54);

            for(int var35 = 0; var35 < var34.size(); ++var35) {
               var51.addLight(var34.getShort(var35), var54);
            }
         }

         var33 = var7.getCompound("CarvingMasks");
         Iterator var55 = var33.getAllKeys().iterator();

         while(var55.hasNext()) {
            String var56 = (String)var55.next();
            GenerationStep.Carving var36 = GenerationStep.Carving.valueOf(var56);
            var51.setCarvingMask(var36, BitSet.valueOf(var33.getByteArray(var56)));
         }

         return var51;
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

      Biome[] var20 = var1.getBiomes();
      int[] var11 = var20 != null ? new int[var20.length] : new int[0];
      if (var20 != null) {
         for(int var21 = 0; var21 < var20.length; ++var21) {
            var11[var21] = Registry.BIOME.getId(var20[var21]);
         }
      }

      var4.putIntArray("Biomes", var11);
      ListTag var22 = new ListTag();
      Iterator var23 = var1.getBlockEntitiesPos().iterator();

      while(var23.hasNext()) {
         BlockPos var25 = (BlockPos)var23.next();
         var15 = var1.getBlockEntityNbtForSaving(var25);
         if (var15 != null) {
            var22.add(var15);
         }
      }

      var4.put("TileEntities", var22);
      ListTag var24 = new ListTag();
      if (var1.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunk var26 = (LevelChunk)var1;
         var26.setLastSaveHadEntities(false);

         for(int var29 = 0; var29 < var26.getEntitySections().length; ++var29) {
            Iterator var16 = var26.getEntitySections()[var29].iterator();

            while(var16.hasNext()) {
               Entity var17 = (Entity)var16.next();
               CompoundTag var18 = new CompoundTag();
               if (var17.save(var18)) {
                  var26.setLastSaveHadEntities(true);
                  var24.add(var18);
               }
            }
         }
      } else {
         ProtoChunk var27 = (ProtoChunk)var1;
         var24.addAll(var27.getEntities());
         var4.put("Lights", packOffsets(var27.getPackedLights()));
         var15 = new CompoundTag();
         GenerationStep.Carving[] var30 = GenerationStep.Carving.values();
         int var33 = var30.length;

         for(int var35 = 0; var35 < var33; ++var35) {
            GenerationStep.Carving var19 = var30[var35];
            var15.putByteArray(var19.toString(), var1.getCarvingMask(var19).toByteArray());
         }

         var4.put("CarvingMasks", var15);
      }

      var4.put("Entities", var24);
      TickList var28 = var1.getBlockTicks();
      if (var28 instanceof ProtoTickList) {
         var4.put("ToBeTicked", ((ProtoTickList)var28).save());
      } else if (var28 instanceof ChunkTickList) {
         var4.put("TileTicks", ((ChunkTickList)var28).save(var0.getGameTime()));
      } else {
         var4.put("TileTicks", var0.getBlockTicks().save(var2));
      }

      TickList var31 = var1.getLiquidTicks();
      if (var31 instanceof ProtoTickList) {
         var4.put("LiquidsToBeTicked", ((ProtoTickList)var31).save());
      } else if (var31 instanceof ChunkTickList) {
         var4.put("LiquidTicks", ((ChunkTickList)var31).save(var0.getGameTime()));
      } else {
         var4.put("LiquidTicks", var0.getLiquidTicks().save(var2));
      }

      var4.put("PostProcessing", packOffsets(var1.getPostProcessing()));
      CompoundTag var32 = new CompoundTag();
      Iterator var34 = var1.getHeightmaps().iterator();

      while(var34.hasNext()) {
         Entry var36 = (Entry)var34.next();
         if (var1.getStatus().heightmapsAfter().contains(var36.getKey())) {
            var32.put(((Heightmap.Types)var36.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var36.getValue()).getRawData()));
         }
      }

      var4.put("Heightmaps", var32);
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

   private static CompoundTag packStructureData(ChunkPos var0, Map<String, StructureStart> var1, Map<String, LongSet> var2) {
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

   private static Map<String, StructureStart> unpackStructureStart(ChunkGenerator<?> var0, StructureManager var1, BiomeSource var2, CompoundTag var3) {
      HashMap var4 = Maps.newHashMap();
      CompoundTag var5 = var3.getCompound("Starts");
      Iterator var6 = var5.getAllKeys().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var4.put(var7, StructureFeatureIO.loadStaticStart(var0, var1, var2, var5.getCompound(var7)));
      }

      return var4;
   }

   private static Map<String, LongSet> unpackStructureReferences(CompoundTag var0) {
      HashMap var1 = Maps.newHashMap();
      CompoundTag var2 = var0.getCompound("References");
      Iterator var3 = var2.getAllKeys().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var1.put(var4, new LongOpenHashSet(var2.getLongArray(var4)));
      }

      return var1;
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
               var6.add(new ShortTag(var8));
            }
         }

         var1.add(var6);
      }

      return var1;
   }
}
