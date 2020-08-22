package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.VanillaPack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPackSource implements RepositorySource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack vanillaPack;
   private final File serverPackDir;
   private final ReentrantLock downloadLock = new ReentrantLock();
   private final AssetIndex assetIndex;
   @Nullable
   private CompletableFuture currentDownload;
   @Nullable
   private UnopenedResourcePack serverPack;

   public ClientPackSource(File var1, AssetIndex var2) {
      this.serverPackDir = var1;
      this.assetIndex = var2;
      this.vanillaPack = new DefaultClientResourcePack(var2);
   }

   public void loadPacks(Map var1, UnopenedPack.UnopenedPackConstructor var2) {
      UnopenedPack var3 = UnopenedPack.create("vanilla", true, () -> {
         return this.vanillaPack;
      }, var2, UnopenedPack.Position.BOTTOM);
      if (var3 != null) {
         var1.put("vanilla", var3);
      }

      if (this.serverPack != null) {
         var1.put("server", this.serverPack);
      }

      File var4 = this.assetIndex.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));
      if (var4 != null && var4.isFile()) {
         UnopenedPack var5 = UnopenedPack.create("programer_art", false, () -> {
            return new FileResourcePack(var4) {
               public String getName() {
                  return "Programmer Art";
               }
            };
         }, var2, UnopenedPack.Position.TOP);
         if (var5 != null) {
            var1.put("programer_art", var5);
         }
      }

   }

   public VanillaPack getVanillaPack() {
      return this.vanillaPack;
   }

   public static Map getDownloadHeaders() {
      HashMap var0 = Maps.newHashMap();
      var0.put("X-Minecraft-Username", Minecraft.getInstance().getUser().getName());
      var0.put("X-Minecraft-UUID", Minecraft.getInstance().getUser().getUuid());
      var0.put("X-Minecraft-Version", SharedConstants.getCurrentVersion().getName());
      var0.put("X-Minecraft-Version-ID", SharedConstants.getCurrentVersion().getId());
      var0.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getCurrentVersion().getPackVersion()));
      var0.put("User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
      return var0;
   }

   public CompletableFuture downloadAndSelectResourcePack(String var1, String var2) {
      String var3 = DigestUtils.sha1Hex(var1);
      String var4 = SHA1.matcher(var2).matches() ? var2 : "";
      this.downloadLock.lock();

      CompletableFuture var13;
      try {
         this.clearServerPack();
         this.clearOldDownloads();
         File var5 = new File(this.serverPackDir, var3);
         CompletableFuture var6;
         if (var5.exists()) {
            var6 = CompletableFuture.completedFuture("");
         } else {
            ProgressScreen var7 = new ProgressScreen();
            Map var8 = getDownloadHeaders();
            Minecraft var9 = Minecraft.getInstance();
            var9.executeBlocking(() -> {
               var9.setScreen(var7);
            });
            var6 = HttpUtil.downloadTo(var5, var1, var8, 104857600, var7, var9.getProxy());
         }

         this.currentDownload = var6.thenCompose((var3x) -> {
            return !this.checkHash(var4, var5) ? Util.failedFuture(new RuntimeException("Hash check failure for file " + var5 + ", see log")) : this.setServerPack(var5);
         }).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               LOGGER.warn("Pack application failed: {}, deleting file {}", var2x.getMessage(), var5);
               deleteQuietly(var5);
            }

         });
         var13 = this.currentDownload;
      } finally {
         this.downloadLock.unlock();
      }

      return var13;
   }

   private static void deleteQuietly(File var0) {
      try {
         Files.delete(var0.toPath());
      } catch (IOException var2) {
         LOGGER.warn("Failed to delete file {}: {}", var0, var2.getMessage());
      }

   }

   public void clearServerPack() {
      this.downloadLock.lock();

      try {
         if (this.currentDownload != null) {
            this.currentDownload.cancel(true);
         }

         this.currentDownload = null;
         if (this.serverPack != null) {
            this.serverPack = null;
            Minecraft.getInstance().delayTextureReload();
         }
      } finally {
         this.downloadLock.unlock();
      }

   }

   private boolean checkHash(String var1, File var2) {
      try {
         FileInputStream var4 = new FileInputStream(var2);
         Throwable var5 = null;

         String var3;
         try {
            var3 = DigestUtils.sha1Hex(var4);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  var4.close();
               }
            }

         }

         if (var1.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", var2);
            return true;
         }

         if (var3.toLowerCase(Locale.ROOT).equals(var1.toLowerCase(Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", var2, var1);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", var2, var1, var3);
      } catch (IOException var17) {
         LOGGER.warn("File {} couldn't be hashed.", var2, var17);
      }

      return false;
   }

   private void clearOldDownloads() {
      try {
         ArrayList var1 = Lists.newArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, (IOFileFilter)null));
         var1.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int var2 = 0;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            File var4 = (File)var3.next();
            if (var2++ >= 10) {
               LOGGER.info("Deleting old server resource pack {}", var4.getName());
               FileUtils.deleteQuietly(var4);
            }
         }
      } catch (IllegalArgumentException var5) {
         LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
      }

   }

   public CompletableFuture setServerPack(File var1) {
      PackMetadataSection var2 = null;
      NativeImage var3 = null;
      String var4 = null;

      try {
         FileResourcePack var5 = new FileResourcePack(var1);
         Throwable var6 = null;

         try {
            var2 = (PackMetadataSection)var5.getMetadataSection(PackMetadataSection.SERIALIZER);

            try {
               InputStream var7 = var5.getRootResource("pack.png");
               Throwable var8 = null;

               try {
                  var3 = NativeImage.read(var7);
               } catch (Throwable var35) {
                  var8 = var35;
                  throw var35;
               } finally {
                  if (var7 != null) {
                     if (var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var34) {
                           var8.addSuppressed(var34);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }
            } catch (IllegalArgumentException | IOException var37) {
               LOGGER.info("Could not read pack.png: {}", var37.getMessage());
            }
         } catch (Throwable var38) {
            var6 = var38;
            throw var38;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (IOException var40) {
         var4 = var40.getMessage();
      }

      if (var4 != null) {
         return Util.failedFuture(new RuntimeException(String.format("Invalid resourcepack at %s: %s", var1, var4)));
      } else {
         LOGGER.info("Applying server pack {}", var1);
         this.serverPack = new UnopenedResourcePack("server", true, () -> {
            return new FileResourcePack(var1);
         }, new TranslatableComponent("resourcePack.server.name", new Object[0]), var2.getDescription(), PackCompatibility.forFormat(var2.getPackFormat()), UnopenedPack.Position.TOP, true, var3);
         return Minecraft.getInstance().delayTextureReload();
      }
   }
}
