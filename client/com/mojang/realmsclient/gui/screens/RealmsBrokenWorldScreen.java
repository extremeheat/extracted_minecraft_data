package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_BUTTON_WIDTH = 80;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private final Component[] message = new Component[]{
      Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")
   };
   private int leftX;
   private int rightX;
   private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(Screen var1, RealmsMainScreen var2, long var3, boolean var5) {
      super(var5 ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
      this.lastScreen = var1;
      this.mainScreen = var2;
      this.serverId = var3;
   }

   @Override
   public void init() {
      this.leftX = this.width / 2 - 150;
      this.rightX = this.width / 2 + 190;
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1 -> this.backButtonClicked()).bounds(this.rightX - 80 + 8, row(13) - 5, 70, 20).build()
      );
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }
   }

   @Override
   public Component getNarrationMessage() {
      return ComponentUtils.formatList(Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), CommonComponents.SPACE);
   }

   private void addButtons() {
      for(Entry var2 : this.serverData.slots.entrySet()) {
         int var3 = var2.getKey();
         boolean var4 = var3 != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
         Button var5;
         if (var4) {
            var5 = Button.builder(
                  Component.translatable("mco.brokenworld.play"),
                  var2x -> {
                     if (this.serverData.slots.get(var3).empty) {
                        RealmsResetWorldScreen var3x = new RealmsResetWorldScreen(
                           this,
                           this.serverData,
                           Component.translatable("mco.configure.world.switch.slot"),
                           Component.translatable("mco.configure.world.switch.slot.subtitle"),
                           10526880,
                           CommonComponents.GUI_CANCEL,
                           this::doSwitchOrReset,
                           () -> {
                              this.minecraft.setScreen(this);
                              this.doSwitchOrReset();
                           }
                        );
                        var3x.setSlot(var3);
                        var3x.setResetTitle(Component.translatable("mco.create.world.reset.title"));
                        this.minecraft.setScreen(var3x);
                     } else {
                        this.minecraft
                           .setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, var3, this::doSwitchOrReset)));
                     }
                  }
               )
               .bounds(this.getFramePositionX(var3), row(8), 80, 20)
               .build();
         } else {
            var5 = Button.builder(Component.translatable("mco.brokenworld.download"), var2x -> {
               MutableComponent var3x = Component.translatable("mco.configure.world.restore.download.question.line1");
               MutableComponent var4x = Component.translatable("mco.configure.world.restore.download.question.line2");
               this.minecraft.setScreen(new RealmsLongConfirmationScreen(var2xx -> {
                  if (var2xx) {
                     this.downloadWorld(var3);
                  } else {
                     this.minecraft.setScreen(this);
                  }
               }, RealmsLongConfirmationScreen.Type.Info, var3x, var4x, true));
            }).bounds(this.getFramePositionX(var3), row(8), 80, 20).build();
         }

         if (this.slotsThatHasBeenDownloaded.contains(var3)) {
            var5.active = false;
            var5.setMessage(Component.translatable("mco.brokenworld.downloaded"));
         }

         this.addRenderableWidget(var5);
         this.addRenderableWidget(Button.builder(Component.translatable("mco.brokenworld.reset"), var2x -> {
            RealmsResetWorldScreen var3x = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
               this.minecraft.setScreen(this);
               this.doSwitchOrReset();
            });
            if (var3 != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
               var3x.setSlot(var3);
            }

            this.minecraft.setScreen(var3x);
         }).bounds(this.getFramePositionX(var3), row(10), 80, 20).build());
      }
   }

   @Override
   public void tick() {
      ++this.animTick;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);

      for(int var5 = 0; var5 < this.message.length; ++var5) {
         drawCenteredString(var1, this.font, this.message[var5], this.width / 2, row(-1) + 3 + var5 * 12, 10526880);
      }

      if (this.serverData != null) {
         for(Entry var6 : this.serverData.slots.entrySet()) {
            if (((RealmsWorldOptions)var6.getValue()).templateImage != null && ((RealmsWorldOptions)var6.getValue()).templateId != -1L) {
               this.drawSlotFrame(
                  var1,
                  this.getFramePositionX(var6.getKey()),
                  row(1) + 5,
                  var2,
                  var3,
                  this.serverData.activeSlot == var6.getKey() && !this.isMinigame(),
                  ((RealmsWorldOptions)var6.getValue()).getSlotName(var6.getKey()),
                  var6.getKey(),
                  ((RealmsWorldOptions)var6.getValue()).templateId,
                  ((RealmsWorldOptions)var6.getValue()).templateImage,
                  ((RealmsWorldOptions)var6.getValue()).empty
               );
            } else {
               this.drawSlotFrame(
                  var1,
                  this.getFramePositionX(var6.getKey()),
                  row(1) + 5,
                  var2,
                  var3,
                  this.serverData.activeSlot == var6.getKey() && !this.isMinigame(),
                  ((RealmsWorldOptions)var6.getValue()).getSlotName(var6.getKey()),
                  var6.getKey(),
                  -1L,
                  null,
                  ((RealmsWorldOptions)var6.getValue()).empty
               );
            }
         }
      }
   }

   private int getFramePositionX(int var1) {
      return this.leftX + (var1 - 1) * 110;
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
      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long var1) {
      new Thread(() -> {
         RealmsClient var3 = RealmsClient.create();

         try {
            this.serverData = var3.getOwnWorld(var1);
            this.addButtons();
         } catch (RealmsServiceException var5) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(var5.getMessage()), this.lastScreen));
         }
      }).start();
   }

   public void doSwitchOrReset() {
      new Thread(
            () -> {
               RealmsClient var1 = RealmsClient.create();
               if (this.serverData.state == RealmsServer.State.CLOSED) {
                  this.minecraft
                     .execute(
                        () -> this.minecraft
                              .setScreen(
                                 new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true, this.minecraft))
                              )
                     );
               } else {
                  try {
                     RealmsServer var2 = var1.getOwnWorld(this.serverId);
                     this.minecraft.execute(() -> this.mainScreen.newScreen().play(var2, this));
                  } catch (RealmsServiceException var3) {
                     LOGGER.error("Couldn't get own world");
                     this.minecraft.execute(() -> this.minecraft.setScreen(this.lastScreen));
                  }
               }
            }
         )
         .start();
   }

   private void downloadWorld(int var1) {
      RealmsClient var2 = RealmsClient.create();

      try {
         WorldDownload var3 = var2.requestDownloadInfo(this.serverData.id, var1);
         RealmsDownloadLatestWorldScreen var4 = new RealmsDownloadLatestWorldScreen(this, var3, this.serverData.getWorldName(var1), var2x -> {
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
         LOGGER.error("Couldn't download world data");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this));
      }
   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
   }

   private void drawSlotFrame(
      PoseStack var1, int var2, int var3, int var4, int var5, boolean var6, String var7, int var8, long var9, @Nullable String var11, boolean var12
   ) {
      if (var12) {
         RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.EMPTY_SLOT_LOCATION);
      } else if (var11 != null && var9 != -1L) {
         RenderSystem.setShaderTexture(0, RealmsTextureManager.worldTemplate(String.valueOf(var9), var11));
      } else if (var8 == 1) {
         RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1);
      } else if (var8 == 2) {
         RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2);
      } else if (var8 == 3) {
         RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3);
      } else {
         RenderSystem.setShaderTexture(0, RealmsTextureManager.worldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage));
      }

      if (!var6) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (var6) {
         float var13 = 0.9F + 0.1F * Mth.cos((float)this.animTick * 0.2F);
         RenderSystem.setShaderColor(var13, var13, var13, 1.0F);
      }

      GuiComponent.blit(var1, var2 + 3, var3 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      RenderSystem.setShaderTexture(0, RealmsWorldSlotButton.SLOT_FRAME_LOCATION);
      if (var6) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 80, 80, 80, 80);
      drawCenteredString(var1, this.font, var7, var2 + 40, var3 + 66, 16777215);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
