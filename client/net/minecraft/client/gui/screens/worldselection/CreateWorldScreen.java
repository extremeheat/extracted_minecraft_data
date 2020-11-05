package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component GAME_MODEL_LABEL = new TranslatableComponent("selectWorld.gameMode");
   private static final Component SEED_LABEL = new TranslatableComponent("selectWorld.enterSeed");
   private static final Component SEED_INFO = new TranslatableComponent("selectWorld.seedInfo");
   private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
   private static final Component OUTPUT_DIR_INFO = new TranslatableComponent("selectWorld.resultFolder");
   private static final Component COMMANDS_INFO = new TranslatableComponent("selectWorld.allowCommands.info");
   private final Screen lastScreen;
   private EditBox nameEdit;
   private String resultFolder;
   private CreateWorldScreen.SelectedGameMode gameMode;
   @Nullable
   private CreateWorldScreen.SelectedGameMode oldGameMode;
   private Difficulty selectedDifficulty;
   private Difficulty effectiveDifficulty;
   private boolean commands;
   private boolean commandsChanged;
   public boolean hardCore;
   protected DataPackConfig dataPacks;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private PackRepository tempDataPackRepository;
   private boolean displayOptions;
   private Button createButton;
   private Button modeButton;
   private Button difficultyButton;
   private Button moreOptionsButton;
   private Button gameRulesButton;
   private Button dataPacksButton;
   private Button commandsButton;
   private Component gameModeHelp1;
   private Component gameModeHelp2;
   private String initName;
   private GameRules gameRules;
   public final WorldGenSettingsComponent worldGenSettingsComponent;

   public CreateWorldScreen(@Nullable Screen var1, LevelSettings var2, WorldGenSettings var3, @Nullable Path var4, DataPackConfig var5, RegistryAccess.RegistryHolder var6) {
      this(var1, var5, new WorldGenSettingsComponent(var6, var3, WorldPreset.of(var3), OptionalLong.of(var3.seed())));
      this.initName = var2.levelName();
      this.commands = var2.allowCommands();
      this.commandsChanged = true;
      this.selectedDifficulty = var2.difficulty();
      this.effectiveDifficulty = this.selectedDifficulty;
      this.gameRules.assignFrom(var2.gameRules(), (MinecraftServer)null);
      if (var2.hardcore()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.HARDCORE;
      } else if (var2.gameType().isSurvival()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      } else if (var2.gameType().isCreative()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.CREATIVE;
      }

      this.tempDataPackDir = var4;
   }

   public static CreateWorldScreen create(@Nullable Screen var0) {
      RegistryAccess.RegistryHolder var1 = RegistryAccess.builtin();
      return new CreateWorldScreen(var0, DataPackConfig.DEFAULT, new WorldGenSettingsComponent(var1, WorldGenSettings.makeDefault(var1.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), var1.registryOrThrow(Registry.BIOME_REGISTRY), var1.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)), Optional.of(WorldPreset.NORMAL), OptionalLong.empty()));
   }

   private CreateWorldScreen(@Nullable Screen var1, DataPackConfig var2, WorldGenSettingsComponent var3) {
      super(new TranslatableComponent("selectWorld.create"));
      this.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      this.selectedDifficulty = Difficulty.NORMAL;
      this.effectiveDifficulty = Difficulty.NORMAL;
      this.gameRules = new GameRules();
      this.lastScreen = var1;
      this.initName = I18n.get("selectWorld.newWorld");
      this.dataPacks = var2;
      this.worldGenSettingsComponent = var3;
   }

   public void tick() {
      this.nameEdit.tick();
      this.worldGenSettingsComponent.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TranslatableComponent("selectWorld.enterName")) {
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append((Component)(new TranslatableComponent("selectWorld.resultFolder"))).append(" ").append(CreateWorldScreen.this.resultFolder);
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((var1x) -> {
         this.initName = var1x;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.children.add(this.nameEdit);
      int var1 = this.width / 2 - 155;
      int var2 = this.width / 2 + 5;
      this.modeButton = (Button)this.addButton(new Button(var1, 100, 150, 20, TextComponent.EMPTY, (var1x) -> {
         switch(this.gameMode) {
         case SURVIVAL:
            this.setGameMode(CreateWorldScreen.SelectedGameMode.HARDCORE);
            break;
         case HARDCORE:
            this.setGameMode(CreateWorldScreen.SelectedGameMode.CREATIVE);
            break;
         case CREATIVE:
            this.setGameMode(CreateWorldScreen.SelectedGameMode.SURVIVAL);
         }

         var1x.queueNarration(250);
      }) {
         public Component getMessage() {
            return new TranslatableComponent("options.generic_value", new Object[]{CreateWorldScreen.GAME_MODEL_LABEL, new TranslatableComponent("selectWorld.gameMode." + CreateWorldScreen.this.gameMode.name)});
         }

         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append(CreateWorldScreen.this.gameModeHelp1).append(" ").append(CreateWorldScreen.this.gameModeHelp2);
         }
      });
      this.difficultyButton = (Button)this.addButton(new Button(var2, 100, 150, 20, new TranslatableComponent("options.difficulty"), (var1x) -> {
         this.selectedDifficulty = this.selectedDifficulty.nextById();
         this.effectiveDifficulty = this.selectedDifficulty;
         var1x.queueNarration(250);
      }) {
         public Component getMessage() {
            return (new TranslatableComponent("options.difficulty")).append(": ").append(CreateWorldScreen.this.effectiveDifficulty.getDisplayName());
         }
      });
      this.commandsButton = (Button)this.addButton(new Button(var1, 151, 150, 20, new TranslatableComponent("selectWorld.allowCommands"), (var1x) -> {
         this.commandsChanged = true;
         this.commands = !this.commands;
         var1x.queueNarration(250);
      }) {
         public Component getMessage() {
            return CommonComponents.optionStatus(super.getMessage(), CreateWorldScreen.this.commands && !CreateWorldScreen.this.hardCore);
         }

         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append((Component)(new TranslatableComponent("selectWorld.allowCommands.info")));
         }
      });
      this.dataPacksButton = (Button)this.addButton(new Button(var2, 151, 150, 20, new TranslatableComponent("selectWorld.dataPacks"), (var1x) -> {
         this.openDataPackSelectionScreen();
      }));
      this.gameRulesButton = (Button)this.addButton(new Button(var1, 185, 150, 20, new TranslatableComponent("selectWorld.gameRules"), (var1x) -> {
         this.minecraft.setScreen(new EditGameRulesScreen(this.gameRules.copy(), (var1) -> {
            this.minecraft.setScreen(this);
            var1.ifPresent((var1x) -> {
               this.gameRules = var1x;
            });
         }));
      }));
      this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
      this.moreOptionsButton = (Button)this.addButton(new Button(var2, 185, 150, 20, new TranslatableComponent("selectWorld.moreWorldOptions"), (var1x) -> {
         this.toggleDisplayOptions();
      }));
      this.createButton = (Button)this.addButton(new Button(var1, this.height - 28, 150, 20, new TranslatableComponent("selectWorld.create"), (var1x) -> {
         this.onCreate();
      }));
      this.createButton.active = !this.initName.isEmpty();
      this.addButton(new Button(var2, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.popScreen();
      }));
      this.updateDisplayOptions();
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line2");
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

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onCreate() {
      this.minecraft.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("createWorld.preparing")));
      if (this.copyTempDataPackDirToNewWorld()) {
         this.cleanupTempResources();
         WorldGenSettings var1 = this.worldGenSettingsComponent.makeSettings(this.hardCore);
         LevelSettings var2;
         if (var1.isDebug()) {
            GameRules var3 = new GameRules();
            ((GameRules.BooleanValue)var3.getRule(GameRules.RULE_DAYLIGHT)).set(false, (MinecraftServer)null);
            var2 = new LevelSettings(this.nameEdit.getValue().trim(), GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, var3, DataPackConfig.DEFAULT);
         } else {
            var2 = new LevelSettings(this.nameEdit.getValue().trim(), this.gameMode.gameType, this.hardCore, this.effectiveDifficulty, this.commands && !this.hardCore, this.gameRules, this.dataPacks);
         }

         this.minecraft.createLevel(this.resultFolder, var2, this.worldGenSettingsComponent.registryHolder(), var1);
      }
   }

   private void toggleDisplayOptions() {
      this.setDisplayOptions(!this.displayOptions);
   }

   private void setGameMode(CreateWorldScreen.SelectedGameMode var1) {
      if (!this.commandsChanged) {
         this.commands = var1 == CreateWorldScreen.SelectedGameMode.CREATIVE;
      }

      if (var1 == CreateWorldScreen.SelectedGameMode.HARDCORE) {
         this.hardCore = true;
         this.commandsButton.active = false;
         this.worldGenSettingsComponent.bonusItemsButton.active = false;
         this.effectiveDifficulty = Difficulty.HARD;
         this.difficultyButton.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.worldGenSettingsComponent.bonusItemsButton.active = true;
         this.effectiveDifficulty = this.selectedDifficulty;
         this.difficultyButton.active = true;
      }

      this.gameMode = var1;
      this.updateGameModeHelp();
   }

   public void updateDisplayOptions() {
      this.setDisplayOptions(this.displayOptions);
   }

   private void setDisplayOptions(boolean var1) {
      this.displayOptions = var1;
      this.modeButton.visible = !this.displayOptions;
      this.difficultyButton.visible = !this.displayOptions;
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

         this.commandsButton.visible = !this.displayOptions;
         this.dataPacksButton.visible = !this.displayOptions;
      }

      this.worldGenSettingsComponent.setDisplayOptions(this.displayOptions);
      this.nameEdit.setVisible(!this.displayOptions);
      if (this.displayOptions) {
         this.moreOptionsButton.setMessage(CommonComponents.GUI_DONE);
      } else {
         this.moreOptionsButton.setMessage(new TranslatableComponent("selectWorld.moreWorldOptions"));
      }

      this.gameRulesButton.visible = !this.displayOptions;
   }

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

   public void onClose() {
      if (this.displayOptions) {
         this.setDisplayOptions(false);
      } else {
         this.popScreen();
      }

   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.cleanupTempResources();
   }

   private void cleanupTempResources() {
      if (this.tempDataPackRepository != null) {
         this.tempDataPackRepository.close();
      }

      this.removeTempDataPackDir();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 20, -1);
      if (this.displayOptions) {
         drawString(var1, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(var1, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
         this.worldGenSettingsComponent.render(var1, var2, var3, var4);
      } else {
         drawString(var1, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(var1, this.font, (new TextComponent("")).append(OUTPUT_DIR_INFO).append(" ").append(this.resultFolder), this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(var1, var2, var3, var4);
         drawString(var1, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
         drawString(var1, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
         if (this.commandsButton.visible) {
            drawString(var1, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
         }
      }

      super.render(var1, var2, var3, var4);
   }

   protected <T extends GuiEventListener> T addWidget(T var1) {
      return super.addWidget(var1);
   }

   protected <T extends AbstractWidget> T addButton(T var1) {
      return super.addButton(var1);
   }

   @Nullable
   protected Path getTempDataPackDir() {
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
         this.minecraft.setScreen(new PackSelectionScreen(this, (PackRepository)var1.getSecond(), this::tryApplyNewDataPacks, (File)var1.getFirst(), new TranslatableComponent("dataPack.title")));
      }

   }

   private void tryApplyNewDataPacks(PackRepository var1) {
      ImmutableList var2 = ImmutableList.copyOf(var1.getSelectedIds());
      List var3 = (List)var1.getAvailableIds().stream().filter((var1x) -> {
         return !var2.contains(var1x);
      }).collect(ImmutableList.toImmutableList());
      DataPackConfig var4 = new DataPackConfig(var2, var3);
      if (var2.equals(this.dataPacks.getEnabled())) {
         this.dataPacks = var4;
      } else {
         this.minecraft.tell(() -> {
            this.minecraft.setScreen(new GenericDirtMessageScreen(new TranslatableComponent("dataPack.validation.working")));
         });
         ServerResources.loadResources(var1.openAllSelected(), Commands.CommandSelection.INTEGRATED, 2, Util.backgroundExecutor(), this.minecraft).handle((var2x, var3x) -> {
            if (var3x != null) {
               LOGGER.warn("Failed to validate datapack", var3x);
               this.minecraft.tell(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((var1) -> {
                     if (var1) {
                        this.openDataPackSelectionScreen();
                     } else {
                        this.dataPacks = DataPackConfig.DEFAULT;
                        this.minecraft.setScreen(this);
                     }

                  }, new TranslatableComponent("dataPack.validation.failed"), TextComponent.EMPTY, new TranslatableComponent("dataPack.validation.back"), new TranslatableComponent("dataPack.validation.reset")));
               });
            } else {
               this.minecraft.tell(() -> {
                  this.dataPacks = var4;
                  this.worldGenSettingsComponent.updateDataPacks(var2x);
                  var2x.close();
                  this.minecraft.setScreen(this);
               });
            }

            return null;
         });
      }
   }

   private void removeTempDataPackDir() {
      if (this.tempDataPackDir != null) {
         try {
            Stream var1 = Files.walk(this.tempDataPackDir);
            Throwable var2 = null;

            try {
               var1.sorted(Comparator.reverseOrder()).forEach((var0) -> {
                  try {
                     Files.delete(var0);
                  } catch (IOException var2) {
                     LOGGER.warn("Failed to remove temporary file {}", var0, var2);
                  }

               });
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (IOException var14) {
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
         throw new CreateWorldScreen.OperationFailedException(var4);
      }
   }

   private boolean copyTempDataPackDirToNewWorld() {
      if (this.tempDataPackDir != null) {
         try {
            LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().createAccess(this.resultFolder);
            Throwable var2 = null;

            try {
               Stream var3 = Files.walk(this.tempDataPackDir);
               Throwable var4 = null;

               try {
                  Path var5 = var1.getLevelPath(LevelResource.DATAPACK_DIR);
                  Files.createDirectories(var5);
                  var3.filter((var1x) -> {
                     return !var1x.equals(this.tempDataPackDir);
                  }).forEach((var2x) -> {
                     copyBetweenDirs(this.tempDataPackDir, var5, var2x);
                  });
               } catch (Throwable var29) {
                  var4 = var29;
                  throw var29;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var28) {
                           var4.addSuppressed(var28);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            } catch (Throwable var31) {
               var2 = var31;
               throw var31;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var27) {
                        var2.addSuppressed(var27);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (CreateWorldScreen.OperationFailedException | IOException var33) {
            LOGGER.warn("Failed to copy datapacks to world {}", this.resultFolder, var33);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
            return false;
         }
      }

      return true;
   }

   @Nullable
   public static Path createTempDataPackDirFromExistingWorld(Path var0, Minecraft var1) {
      MutableObject var2 = new MutableObject();

      try {
         Stream var3 = Files.walk(var0);
         Throwable var4 = null;

         try {
            var3.filter((var1x) -> {
               return !var1x.equals(var0);
            }).forEach((var2x) -> {
               Path var3 = (Path)var2.getValue();
               if (var3 == null) {
                  try {
                     var3 = Files.createTempDirectory("mcworld-");
                  } catch (IOException var5) {
                     LOGGER.warn("Failed to create temporary dir");
                     throw new CreateWorldScreen.OperationFailedException(var5);
                  }

                  var2.setValue(var3);
               }

               copyBetweenDirs(var0, var3, var2x);
            });
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
      } catch (CreateWorldScreen.OperationFailedException | IOException var16) {
         LOGGER.warn("Failed to copy datapacks from world {}", var0, var16);
         SystemToast.onPackCopyFailure(var1, var0.toString());
         return null;
      }

      return (Path)var2.getValue();
   }

   @Nullable
   private Pair<File, PackRepository> getDataPackSelectionSettings() {
      Path var1 = this.getTempDataPackDir();
      if (var1 != null) {
         File var2 = var1.toFile();
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = new PackRepository(new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var2, PackSource.DEFAULT)});
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(this.dataPacks.getEnabled());
         return Pair.of(var2, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   static class OperationFailedException extends RuntimeException {
      public OperationFailedException(Throwable var1) {
         super(var1);
      }
   }

   static enum SelectedGameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      private final String name;
      private final GameType gameType;

      private SelectedGameMode(String var3, GameType var4) {
         this.name = var3;
         this.gameType = var4;
      }
   }
}
