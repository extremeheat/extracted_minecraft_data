package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class McRegionUpgrader {
   private static final Logger LOGGER = LogManager.getLogger();

   static boolean convertLevel(LevelStorageSource.LevelStorageAccess var0, ProgressListener var1) {
      var1.progressStagePercentage(0);
      ArrayList var2 = Lists.newArrayList();
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      File var5 = var0.getDimensionPath(Level.OVERWORLD);
      File var6 = var0.getDimensionPath(Level.NETHER);
      File var7 = var0.getDimensionPath(Level.END);
      LOGGER.info("Scanning folders...");
      addRegionFiles(var5, var2);
      if (var6.exists()) {
         addRegionFiles(var6, var3);
      }

      if (var7.exists()) {
         addRegionFiles(var7, var4);
      }

      int var8 = var2.size() + var3.size() + var4.size();
      LOGGER.info("Total conversion count is {}", var8);
      RegistryAccess.RegistryHolder var9 = RegistryAccess.builtin();
      RegistryReadOps var10 = RegistryReadOps.create(NbtOps.INSTANCE, (ResourceManager)ResourceManager.Empty.INSTANCE, var9);
      WorldData var11 = var0.getDataTag(var10, DataPackConfig.DEFAULT);
      long var12 = var11 != null ? var11.worldGenSettings().seed() : 0L;
      WritableRegistry var15 = var9.registryOrThrow(Registry.BIOME_REGISTRY);
      Object var14;
      if (var11 != null && var11.worldGenSettings().isFlatWorld()) {
         var14 = new FixedBiomeSource((Biome)var15.getOrThrow(Biomes.PLAINS));
      } else {
         var14 = new OverworldBiomeSource(var12, false, false, var15);
      }

      convertRegions(var9, new File(var5, "region"), var2, (BiomeSource)var14, 0, var8, var1);
      convertRegions(var9, new File(var6, "region"), var3, new FixedBiomeSource((Biome)var15.getOrThrow(Biomes.NETHER_WASTES)), var2.size(), var8, var1);
      convertRegions(var9, new File(var7, "region"), var4, new FixedBiomeSource((Biome)var15.getOrThrow(Biomes.THE_END)), var2.size() + var3.size(), var8, var1);
      makeMcrLevelDatBackup(var0);
      var0.saveDataTag(var9, var11);
      return true;
   }

   private static void makeMcrLevelDatBackup(LevelStorageSource.LevelStorageAccess var0) {
      File var1 = var0.getLevelPath(LevelResource.LEVEL_DATA_FILE).toFile();
      if (!var1.exists()) {
         LOGGER.warn("Unable to create level.dat_mcr backup");
      } else {
         File var2 = new File(var1.getParent(), "level.dat_mcr");
         if (!var1.renameTo(var2)) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
         }

      }
   }

   private static void convertRegions(RegistryAccess.RegistryHolder var0, File var1, Iterable<File> var2, BiomeSource var3, int var4, int var5, ProgressListener var6) {
      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         File var8 = (File)var7.next();
         convertRegion(var0, var1, var8, var3, var4, var5, var6);
         ++var4;
         int var9 = (int)Math.round(100.0D * (double)var4 / (double)var5);
         var6.progressStagePercentage(var9);
      }

   }

   private static void convertRegion(RegistryAccess.RegistryHolder var0, File var1, File var2, BiomeSource var3, int var4, int var5, ProgressListener var6) {
      String var7 = var2.getName();

      try {
         RegionFile var8 = new RegionFile(var2, var1, true);
         Throwable var9 = null;

         try {
            RegionFile var10 = new RegionFile(new File(var1, var7.substring(0, var7.length() - ".mcr".length()) + ".mca"), var1, true);
            Throwable var11 = null;

            try {
               for(int var12 = 0; var12 < 32; ++var12) {
                  int var13;
                  for(var13 = 0; var13 < 32; ++var13) {
                     ChunkPos var14 = new ChunkPos(var12, var13);
                     if (var8.hasChunk(var14) && !var10.hasChunk(var14)) {
                        CompoundTag var15;
                        try {
                           DataInputStream var16 = var8.getChunkDataInputStream(var14);
                           Throwable var17 = null;

                           try {
                              if (var16 == null) {
                                 LOGGER.warn("Failed to fetch input stream for chunk {}", var14);
                                 continue;
                              }

                              var15 = NbtIo.read((DataInput)var16);
                           } catch (Throwable var105) {
                              var17 = var105;
                              throw var105;
                           } finally {
                              if (var16 != null) {
                                 if (var17 != null) {
                                    try {
                                       var16.close();
                                    } catch (Throwable var102) {
                                       var17.addSuppressed(var102);
                                    }
                                 } else {
                                    var16.close();
                                 }
                              }

                           }
                        } catch (IOException var107) {
                           LOGGER.warn("Failed to read data for chunk {}", var14, var107);
                           continue;
                        }

                        CompoundTag var114 = var15.getCompound("Level");
                        OldChunkStorage.OldLevelChunk var115 = OldChunkStorage.load(var114);
                        CompoundTag var18 = new CompoundTag();
                        CompoundTag var19 = new CompoundTag();
                        var18.put("Level", var19);
                        OldChunkStorage.convertToAnvilFormat(var0, var115, var19, var3);
                        DataOutputStream var20 = var10.getChunkDataOutputStream(var14);
                        Throwable var21 = null;

                        try {
                           NbtIo.write(var18, (DataOutput)var20);
                        } catch (Throwable var103) {
                           var21 = var103;
                           throw var103;
                        } finally {
                           if (var20 != null) {
                              if (var21 != null) {
                                 try {
                                    var20.close();
                                 } catch (Throwable var101) {
                                    var21.addSuppressed(var101);
                                 }
                              } else {
                                 var20.close();
                              }
                           }

                        }
                     }
                  }

                  var13 = (int)Math.round(100.0D * (double)(var4 * 1024) / (double)(var5 * 1024));
                  int var113 = (int)Math.round(100.0D * (double)((var12 + 1) * 32 + var4 * 1024) / (double)(var5 * 1024));
                  if (var113 > var13) {
                     var6.progressStagePercentage(var113);
                  }
               }
            } catch (Throwable var108) {
               var11 = var108;
               throw var108;
            } finally {
               if (var10 != null) {
                  if (var11 != null) {
                     try {
                        var10.close();
                     } catch (Throwable var100) {
                        var11.addSuppressed(var100);
                     }
                  } else {
                     var10.close();
                  }
               }

            }
         } catch (Throwable var110) {
            var9 = var110;
            throw var110;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var99) {
                     var9.addSuppressed(var99);
                  }
               } else {
                  var8.close();
               }
            }

         }
      } catch (IOException var112) {
         LOGGER.error("Failed to upgrade region file {}", var2, var112);
      }

   }

   private static void addRegionFiles(File var0, Collection<File> var1) {
      File var2 = new File(var0, "region");
      File[] var3 = var2.listFiles((var0x, var1x) -> {
         return var1x.endsWith(".mcr");
      });
      if (var3 != null) {
         Collections.addAll(var1, var3);
      }

   }
}
