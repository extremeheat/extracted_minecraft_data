package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConfigureWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final Component WORLD_LIST_TITLE = new TranslatableComponent("mco.configure.worlds.title");
   private static final Component TITLE = new TranslatableComponent("mco.configure.world.title");
   private static final Component MINIGAME_PREFIX = (new TranslatableComponent("mco.configure.current.minigame")).append(": ");
   private static final Component SERVER_EXPIRED_TOOLTIP = new TranslatableComponent("mco.selectServer.expired");
   private static final Component SERVER_EXPIRING_SOON_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRING_IN_DAY_TOOLTIP = new TranslatableComponent("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = new TranslatableComponent("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = new TranslatableComponent("mco.selectServer.closed");
   private static final int DEFAULT_BUTTON_WIDTH = 80;
   private static final int DEFAULT_BUTTON_OFFSET = 5;
   @Nullable
   private Component toolTip;
   private final RealmsMainScreen lastScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private int leftX;
   private int rightX;
   private Button playersButton;
   private Button settingsButton;
   private Button subscriptionButton;
   private Button optionsButton;
   private Button backupButton;
   private Button resetWorldButton;
   private Button switchMinigameButton;
   private boolean stateChanged;
   private int animTick;
   private int clicks;
   private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

   public RealmsConfigureWorldScreen(RealmsMainScreen var1, long var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.serverId = var2;
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.leftX = this.width / 2 - 187;
      this.rightX = this.width / 2 + 190;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.playersButton = (Button)this.addRenderableWidget(new Button(this.centerButton(0, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.players"), (var1x) -> {
         this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData));
      }));
      this.settingsButton = (Button)this.addRenderableWidget(new Button(this.centerButton(1, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.settings"), (var1x) -> {
         this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()));
      }));
      this.subscriptionButton = (Button)this.addRenderableWidget(new Button(this.centerButton(2, 3), row(0), 100, 20, new TranslatableComponent("mco.configure.world.buttons.subscription"), (var1x) -> {
         this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen));
      }));
      this.slotButtonList.clear();

      for(int var1 = 1; var1 < 5; ++var1) {
         this.slotButtonList.add(this.addSlotButton(var1));
      }

      this.switchMinigameButton = (Button)this.addRenderableWidget(new Button(this.leftButton(0), row(13) - 5, 100, 20, new TranslatableComponent("mco.configure.world.buttons.switchminigame"), (var1x) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME));
      }));
      this.optionsButton = (Button)this.addRenderableWidget(new Button(this.leftButton(0), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.options"), (var1x) -> {
         this.minecraft.setScreen(new RealmsSlotOptionsScreen(this, ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).clone(), this.serverData.worldType, this.serverData.activeSlot));
      }));
      this.backupButton = (Button)this.addRenderableWidget(new Button(this.leftButton(1), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.backup"), (var1x) -> {
         this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot));
      }));
      this.resetWorldButton = (Button)this.addRenderableWidget(new Button(this.leftButton(2), row(13) - 5, 90, 20, new TranslatableComponent("mco.configure.world.buttons.resetworld"), (var1x) -> {
         this.minecraft.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), () -> {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(this.getNewScreen());
            });
         }, () -> {
            this.minecraft.setScreen(this.getNewScreen());
         }));
      }));
      this.addRenderableWidget(new Button(this.rightX - 80 + 8, row(13) - 5, 70, 20, CommonComponents.GUI_BACK, (var1x) -> {
         this.backButtonClicked();
      }));
      this.backupButton.active = true;
      if (this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active = false;
         this.settingsButton.active = false;
         this.subscriptionButton.active = false;
      } else {
         this.disableButtons();
         if (this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private RealmsWorldSlotButton addSlotButton(int var1) {
      int var2 = this.frame(var1);
      int var3 = row(5) + 5;
      RealmsWorldSlotButton var4 = new RealmsWorldSlotButton(var2, var3, 80, 80, () -> {
         return this.serverData;
      }, (var1x) -> {
         this.toolTip = var1x;
      }, var1, (var2x) -> {
         RealmsWorldSlotButton.State var3 = ((RealmsWorldSlotButton)var2x).getState();
         if (var3 != null) {
            switch(var3.action) {
            case NOTHING:
               break;
            case JOIN:
               this.joinRealm(this.serverData);
               break;
            case SWITCH_SLOT:
               if (var3.minigame) {
                  this.switchToMinigame();
               } else if (var3.empty) {
                  this.switchToEmptySlot(var1, this.serverData);
               } else {
                  this.switchToFullSlot(var1, this.serverData);
               }
               break;
            default:
               throw new IllegalStateException("Unknown action " + var3.action);
            }
         }

      });
      return (RealmsWorldSlotButton)this.addRenderableWidget(var4);
   }

   private int leftButton(int var1) {
      return this.leftX + var1 * 95;
   }

   private int centerButton(int var1, int var2) {
      return this.width / 2 - (var2 * 105 - 5) / 2 + var1 * 105;
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

      this.slotButtonList.forEach(RealmsWorldSlotButton::tick);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.toolTip = null;
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, WORLD_LIST_TITLE, this.width / 2, row(4), 16777215);
      super.render(var1, var2, var3, var4);
      if (this.serverData == null) {
         drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      } else {
         String var5 = this.serverData.getName();
         int var6 = this.font.width(var5);
         int var7 = this.serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
         int var8 = this.font.width((FormattedText)this.title);
         drawCenteredString(var1, this.font, this.title, this.width / 2, 12, 16777215);
         drawCenteredString(var1, this.font, var5, this.width / 2, 24, var7);
         int var9 = Math.min(this.centerButton(2, 3) + 80 - 11, this.width / 2 + var6 / 2 + var8 / 2 + 10);
         this.drawServerStatus(var1, var9, 7, var2, var3);
         if (this.isMinigame()) {
            this.font.draw(var1, (Component)MINIGAME_PREFIX.copy().append(this.serverData.getMinigameName()), (float)(this.leftX + 80 + 20 + 10), (float)row(13), 16777215);
         }

         if (this.toolTip != null) {
            this.renderMousehoverTooltip(var1, this.toolTip, var2, var3);
         }

      }
   }

   private int frame(int var1) {
      return this.leftX + (var1 - 1) * 98;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.lastScreen.resetScreen();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long var1) {
      (new Thread(() -> {
         RealmsClient var3 = RealmsClient.create();

         try {
            RealmsServer var4 = var3.getOwnWorld(var1);
            this.minecraft.execute(() -> {
               this.serverData = var4;
               this.disableButtons();
               if (this.isMinigame()) {
                  this.show(this.switchMinigameButton);
               } else {
                  this.show(this.optionsButton);
                  this.show(this.backupButton);
                  this.show(this.resetWorldButton);
               }

            });
         } catch (RealmsServiceException var5) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(var5.getMessage()), this.lastScreen));
            });
         }

      })).start();
   }

   private void disableButtons() {
      this.playersButton.active = !this.serverData.expired;
      this.settingsButton.active = !this.serverData.expired;
      this.subscriptionButton.active = true;
      this.switchMinigameButton.active = !this.serverData.expired;
      this.optionsButton.active = !this.serverData.expired;
      this.resetWorldButton.active = !this.serverData.expired;
   }

   private void joinRealm(RealmsServer var1) {
      if (this.serverData.state == RealmsServer.State.OPEN) {
         this.lastScreen.play(var1, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      } else {
         this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      }

   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME);
      var1.setWarning(new TranslatableComponent("mco.minigame.world.info.line1"), new TranslatableComponent("mco.minigame.world.info.line2"));
      this.minecraft.setScreen(var1);
   }

   private void switchToFullSlot(int var1, RealmsServer var2) {
      TranslatableComponent var3 = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
      TranslatableComponent var4 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((var3x) -> {
         if (var3x) {
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(var2.field_121, var1, () -> {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.getNewScreen());
               });
            })));
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, var3, var4, true));
   }

   private void switchToEmptySlot(int var1, RealmsServer var2) {
      TranslatableComponent var3 = new TranslatableComponent("mco.configure.world.slot.switch.question.line1");
      TranslatableComponent var4 = new TranslatableComponent("mco.configure.world.slot.switch.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((var3x) -> {
         if (var3x) {
            RealmsResetWorldScreen var4 = new RealmsResetWorldScreen(this, var2, new TranslatableComponent("mco.configure.world.switch.slot"), new TranslatableComponent("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, () -> {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.getNewScreen());
               });
            }, () -> {
               this.minecraft.setScreen(this.getNewScreen());
            });
            var4.setSlot(var1);
            var4.setResetTitle(new TranslatableComponent("mco.create.world.reset.title"));
            this.minecraft.setScreen(var4);
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, var3, var4, true));
   }

   protected void renderMousehoverTooltip(PoseStack var1, @Nullable Component var2, int var3, int var4) {
      int var5 = var3 + 12;
      int var6 = var4 - 12;
      int var7 = this.font.width((FormattedText)var2);
      if (var5 + var7 + 3 > this.rightX) {
         var5 = var5 - var7 - 20;
      }

      this.fillGradient(var1, var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
      this.font.drawShadow(var1, var2, (float)var5, (float)var6, 16777215);
   }

   private void drawServerStatus(PoseStack var1, int var2, int var3, int var4, int var5) {
      if (this.serverData.expired) {
         this.drawExpired(var1, var2, var3, var4, var5);
      } else if (this.serverData.state == RealmsServer.State.CLOSED) {
         this.drawClose(var1, var2, var3, var4, var5);
      } else if (this.serverData.state == RealmsServer.State.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.drawExpiring(var1, var2, var3, var4, var5, this.serverData.daysLeft);
         } else {
            this.drawOpen(var1, var2, var3, var4, var5);
         }
      }

   }

   private void drawExpired(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27) {
         this.toolTip = SERVER_EXPIRED_TOOLTIP;
      }

   }

   private void drawExpiring(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
      RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.animTick % 20 < 10) {
         GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         GuiComponent.blit(var1, var2, var3, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27) {
         if (var6 <= 0) {
            this.toolTip = SERVER_EXPIRING_SOON_TOOLTIP;
         } else if (var6 == 1) {
            this.toolTip = SERVER_EXPIRING_IN_DAY_TOOLTIP;
         } else {
            this.toolTip = new TranslatableComponent("mco.selectServer.expires.days", new Object[]{var6});
         }
      }

   }

   private void drawOpen(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27) {
         this.toolTip = SERVER_OPEN_TOOLTIP;
      }

   }

   private void drawClose(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27) {
         this.toolTip = SERVER_CLOSED_TOOLTIP;
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
   }

   private void hideRegularButtons() {
      this.hide(this.optionsButton);
      this.hide(this.backupButton);
      this.hide(this.resetWorldButton);
   }

   private void hide(Button var1) {
      var1.visible = false;
      this.removeWidget(var1);
   }

   private void show(Button var1) {
      var1.visible = true;
      this.addRenderableWidget(var1);
   }

   private void hideMinigameButtons() {
      this.hide(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions var1) {
      RealmsWorldOptions var2 = (RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot);
      var1.templateId = var2.templateId;
      var1.templateImage = var2.templateImage;
      RealmsClient var3 = RealmsClient.create();

      try {
         var3.updateSlot(this.serverData.field_121, this.serverData.activeSlot, var1);
         this.serverData.slots.put(this.serverData.activeSlot, var1);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't save slot settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void saveSettings(String var1, String var2) {
      String var3 = var2.trim().isEmpty() ? null : var2;
      RealmsClient var4 = RealmsClient.create();

      try {
         var4.update(this.serverData.field_121, var1, var3);
         this.serverData.setName(var1);
         this.serverData.setDescription(var3);
      } catch (RealmsServiceException var6) {
         LOGGER.error("Couldn't save settings");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var6, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void openTheWorld(boolean var1, Screen var2) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var2, new OpenServerTask(this.serverData, this, this.lastScreen, var1, this.minecraft)));
   }

   public void closeTheWorld(Screen var1) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var1, new CloseServerTask(this.serverData, this)));
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   private void templateSelectionCallback(@Nullable WorldTemplate var1) {
      if (var1 != null && WorldTemplate.WorldTemplateType.MINIGAME == var1.type) {
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.serverData.field_121, var1, this.getNewScreen())));
      } else {
         this.minecraft.setScreen(this);
      }

   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}
