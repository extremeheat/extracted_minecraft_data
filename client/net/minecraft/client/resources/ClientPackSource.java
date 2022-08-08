package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

public class ClientPackSource implements RepositorySource {
   private static final PackMetadataSection BUILT_IN;
   private static final Logger LOGGER;
   private static final Pattern SHA1;
   private static final int MAX_PACK_SIZE_BYTES = 262144000;
   private static final int MAX_KEPT_PACKS = 10;
   private static final String VANILLA_ID = "vanilla";
   private static final String SERVER_ID = "server";
   private static final String PROGRAMMER_ART_ID = "programer_art";
   private static final String PROGRAMMER_ART_NAME = "Programmer Art";
   private static final Component APPLYING_PACK_TEXT;
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

   public CompletableFuture<?> downloadAndSelectResourcePack(URL var1, String var2, boolean var3) {
      String var4 = Hashing.sha1().hashString(var1.toString(), StandardCharsets.UTF_8).toString();
      String var5 = SHA1.matcher(var2).matches() ? var2 : "";
      this.downloadLock.lock();

      CompletableFuture var14;
      try {
         this.clearServerPack();
         this.clearOldDownloads();
         File var6 = new File(this.serverPackDir, var4);
         CompletableFuture var7;
         if (var6.exists()) {
            var7 = CompletableFuture.completedFuture("");
         } else {
            ProgressScreen var8 = new ProgressScreen(var3);
            Map var9 = getDownloadHeaders();
            Minecraft var10 = Minecraft.getInstance();
            var10.executeBlocking(() -> {
               var10.setScreen(var8);
            });
            var7 = HttpUtil.downloadTo(var6, var1, var9, 262144000, var8, var10.getProxy());
         }

         this.currentDownload = var7.thenCompose((var4x) -> {
            if (!this.checkHash(var5, var6)) {
               return Util.failedFuture(new RuntimeException("Hash check failure for file " + var6 + ", see log"));
            } else {
               Minecraft var5x = Minecraft.getInstance();
               var5x.execute(() -> {
                  if (!var3) {
                     var5x.setScreen(new GenericDirtMessageScreen(APPLYING_PACK_TEXT));
                  }

               });
               return this.setServerPack(var6, PackSource.SERVER);
            }
         }).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               LOGGER.warn("Pack application failed: {}, deleting file {}", var2x.getMessage(), var6);
               deleteQuietly(var6);
               Minecraft var3 = Minecraft.getInstance();
               var3.execute(() -> {
                  var3.setScreen(new ConfirmScreen((var1) -> {
                     if (var1) {
                        var3.setScreen((Screen)null);
                     } else {
                        ClientPacketListener var2 = var3.getConnection();
                        if (var2 != null) {
                           var2.getConnection().disconnect(Component.translatable("connect.aborted"));
                        }
                     }

                  }, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, Component.translatable("menu.disconnect")));
               });
            }

         });
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

   public CompletableFuture<?> clearServerPack() {
      this.downloadLock.lock();

      try {
         if (this.currentDownload != null) {
            this.currentDownload.cancel(true);
         }

         this.currentDownload = null;
         if (this.serverPack != null) {
            this.serverPack = null;
            CompletableFuture var1 = Minecraft.getInstance().delayTextureReload();
            return var1;
         }
      } finally {
         this.downloadLock.unlock();
      }

      return CompletableFuture.completedFuture((Object)null);
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
         } catch (Exception var5) {
            LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
         }

      }
   }

   public CompletableFuture<Void> loadBundledResourcePack(LevelStorageSource.LevelStorageAccess var1) {
      Path var2 = var1.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
      return Files.exists(var2, new LinkOption[0]) && !Files.isDirectory(var2, new LinkOption[0]) ? this.setServerPack(var2.toFile(), PackSource.WORLD) : CompletableFuture.completedFuture((Object)null);
   }

   public CompletableFuture<Void> setServerPack(File var1, PackSource var2) {
      PackMetadataSection var3;
      try {
         FilePackResources var4 = new FilePackResources(var1);

         try {
            var3 = (PackMetadataSection)var4.getMetadataSection(PackMetadataSection.SERIALIZER);
         } catch (Throwable var8) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var4.close();
      } catch (IOException var9) {
         return Util.failedFuture(new IOException(String.format("Invalid resourcepack at %s", var1), var9));
      }

      LOGGER.info("Applying server pack {}", var1);
      this.serverPack = new Pack("server", true, () -> {
         return new FilePackResources(var1);
      }, Component.translatable("resourcePack.server.name"), var3.getDescription(), PackCompatibility.forMetadata(var3, PackType.CLIENT_RESOURCES), Pack.Position.TOP, true, var2);
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
      BUILT_IN = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"), PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()));
      LOGGER = LogUtils.getLogger();
      SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
      APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
   }
}
