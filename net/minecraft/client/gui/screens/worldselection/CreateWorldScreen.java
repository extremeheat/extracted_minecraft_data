package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.storage.LevelData;
import org.apache.commons.lang3.StringUtils;

public class CreateWorldScreen extends Screen {
   private final Screen lastScreen;
   private EditBox nameEdit;
   private EditBox seedEdit;
   private String resultFolder;
   private CreateWorldScreen.SelectedGameMode gameMode;
   @Nullable
   private CreateWorldScreen.SelectedGameMode oldGameMode;
   private boolean features;
   private boolean commands;
   private boolean commandsChanged;
   private boolean bonusItems;
   private boolean hardCore;
   private boolean done;
   private boolean displayOptions;
   private Button createButton;
   private Button modeButton;
   private Button moreOptionsButton;
   private Button featuresButton;
   private Button bonusItemsButton;
   private Button typeButton;
   private Button commandsButton;
   private Button customizeTypeButton;
   private String gameModeHelp1;
   private String gameModeHelp2;
   private String initSeed;
   private String initName;
   private int levelTypeIndex;
   public CompoundTag levelTypeOptions;

   public CreateWorldScreen(Screen var1) {
      super(new TranslatableComponent("selectWorld.create", new Object[0]));
      this.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      this.features = true;
      this.levelTypeOptions = new CompoundTag();
      this.lastScreen = var1;
      this.initSeed = "";
      this.initName = I18n.get("selectWorld.newWorld");
   }

   public void tick() {
      this.nameEdit.tick();
      this.seedEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterName")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.resultFolder") + " " + CreateWorldScreen.this.resultFolder;
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((var1) -> {
         this.initName = var1;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.children.add(this.nameEdit);
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.get("selectWorld.gameMode"), (var1) -> {
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

         var1.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + CreateWorldScreen.this.gameMode.name);
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + CreateWorldScreen.this.gameModeHelp1 + " " + CreateWorldScreen.this.gameModeHelp2;
         }
      });
      this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterSeed"));
      this.seedEdit.setValue(this.initSeed);
      this.seedEdit.setResponder((var1) -> {
         this.initSeed = this.seedEdit.getValue();
      });
      this.children.add(this.seedEdit);
      this.featuresButton = (Button)this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.mapFeatures"), (var1) -> {
         this.features = !this.features;
         var1.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.mapFeatures") + ' ' + I18n.get(CreateWorldScreen.this.features ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.mapFeatures.info");
         }
      });
      this.featuresButton.visible = false;
      this.typeButton = (Button)this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.mapType"), (var1) -> {
         ++this.levelTypeIndex;
         if (this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
            this.levelTypeIndex = 0;
         }

         while(!this.isValidLevelType()) {
            ++this.levelTypeIndex;
            if (this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
               this.levelTypeIndex = 0;
            }
         }

         this.levelTypeOptions = new CompoundTag();
         this.setDisplayOptions(this.displayOptions);
         var1.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.mapType") + ' ' + I18n.get(LevelType.LEVEL_TYPES[CreateWorldScreen.this.levelTypeIndex].getDescriptionId());
         }

         protected String getNarrationMessage() {
            LevelType var1 = LevelType.LEVEL_TYPES[CreateWorldScreen.this.levelTypeIndex];
            return var1.hasHelpText() ? super.getNarrationMessage() + ". " + I18n.get(var1.getHelpTextId()) : super.getNarrationMessage();
         }
      });
      this.typeButton.visible = false;
      this.customizeTypeButton = (Button)this.addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.get("selectWorld.customizeType"), (var1) -> {
         if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.FLAT) {
            this.minecraft.setScreen(new CreateFlatWorldScreen(this, this.levelTypeOptions));
         }

         if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.BUFFET) {
            this.minecraft.setScreen(new CreateBuffetWorldScreen(this, this.levelTypeOptions));
         }

      }));
      this.customizeTypeButton.visible = false;
      this.commandsButton = (Button)this.addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.get("selectWorld.allowCommands"), (var1) -> {
         this.commandsChanged = true;
         this.commands = !this.commands;
         var1.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.allowCommands") + ' ' + I18n.get(CreateWorldScreen.this.commands && !CreateWorldScreen.this.hardCore ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.allowCommands.info");
         }
      });
      this.commandsButton.visible = false;
      this.bonusItemsButton = (Button)this.addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.get("selectWorld.bonusItems"), (var1) -> {
         this.bonusItems = !this.bonusItems;
         var1.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.bonusItems") + ' ' + I18n.get(CreateWorldScreen.this.bonusItems && !CreateWorldScreen.this.hardCore ? "options.on" : "options.off");
         }
      });
      this.bonusItemsButton.visible = false;
      this.moreOptionsButton = (Button)this.addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.get("selectWorld.moreWorldOptions"), (var1) -> {
         this.toggleDisplayOptions();
      }));
      this.createButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("selectWorld.create"), (var1) -> {
         this.onCreate();
      }));
      this.createButton.active = !this.initName.isEmpty();
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.setDisplayOptions(this.displayOptions);
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = I18n.get("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = I18n.get("selectWorld.gameMode." + this.gameMode.name + ".line2");
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
      this.minecraft.setScreen((Screen)null);
      if (!this.done) {
         this.done = true;
         long var1 = (new Random()).nextLong();
         String var3 = this.seedEdit.getValue();
         if (!StringUtils.isEmpty(var3)) {
            try {
               long var4 = Long.parseLong(var3);
               if (var4 != 0L) {
                  var1 = var4;
               }
            } catch (NumberFormatException var6) {
               var1 = (long)var3.hashCode();
            }
         }

         LevelSettings var7 = new LevelSettings(var1, this.gameMode.gameType, this.features, this.hardCore, LevelType.LEVEL_TYPES[this.levelTypeIndex]);
         var7.setLevelTypeOptions((JsonElement)Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.levelTypeOptions));
         if (this.bonusItems && !this.hardCore) {
            var7.enableStartingBonusItems();
         }

         if (this.commands && !this.hardCore) {
            var7.enableSinglePlayerCommands();
         }

         this.minecraft.selectLevel(this.resultFolder, this.nameEdit.getValue().trim(), var7);
      }
   }

   private boolean isValidLevelType() {
      LevelType var1 = LevelType.LEVEL_TYPES[this.levelTypeIndex];
      if (var1 != null && var1.isSelectable()) {
         return var1 == LevelType.DEBUG_ALL_BLOCK_STATES ? hasShiftDown() : true;
      } else {
         return false;
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
         this.bonusItemsButton.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.bonusItemsButton.active = true;
      }

      this.gameMode = var1;
      this.updateGameModeHelp();
   }

   private void setDisplayOptions(boolean var1) {
      this.displayOptions = var1;
      this.modeButton.visible = !this.displayOptions;
      this.typeButton.visible = this.displayOptions;
      if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.modeButton.active = false;
         if (this.oldGameMode == null) {
            this.oldGameMode = this.gameMode;
         }

         this.setGameMode(CreateWorldScreen.SelectedGameMode.DEBUG);
         this.featuresButton.visible = false;
         this.bonusItemsButton.visible = false;
         this.commandsButton.visible = false;
         this.customizeTypeButton.visible = false;
      } else {
         this.modeButton.active = true;
         if (this.oldGameMode != null) {
            this.setGameMode(this.oldGameMode);
         }

         this.featuresButton.visible = this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex] != LevelType.CUSTOMIZED;
         this.bonusItemsButton.visible = this.displayOptions;
         this.commandsButton.visible = this.displayOptions;
         this.customizeTypeButton.visible = this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex].hasCustomOptions();
      }

      this.seedEdit.setVisible(this.displayOptions);
      this.nameEdit.setVisible(!this.displayOptions);
      if (this.displayOptions) {
         this.moreOptionsButton.setMessage(I18n.get("gui.done"));
      } else {
         this.moreOptionsButton.setMessage(I18n.get("selectWorld.moreWorldOptions"));
      }

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
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, -1);
      if (this.displayOptions) {
         this.drawString(this.font, I18n.get("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.featuresButton.visible) {
            this.drawString(this.font, I18n.get("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.commandsButton.visible) {
            this.drawString(this.font, I18n.get("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.seedEdit.render(var1, var2, var3);
         if (LevelType.LEVEL_TYPES[this.levelTypeIndex].hasHelpText()) {
            this.font.drawWordWrap(I18n.get(LevelType.LEVEL_TYPES[this.levelTypeIndex].getHelpTextId()), this.typeButton.x + 2, this.typeButton.y + 22, this.typeButton.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.font, I18n.get("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.resultFolder") + " " + this.resultFolder, this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(var1, var2, var3);
         this.drawCenteredString(this.font, this.gameModeHelp1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.font, this.gameModeHelp2, this.width / 2, 149, -6250336);
      }

      super.render(var1, var2, var3);
   }

   public void copyFromWorld(LevelData var1) {
      this.initName = var1.getLevelName();
      this.initSeed = Long.toString(var1.getSeed());
      LevelType var2 = var1.getGeneratorType() == LevelType.CUSTOMIZED ? LevelType.NORMAL : var1.getGeneratorType();
      this.levelTypeIndex = var2.getId();
      this.levelTypeOptions = var1.getGeneratorOptions();
      this.features = var1.isGenerateMapFeatures();
      this.commands = var1.getAllowCommands();
      if (var1.isHardcore()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.HARDCORE;
      } else if (var1.getGameType().isSurvival()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.SURVIVAL;
      } else if (var1.getGameType().isCreative()) {
         this.gameMode = CreateWorldScreen.SelectedGameMode.CREATIVE;
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
