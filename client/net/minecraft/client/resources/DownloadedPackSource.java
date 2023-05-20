package net.minecraft.client.resources;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

public class DownloadedPackSource implements RepositorySource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private static final int MAX_PACK_SIZE_BYTES = 262144000;
   private static final int MAX_KEPT_PACKS = 10;
   private static final String SERVER_ID = "server";
   private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
   private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
   private final File serverPackDir;
   private final ReentrantLock downloadLock = new ReentrantLock();
   @Nullable
   private CompletableFuture<?> currentDownload;
   @Nullable
   private Pack serverPack;

   public DownloadedPackSource(File var1) {
      super();
      this.serverPackDir = var1;
   }

   @Override
   public void loadPacks(Consumer<Pack> var1) {
      if (this.serverPack != null) {
         var1.accept(this.serverPack);
      }
   }

   private static Map<String, String> getDownloadHeaders() {
      return Map.of(
         "X-Minecraft-Username",
         Minecraft.getInstance().getUser().getName(),
         "X-Minecraft-UUID",
         Minecraft.getInstance().getUser().getUuid(),
         "X-Minecraft-Version",
         SharedConstants.getCurrentVersion().getName(),
         "X-Minecraft-Version-ID",
         SharedConstants.getCurrentVersion().getId(),
         "X-Minecraft-Pack-Format",
         String.valueOf(SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)),
         "User-Agent",
         "Minecraft Java/" + SharedConstants.getCurrentVersion().getName()
      );
   }

   public CompletableFuture<?> downloadAndSelectResourcePack(URL var1, String var2, boolean var3) {
      String var4 = Hashing.sha1().hashString(var1.toString(), StandardCharsets.UTF_8).toString();
      String var5 = SHA1.matcher(var2).matches() ? var2 : "";
      this.downloadLock.lock();

      CompletableFuture var14;
      try {
         Minecraft var6 = Minecraft.getInstance();
         File var7 = new File(this.serverPackDir, var4);
         CompletableFuture var8;
         if (var7.exists()) {
            var8 = CompletableFuture.completedFuture("");
         } else {
            ProgressScreen var9 = new ProgressScreen(var3);
            Map var10 = getDownloadHeaders();
            var6.executeBlocking(() -> var6.setScreen(var9));
            var8 = HttpUtil.downloadTo(var7, var1, var10, 262144000, var9, var6.getProxy());
         }

         this.currentDownload = var8.<Void>thenCompose(var5x -> {
               if (!this.checkHash(var5, var7)) {
                  return CompletableFuture.failedFuture(new RuntimeException("Hash check failure for file " + var7 + ", see log"));
               } else {
                  var6.execute(() -> {
                     if (!var3) {
                        var6.setScreen(new GenericDirtMessageScreen(APPLYING_PACK_TEXT));
                     }
                  });
                  return this.setServerPack(var7, PackSource.SERVER);
               }
            })
            .exceptionallyCompose(
               var3x -> this.clearServerPack()
                     .thenAcceptAsync(var2xx -> {
                        LOGGER.warn("Pack application failed: {}, deleting file {}", var3x.getMessage(), var7);
                        deleteQuietly(var7);
                     }, Util.ioPool())
                     .thenAcceptAsync(
                        var1xx -> var6.setScreen(
                              new ConfirmScreen(
                                 var1xxx -> {
                                    if (var1xxx) {
                                       var6.setScreen(null);
                                    } else {
                                       ClientPacketListener var2xx = var6.getConnection();
                                       if (var2xx != null) {
                                          var2xx.getConnection().disconnect(Component.translatable("connect.aborted"));
                                       }
                                    }
                                 },
                                 Component.translatable("multiplayer.texturePrompt.failure.line1"),
                                 Component.translatable("multiplayer.texturePrompt.failure.line2"),
                                 CommonComponents.GUI_PROCEED,
                                 Component.translatable("menu.disconnect")
                              )
                           ),
                        var6
                     )
            )
            .thenAcceptAsync(var1x -> this.clearOldDownloads(), Util.ioPool());
         var14 = this.currentDownload;
      } finally {
         this.downloadLock.unlock();
      }

      return var14;
   }

   private static void deleteQuietly(File var0) {
      try {
         Files.delete(var0.toPath());
      } catch (IOException var2) {
         LOGGER.warn("Failed to delete file {}: {}", var0, var2.getMessage());
      }
   }

   public CompletableFuture<Void> clearServerPack() {
      this.downloadLock.lock();

      try {
         if (this.currentDownload != null) {
            this.currentDownload.cancel(true);
         }

         this.currentDownload = null;
         if (this.serverPack != null) {
            this.serverPack = null;
            return Minecraft.getInstance().delayTextureReload();
         }
      } finally {
         this.downloadLock.unlock();
      }

      return CompletableFuture.completedFuture(null);
   }

   private boolean checkHash(String var1, File var2) {
      try {
         String var3 = com.google.common.io.Files.asByteSource(var2).hash(Hashing.sha1()).toString();
         if (var1.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", var2);
            return true;
         }

         if (var3.toLowerCase(Locale.ROOT).equals(var1.toLowerCase(Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", var2, var1);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", new Object[]{var2, var1, var3});
      } catch (IOException var4) {
         LOGGER.warn("File {} couldn't be hashed.", var2, var4);
      }

      return false;
   }

   private void clearOldDownloads() {
      if (this.serverPackDir.isDirectory()) {
         try {
            ArrayList var1 = new ArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, null));
            var1.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int var2 = 0;

            for(File var4 : var1) {
               if (var2++ >= 10) {
                  LOGGER.info("Deleting old server resource pack {}", var4.getName());
                  FileUtils.deleteQuietly(var4);
               }
            }
         } catch (Exception var5) {
            LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
         }
      }
   }

   public CompletableFuture<Void> setServerPack(File var1, PackSource var2) {
      Pack.ResourcesSupplier var3 = var1x -> new FilePackResources(var1x, var1, false);
      Pack.Info var4 = Pack.readPackInfo("server", var3);
      if (var4 == null) {
         return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pack metadata at " + var1));
      } else {
         LOGGER.info("Applying server pack {}", var1);
         this.serverPack = Pack.create("server", SERVER_NAME, true, var3, var4, PackType.CLIENT_RESOURCES, Pack.Position.TOP, true, var2);
         return Minecraft.getInstance().delayTextureReload();
      }
   }

   public CompletableFuture<Void> loadBundledResourcePack(LevelStorageSource.LevelStorageAccess var1) {
      Path var2 = var1.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
      return Files.exists(var2) && !Files.isDirectory(var2) ? this.setServerPack(var2.toFile(), PackSource.WORLD) : CompletableFuture.completedFuture(null);
   }
}
