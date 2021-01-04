package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nonnull;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate> implements RealmsWorldSlotButton.Listener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String toolTip;
   private final RealmsMainScreen lastScreen;
   private RealmsServer serverData;
   private final long serverId;
   private int left_x;
   private int right_x;
   private final int default_button_width = 80;
   private final int default_button_offset = 5;
   private RealmsButton playersButton;
   private RealmsButton settingsButton;
   private RealmsButton subscriptionButton;
   private RealmsButton optionsButton;
   private RealmsButton backupButton;
   private RealmsButton resetWorldButton;
   private RealmsButton switchMinigameButton;
   private boolean stateChanged;
   private int animTick;
   private int clicks;

   public RealmsConfigureWorldScreen(RealmsMainScreen var1, long var2) {
      super();
      this.lastScreen = var1;
      this.serverId = var2;
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.left_x = this.width() / 2 - 187;
      this.right_x = this.width() / 2 + 190;
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.playersButton = new RealmsButton(2, this.centerButton(0, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.players")) {
         public void onPress() {
            Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData));
         }
      });
      this.buttonsAdd(this.settingsButton = new RealmsButton(3, this.centerButton(1, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.settings")) {
         public void onPress() {
            Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone()));
         }
      });
      this.buttonsAdd(this.subscriptionButton = new RealmsButton(4, this.centerButton(2, 3), RealmsConstants.row(0), 100, 20, getLocalizedString("mco.configure.world.buttons.subscription")) {
         public void onPress() {
            Realms.setScreen(new RealmsSubscriptionInfoScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.lastScreen));
         }
      });

      for(int var1 = 1; var1 < 5; ++var1) {
         this.addSlotButton(var1);
      }

      this.buttonsAdd(this.switchMinigameButton = new RealmsButton(8, this.leftButton(0), RealmsConstants.row(13) - 5, 100, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(RealmsConfigureWorldScreen.this, RealmsServer.WorldType.MINIGAME);
            var1.setTitle(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
            Realms.setScreen(var1);
         }
      });
      this.buttonsAdd(this.optionsButton = new RealmsButton(5, this.leftButton(0), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.options")) {
         public void onPress() {
            Realms.setScreen(new RealmsSlotOptionsScreen(RealmsConfigureWorldScreen.this, ((RealmsWorldOptions)RealmsConfigureWorldScreen.this.serverData.slots.get(RealmsConfigureWorldScreen.this.serverData.activeSlot)).clone(), RealmsConfigureWorldScreen.this.serverData.worldType, RealmsConfigureWorldScreen.this.serverData.activeSlot));
         }
      });
      this.buttonsAdd(this.backupButton = new RealmsButton(6, this.leftButton(1), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.backup")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.serverData.activeSlot));
         }
      });
      this.buttonsAdd(this.resetWorldButton = new RealmsButton(7, this.leftButton(2), RealmsConstants.row(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.resetworld")) {
         public void onPress() {
            Realms.setScreen(new RealmsResetWorldScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.getNewScreen()));
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsConfigureWorldScreen.this.backButtonClicked();
         }
      });
      this.backupButton.active(true);
      if (this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active(false);
         this.settingsButton.active(false);
         this.subscriptionButton.active(false);
      } else {
         this.disableButtons();
         if (this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private void addSlotButton(int var1) {
      int var2 = this.frame(var1);
      int var3 = RealmsConstants.row(5) + 5;
      int var4 = 100 + var1;
      RealmsWorldSlotButton var5 = new RealmsWorldSlotButton(var2, var3, 80, 80, () -> {
         return this.serverData;
      }, (var1x) -> {
         this.toolTip = var1x;
      }, var4, var1, this);
      this.getProxy().buttonsAdd(var5);
   }

   private int leftButton(int var1) {
      return this.left_x + var1 * 95;
   }

   private int centerButton(int var1, int var2) {
      return this.width() / 2 - (var2 * 105 - 5) / 2 + var1 * 105;
   }

   public void tick() {
      this.tickButtons();
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.row(4), 16777215);
      super.render(var1, var2, var3);
      if (this.serverData == null) {
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
      } else {
         String var4 = this.serverData.getName();
         int var5 = this.fontWidth(var4);
         int var6 = this.serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
         int var7 = this.fontWidth(getLocalizedString("mco.configure.world.title"));
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
         this.drawCenteredString(var4, this.width() / 2, 24, var6);
         int var8 = Math.min(this.centerButton(2, 3) + 80 - 11, this.width() / 2 + var5 / 2 + var7 / 2 + 10);
         this.drawServerStatus(var8, 7, var1, var2);
         if (this.isMinigame()) {
            this.drawString(getLocalizedString("mco.configure.current.minigame") + ": " + this.serverData.getMinigameName(), this.left_x + 80 + 20 + 10, RealmsConstants.row(13), 16777215);
         }

         if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, var1, var2);
         }

      }
   }

   private int frame(int var1) {
      return this.left_x + (var1 - 1) * 98;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
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
         this.lastScreen.removeSelection();
      }

      Realms.setScreen(this.lastScreen);
   }

   private void fetchServerData(final long var1) {
      (new Thread() {
         public void run() {
            RealmsClient var1x = RealmsClient.createRealmsClient();

            try {
               RealmsConfigureWorldScreen.this.serverData = var1x.getOwnWorld(var1);
               RealmsConfigureWorldScreen.this.disableButtons();
               if (RealmsConfigureWorldScreen.this.isMinigame()) {
                  RealmsConfigureWorldScreen.this.showMinigameButtons();
               } else {
                  RealmsConfigureWorldScreen.this.showRegularButtons();
               }
            } catch (RealmsServiceException var3) {
               RealmsConfigureWorldScreen.LOGGER.error("Couldn't get own world");
               Realms.setScreen(new RealmsGenericErrorScreen(var3.getMessage(), RealmsConfigureWorldScreen.this.lastScreen));
            } catch (IOException var4) {
               RealmsConfigureWorldScreen.LOGGER.error("Couldn't parse response getting own world");
            }

         }
      }).start();
   }

   private void disableButtons() {
      this.playersButton.active(!this.serverData.expired);
      this.settingsButton.active(!this.serverData.expired);
      this.subscriptionButton.active(true);
      this.switchMinigameButton.active(!this.serverData.expired);
      this.optionsButton.active(!this.serverData.expired);
      this.resetWorldButton.active(!this.serverData.expired);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return super.mouseClicked(var1, var3, var5);
   }

   private void joinRealm(RealmsServer var1) {
      if (this.serverData.state == RealmsServer.State.OPEN) {
         this.lastScreen.play(var1, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      } else {
         this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      }

   }

   public void onSlotClick(int var1, @Nonnull RealmsWorldSlotButton.Action var2, boolean var3, boolean var4) {
      switch(var2) {
      case NOTHING:
         break;
      case JOIN:
         this.joinRealm(this.serverData);
         break;
      case SWITCH_SLOT:
         if (var3) {
            this.switchToMinigame();
         } else if (var4) {
            this.switchToEmptySlot(var1, this.serverData);
         } else {
            this.switchToFullSlot(var1, this.serverData);
         }
         break;
      default:
         throw new IllegalStateException("Unknown action " + var2);
      }

   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
      var1.setTitle(getLocalizedString("mco.template.title.minigame"));
      var1.setWarning(getLocalizedString("mco.minigame.world.info.line1") + "\\n" + getLocalizedString("mco.minigame.world.info.line2"));
      Realms.setScreen(var1);
   }

   private void switchToFullSlot(int var1, RealmsServer var2) {
      String var3 = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String var4 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((var3x, var4x) -> {
         if (var3x) {
            this.switchSlot(var2.id, var1);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, var3, var4, true, 9));
   }

   private void switchToEmptySlot(int var1, RealmsServer var2) {
      String var3 = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String var4 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((var3x, var4x) -> {
         if (var3x) {
            RealmsResetWorldScreen var5 = new RealmsResetWorldScreen(this, var2, this.getNewScreen(), getLocalizedString("mco.configure.world.switch.slot"), getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, getLocalizedString("gui.cancel"));
            var5.setSlot(var1);
            var5.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
            Realms.setScreen(var5);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, var3, var4, true, 10));
   }

   protected void renderMousehoverTooltip(String var1, int var2, int var3) {
      if (var1 != null) {
         int var4 = var2 + 12;
         int var5 = var3 - 12;
         int var6 = this.fontWidth(var1);
         if (var4 + var6 + 3 > this.right_x) {
            var4 = var4 - var6 - 20;
         }

         this.fillGradient(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(var1, var4, var5, 16777215);
      }
   }

   private void drawServerStatus(int var1, int var2, int var3, int var4) {
      if (this.serverData.expired) {
         this.drawExpired(var1, var2, var3, var4);
      } else if (this.serverData.state == RealmsServer.State.CLOSED) {
         this.drawClose(var1, var2, var3, var4);
      } else if (this.serverData.state == RealmsServer.State.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.drawExpiring(var1, var2, var3, var4, this.serverData.daysLeft);
         } else {
            this.drawOpen(var1, var2, var3, var4);
         }
      }

   }

   private void drawExpired(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if (var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void drawExpiring(int var1, int var2, int var3, int var4, int var5) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      if (this.animTick % 20 < 10) {
         RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(var1, var2, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      GlStateManager.popMatrix();
      if (var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27) {
         if (var5 <= 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if (var5 == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{var5});
         }
      }

   }

   private void drawOpen(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if (var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void drawClose(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if (var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
   }

   private void hideRegularButtons() {
      this.hide(this.optionsButton);
      this.hide(this.backupButton);
      this.hide(this.resetWorldButton);
   }

   private void hide(RealmsButton var1) {
      var1.setVisible(false);
      this.removeButton(var1);
   }

   private void showRegularButtons() {
      this.show(this.optionsButton);
      this.show(this.backupButton);
      this.show(this.resetWorldButton);
   }

   private void show(RealmsButton var1) {
      var1.setVisible(true);
      this.buttonsAdd(var1);
   }

   private void hideMinigameButtons() {
      this.hide(this.switchMinigameButton);
   }

   private void showMinigameButtons() {
      this.show(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions var1) {
      RealmsWorldOptions var2 = (RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot);
      var1.templateId = var2.templateId;
      var1.templateImage = var2.templateImage;
      RealmsClient var3 = RealmsClient.createRealmsClient();

      try {
         var3.updateSlot(this.serverData.id, this.serverData.activeSlot, var1);
         this.serverData.slots.put(this.serverData.activeSlot, var1);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't save slot settings");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
         return;
      } catch (UnsupportedEncodingException var6) {
         LOGGER.error("Couldn't save slot settings");
      }

      Realms.setScreen(this);
   }

   public void saveSettings(String var1, String var2) {
      String var3 = var2 != null && !var2.trim().isEmpty() ? var2 : null;
      RealmsClient var4 = RealmsClient.createRealmsClient();

      try {
         var4.update(this.serverData.id, var1, var3);
         this.serverData.setName(var1);
         this.serverData.setDescription(var3);
      } catch (RealmsServiceException var6) {
         LOGGER.error("Couldn't save settings");
         Realms.setScreen(new RealmsGenericErrorScreen(var6, this));
         return;
      } catch (UnsupportedEncodingException var7) {
         LOGGER.error("Couldn't save settings");
      }

      Realms.setScreen(this);
   }

   public void openTheWorld(boolean var1, RealmsScreen var2) {
      RealmsTasks.OpenServerTask var3 = new RealmsTasks.OpenServerTask(this.serverData, this, this.lastScreen, var1);
      RealmsLongRunningMcoTaskScreen var4 = new RealmsLongRunningMcoTaskScreen(var2, var3);
      var4.start();
      Realms.setScreen(var4);
   }

   public void closeTheWorld(RealmsScreen var1) {
      RealmsTasks.CloseServerTask var2 = new RealmsTasks.CloseServerTask(this.serverData, this);
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(var1, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   void callback(WorldTemplate var1) {
      if (var1 != null) {
         if (WorldTemplate.WorldTemplateType.MINIGAME.equals(var1.type)) {
            this.switchMinigame(var1);
         }

      }
   }

   private void switchSlot(long var1, int var3) {
      RealmsConfigureWorldScreen var4 = this.getNewScreen();
      RealmsTasks.SwitchSlotTask var5 = new RealmsTasks.SwitchSlotTask(var1, var3, (var1x, var2) -> {
         Realms.setScreen(var4);
      }, 11);
      RealmsLongRunningMcoTaskScreen var6 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var5);
      var6.start();
      Realms.setScreen(var6);
   }

   private void switchMinigame(WorldTemplate var1) {
      RealmsTasks.SwitchMinigameTask var2 = new RealmsTasks.SwitchMinigameTask(this.serverData.id, var1, this.getNewScreen());
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}
