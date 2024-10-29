package com.mojang.realmsclient.client.worldupload;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class RealmsCreateWorldFlow {
   private static final Logger LOGGER = LogUtils.getLogger();

   public RealmsCreateWorldFlow() {
      super();
   }

   public static void createWorld(Minecraft var0, Screen var1, Screen var2, int var3, RealmsServer var4, @Nullable RealmCreationTask var5) {
      CreateWorldScreen.openFresh(var0, var1, (var6, var7, var8, var9) -> {
         Path var10;
         try {
            var10 = createTemporaryWorldFolder(var7, var8, var9);
         } catch (IOException var13) {
            LOGGER.warn("Failed to create temporary world folder.");
            var0.setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), var2));
            return true;
         }

         RealmsWorldOptions var11 = RealmsWorldOptions.createFromSettings(var8.getLevelSettings(), SharedConstants.getCurrentVersion().getName());
         RealmsWorldUpload var12 = new RealmsWorldUpload(var10, var11, var0.getUser(), var4.id, var3, RealmsWorldUploadStatusTracker.noOp());
         Objects.requireNonNull(var12);
         var0.forceSetScreen(new AlertScreen(var12::cancel, Component.translatable("mco.create.world.reset.title"), Component.empty(), CommonComponents.GUI_CANCEL, false));
         if (var5 != null) {
            var5.run();
         }

         var12.packAndUpload().handleAsync((var5x, var6x) -> {
            if (var6x != null) {
               if (var6x instanceof CompletionException) {
                  CompletionException var7 = (CompletionException)var6x;
                  var6x = var7.getCause();
               }

               if (var6x instanceof RealmsUploadCanceledException) {
                  var0.forceSetScreen(var2);
               } else {
                  if (var6x instanceof RealmsUploadFailedException) {
                     RealmsUploadFailedException var8 = (RealmsUploadFailedException)var6x;
                     LOGGER.warn("Failed to create realms world {}", var8.getStatusMessage());
                  } else {
                     LOGGER.warn("Failed to create realms world {}", var6x.getMessage());
                  }

                  var0.forceSetScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), var2));
               }
            } else {
               if (var1 instanceof RealmsConfigureWorldScreen) {
                  RealmsConfigureWorldScreen var9 = (RealmsConfigureWorldScreen)var1;
                  var9.fetchServerData(var4.id);
               }

               if (var5 != null) {
                  RealmsMainScreen.play(var4, var1, true);
               } else {
                  var0.forceSetScreen(var1);
               }

               RealmsMainScreen.refreshServerList();
            }

            return null;
         }, var0);
         return true;
      });
   }

   private static Path createTemporaryWorldFolder(LayeredRegistryAccess<RegistryLayer> var0, PrimaryLevelData var1, @Nullable Path var2) throws IOException {
      Path var3 = Files.createTempDirectory("minecraft_realms_world_upload");
      if (var2 != null) {
         Files.move(var2, var3.resolve("datapacks"));
      }

      CompoundTag var4 = var1.createTag(var0.compositeAccess(), (CompoundTag)null);
      CompoundTag var5 = new CompoundTag();
      var5.put("Data", var4);
      Path var6 = Files.createFile(var3.resolve("level.dat"));
      NbtIo.writeCompressed(var5, var6);
      return var3;
   }
}
