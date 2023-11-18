package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
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
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsConfigureWorldScreen extends RealmsScreen {
   private static final ResourceLocation EXPIRED_SPRITE = new ResourceLocation("realm_status/expired");
   private static final ResourceLocation EXPIRES_SOON_SPRITE = new ResourceLocation("realm_status/expires_soon");
   private static final ResourceLocation OPEN_SPRITE = new ResourceLocation("realm_status/open");
   private static final ResourceLocation CLOSED_SPRITE = new ResourceLocation("realm_status/closed");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component WORLD_LIST_TITLE = Component.translatable("mco.configure.worlds.title");
   private static final Component TITLE = Component.translatable("mco.configure.world.title");
   private static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
   private static final Component SERVER_EXPIRING_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRING_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
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
   private int clicks;
   private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

   public RealmsConfigureWorldScreen(RealmsMainScreen var1, long var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.serverId = var2;
   }

   @Override
   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.leftX = this.width / 2 - 187;
      this.rightX = this.width / 2 + 190;
      this.playersButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.players"), var1x -> this.minecraft.setScreen(new RealmsPlayerScreen(this, this.serverData))
            )
            .bounds(this.centerButton(0, 3), row(0), 100, 20)
            .build()
      );
      this.settingsButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.settings"),
               var1x -> this.minecraft.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()))
            )
            .bounds(this.centerButton(1, 3), row(0), 100, 20)
            .build()
      );
      this.subscriptionButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.subscription"),
               var1x -> this.minecraft.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen))
            )
            .bounds(this.centerButton(2, 3), row(0), 100, 20)
            .build()
      );
      this.slotButtonList.clear();

      for(int var1 = 1; var1 < 5; ++var1) {
         this.slotButtonList.add(this.addSlotButton(var1));
      }

      this.switchMinigameButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.switchminigame"),
               var1x -> this.minecraft
                     .setScreen(
                        new RealmsSelectWorldTemplateScreen(
                           Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME
                        )
                     )
            )
            .bounds(this.leftButton(0), row(13) - 5, 100, 20)
            .build()
      );
      this.optionsButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.options"),
               var1x -> this.minecraft
                     .setScreen(
                        new RealmsSlotOptionsScreen(
                           this, this.serverData.slots.get(this.serverData.activeSlot).clone(), this.serverData.worldType, this.serverData.activeSlot
                        )
                     )
            )
            .bounds(this.leftButton(0), row(13) - 5, 90, 20)
            .build()
      );
      this.backupButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.backup"),
               var1x -> this.minecraft.setScreen(new RealmsBackupScreen(this, this.serverData.clone(), this.serverData.activeSlot))
            )
            .bounds(this.leftButton(1), row(13) - 5, 90, 20)
            .build()
      );
      this.resetWorldButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.resetworld"),
               var1x -> this.minecraft
                     .setScreen(
                        RealmsResetWorldScreen.forResetSlot(
                           this, this.serverData.clone(), () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen()))
                        )
                     )
            )
            .bounds(this.leftButton(2), row(13) - 5, 90, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1x -> this.backButtonClicked()).bounds(this.rightX - 80 + 8, row(13) - 5, 70, 20).build()
      );
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
      RealmsWorldSlotButton var4 = new RealmsWorldSlotButton(var2, var3, 80, 80, () -> this.serverData, var1x -> this.toolTip = var1x, var1, var2x -> {
         RealmsWorldSlotButton.State var3x = ((RealmsWorldSlotButton)var2x).getState();
         if (var3x != null) {
            switch(var3x.action) {
               case NOTHING:
                  break;
               case JOIN:
                  this.joinRealm(this.serverData);
                  break;
               case SWITCH_SLOT:
                  if (var3x.minigame) {
                     this.switchToMinigame();
                  } else if (var3x.empty) {
                     this.switchToEmptySlot(var1, this.serverData);
                  } else {
                     this.switchToFullSlot(var1, this.serverData);
                  }
                  break;
               default:
                  throw new IllegalStateException("Unknown action " + var3x.action);
            }
         }
      });
      return this.addRenderableWidget(var4);
   }

   private int leftButton(int var1) {
      return this.leftX + var1 * 95;
   }

   private int centerButton(int var1, int var2) {
      return this.width / 2 - (var2 * 105 - 5) / 2 + var1 * 105;
   }

   @Override
   public void tick() {
      super.tick();
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

      this.slotButtonList.forEach(RealmsWorldSlotButton::tick);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.toolTip = null;
      var1.drawCenteredString(this.font, WORLD_LIST_TITLE, this.width / 2, row(4), -1);
      if (this.serverData == null) {
         var1.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
      } else {
         String var5 = this.serverData.getName();
         int var6 = this.font.width(var5);
         int var7 = this.serverData.state == RealmsServer.State.CLOSED ? -6250336 : 8388479;
         int var8 = this.font.width(this.title);
         var1.drawCenteredString(this.font, this.title, this.width / 2, 12, -1);
         var1.drawCenteredString(this.font, var5, this.width / 2, 24, var7);
         int var9 = Math.min(this.centerButton(2, 3) + 80 - 11, this.width / 2 + var6 / 2 + var8 / 2 + 10);
         this.drawServerStatus(var1, var9, 7, var2, var3);
         if (this.isMinigame()) {
            var1.drawString(
               this.font,
               Component.translatable("mco.configure.world.minigame", this.serverData.getMinigameName()),
               this.leftX + 80 + 20 + 10,
               row(13),
               -1,
               false
            );
         }

         if (this.toolTip != null) {
            var1.renderTooltip(this.font, this.toolTip, var2, var3);
         }
      }
   }

   private int frame(int var1) {
      return this.leftX + (var1 - 1) * 98;
   }

   @Override
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
      new Thread(() -> {
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
            LOGGER.error("Couldn't get own world", var5);
            this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen)));
         }
      }).start();
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
         RealmsMainScreen.play(var1, new RealmsConfigureWorldScreen(this.lastScreen, this.serverId));
      } else {
         this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen, this.serverId));
      }
   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(
         Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME
      );
      var1.setWarning(Component.translatable("mco.minigame.world.info.line1"), Component.translatable("mco.minigame.world.info.line2"));
      this.minecraft.setScreen(var1);
   }

   private void switchToFullSlot(int var1, RealmsServer var2) {
      MutableComponent var3 = Component.translatable("mco.configure.world.slot.switch.question.line1");
      MutableComponent var4 = Component.translatable("mco.configure.world.slot.switch.question.line2");
      this.minecraft
         .setScreen(
            new RealmsLongConfirmationScreen(
               var3x -> {
                  if (var3x) {
                     this.minecraft
                        .setScreen(
                           new RealmsLongRunningMcoTaskScreen(
                              this.lastScreen,
                              new SwitchSlotTask(var2.id, var1, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen())))
                           )
                        );
                  } else {
                     this.minecraft.setScreen(this);
                  }
               },
               RealmsLongConfirmationScreen.Type.INFO,
               var3,
               var4,
               true
            )
         );
   }

   private void switchToEmptySlot(int var1, RealmsServer var2) {
      MutableComponent var3 = Component.translatable("mco.configure.world.slot.switch.question.line1");
      MutableComponent var4 = Component.translatable("mco.configure.world.slot.switch.question.line2");
      this.minecraft
         .setScreen(
            new RealmsLongConfirmationScreen(
               var3x -> {
                  if (var3x) {
                     RealmsResetWorldScreen var4x = RealmsResetWorldScreen.forEmptySlot(
                        this, var1, var2, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.getNewScreen()))
                     );
                     this.minecraft.setScreen(var4x);
                  } else {
                     this.minecraft.setScreen(this);
                  }
               },
               RealmsLongConfirmationScreen.Type.INFO,
               var3,
               var4,
               true
            )
         );
   }

   private void drawServerStatus(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (this.serverData.expired) {
         this.drawRealmStatus(var1, var2, var3, var4, var5, EXPIRED_SPRITE, () -> SERVER_EXPIRED_TOOLTIP);
      } else if (this.serverData.state == RealmsServer.State.CLOSED) {
         this.drawRealmStatus(var1, var2, var3, var4, var5, CLOSED_SPRITE, () -> SERVER_CLOSED_TOOLTIP);
      } else if (this.serverData.state == RealmsServer.State.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.drawRealmStatus(
               var1,
               var2,
               var3,
               var4,
               var5,
               EXPIRES_SOON_SPRITE,
               () -> {
                  if (this.serverData.daysLeft <= 0) {
                     return SERVER_EXPIRING_SOON_TOOLTIP;
                  } else {
                     return (Component)(this.serverData.daysLeft == 1
                        ? SERVER_EXPIRING_IN_DAY_TOOLTIP
                        : Component.translatable("mco.selectServer.expires.days", this.serverData.daysLeft));
                  }
               }
            );
         } else {
            this.drawRealmStatus(var1, var2, var3, var4, var5, OPEN_SPRITE, () -> SERVER_OPEN_TOOLTIP);
         }
      }
   }

   private void drawRealmStatus(GuiGraphics var1, int var2, int var3, int var4, int var5, ResourceLocation var6, Supplier<Component> var7) {
      var1.blitSprite(var6, var2, var3, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27) {
         this.toolTip = (Component)var7.get();
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
   }

   private void show(Button var1) {
      var1.visible = true;
   }

   private void hideMinigameButtons() {
      this.hide(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions var1) {
      RealmsWorldOptions var2 = this.serverData.slots.get(this.serverData.activeSlot);
      var1.templateId = var2.templateId;
      var1.templateImage = var2.templateImage;
      RealmsClient var3 = RealmsClient.create();

      try {
         var3.updateSlot(this.serverData.id, this.serverData.activeSlot, var1);
         this.serverData.slots.put(this.serverData.activeSlot, var1);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't save slot settings", var5);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void saveSettings(String var1, String var2) {
      String var3 = Util.isBlank(var2) ? null : var2;
      RealmsClient var4 = RealmsClient.create();

      try {
         var4.update(this.serverData.id, var1, var3);
         this.serverData.setName(var1);
         this.serverData.setDescription(var3);
      } catch (RealmsServiceException var6) {
         LOGGER.error("Couldn't save settings", var6);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var6, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void openTheWorld(boolean var1, Screen var2) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var2, new OpenServerTask(this.serverData, this, var1, this.minecraft)));
   }

   public void closeTheWorld(Screen var1) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var1, new CloseServerTask(this.serverData, this)));
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   private void templateSelectionCallback(@Nullable WorldTemplate var1) {
      if (var1 != null && WorldTemplate.WorldTemplateType.MINIGAME == var1.type) {
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameTask(this.serverData.id, var1, this.getNewScreen())));
      } else {
         this.minecraft.setScreen(this);
      }
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}
