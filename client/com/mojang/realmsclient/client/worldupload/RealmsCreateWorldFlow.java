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

   public static void createWorld(Minecraft var0, Screen var1, Screen var2, RealmsServer var3, @Nullable RealmCreationTask var4) {
      CreateWorldScreen.openFresh(
         var0,
         var1,
         (var5, var6, var7, var8) -> {
            Path var9;
            try {
               var9 = createTemporaryWorldFolder(var6, var7, var8);
            } catch (IOException var12) {
               LOGGER.warn("Failed to create temporary world folder.");
               var0.setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), var2));
               return true;
            }

            RealmsWorldOptions var10 = RealmsWorldOptions.createFromSettings(var7.getLevelSettings(), SharedConstants.getCurrentVersion().getName());
            RealmsWorldUpload var11 = new RealmsWorldUpload(var9, var10, var0.getUser(), var3.id, var3.activeSlot, RealmsWorldUploadStatusTracker.noOp());
            var0.forceSetScreen(
               new AlertScreen(var11::cancel, Component.translatable("mco.create.world.reset.title"), Component.empty(), CommonComponents.GUI_CANCEL, false)
            );
            if (var4 != null) {
               var4.run();
            }

            var11.packAndUpload().handleAsync((var6x, var7x) -> {
               if (var7x != null) {
                  if (var7x instanceof CompletionException var8x) {
                     var7x = var8x.getCause();
                  }

                  if (var7x instanceof RealmsUploadCanceledException) {
                     var0.forceSetScreen(var2);
                  } else {
                     if (var7x instanceof RealmsUploadFailedException var9x) {
                        LOGGER.warn("Failed to create realms world {}", var9x.getStatusMessage());
                     } else {
                        LOGGER.warn("Failed to create realms world {}", var7x.getMessage());
                     }

                     var0.forceSetScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), var2));
                  }
               } else {
                  if (var1 instanceof RealmsConfigureWorldScreen var10x) {
                     var10x.saveSlotSettingsLocally(var10);
                  }

                  if (var4 != null) {
                     RealmsMainScreen.play(var3, var1);
                  } else {
                     var0.forceSetScreen(var1);
                  }
               }

               return null;
            }, var0);
            return true;
         }
      );
   }

   private static Path createTemporaryWorldFolder(LayeredRegistryAccess<RegistryLayer> var0, PrimaryLevelData var1, @Nullable Path var2) throws IOException {
      Path var3 = Files.createTempDirectory("minecraft_realms_world_upload");
      if (var2 != null) {
         Files.move(var2, var3.resolve("datapacks"));
      }

      CompoundTag var4 = var1.createTag(var0.compositeAccess(), null);
      CompoundTag var5 = new CompoundTag();
      var5.put("Data", var4);
      Path var6 = Files.createFile(var3.resolve("level.dat"));
      NbtIo.writeCompressed(var5, var6);
      return var3;
   }
}
