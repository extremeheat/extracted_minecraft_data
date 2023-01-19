package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class CreateWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TEMP_WORLD_PREFIX = "mcworld-";
   private static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
   private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
   private static final Component SEED_INFO = Component.translatable("selectWorld.seedInfo");
   private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
   private static final Component OUTPUT_DIR_INFO = Component.translatable("selectWorld.resultFolder");
   private static final Component COMMANDS_INFO = Component.translatable("selectWorld.allowCommands.info");
   private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
   @Nullable
   private final Screen lastScreen;
   private EditBox nameEdit;
   String resultFolder;
   private CreateWorldScreen.SelectedGameMode gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
   @Nullable
   private CreateWorldScreen.SelectedGameMode oldGameMode;
   private Difficulty difficulty = Difficulty.NORMAL;
   private boolean commands;
   private boolean commandsChanged;
   public boolean hardCore;
   protected WorldDataConfiguration dataConfiguration;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private PackRepository tempDataPackRepository;
   private boolean worldGenSettingsVisible;
   private Button createButton;
   private CycleButton<CreateWorldScreen.SelectedGameMode> modeButton;
   private CycleButton<Difficulty> difficultyButton;
   private Button moreOptionsButton;
   private Button gameRulesButton;
   private Button dataPacksButton;
   private CycleButton<Boolean> commandsButton;
   private Component gameModeHelp1;
   private Component gameModeHelp2;
   private String initName;
   private GameRules gameRules = new GameRules();
   public final WorldGenSettingsComponent worldGenSettingsComponent;

   public static void openFresh(Minecraft var0, @Nullable Screen var1) {
      queueLoadScreen(var0, PREPARING_WORLD_DATA);
      PackRepository var2 = new PackRepository(new ServerPacksSource());
      WorldLoader.InitConfig var3 = createDefaultLoadConfig(var2, WorldDataConfiguration.DEFAULT);
      CompletableFuture var4 = WorldLoader.load(
         var3,
         var0x -> new WorldLoader.DataLoadOutput<>(
               new CreateWorldScreen.DataPackReloadCookie(
                  new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), WorldPresets.createNormalWorldDimensions(var0x.datapackWorldgen())),
                  var0x.dataConfiguration()
               ),
               var0x.datapackDimensions()
            ),
         (var0x, var1x, var2x, var3x) -> {
            var0x.close();
            return new WorldCreationContext(var3x.worldGenSettings(), var2x, var1x, var3x.dataConfiguration());
         },
         Util.backgroundExecutor(),
         var0
      );
      var0.managedBlock(var4::isDone);
      var0.setScreen(
         new CreateWorldScreen(
            var1,
            WorldDataConfiguration.DEFAULT,
            new WorldGenSettingsComponent((WorldCreationContext)var4.join(), Optional.of(WorldPresets.NORMAL), OptionalLong.empty())
         )
      );
   }

   public static CreateWorldScreen createFromExisting(@Nullable Screen var0, LevelSettings var1, WorldCreationContext var2, @Nullable Path var3) {
      CreateWorldScreen var4 = new CreateWorldScreen(
         var0,
         var2.dataConfiguration(),
         new WorldGenSettingsComponent(var2, WorldPresets.fromSettings(var2.selectedDimensions().dimensions()), OptionalLong.of(var2.options().seed()))
      );
      var4.initName = var1.levelName();
      var4.commands = var1.allowCommands();
      var4.commandsChanged = true;
      var4.difficulty = var1.difficulty();
      var4.gameRules.assignFrom(var1.gameRules(), null);
      if (var1.hardcore()) {
         var4.gameMode = CreateWorldScreen.SelectedGameMode.HARDCORE;
      } else if (var1.gameType().isSurvival()) {
         var4.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      } else if (var1.gameType().isCreative()) {
         var4.gameMode = CreateWorldScreen.SelectedGameMode.CREATIVE;
      }

      var4.tempDataPackDir = var3;
      return var4;
   }

   private CreateWorldScreen(@Nullable Screen var1, WorldDataConfiguration var2, WorldGenSettingsComponent var3) {
      super(Component.translatable("selectWorld.create"));
      this.lastScreen = var1;
      this.initName = I18n.get("selectWorld.newWorld");
      this.dataConfiguration = var2;
      this.worldGenSettingsComponent = var3;
   }

   @Override
   public void tick() {
      this.nameEdit.tick();
      this.worldGenSettingsComponent.tick();
   }

   @Override
   protected void init() {
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, Component.translatable("selectWorld.enterName")) {
         @Override
         protected MutableComponent createNarrationMessage() {
            return CommonComponents.joinForNarration(super.createNarrationMessage(), Component.translatable("selectWorld.resultFolder"))
               .append(CommonComponents.SPACE)
               .append(CreateWorldScreen.this.resultFolder);
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder(var1x -> {
         this.initName = var1x;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.addWidget(this.nameEdit);
      int var1 = this.width / 2 - 155;
      int var2 = this.width / 2 + 5;
      this.modeButton = this.addRenderableWidget(
         CycleButton.builder(CreateWorldScreen.SelectedGameMode::getDisplayName)
            .withValues(CreateWorldScreen.SelectedGameMode.SURVIVAL, CreateWorldScreen.SelectedGameMode.HARDCORE, CreateWorldScreen.SelectedGameMode.CREATIVE)
            .withInitialValue(this.gameMode)
            .withCustomNarration(
               var1x -> AbstractWidget.wrapDefaultNarrationMessage(var1x.getMessage())
                     .append(CommonComponents.NARRATION_SEPARATOR)
                     .append(this.gameModeHelp1)
                     .append(CommonComponents.SPACE)
                     .append(this.gameModeHelp2)
            )
            .create(var1, 100, 150, 20, GAME_MODEL_LABEL, (var1x, var2x) -> this.setGameMode(var2x))
      );
      this.difficultyButton = this.addRenderableWidget(
         CycleButton.builder(Difficulty::getDisplayName)
            .withValues(Difficulty.values())
            .withInitialValue(this.getEffectiveDifficulty())
            .create(var2, 100, 150, 20, Component.translatable("options.difficulty"), (var1x, var2x) -> this.difficulty = var2x)
      );
      this.commandsButton = this.addRenderableWidget(
         CycleButton.onOffBuilder(this.commands && !this.hardCore)
            .withCustomNarration(
               var0 -> CommonComponents.joinForNarration(var0.createDefaultNarrationMessage(), Component.translatable("selectWorld.allowCommands.info"))
            )
            .create(var1, 151, 150, 20, Component.translatable("selectWorld.allowCommands"), (var1x, var2x) -> {
               this.commandsChanged = true;
               this.commands = var2x;
            })
      );
      this.dataPacksButton = this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.dataPacks"), var1x -> this.openDataPackSelectionScreen()).bounds(var2, 151, 150, 20).build()
      );
      this.gameRulesButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.gameRules"), var1x -> this.minecraft.setScreen(new EditGameRulesScreen(this.gameRules.copy(), var1xx -> {
                     this.minecraft.setScreen(this);
                     var1xx.ifPresent(var1xxx -> this.gameRules = var1xxx);
                  }))
            )
            .bounds(var1, 185, 150, 20)
            .build()
      );
      this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
      this.moreOptionsButton = this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.moreWorldOptions"), var1x -> this.toggleWorldGenSettingsVisibility())
            .bounds(var2, 185, 150, 20)
            .build()
      );
      this.createButton = this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.create"), var1x -> this.onCreate()).bounds(var1, this.height - 28, 150, 20).build()
      );
      this.createButton.active = !this.initName.isEmpty();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.popScreen()).bounds(var2, this.height - 28, 150, 20).build());
      this.refreshWorldGenSettingsVisibility();
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private Difficulty getEffectiveDifficulty() {
      return this.gameMode == CreateWorldScreen.SelectedGameMode.HARDCORE ? Difficulty.HARD : this.difficulty;
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = Component.translatable("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = Component.translatable("selectWorld.gameMode." + this.gameMode.name + ".line2");
   }

   private void updateResultFolder() {
      this.resultFolder = this.nameEdit.getValue().trim();
      if (this.resultFolder.isEmpty()) {
         this.resultFolder = "World";
      }

      try {
         this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
      } catch (Exception var4) {
         this.resultFolder = "World";

         try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
         } catch (Exception var3) {
            throw new RuntimeException("Could not create save folder", var3);
         }
      }
   }

   private static void queueLoadScreen(Minecraft var0, Component var1) {
      var0.forceSetScreen(new GenericDirtMessageScreen(var1));
   }

   private void onCreate() {
      WorldCreationContext var1 = this.worldGenSettingsComponent.settings();
      WorldDimensions.Complete var2 = var1.selectedDimensions().bake(var1.datapackDimensions());
      LayeredRegistryAccess var3 = var1.worldgenRegistries().replaceFrom(RegistryLayer.DIMENSIONS, var2.dimensionsRegistryAccess());
      Lifecycle var4 = FeatureFlags.isExperimental(var1.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
      Lifecycle var5 = var3.compositeAccess().allRegistriesLifecycle();
      Lifecycle var6 = var5.add(var4);
      WorldOpenFlows.confirmWorldCreation(this.minecraft, this, var6, () -> this.createNewWorld(var2.specialWorldProperty(), var3, var6));
   }

   private void createNewWorld(PrimaryLevelData.SpecialWorldProperty var1, LayeredRegistryAccess<RegistryLayer> var2, Lifecycle var3) {
      queueLoadScreen(this.minecraft, PREPARING_WORLD_DATA);
      Optional var4 = this.createNewWorldDirectory();
      if (!var4.isEmpty()) {
         this.removeTempDataPackDir();
         boolean var5 = var1 == PrimaryLevelData.SpecialWorldProperty.DEBUG;
         WorldCreationContext var6 = this.worldGenSettingsComponent.settings();
         WorldOptions var7 = this.worldGenSettingsComponent.createFinalOptions(var5, this.hardCore);
         LevelSettings var8 = this.createLevelSettings(var5);
         PrimaryLevelData var9 = new PrimaryLevelData(var8, var7, var1, var3);
         this.minecraft
            .createWorldOpenFlows()
            .createLevelFromExistingSettings((LevelStorageSource.LevelStorageAccess)var4.get(), var6.dataPackResources(), var2, var9);
      }
   }

   private LevelSettings createLevelSettings(boolean var1) {
      String var2 = this.nameEdit.getValue().trim();
      if (var1) {
         GameRules var3 = new GameRules();
         var3.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
         return new LevelSettings(var2, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, var3, WorldDataConfiguration.DEFAULT);
      } else {
         return new LevelSettings(
            var2,
            this.gameMode.gameType,
            this.hardCore,
            this.getEffectiveDifficulty(),
            this.commands && !this.hardCore,
            this.gameRules,
            this.dataConfiguration
         );
      }
   }

   private void toggleWorldGenSettingsVisibility() {
      this.setWorldGenSettingsVisible(!this.worldGenSettingsVisible);
   }

   private void setGameMode(CreateWorldScreen.SelectedGameMode var1) {
      if (!this.commandsChanged) {
         this.commands = var1 == CreateWorldScreen.SelectedGameMode.CREATIVE;
         this.commandsButton.setValue(this.commands);
      }

      if (var1 == CreateWorldScreen.SelectedGameMode.HARDCORE) {
         this.hardCore = true;
         this.commandsButton.active = false;
         this.commandsButton.setValue(false);
         this.worldGenSettingsComponent.switchToHardcore();
         this.difficultyButton.setValue(Difficulty.HARD);
         this.difficultyButton.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.commandsButton.setValue(this.commands);
         this.worldGenSettingsComponent.switchOutOfHardcode();
         this.difficultyButton.setValue(this.difficulty);
         this.difficultyButton.active = true;
      }

      this.gameMode = var1;
      this.updateGameModeHelp();
   }

   public void refreshWorldGenSettingsVisibility() {
      this.setWorldGenSettingsVisible(this.worldGenSettingsVisible);
   }

   private void setWorldGenSettingsVisible(boolean var1) {
      this.worldGenSettingsVisible = var1;
      this.modeButton.visible = !var1;
      this.difficultyButton.visible = !var1;
      if (this.worldGenSettingsComponent.isDebug()) {
         this.dataPacksButton.visible = false;
         this.modeButton.active = false;
         if (this.oldGameMode == null) {
            this.oldGameMode = this.gameMode;
         }

         this.setGameMode(CreateWorldScreen.SelectedGameMode.DEBUG);
         this.commandsButton.visible = false;
      } else {
         this.modeButton.active = true;
         if (this.oldGameMode != null) {
            this.setGameMode(this.oldGameMode);
         }

         this.commandsButton.visible = !var1;
         this.dataPacksButton.visible = !var1;
      }

      this.worldGenSettingsComponent.setVisibility(var1);
      this.nameEdit.setVisible(!var1);
      if (var1) {
         this.moreOptionsButton.setMessage(CommonComponents.GUI_DONE);
      } else {
         this.moreOptionsButton.setMessage(Component.translatable("selectWorld.moreWorldOptions"));
      }

      this.gameRulesButton.visible = !var1;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   @Override
   public void onClose() {
      if (this.worldGenSettingsVisible) {
         this.setWorldGenSettingsVisible(false);
      } else {
         this.popScreen();
      }
   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.removeTempDataPackDir();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 20, -1);
      if (this.worldGenSettingsVisible) {
         drawString(var1, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(var1, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
         this.worldGenSettingsComponent.render(var1, var2, var3, var4);
      } else {
         drawString(var1, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(
            var1,
            this.font,
            Component.empty().append(OUTPUT_DIR_INFO).append(CommonComponents.SPACE).append(this.resultFolder),
            this.width / 2 - 100,
            85,
            -6250336
         );
         this.nameEdit.render(var1, var2, var3, var4);
         drawString(var1, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
         drawString(var1, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
         if (this.commandsButton.visible) {
            drawString(var1, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
         }
      }

      super.render(var1, var2, var3, var4);
   }

   @Override
   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T var1) {
      return super.addWidget((T)var1);
   }

   @Override
   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T var1) {
      return super.addRenderableWidget((T)var1);
   }

   @Nullable
   private Path getTempDataPackDir() {
      if (this.tempDataPackDir == null) {
         try {
            this.tempDataPackDir = Files.createTempDirectory("mcworld-");
         } catch (IOException var2) {
            LOGGER.warn("Failed to create temporary dir", var2);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
         }
      }

      return this.tempDataPackDir;
   }

   private void openDataPackSelectionScreen() {
      Pair var1 = this.getDataPackSelectionSettings();
      if (var1 != null) {
         this.minecraft
            .setScreen(
               new PackSelectionScreen(
                  this, (PackRepository)var1.getSecond(), this::tryApplyNewDataPacks, (Path)var1.getFirst(), Component.translatable("dataPack.title")
               )
            );
      }
   }

   private void tryApplyNewDataPacks(PackRepository var1) {
      ImmutableList var2 = ImmutableList.copyOf(var1.getSelectedIds());
      List var3 = var1.getAvailableIds().stream().filter(var1x -> !var2.contains(var1x)).collect(ImmutableList.toImmutableList());
      WorldDataConfiguration var4 = new WorldDataConfiguration(new DataPackConfig(var2, var3), this.dataConfiguration.enabledFeatures());
      if (var2.equals(this.dataConfiguration.dataPacks().getEnabled())) {
         this.dataConfiguration = var4;
      } else {
         FeatureFlagSet var5 = var1.getRequestedFeatureFlags();
         if (FeatureFlags.isExperimental(var5)) {
            this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen(var1.getSelectedPacks(), var3x -> {
                  if (var3x) {
                     this.applyNewPackConfig(var1, var4);
                  } else {
                     this.openDataPackSelectionScreen();
                  }
               })));
         } else {
            this.applyNewPackConfig(var1, var4);
         }
      }
   }

   private void applyNewPackConfig(PackRepository var1, WorldDataConfiguration var2) {
      this.minecraft.tell(() -> this.minecraft.setScreen(new GenericDirtMessageScreen(Component.translatable("dataPack.validation.working"))));
      WorldLoader.InitConfig var3 = createDefaultLoadConfig(var1, var2);
      WorldLoader.<CreateWorldScreen.DataPackReloadCookie, WorldCreationContext>load(
            var3,
            var1x -> {
               if (var1x.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).size() == 0) {
                  throw new IllegalStateException("Needs at least one world preset to continue");
               } else if (var1x.datapackWorldgen().registryOrThrow(Registries.BIOME).size() == 0) {
                  throw new IllegalStateException("Needs at least one biome continue");
               } else {
                  WorldCreationContext var2x = this.worldGenSettingsComponent.settings();
                  RegistryOps var3x = RegistryOps.create(JsonOps.INSTANCE, var2x.worldgenLoadContext());
                  DataResult var4 = WorldGenSettings.encode(var3x, var2x.options(), var2x.selectedDimensions()).setLifecycle(Lifecycle.stable());
                  RegistryOps var5 = RegistryOps.create(JsonOps.INSTANCE, var1x.datapackWorldgen());
                  WorldGenSettings var6 = (WorldGenSettings)var4.flatMap(var1xx -> WorldGenSettings.CODEC.parse(var5, var1xx))
                     .getOrThrow(false, Util.prefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
                  return new WorldLoader.DataLoadOutput<>(
                     new CreateWorldScreen.DataPackReloadCookie(var6, var1x.dataConfiguration()), var1x.datapackDimensions()
                  );
               }
            },
            (var0, var1x, var2x, var3x) -> {
               var0.close();
               return new WorldCreationContext(var3x.worldGenSettings(), var2x, var1x, var3x.dataConfiguration());
            },
            Util.backgroundExecutor(),
            this.minecraft
         )
         .thenAcceptAsync(var1x -> {
            this.dataConfiguration = var1x.dataConfiguration();
            this.worldGenSettingsComponent.updateSettings(var1x);
            this.rebuildWidgets();
         }, this.minecraft)
         .handle(
            (var1x, var2x) -> {
               if (var2x != null) {
                  LOGGER.warn("Failed to validate datapack", var2x);
                  this.minecraft
                     .tell(
                        () -> this.minecraft
                              .setScreen(
                                 new ConfirmScreen(
                                    var1xx -> {
                                       if (var1xx) {
                                          this.openDataPackSelectionScreen();
                                       } else {
                                          this.dataConfiguration = WorldDataConfiguration.DEFAULT;
                                          this.minecraft.setScreen(this);
                                       }
                                    },
                                    Component.translatable("dataPack.validation.failed"),
                                    CommonComponents.EMPTY,
                                    Component.translatable("dataPack.validation.back"),
                                    Component.translatable("dataPack.validation.reset")
                                 )
                              )
                     );
               } else {
                  this.minecraft.tell(() -> this.minecraft.setScreen(this));
               }
      
               return null;
            }
         );
   }

   private static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository var0, WorldDataConfiguration var1) {
      WorldLoader.PackConfig var2 = new WorldLoader.PackConfig(var0, var1, false, true);
      return new WorldLoader.InitConfig(var2, Commands.CommandSelection.INTEGRATED, 2);
   }

   private void removeTempDataPackDir() {
      if (this.tempDataPackDir != null) {
         try (Stream var1 = Files.walk(this.tempDataPackDir)) {
            var1.sorted(Comparator.reverseOrder()).forEach(var0 -> {
               try {
                  Files.delete(var0);
               } catch (IOException var2) {
                  LOGGER.warn("Failed to remove temporary file {}", var0, var2);
               }
            });
         } catch (IOException var6) {
            LOGGER.warn("Failed to list temporary dir {}", this.tempDataPackDir);
         }

         this.tempDataPackDir = null;
      }
   }

   private static void copyBetweenDirs(Path var0, Path var1, Path var2) {
      try {
         Util.copyBetweenDirs(var0, var1, var2);
      } catch (IOException var4) {
         LOGGER.warn("Failed to copy datapack file from {} to {}", var2, var1);
         throw new UncheckedIOException(var4);
      }
   }

   private Optional<LevelStorageSource.LevelStorageAccess> createNewWorldDirectory() {
      try {
         LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().createAccess(this.resultFolder);
         if (this.tempDataPackDir == null) {
            return Optional.of(var1);
         }

         try {
            Optional var4;
            try (Stream var2 = Files.walk(this.tempDataPackDir)) {
               Path var3 = var1.getLevelPath(LevelResource.DATAPACK_DIR);
               FileUtil.createDirectoriesSafe(var3);
               var2.filter(var1x -> !var1x.equals(this.tempDataPackDir)).forEach(var2x -> copyBetweenDirs(this.tempDataPackDir, var3, var2x));
               var4 = Optional.of(var1);
            }

            return var4;
         } catch (UncheckedIOException | IOException var7) {
            LOGGER.warn("Failed to copy datapacks to world {}", this.resultFolder, var7);
            var1.close();
         }
      } catch (UncheckedIOException | IOException var8) {
         LOGGER.warn("Failed to create access for {}", this.resultFolder, var8);
      }

      SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
      this.popScreen();
      return Optional.empty();
   }

   @Nullable
   public static Path createTempDataPackDirFromExistingWorld(Path var0, Minecraft var1) {
      MutableObject var2 = new MutableObject();

      try (Stream var3 = Files.walk(var0)) {
         var3.filter(var1x -> !var1x.equals(var0)).forEach(var2x -> {
            Path var3x = (Path)var2.getValue();
            if (var3x == null) {
               try {
                  var3x = Files.createTempDirectory("mcworld-");
               } catch (IOException var5) {
                  LOGGER.warn("Failed to create temporary dir");
                  throw new UncheckedIOException(var5);
               }

               var2.setValue(var3x);
            }

            copyBetweenDirs(var0, var3x, var2x);
         });
      } catch (UncheckedIOException | IOException var8) {
         LOGGER.warn("Failed to copy datapacks from world {}", var0, var8);
         SystemToast.onPackCopyFailure(var1, var0.toString());
         return null;
      }

      return (Path)var2.getValue();
   }

   @Nullable
   private Pair<Path, PackRepository> getDataPackSelectionSettings() {
      Path var1 = this.getTempDataPackDir();
      if (var1 != null) {
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = ServerPacksSource.createPackRepository(var1);
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(this.dataConfiguration.dataPacks().getEnabled());
         return Pair.of(var1, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   static record DataPackReloadCookie(WorldGenSettings a, WorldDataConfiguration b) {
      private final WorldGenSettings worldGenSettings;
      private final WorldDataConfiguration dataConfiguration;

      DataPackReloadCookie(WorldGenSettings var1, WorldDataConfiguration var2) {
         super();
         this.worldGenSettings = var1;
         this.dataConfiguration = var2;
      }
   }

   static enum SelectedGameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      final String name;
      final GameType gameType;
      private final Component displayName;

      private SelectedGameMode(String var3, GameType var4) {
         this.name = var3;
         this.gameType = var4;
         this.displayName = Component.translatable("selectWorld.gameMode." + var3);
      }

      public Component getDisplayName() {
         return this.displayName;
      }
   }
}
