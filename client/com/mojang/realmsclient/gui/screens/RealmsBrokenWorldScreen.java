package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_BUTTON_WIDTH = 80;
   private final Screen lastScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private final Component[] message = new Component[]{Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")};
   private int leftX;
   private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(Screen var1, long var2, boolean var4) {
      super(var4 ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
      this.lastScreen = var1;
      this.serverId = var2;
   }

   public void init() {
      this.leftX = this.width / 2 - 150;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.onClose();
      }).bounds((this.width - 150) / 2, row(13) - 5, 150, 20).build());
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }

   }

   public Component getNarrationMessage() {
      return ComponentUtils.formatList((Collection)Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), CommonComponents.SPACE);
   }

   private void addButtons() {
      Button var5;
      for(Iterator var1 = this.serverData.slots.entrySet().iterator(); var1.hasNext(); this.addRenderableWidget(var5)) {
         Map.Entry var2 = (Map.Entry)var1.next();
         int var3 = (Integer)var2.getKey();
         boolean var4 = var3 != this.serverData.activeSlot || this.serverData.isMinigameActive();
         if (var4) {
            var5 = Button.builder(Component.translatable("mco.brokenworld.play"), (var2x) -> {
               this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new LongRunningTask[]{new SwitchSlotTask(this.serverData.id, var3, this::doSwitchOrReset)}));
            }).bounds(this.getFramePositionX(var3), row(8), 80, 20).build();
            var5.active = !((RealmsWorldOptions)this.serverData.slots.get(var3)).empty;
         } else {
            var5 = Button.builder(Component.translatable("mco.brokenworld.download"), (var2x) -> {
               this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, Component.translatable("mco.configure.world.restore.download.question.line1"), (var2) -> {
                  this.downloadWorld(var3);
               }));
            }).bounds(this.getFramePositionX(var3), row(8), 80, 20).build();
         }

         if (this.slotsThatHasBeenDownloaded.contains(var3)) {
            var5.active = false;
            var5.setMessage(Component.translatable("mco.brokenworld.downloaded"));
         }
      }

   }

   public void tick() {
      ++this.animTick;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 17, -1);

      for(int var5 = 0; var5 < this.message.length; ++var5) {
         var1.drawCenteredString(this.font, this.message[var5], this.width / 2, row(-1) + 3 + var5 * 12, -6250336);
      }

      if (this.serverData != null) {
         Iterator var7 = this.serverData.slots.entrySet().iterator();

         while(true) {
            while(var7.hasNext()) {
               Map.Entry var6 = (Map.Entry)var7.next();
               if (((RealmsWorldOptions)var6.getValue()).templateImage != null && ((RealmsWorldOptions)var6.getValue()).templateId != -1L) {
                  this.drawSlotFrame(var1, this.getFramePositionX((Integer)var6.getKey()), row(1) + 5, var2, var3, this.serverData.activeSlot == (Integer)var6.getKey() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName((Integer)var6.getKey()), (Integer)var6.getKey(), ((RealmsWorldOptions)var6.getValue()).templateId, ((RealmsWorldOptions)var6.getValue()).templateImage, ((RealmsWorldOptions)var6.getValue()).empty);
               } else {
                  this.drawSlotFrame(var1, this.getFramePositionX((Integer)var6.getKey()), row(1) + 5, var2, var3, this.serverData.activeSlot == (Integer)var6.getKey() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName((Integer)var6.getKey()), (Integer)var6.getKey(), -1L, (String)null, ((RealmsWorldOptions)var6.getValue()).empty);
               }
            }

            return;
         }
      }
   }

   private int getFramePositionX(int var1) {
      return this.leftX + (var1 - 1) * 110;
   }

   private void fetchServerData(long var1) {
      (new Thread(() -> {
         RealmsClient var3 = RealmsClient.create();

         try {
            this.serverData = var3.getOwnRealm(var1);
            this.addButtons();
         } catch (RealmsServiceException var5) {
            LOGGER.error("Couldn't get own world", var5);
            this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
         }

      })).start();
   }

   public void doSwitchOrReset() {
      (new Thread(() -> {
         RealmsClient var1 = RealmsClient.create();
         if (this.serverData.state == RealmsServer.State.CLOSED) {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new LongRunningTask[]{new OpenServerTask(this.serverData, this, true, this.minecraft)}));
            });
         } else {
            try {
               RealmsServer var2 = var1.getOwnRealm(this.serverId);
               this.minecraft.execute(() -> {
                  RealmsMainScreen.play(var2, this);
               });
            } catch (RealmsServiceException var3) {
               LOGGER.error("Couldn't get own world", var3);
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.lastScreen);
               });
            }
         }

      })).start();
   }

   private void downloadWorld(int var1) {
      RealmsClient var2 = RealmsClient.create();

      try {
         WorldDownload var3 = var2.requestDownloadInfo(this.serverData.id, var1);
         RealmsDownloadLatestWorldScreen var4 = new RealmsDownloadLatestWorldScreen(this, var3, this.serverData.getWorldName(var1), (var2x) -> {
            if (var2x) {
               this.slotsThatHasBeenDownloaded.add(var1);
               this.clearWidgets();
               this.addButtons();
            } else {
               this.minecraft.setScreen(this);
            }

         });
         this.minecraft.setScreen(var4);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't download world data", var5);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this));
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.isMinigameActive();
   }

   private void drawSlotFrame(GuiGraphics var1, int var2, int var3, int var4, int var5, boolean var6, String var7, int var8, long var9, @Nullable String var11, boolean var12) {
      ResourceLocation var13;
      if (var12) {
         var13 = RealmsWorldSlotButton.EMPTY_SLOT_LOCATION;
      } else if (var11 != null && var9 != -1L) {
         var13 = RealmsTextureManager.worldTemplate(String.valueOf(var9), var11);
      } else if (var8 == 1) {
         var13 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1;
      } else if (var8 == 2) {
         var13 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2;
      } else if (var8 == 3) {
         var13 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3;
      } else {
         var13 = RealmsTextureManager.worldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if (!var6) {
         var1.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (var6) {
         float var14 = 0.9F + 0.1F * Mth.cos((float)this.animTick * 0.2F);
         var1.setColor(var14, var14, var14, 1.0F);
      }

      var1.blit(var13, var2 + 3, var3 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      if (var6) {
         var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         var1.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      var1.blitSprite(SLOT_FRAME_SPRITE, var2, var3, 80, 80);
      var1.drawCenteredString(this.font, (String)var7, var2 + 40, var3 + 66, -1);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
