package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsMainScreen mainScreen;
   private RealmsServer serverData;
   private final long serverId;
   private String title = getLocalizedString("mco.brokenworld.title");
   private final String message = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
   private int left_x;
   private int right_x;
   private final int default_button_width = 80;
   private final int default_button_offset = 5;
   private static final List<Integer> playButtonIds = Arrays.asList(1, 2, 3);
   private static final List<Integer> resetButtonIds = Arrays.asList(4, 5, 6);
   private static final List<Integer> downloadButtonIds = Arrays.asList(7, 8, 9);
   private static final List<Integer> downloadConfirmationIds = Arrays.asList(10, 11, 12);
   private final List<Integer> slotsThatHasBeenDownloaded = new ArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(RealmsScreen var1, RealmsMainScreen var2, long var3) {
      super();
      this.lastScreen = var1;
      this.mainScreen = var2;
      this.serverId = var3;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public void init() {
      this.left_x = this.width() / 2 - 150;
      this.right_x = this.width() / 2 + 190;
      this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsBrokenWorldScreen.this.backButtonClicked();
         }
      });
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }

      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void addButtons() {
      Iterator var1 = this.serverData.slots.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         RealmsWorldOptions var3 = (RealmsWorldOptions)var2.getValue();
         boolean var4 = (Integer)var2.getKey() != this.serverData.activeSlot || this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
         Object var5;
         if (var4) {
            var5 = new RealmsBrokenWorldScreen.PlayButton((Integer)playButtonIds.get((Integer)var2.getKey() - 1), this.getFramePositionX((Integer)var2.getKey()), getLocalizedString("mco.brokenworld.play"));
         } else {
            var5 = new RealmsBrokenWorldScreen.DownloadButton((Integer)downloadButtonIds.get((Integer)var2.getKey() - 1), this.getFramePositionX((Integer)var2.getKey()), getLocalizedString("mco.brokenworld.download"));
         }

         if (this.slotsThatHasBeenDownloaded.contains(var2.getKey())) {
            ((RealmsButton)var5).active(false);
            ((RealmsButton)var5).setMessage(getLocalizedString("mco.brokenworld.downloaded"));
         }

         this.buttonsAdd((AbstractRealmsButton)var5);
         this.buttonsAdd(new RealmsButton((Integer)resetButtonIds.get((Integer)var2.getKey() - 1), this.getFramePositionX((Integer)var2.getKey()), RealmsConstants.row(10), 80, 20, getLocalizedString("mco.brokenworld.reset")) {
            public void onPress() {
               int var1 = RealmsBrokenWorldScreen.resetButtonIds.indexOf(this.id()) + 1;
               RealmsResetWorldScreen var2 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this);
               if (var1 != RealmsBrokenWorldScreen.this.serverData.activeSlot || RealmsBrokenWorldScreen.this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                  var2.setSlot(var1);
               }

               var2.setConfirmationId(14);
               Realms.setScreen(var2);
            }
         });
      }

   }

   public void tick() {
      ++this.animTick;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.drawCenteredString(this.title, this.width() / 2, 17, 16777215);
      String[] var4 = this.message.split("\\\\n");

      for(int var5 = 0; var5 < var4.length; ++var5) {
         this.drawCenteredString(var4[var5], this.width() / 2, RealmsConstants.row(-1) + 3 + var5 * 12, 10526880);
      }

      if (this.serverData != null) {
         Iterator var7 = this.serverData.slots.entrySet().iterator();

         while(true) {
            while(var7.hasNext()) {
               Entry var6 = (Entry)var7.next();
               if (((RealmsWorldOptions)var6.getValue()).templateImage != null && ((RealmsWorldOptions)var6.getValue()).templateId != -1L) {
                  this.drawSlotFrame(this.getFramePositionX((Integer)var6.getKey()), RealmsConstants.row(1) + 5, var1, var2, this.serverData.activeSlot == (Integer)var6.getKey() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName((Integer)var6.getKey()), (Integer)var6.getKey(), ((RealmsWorldOptions)var6.getValue()).templateId, ((RealmsWorldOptions)var6.getValue()).templateImage, ((RealmsWorldOptions)var6.getValue()).empty);
               } else {
                  this.drawSlotFrame(this.getFramePositionX((Integer)var6.getKey()), RealmsConstants.row(1) + 5, var1, var2, this.serverData.activeSlot == (Integer)var6.getKey() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName((Integer)var6.getKey()), (Integer)var6.getKey(), -1L, (String)null, ((RealmsWorldOptions)var6.getValue()).empty);
               }
            }

            return;
         }
      }
   }

   private int getFramePositionX(int var1) {
      return this.left_x + (var1 - 1) * 110;
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
      Realms.setScreen(this.lastScreen);
   }

   private void fetchServerData(final long var1) {
      (new Thread() {
         public void run() {
            RealmsClient var1x = RealmsClient.createRealmsClient();

            try {
               RealmsBrokenWorldScreen.this.serverData = var1x.getOwnWorld(var1);
               RealmsBrokenWorldScreen.this.addButtons();
            } catch (RealmsServiceException var3) {
               RealmsBrokenWorldScreen.LOGGER.error("Couldn't get own world");
               Realms.setScreen(new RealmsGenericErrorScreen(var3.getMessage(), RealmsBrokenWorldScreen.this.lastScreen));
            } catch (IOException var4) {
               RealmsBrokenWorldScreen.LOGGER.error("Couldn't parse response getting own world");
            }

         }
      }).start();
   }

   public void confirmResult(boolean var1, int var2) {
      if (!var1) {
         Realms.setScreen(this);
      } else {
         if (var2 != 13 && var2 != 14) {
            if (downloadButtonIds.contains(var2)) {
               this.downloadWorld(downloadButtonIds.indexOf(var2) + 1);
            } else if (downloadConfirmationIds.contains(var2)) {
               this.slotsThatHasBeenDownloaded.add(downloadConfirmationIds.indexOf(var2) + 1);
               this.childrenClear();
               this.addButtons();
            }
         } else {
            (new Thread() {
               public void run() {
                  RealmsClient var1 = RealmsClient.createRealmsClient();
                  if (RealmsBrokenWorldScreen.this.serverData.state.equals(RealmsServer.State.CLOSED)) {
                     RealmsTasks.OpenServerTask var2 = new RealmsTasks.OpenServerTask(RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.lastScreen, true);
                     RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(RealmsBrokenWorldScreen.this, var2);
                     var3.start();
                     Realms.setScreen(var3);
                  } else {
                     try {
                        RealmsBrokenWorldScreen.this.mainScreen.newScreen().play(var1.getOwnWorld(RealmsBrokenWorldScreen.this.serverId), RealmsBrokenWorldScreen.this);
                     } catch (RealmsServiceException var4) {
                        RealmsBrokenWorldScreen.LOGGER.error("Couldn't get own world");
                        Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                     } catch (IOException var5) {
                        RealmsBrokenWorldScreen.LOGGER.error("Couldn't parse response getting own world");
                        Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                     }
                  }

               }
            }).start();
         }

      }
   }

   private void downloadWorld(int var1) {
      RealmsClient var2 = RealmsClient.createRealmsClient();

      try {
         WorldDownload var3 = var2.download(this.serverData.id, var1);
         RealmsDownloadLatestWorldScreen var4 = new RealmsDownloadLatestWorldScreen(this, var3, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(var1)).getSlotName(var1) + ")");
         var4.setConfirmationId((Integer)downloadConfirmationIds.get(var1 - 1));
         Realms.setScreen(var4);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't download world data");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
   }

   private void drawSlotFrame(int var1, int var2, int var3, int var4, boolean var5, String var6, int var7, long var8, String var10, boolean var11) {
      if (var11) {
         bind("realms:textures/gui/realms/empty_frame.png");
      } else if (var10 != null && var8 != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var8), var10);
      } else if (var7 == 1) {
         bind("textures/gui/title/background/panorama_0.png");
      } else if (var7 == 2) {
         bind("textures/gui/title/background/panorama_2.png");
      } else if (var7 == 3) {
         bind("textures/gui/title/background/panorama_3.png");
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if (!var5) {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (var5) {
         float var12 = 0.9F + 0.1F * RealmsMth.cos((float)this.animTick * 0.2F);
         GlStateManager.color4f(var12, var12, var12, 1.0F);
      }

      RealmsScreen.blit(var1 + 3, var2 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      bind("realms:textures/gui/realms/slot_frame.png");
      if (var5) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(var6, var1 + 40, var2 + 66, 16777215);
   }

   private void switchSlot(int var1) {
      RealmsTasks.SwitchSlotTask var2 = new RealmsTasks.SwitchSlotTask(this.serverData.id, var1, this, 13);
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   class DownloadButton extends RealmsButton {
      public DownloadButton(int var2, int var3, String var4) {
         super(var2, var3, RealmsConstants.row(8), 80, 20, var4);
      }

      public void onPress() {
         String var1 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
         String var2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, var1, var2, true, this.id()));
      }
   }

   class PlayButton extends RealmsButton {
      public PlayButton(int var2, int var3, String var4) {
         super(var2, var3, RealmsConstants.row(8), 80, 20, var4);
      }

      public void onPress() {
         int var1 = RealmsBrokenWorldScreen.playButtonIds.indexOf(this.id()) + 1;
         if (((RealmsWorldOptions)RealmsBrokenWorldScreen.this.serverData.slots.get(var1)).empty) {
            RealmsResetWorldScreen var2 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
            var2.setSlot(var1);
            var2.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
            var2.setConfirmationId(14);
            Realms.setScreen(var2);
         } else {
            RealmsBrokenWorldScreen.this.switchSlot(var1);
         }

      }
   }
}
