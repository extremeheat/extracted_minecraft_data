package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson WORLD_GEN_SETTINGS_GSON = (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
   private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
   private Button renameButton;
   private final BooleanConsumer callback;
   private EditBox nameEdit;
   private final LevelStorageSource.LevelStorageAccess levelAccess;

   public EditWorldScreen(BooleanConsumer var1, LevelStorageSource.LevelStorageAccess var2) {
      super(new TranslatableComponent("selectWorld.edit.title"));
      this.callback = var1;
      this.levelAccess = var2;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      Button var1 = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.resetIcon"), (var1x) -> {
         FileUtils.deleteQuietly(this.levelAccess.getIconFile());
         var1x.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.openFolder"), (var1x) -> {
         Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backup"), (var1x) -> {
         boolean var2 = makeBackupAndShowToast(this.levelAccess);
         this.callback.accept(!var2);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backupFolder"), (var1x) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         Path var3 = var2.getBackupPath();

         try {
            Files.createDirectories(Files.exists(var3, new LinkOption[0]) ? var3.toRealPath() : var3);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }

         Util.getPlatform().openFile(var3.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.optimize"), (var1x) -> {
         this.minecraft.setScreen(new BackupConfirmScreen(this, (var1, var2) -> {
            if (var1) {
               makeBackupAndShowToast(this.levelAccess);
            }

            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, var2));
         }, new TranslatableComponent("optimizeWorld.confirm.title"), new TranslatableComponent("optimizeWorld.confirm.description"), true));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.export_worldgen_settings"), (var1x) -> {
         RegistryAccess.RegistryHolder var3 = RegistryAccess.builtin();

         DataResult var2;
         try {
            Minecraft.ServerStem var4 = this.minecraft.makeServerStem(var3, Minecraft::loadDataPacks, Minecraft::loadWorldData, false, this.levelAccess);
            Throwable var5 = null;

            try {
               RegistryWriteOps var6 = RegistryWriteOps.create(JsonOps.INSTANCE, var3);
               DataResult var7 = WorldGenSettings.CODEC.encodeStart(var6, var4.worldData().worldGenSettings());
               var2 = var7.flatMap((var1) -> {
                  Path var2 = this.levelAccess.getLevelPath(LevelResource.ROOT).resolve("worldgen_settings_export.json");

                  try {
                     JsonWriter var3 = WORLD_GEN_SETTINGS_GSON.newJsonWriter(Files.newBufferedWriter(var2, StandardCharsets.UTF_8));
                     Throwable var4 = null;

                     try {
                        WORLD_GEN_SETTINGS_GSON.toJson(var1, var3);
                     } catch (Throwable var14) {
                        var4 = var14;
                        throw var14;
                     } finally {
                        if (var3 != null) {
                           if (var4 != null) {
                              try {
                                 var3.close();
                              } catch (Throwable var13) {
                                 var4.addSuppressed(var13);
                              }
                           } else {
                              var3.close();
                           }
                        }

                     }
                  } catch (JsonIOException | IOException var16) {
                     return DataResult.error("Error writing file: " + var16.getMessage());
                  }

                  return DataResult.success(var2.toString());
               });
            } catch (Throwable var16) {
               var5 = var16;
               throw var16;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var15) {
                        var5.addSuppressed(var15);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (ExecutionException | InterruptedException var18) {
            var2 = DataResult.error("Could not parse level data!");
         }

         TextComponent var19 = new TextComponent((String)var2.get().map(Function.identity(), PartialResult::message));
         TranslatableComponent var20 = new TranslatableComponent(var2.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
         var2.error().ifPresent((var0) -> {
            LOGGER.error("Error exporting world settings: {}", var0);
         });
         this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, var20, var19));
      }));
      this.renameButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableComponent("selectWorld.edit.save"), (var1x) -> {
         this.onRename();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.callback.accept(false);
      }));
      var1.active = this.levelAccess.getIconFile().isFile();
      LevelSummary var2 = this.levelAccess.getSummary();
      String var3 = var2 == null ? "" : var2.getLevelName();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, new TranslatableComponent("selectWorld.enterName"));
      this.nameEdit.setValue(var3);
      this.nameEdit.setResponder((var1x) -> {
         this.renameButton.active = !var1x.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.nameEdit.getValue();
      this.init(var1, var2, var3);
      this.nameEdit.setValue(var4);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onRename() {
      try {
         this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
         this.callback.accept(true);
      } catch (IOException var2) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), var2);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
         this.callback.accept(true);
      }

   }

   public static void makeBackupAndShowToast(LevelStorageSource var0, String var1) {
      boolean var2 = false;

      try {
         LevelStorageSource.LevelStorageAccess var3 = var0.createAccess(var1);
         Throwable var4 = null;

         try {
            var2 = true;
            makeBackupAndShowToast(var3);
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var16) {
         if (!var2) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), var1);
         }

         LOGGER.warn("Failed to create backup of level {}", var1, var16);
      }

   }

   public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess var0) {
      long var1 = 0L;
      IOException var3 = null;

      try {
         var1 = var0.makeWorldBackup();
      } catch (IOException var6) {
         var3 = var6;
      }

      TranslatableComponent var4;
      if (var3 != null) {
         var4 = new TranslatableComponent("selectWorld.edit.backupFailed");
         TextComponent var7 = new TextComponent(var3.getMessage());
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, var4, var7));
         return false;
      } else {
         var4 = new TranslatableComponent("selectWorld.edit.backupCreated", new Object[]{var0.getLevelId()});
         TranslatableComponent var5 = new TranslatableComponent("selectWorld.edit.backupSize", new Object[]{Mth.ceil((double)var1 / 1048576.0D)});
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, var4, var5));
         return true;
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      drawString(var1, this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
      this.nameEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
