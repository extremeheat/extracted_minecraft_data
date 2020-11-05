package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class WorldGenSettingsComponent implements TickableWidget, Widget {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component CUSTOM_WORLD_DESCRIPTION = new TranslatableComponent("generator.custom");
   private static final Component AMPLIFIED_HELP_TEXT = new TranslatableComponent("generator.amplified.info");
   private static final Component MAP_FEATURES_INFO = new TranslatableComponent("selectWorld.mapFeatures.info");
   private MultiLineLabel amplifiedWorldInfo;
   private Font font;
   private int width;
   private EditBox seedEdit;
   private Button featuresButton;
   public Button bonusItemsButton;
   private Button typeButton;
   private Button customizeTypeButton;
   private Button importSettingsButton;
   private RegistryAccess.RegistryHolder registryHolder;
   private WorldGenSettings settings;
   private Optional<WorldPreset> preset;
   private OptionalLong seed;

   public WorldGenSettingsComponent(RegistryAccess.RegistryHolder var1, WorldGenSettings var2, Optional<WorldPreset> var3, OptionalLong var4) {
      super();
      this.amplifiedWorldInfo = MultiLineLabel.EMPTY;
      this.registryHolder = var1;
      this.settings = var2;
      this.preset = var3;
      this.seed = var4;
   }

   public void init(final CreateWorldScreen var1, Minecraft var2, Font var3) {
      this.font = var3;
      this.width = var1.width;
      this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TranslatableComponent("selectWorld.enterSeed"));
      this.seedEdit.setValue(toString(this.seed));
      this.seedEdit.setResponder((var1x) -> {
         this.seed = this.parseSeed();
      });
      var1.addWidget(this.seedEdit);
      int var4 = this.width / 2 - 155;
      int var5 = this.width / 2 + 5;
      this.featuresButton = (Button)var1.addButton(new Button(var4, 100, 150, 20, new TranslatableComponent("selectWorld.mapFeatures"), (var1x) -> {
         this.settings = this.settings.withFeaturesToggled();
         var1x.queueNarration(250);
      }) {
         public Component getMessage() {
            return CommonComponents.optionStatus(super.getMessage(), WorldGenSettingsComponent.this.settings.generateFeatures());
         }

         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append((Component)(new TranslatableComponent("selectWorld.mapFeatures.info")));
         }
      });
      this.featuresButton.visible = false;
      this.typeButton = (Button)var1.addButton(new Button(var5, 100, 150, 20, new TranslatableComponent("selectWorld.mapType"), (var2x) -> {
         while(true) {
            if (this.preset.isPresent()) {
               int var3 = WorldPreset.PRESETS.indexOf(this.preset.get()) + 1;
               if (var3 >= WorldPreset.PRESETS.size()) {
                  var3 = 0;
               }

               WorldPreset var4 = (WorldPreset)WorldPreset.PRESETS.get(var3);
               this.preset = Optional.of(var4);
               this.settings = var4.create(this.registryHolder, this.settings.seed(), this.settings.generateFeatures(), this.settings.generateBonusChest());
               if (this.settings.isDebug() && !Screen.hasShiftDown()) {
                  continue;
               }
            }

            var1.updateDisplayOptions();
            var2x.queueNarration(250);
            return;
         }
      }) {
         public Component getMessage() {
            return super.getMessage().copy().append(" ").append((Component)WorldGenSettingsComponent.this.preset.map(WorldPreset::description).orElse(WorldGenSettingsComponent.CUSTOM_WORLD_DESCRIPTION));
         }

         protected MutableComponent createNarrationMessage() {
            return Objects.equals(WorldGenSettingsComponent.this.preset, Optional.of(WorldPreset.AMPLIFIED)) ? super.createNarrationMessage().append(". ").append(WorldGenSettingsComponent.AMPLIFIED_HELP_TEXT) : super.createNarrationMessage();
         }
      });
      this.typeButton.visible = false;
      this.typeButton.active = this.preset.isPresent();
      this.customizeTypeButton = (Button)var1.addButton(new Button(var5, 120, 150, 20, new TranslatableComponent("selectWorld.customizeType"), (var3x) -> {
         WorldPreset.PresetEditor var4 = (WorldPreset.PresetEditor)WorldPreset.EDITORS.get(this.preset);
         if (var4 != null) {
            var2.setScreen(var4.createEditScreen(var1, this.settings));
         }

      }));
      this.customizeTypeButton.visible = false;
      this.bonusItemsButton = (Button)var1.addButton(new Button(var4, 151, 150, 20, new TranslatableComponent("selectWorld.bonusItems"), (var1x) -> {
         this.settings = this.settings.withBonusChestToggled();
         var1x.queueNarration(250);
      }) {
         public Component getMessage() {
            return CommonComponents.optionStatus(super.getMessage(), WorldGenSettingsComponent.this.settings.generateBonusChest() && !var1.hardCore);
         }
      });
      this.bonusItemsButton.visible = false;
      this.importSettingsButton = (Button)var1.addButton(new Button(var4, 185, 150, 20, new TranslatableComponent("selectWorld.import_worldgen_settings"), (var3x) -> {
         TranslatableComponent var4 = new TranslatableComponent("selectWorld.import_worldgen_settings.select_file");
         String var5 = TinyFileDialogs.tinyfd_openFileDialog(var4.getString(), (CharSequence)null, (PointerBuffer)null, (CharSequence)null, false);
         if (var5 != null) {
            RegistryAccess.RegistryHolder var6 = RegistryAccess.builtin();
            PackRepository var7 = new PackRepository(new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var1.getTempDataPackDir().toFile(), PackSource.WORLD)});

            ServerResources var8;
            try {
               MinecraftServer.configurePackRepository(var7, var1.dataPacks, false);
               CompletableFuture var9 = ServerResources.loadResources(var7.openAllSelected(), Commands.CommandSelection.INTEGRATED, 2, Util.backgroundExecutor(), var2);
               var2.managedBlock(var9::isDone);
               var8 = (ServerResources)var9.get();
            } catch (ExecutionException | InterruptedException var25) {
               LOGGER.error("Error loading data packs when importing world settings", var25);
               TranslatableComponent var10 = new TranslatableComponent("selectWorld.import_worldgen_settings.failure");
               TextComponent var11 = new TextComponent(var25.getMessage());
               var2.getToasts().addToast(SystemToast.multiline(var2, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, var10, var11));
               var7.close();
               return;
            }

            RegistryReadOps var28 = RegistryReadOps.create(JsonOps.INSTANCE, (ResourceManager)var8.getResourceManager(), var6);
            JsonParser var29 = new JsonParser();

            DataResult var30;
            try {
               BufferedReader var12 = Files.newBufferedReader(Paths.get(var5));
               Throwable var13 = null;

               try {
                  JsonElement var14 = var29.parse(var12);
                  var30 = WorldGenSettings.CODEC.parse(var28, var14);
               } catch (Throwable var24) {
                  var13 = var24;
                  throw var24;
               } finally {
                  if (var12 != null) {
                     if (var13 != null) {
                        try {
                           var12.close();
                        } catch (Throwable var23) {
                           var13.addSuppressed(var23);
                        }
                     } else {
                        var12.close();
                     }
                  }

               }
            } catch (JsonIOException | JsonSyntaxException | IOException var27) {
               var30 = DataResult.error("Failed to parse file: " + var27.getMessage());
            }

            if (var30.error().isPresent()) {
               TranslatableComponent var31 = new TranslatableComponent("selectWorld.import_worldgen_settings.failure");
               String var33 = ((PartialResult)var30.error().get()).message();
               LOGGER.error("Error parsing world settings: {}", var33);
               TextComponent var34 = new TextComponent(var33);
               var2.getToasts().addToast(SystemToast.multiline(var2, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, var31, var34));
            }

            var8.close();
            Lifecycle var32 = var30.lifecycle();
            Logger var10001 = LOGGER;
            var10001.getClass();
            var30.resultOrPartial(var10001::error).ifPresent((var5x) -> {
               BooleanConsumer var6x = (var5) -> {
                  var2.setScreen(var1);
                  if (var5) {
                     this.importSettings(var6, var5x);
                  }

               };
               if (var32 == Lifecycle.stable()) {
                  this.importSettings(var6, var5x);
               } else if (var32 == Lifecycle.experimental()) {
                  var2.setScreen(new ConfirmScreen(var6x, new TranslatableComponent("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableComponent("selectWorld.import_worldgen_settings.experimental.question")));
               } else {
                  var2.setScreen(new ConfirmScreen(var6x, new TranslatableComponent("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableComponent("selectWorld.import_worldgen_settings.deprecated.question")));
               }

            });
         }
      }));
      this.importSettingsButton.visible = false;
      this.amplifiedWorldInfo = MultiLineLabel.create(var3, AMPLIFIED_HELP_TEXT, this.typeButton.getWidth());
   }

   private void importSettings(RegistryAccess.RegistryHolder var1, WorldGenSettings var2) {
      this.registryHolder = var1;
      this.settings = var2;
      this.preset = WorldPreset.of(var2);
      this.seed = OptionalLong.of(var2.seed());
      this.seedEdit.setValue(toString(this.seed));
      this.typeButton.active = this.preset.isPresent();
   }

   public void tick() {
      this.seedEdit.tick();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.featuresButton.visible) {
         this.font.drawShadow(var1, MAP_FEATURES_INFO, (float)(this.width / 2 - 150), 122.0F, -6250336);
      }

      this.seedEdit.render(var1, var2, var3, var4);
      if (this.preset.equals(Optional.of(WorldPreset.AMPLIFIED))) {
         MultiLineLabel var10000 = this.amplifiedWorldInfo;
         int var10002 = this.typeButton.x + 2;
         int var10003 = this.typeButton.y + 22;
         this.font.getClass();
         var10000.renderLeftAligned(var1, var10002, var10003, 9, 10526880);
      }

   }

   protected void updateSettings(WorldGenSettings var1) {
      this.settings = var1;
   }

   private static String toString(OptionalLong var0) {
      return var0.isPresent() ? Long.toString(var0.getAsLong()) : "";
   }

   private static OptionalLong parseLong(String var0) {
      try {
         return OptionalLong.of(Long.parseLong(var0));
      } catch (NumberFormatException var2) {
         return OptionalLong.empty();
      }
   }

   public WorldGenSettings makeSettings(boolean var1) {
      OptionalLong var2 = this.parseSeed();
      return this.settings.withSeed(var1, var2);
   }

   private OptionalLong parseSeed() {
      String var1 = this.seedEdit.getValue();
      OptionalLong var2;
      if (StringUtils.isEmpty(var1)) {
         var2 = OptionalLong.empty();
      } else {
         OptionalLong var3 = parseLong(var1);
         if (var3.isPresent() && var3.getAsLong() != 0L) {
            var2 = var3;
         } else {
            var2 = OptionalLong.of((long)var1.hashCode());
         }
      }

      return var2;
   }

   public boolean isDebug() {
      return this.settings.isDebug();
   }

   public void setDisplayOptions(boolean var1) {
      this.typeButton.visible = var1;
      if (this.settings.isDebug()) {
         this.featuresButton.visible = false;
         this.bonusItemsButton.visible = false;
         this.customizeTypeButton.visible = false;
         this.importSettingsButton.visible = false;
      } else {
         this.featuresButton.visible = var1;
         this.bonusItemsButton.visible = var1;
         this.customizeTypeButton.visible = var1 && WorldPreset.EDITORS.containsKey(this.preset);
         this.importSettingsButton.visible = var1;
      }

      this.seedEdit.setVisible(var1);
   }

   public RegistryAccess.RegistryHolder registryHolder() {
      return this.registryHolder;
   }

   void updateDataPacks(ServerResources var1) {
      RegistryAccess.RegistryHolder var2 = RegistryAccess.builtin();
      RegistryWriteOps var3 = RegistryWriteOps.create(JsonOps.INSTANCE, this.registryHolder);
      RegistryReadOps var4 = RegistryReadOps.create(JsonOps.INSTANCE, (ResourceManager)var1.getResourceManager(), var2);
      DataResult var5 = WorldGenSettings.CODEC.encodeStart(var3, this.settings).flatMap((var1x) -> {
         return WorldGenSettings.CODEC.parse(var4, var1x);
      });
      Logger var10002 = LOGGER;
      var10002.getClass();
      var5.resultOrPartial(Util.prefix("Error parsing worldgen settings after loading data packs: ", var10002::error)).ifPresent((var2x) -> {
         this.settings = var2x;
         this.registryHolder = var2;
      });
   }
}
