package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
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
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class CreateWorldScreen extends Screen {
   private static final int GROUP_BOTTOM = 1;
   private static final int TAB_COLUMN_WIDTH = 210;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TEMP_WORLD_PREFIX = "mcworld-";
   static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
   static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
   static final Component EXPERIMENTS_LABEL = Component.translatable("selectWorld.experiments");
   static final Component ALLOW_COMMANDS_INFO = Component.translatable("selectWorld.allowCommands.info");
   private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
   private static final int HORIZONTAL_BUTTON_SPACING = 10;
   private static final int VERTICAL_BUTTON_SPACING = 8;
   public static final ResourceLocation TAB_HEADER_BACKGROUND = new ResourceLocation("textures/gui/tab_header_background.png");
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   final WorldCreationUiState uiState;
   private final TabManager tabManager = new TabManager(this::addRenderableWidget, var1x -> this.removeWidget(var1x));
   private boolean recreated;
   private final DirectoryValidator packValidator;
   @Nullable
   private final Screen lastScreen;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private PackRepository tempDataPackRepository;
   @Nullable
   private TabNavigationBar tabNavigationBar;

   public static void openFresh(Minecraft var0, @Nullable Screen var1) {
      queueLoadScreen(var0, PREPARING_WORLD_DATA);
      PackRepository var2 = new PackRepository(new ServerPacksSource(var0.directoryValidator()));
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
      var0.setScreen(new CreateWorldScreen(var0, var1, (WorldCreationContext)var4.join(), Optional.of(WorldPresets.NORMAL), OptionalLong.empty()));
   }

   public static CreateWorldScreen createFromExisting(Minecraft var0, @Nullable Screen var1, LevelSettings var2, WorldCreationContext var3, @Nullable Path var4) {
      CreateWorldScreen var5 = new CreateWorldScreen(
         var0, var1, var3, WorldPresets.fromSettings(var3.selectedDimensions()), OptionalLong.of(var3.options().seed())
      );
      var5.recreated = true;
      var5.uiState.setName(var2.levelName());
      var5.uiState.setAllowCommands(var2.allowCommands());
      var5.uiState.setDifficulty(var2.difficulty());
      var5.uiState.getGameRules().assignFrom(var2.gameRules(), null);
      if (var2.hardcore()) {
         var5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
      } else if (var2.gameType().isSurvival()) {
         var5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
      } else if (var2.gameType().isCreative()) {
         var5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
      }

      var5.tempDataPackDir = var4;
      return var5;
   }

   private CreateWorldScreen(Minecraft var1, @Nullable Screen var2, WorldCreationContext var3, Optional<ResourceKey<WorldPreset>> var4, OptionalLong var5) {
      super(Component.translatable("selectWorld.create"));
      this.lastScreen = var2;
      this.packValidator = var1.directoryValidator();
      this.uiState = new WorldCreationUiState(var1.getLevelSource().getBaseDir(), var3, var4, var5);
   }

   public WorldCreationUiState getUiState() {
      return this.uiState;
   }

   @Override
   protected void init() {
      this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
         .addTabs(new CreateWorldScreen.GameTab(), new CreateWorldScreen.WorldTab(), new CreateWorldScreen.MoreTab())
         .build();
      this.addRenderableWidget(this.tabNavigationBar);
      LinearLayout var1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(Component.translatable("selectWorld.create"), var1x -> this.onCreate()).build());
      var1.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.popScreen()).build());
      this.layout.visitWidgets(var1x -> {
         var1x.setTabOrderGroup(1);
         this.addRenderableWidget(var1x);
      });
      this.tabNavigationBar.selectTab(0, false);
      this.uiState.onChanged();
      this.repositionElements();
   }

   @Override
   protected void setInitialFocus() {
   }

   @Override
   public void repositionElements() {
      if (this.tabNavigationBar != null) {
         this.tabNavigationBar.setWidth(this.width);
         this.tabNavigationBar.arrangeElements();
         int var1 = this.tabNavigationBar.getRectangle().bottom();
         ScreenRectangle var2 = new ScreenRectangle(0, var1, this.width, this.height - this.layout.getFooterHeight() - var1);
         this.tabManager.setTabArea(var2);
         this.layout.setHeaderHeight(var1);
         this.layout.arrangeElements();
      }
   }

   private static void queueLoadScreen(Minecraft var0, Component var1) {
      var0.forceSetScreen(new GenericMessageScreen(var1));
   }

   private void onCreate() {
      WorldCreationContext var1 = this.uiState.getSettings();
      WorldDimensions.Complete var2 = var1.selectedDimensions().bake(var1.datapackDimensions());
      LayeredRegistryAccess var3 = var1.worldgenRegistries().replaceFrom(RegistryLayer.DIMENSIONS, var2.dimensionsRegistryAccess());
      Lifecycle var4 = FeatureFlags.isExperimental(var1.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
      Lifecycle var5 = var3.compositeAccess().allRegistriesLifecycle();
      Lifecycle var6 = var5.add(var4);
      boolean var7 = !this.recreated && var5 == Lifecycle.stable();
      WorldOpenFlows.confirmWorldCreation(this.minecraft, this, var6, () -> this.createNewWorld(var2.specialWorldProperty(), var3, var6), var7);
   }

   private void createNewWorld(PrimaryLevelData.SpecialWorldProperty var1, LayeredRegistryAccess<RegistryLayer> var2, Lifecycle var3) {
      queueLoadScreen(this.minecraft, PREPARING_WORLD_DATA);
      Optional var4 = this.createNewWorldDirectory();
      if (!var4.isEmpty()) {
         this.removeTempDataPackDir();
         boolean var5 = var1 == PrimaryLevelData.SpecialWorldProperty.DEBUG;
         WorldCreationContext var6 = this.uiState.getSettings();
         LevelSettings var7 = this.createLevelSettings(var5);
         PrimaryLevelData var8 = new PrimaryLevelData(var7, var6.options(), var1, var3);
         this.minecraft
            .createWorldOpenFlows()
            .createLevelFromExistingSettings((LevelStorageSource.LevelStorageAccess)var4.get(), var6.dataPackResources(), var2, var8);
      }
   }

   private LevelSettings createLevelSettings(boolean var1) {
      String var2 = this.uiState.getName().trim();
      if (var1) {
         GameRules var3 = new GameRules();
         var3.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
         return new LevelSettings(var2, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, var3, WorldDataConfiguration.DEFAULT);
      } else {
         return new LevelSettings(
            var2,
            this.uiState.getGameMode().gameType,
            this.uiState.isHardcore(),
            this.uiState.getDifficulty(),
            this.uiState.isAllowCommands(),
            this.uiState.getGameRules(),
            this.uiState.getSettings().dataConfiguration()
         );
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.tabNavigationBar.keyPressed(var1)) {
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
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
      this.popScreen();
   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.removeTempDataPackDir();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      RenderSystem.enableBlend();
      var1.blit(Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   @Override
   protected void renderMenuBackground(GuiGraphics var1) {
      var1.blit(TAB_HEADER_BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
      this.renderMenuBackground(var1, 0, this.layout.getHeaderHeight(), this.width, this.height);
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
            SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
            this.popScreen();
         }
      }

      return this.tempDataPackDir;
   }

   void openExperimentsScreen(WorldDataConfiguration var1) {
      Pair var2 = this.getDataPackSelectionSettings(var1);
      if (var2 != null) {
         this.minecraft
            .setScreen(
               new ExperimentsScreen(this, (PackRepository)var2.getSecond(), var1x -> this.tryApplyNewDataPacks(var1x, false, this::openExperimentsScreen))
            );
      }
   }

   void openDataPackSelectionScreen(WorldDataConfiguration var1) {
      Pair var2 = this.getDataPackSelectionSettings(var1);
      if (var2 != null) {
         this.minecraft
            .setScreen(
               new PackSelectionScreen(
                  (PackRepository)var2.getSecond(),
                  var1x -> this.tryApplyNewDataPacks(var1x, true, this::openDataPackSelectionScreen),
                  (Path)var2.getFirst(),
                  Component.translatable("dataPack.title")
               )
            );
      }
   }

   private void tryApplyNewDataPacks(PackRepository var1, boolean var2, Consumer<WorldDataConfiguration> var3) {
      ImmutableList var4 = ImmutableList.copyOf(var1.getSelectedIds());
      List var5 = var1.getAvailableIds().stream().filter(var1x -> !var4.contains(var1x)).collect(ImmutableList.toImmutableList());
      WorldDataConfiguration var6 = new WorldDataConfiguration(new DataPackConfig(var4, var5), this.uiState.getSettings().dataConfiguration().enabledFeatures());
      if (this.uiState.tryUpdateDataConfiguration(var6)) {
         this.minecraft.setScreen(this);
      } else {
         FeatureFlagSet var7 = var1.getRequestedFeatureFlags();
         if (FeatureFlags.isExperimental(var7) && var2) {
            this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen(var1.getSelectedPacks(), var4x -> {
               if (var4x) {
                  this.applyNewPackConfig(var1, var6, var3);
               } else {
                  var3.accept(this.uiState.getSettings().dataConfiguration());
               }
            }));
         } else {
            this.applyNewPackConfig(var1, var6, var3);
         }
      }
   }

   private void applyNewPackConfig(PackRepository var1, WorldDataConfiguration var2, Consumer<WorldDataConfiguration> var3) {
      this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("dataPack.validation.working")));
      WorldLoader.InitConfig var4 = createDefaultLoadConfig(var1, var2);
      WorldLoader.<CreateWorldScreen.DataPackReloadCookie, WorldCreationContext>load(
            var4,
            var1x -> {
               if (var1x.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).size() == 0) {
                  throw new IllegalStateException("Needs at least one world preset to continue");
               } else if (var1x.datapackWorldgen().registryOrThrow(Registries.BIOME).size() == 0) {
                  throw new IllegalStateException("Needs at least one biome continue");
               } else {
                  WorldCreationContext var2x = this.uiState.getSettings();
                  RegistryOps var3x = var2x.worldgenLoadContext().createSerializationContext(JsonOps.INSTANCE);
                  DataResult var4x = WorldGenSettings.encode(var3x, var2x.options(), var2x.selectedDimensions()).setLifecycle(Lifecycle.stable());
                  RegistryOps var5 = var1x.datapackWorldgen().createSerializationContext(JsonOps.INSTANCE);
                  WorldGenSettings var6 = (WorldGenSettings)var4x.flatMap(var1xx -> WorldGenSettings.CODEC.parse(var5, var1xx))
                     .getOrThrow(var0 -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + var0));
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
         .thenAcceptAsync(this.uiState::setSettings, this.minecraft)
         .handle(
            (var2x, var3x) -> {
               if (var3x != null) {
                  LOGGER.warn("Failed to validate datapack", var3x);
                  this.minecraft
                     .setScreen(
                        new ConfirmScreen(
                           var2xx -> {
                              if (var2xx) {
                                 var3.accept(this.uiState.getSettings().dataConfiguration());
                              } else {
                                 var3.accept(WorldDataConfiguration.DEFAULT);
                              }
                           },
                           Component.translatable("dataPack.validation.failed"),
                           CommonComponents.EMPTY,
                           Component.translatable("dataPack.validation.back"),
                           Component.translatable("dataPack.validation.reset")
                        )
                     );
               } else {
                  this.minecraft.setScreen(this);
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
      String var1 = this.uiState.getTargetFolder();

      try {
         LevelStorageSource.LevelStorageAccess var2 = this.minecraft.getLevelSource().createAccess(var1);
         if (this.tempDataPackDir == null) {
            return Optional.of(var2);
         }

         try {
            Optional var5;
            try (Stream var3 = Files.walk(this.tempDataPackDir)) {
               Path var4 = var2.getLevelPath(LevelResource.DATAPACK_DIR);
               FileUtil.createDirectoriesSafe(var4);
               var3.filter(var1x -> !var1x.equals(this.tempDataPackDir)).forEach(var2x -> copyBetweenDirs(this.tempDataPackDir, var4, var2x));
               var5 = Optional.of(var2);
            }

            return var5;
         } catch (UncheckedIOException | IOException var8) {
            LOGGER.warn("Failed to copy datapacks to world {}", var1, var8);
            var2.close();
         }
      } catch (UncheckedIOException | IOException var9) {
         LOGGER.warn("Failed to create access for {}", var1, var9);
      }

      SystemToast.onPackCopyFailure(this.minecraft, var1);
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
   private Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration var1) {
      Path var2 = this.getTempDataPackDir();
      if (var2 != null) {
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = ServerPacksSource.createPackRepository(var2, this.packValidator);
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(var1.dataPacks().getEnabled());
         return Pair.of(var2, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   static record DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
      DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
         super();
         this.worldGenSettings = worldGenSettings;
         this.dataConfiguration = dataConfiguration;
      }
   }

   class GameTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.game.title");
      private static final Component ALLOW_COMMANDS = Component.translatable("selectWorld.allowCommands.new");
      private final EditBox nameEdit;

      GameTab() {
         super(TITLE);
         GridLayout.RowHelper var2 = this.layout.rowSpacing(8).createRowHelper(1);
         LayoutSettings var3 = var2.newCellSettings();
         this.nameEdit = new EditBox(CreateWorldScreen.this.font, 208, 20, Component.translatable("selectWorld.enterName"));
         this.nameEdit.setValue(CreateWorldScreen.this.uiState.getName());
         this.nameEdit.setResponder(CreateWorldScreen.this.uiState::setName);
         CreateWorldScreen.this.uiState
            .addListener(
               var1 -> this.nameEdit
                     .setTooltip(
                        Tooltip.create(
                           Component.translatable("selectWorld.targetFolder", Component.literal(var1.getTargetFolder()).withStyle(ChatFormatting.ITALIC))
                        )
                     )
            );
         CreateWorldScreen.this.setInitialFocus(this.nameEdit);
         var2.addChild(
            CommonLayouts.labeledElement(CreateWorldScreen.this.font, this.nameEdit, CreateWorldScreen.NAME_LABEL),
            var2.newCellSettings().alignHorizontallyCenter()
         );
         CycleButton var4 = var2.addChild(
            CycleButton.<WorldCreationUiState.SelectedGameMode>builder(var0 -> var0.displayName)
               .withValues(
                  WorldCreationUiState.SelectedGameMode.SURVIVAL,
                  WorldCreationUiState.SelectedGameMode.HARDCORE,
                  WorldCreationUiState.SelectedGameMode.CREATIVE
               )
               .create(0, 0, 210, 20, CreateWorldScreen.GAME_MODEL_LABEL, (var1, var2x) -> CreateWorldScreen.this.uiState.setGameMode(var2x)),
            var3
         );
         CreateWorldScreen.this.uiState.addListener(var1 -> {
            var4.setValue(var1.getGameMode());
            var4.active = !var1.isDebug();
            var4.setTooltip(Tooltip.create(var1.getGameMode().getInfo()));
         });
         CycleButton var5 = var2.addChild(
            CycleButton.builder(Difficulty::getDisplayName)
               .withValues(Difficulty.values())
               .create(0, 0, 210, 20, Component.translatable("options.difficulty"), (var1, var2x) -> CreateWorldScreen.this.uiState.setDifficulty(var2x)),
            var3
         );
         CreateWorldScreen.this.uiState.addListener(var2x -> {
            var5.setValue(CreateWorldScreen.this.uiState.getDifficulty());
            var5.active = !CreateWorldScreen.this.uiState.isHardcore();
            var5.setTooltip(Tooltip.create(CreateWorldScreen.this.uiState.getDifficulty().getInfo()));
         });
         CycleButton var6 = var2.addChild(
            CycleButton.onOffBuilder()
               .withTooltip(var0 -> Tooltip.create(CreateWorldScreen.ALLOW_COMMANDS_INFO))
               .create(0, 0, 210, 20, ALLOW_COMMANDS, (var1, var2x) -> CreateWorldScreen.this.uiState.setAllowCommands(var2x))
         );
         CreateWorldScreen.this.uiState.addListener(var2x -> {
            var6.setValue(CreateWorldScreen.this.uiState.isAllowCommands());
            var6.active = !CreateWorldScreen.this.uiState.isDebug() && !CreateWorldScreen.this.uiState.isHardcore();
         });
         if (!SharedConstants.getCurrentVersion().isStable()) {
            var2.addChild(
               Button.builder(
                     CreateWorldScreen.EXPERIMENTS_LABEL,
                     var1 -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())
                  )
                  .width(210)
                  .build()
            );
         }
      }
   }

   class MoreTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.more.title");
      private static final Component GAME_RULES_LABEL = Component.translatable("selectWorld.gameRules");
      private static final Component DATA_PACKS_LABEL = Component.translatable("selectWorld.dataPacks");

      MoreTab() {
         super(TITLE);
         GridLayout.RowHelper var2 = this.layout.rowSpacing(8).createRowHelper(1);
         var2.addChild(Button.builder(GAME_RULES_LABEL, var1 -> this.openGameRulesScreen()).width(210).build());
         var2.addChild(
            Button.builder(
                  CreateWorldScreen.EXPERIMENTS_LABEL,
                  var1 -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())
               )
               .width(210)
               .build()
         );
         var2.addChild(
            Button.builder(
                  DATA_PACKS_LABEL,
                  var1 -> CreateWorldScreen.this.openDataPackSelectionScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())
               )
               .width(210)
               .build()
         );
      }

      private void openGameRulesScreen() {
         CreateWorldScreen.this.minecraft.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.uiState.getGameRules().copy(), var1 -> {
            CreateWorldScreen.this.minecraft.setScreen(CreateWorldScreen.this);
            var1.ifPresent(CreateWorldScreen.this.uiState::setGameRules);
         }));
      }
   }

   class WorldTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.world.title");
      private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
      private static final Component GENERATE_STRUCTURES = Component.translatable("selectWorld.mapFeatures");
      private static final Component GENERATE_STRUCTURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
      private static final Component BONUS_CHEST = Component.translatable("selectWorld.bonusItems");
      private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
      static final Component SEED_EMPTY_HINT = Component.translatable("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
      private static final int WORLD_TAB_WIDTH = 310;
      private final EditBox seedEdit;
      private final Button customizeTypeButton;

      WorldTab() {
         super(TITLE);
         GridLayout.RowHelper var2 = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
         CycleButton var3 = var2.addChild(
            CycleButton.builder(WorldCreationUiState.WorldTypeEntry::describePreset)
               .withValues(this.createWorldTypeValueSupplier())
               .withCustomNarration(CreateWorldScreen.WorldTab::createTypeButtonNarration)
               .create(0, 0, 150, 20, Component.translatable("selectWorld.mapType"), (var1, var2x) -> CreateWorldScreen.this.uiState.setWorldType(var2x))
         );
         var3.setValue(CreateWorldScreen.this.uiState.getWorldType());
         CreateWorldScreen.this.uiState.addListener(var2x -> {
            WorldCreationUiState.WorldTypeEntry var3x = var2x.getWorldType();
            var3.setValue(var3x);
            if (var3x.isAmplified()) {
               var3.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
            } else {
               var3.setTooltip(null);
            }

            var3.active = CreateWorldScreen.this.uiState.getWorldType().preset() != null;
         });
         this.customizeTypeButton = var2.addChild(Button.builder(Component.translatable("selectWorld.customizeType"), var1 -> this.openPresetEditor()).build());
         CreateWorldScreen.this.uiState.addListener(var1 -> this.customizeTypeButton.active = !var1.isDebug() && var1.getPresetEditor() != null);
         this.seedEdit = new EditBox(CreateWorldScreen.this.font, 308, 20, Component.translatable("selectWorld.enterSeed")) {
            @Override
            protected MutableComponent createNarrationMessage() {
               return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(CreateWorldScreen.WorldTab.SEED_EMPTY_HINT);
            }
         };
         this.seedEdit.setHint(SEED_EMPTY_HINT);
         this.seedEdit.setValue(CreateWorldScreen.this.uiState.getSeed());
         this.seedEdit.setResponder(var1 -> CreateWorldScreen.this.uiState.setSeed(this.seedEdit.getValue()));
         var2.addChild(CommonLayouts.labeledElement(CreateWorldScreen.this.font, this.seedEdit, SEED_LABEL), 2);
         SwitchGrid.Builder var4 = SwitchGrid.builder(310);
         var4.addSwitch(GENERATE_STRUCTURES, CreateWorldScreen.this.uiState::isGenerateStructures, CreateWorldScreen.this.uiState::setGenerateStructures)
            .withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isDebug())
            .withInfo(GENERATE_STRUCTURES_INFO);
         var4.addSwitch(BONUS_CHEST, CreateWorldScreen.this.uiState::isBonusChest, CreateWorldScreen.this.uiState::setBonusChest)
            .withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isHardcore() && !CreateWorldScreen.this.uiState.isDebug());
         SwitchGrid var5 = var4.build(var1 -> var2.addChild(var1, 2));
         CreateWorldScreen.this.uiState.addListener(var1 -> var5.refreshStates());
      }

      private void openPresetEditor() {
         PresetEditor var1 = CreateWorldScreen.this.uiState.getPresetEditor();
         if (var1 != null) {
            CreateWorldScreen.this.minecraft.setScreen(var1.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.uiState.getSettings()));
         }
      }

      private CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry> createWorldTypeValueSupplier() {
         return new CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry>() {
            @Override
            public List<WorldCreationUiState.WorldTypeEntry> getSelectedList() {
               return CycleButton.DEFAULT_ALT_LIST_SELECTOR.getAsBoolean()
                  ? CreateWorldScreen.this.uiState.getAltPresetList()
                  : CreateWorldScreen.this.uiState.getNormalPresetList();
            }

            @Override
            public List<WorldCreationUiState.WorldTypeEntry> getDefaultList() {
               return CreateWorldScreen.this.uiState.getNormalPresetList();
            }
         };
      }

      private static MutableComponent createTypeButtonNarration(CycleButton<WorldCreationUiState.WorldTypeEntry> var0) {
         return var0.getValue().isAmplified()
            ? CommonComponents.joinForNarration(var0.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT)
            : var0.createDefaultNarrationMessage();
      }
   }
}
