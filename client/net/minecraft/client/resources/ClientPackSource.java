package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPackSource implements RepositorySource {
   private static final PackMetadataSection BUILT_IN;
   private static final Logger LOGGER;
   private static final Pattern SHA1;
   private final VanillaPackResources vanillaPack;
   private final File serverPackDir;
   private final ReentrantLock downloadLock = new ReentrantLock();
   private final AssetIndex assetIndex;
   @Nullable
   private CompletableFuture<?> currentDownload;
   @Nullable
   private Pack serverPack;

   public ClientPackSource(File var1, AssetIndex var2) {
      super();
      this.serverPackDir = var1;
      this.assetIndex = var2;
      this.vanillaPack = new DefaultClientPackResources(BUILT_IN, var2);
   }

   public void loadPacks(Consumer<Pack> var1, Pack.PackConstructor var2) {
      Pack var3 = Pack.create("vanilla", true, () -> {
         return this.vanillaPack;
      }, var2, Pack.Position.BOTTOM, PackSource.BUILT_IN);
      if (var3 != null) {
         var1.accept(var3);
      }

      if (this.serverPack != null) {
         var1.accept(this.serverPack);
      }

      Pack var4 = this.createProgrammerArtPack(var2);
      if (var4 != null) {
         var1.accept(var4);
      }

   }

   public VanillaPackResources getVanillaPack() {
      return this.vanillaPack;
   }

   private static Map<String, String> getDownloadHeaders() {
      HashMap var0 = Maps.newHashMap();
      var0.put("X-Minecraft-Username", Minecraft.getInstance().getUser().getName());
      var0.put("X-Minecraft-UUID", Minecraft.getInstance().getUser().getUuid());
      var0.put("X-Minecraft-Version", SharedConstants.getCurrentVersion().getName());
      var0.put("X-Minecraft-Version-ID", SharedConstants.getCurrentVersion().getId());
      var0.put("X-Minecraft-Pack-Format", String.valueOf(PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion())));
      var0.put("User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
      return var0;
   }

   public CompletableFuture<?> downloadAndSelectResourcePack(String var1, String var2) {
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
            return !this.checkHash(var4, var5) ? Util.failedFuture(new RuntimeException("Hash check failure for file " + var5 + ", see log")) : this.setServerPack(var5, PackSource.SERVER);
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

   public CompletableFuture<Void> setServerPack(File var1, PackSource var2) {
      PackMetadataSection var3;
      try {
         FilePackResources var4 = new FilePackResources(var1);
         Throwable var5 = null;

         try {
            var3 = (PackMetadataSection)var4.getMetadataSection(PackMetadataSection.SERIALIZER);
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
      } catch (IOException var17) {
         return Util.failedFuture(new IOException(String.format("Invalid resourcepack at %s", var1), var17));
      }

      LOGGER.info("Applying server pack {}", var1);
      this.serverPack = new Pack("server", true, () -> {
         return new FilePackResources(var1);
      }, new TranslatableComponent("resourcePack.server.name"), var3.getDescription(), PackCompatibility.forMetadata(var3, PackType.CLIENT_RESOURCES), Pack.Position.TOP, true, var2);
      return Minecraft.getInstance().delayTextureReload();
   }

   @Nullable
   private Pack createProgrammerArtPack(Pack.PackConstructor var1) {
      Pack var2 = null;
      File var3 = this.assetIndex.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));
      if (var3 != null && var3.isFile()) {
         var2 = createProgrammerArtPack(var1, () -> {
            return createProgrammerArtZipPack(var3);
         });
      }

      if (var2 == null && SharedConstants.IS_RUNNING_IN_IDE) {
         File var4 = this.assetIndex.getRootFile("../resourcepacks/programmer_art");
         if (var4 != null && var4.isDirectory()) {
            var2 = createProgrammerArtPack(var1, () -> {
               return createProgrammerArtDirPack(var4);
            });
         }
      }

      return var2;
   }

   @Nullable
   private static Pack createProgrammerArtPack(Pack.PackConstructor var0, Supplier<PackResources> var1) {
      return Pack.create("programer_art", false, var1, var0, Pack.Position.TOP, PackSource.BUILT_IN);
   }

   private static FolderPackResources createProgrammerArtDirPack(File var0) {
      return new FolderPackResources(var0) {
         public String getName() {
            return "Programmer Art";
         }
      };
   }

   private static PackResources createProgrammerArtZipPack(File var0) {
      return new FilePackResources(var0) {
         public String getName() {
            return "Programmer Art";
         }
      };
   }

   static {
      BUILT_IN = new PackMetadataSection(new TranslatableComponent("resourcePack.vanilla.description"), PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()));
      LOGGER = LogManager.getLogger();
      SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   }
}
