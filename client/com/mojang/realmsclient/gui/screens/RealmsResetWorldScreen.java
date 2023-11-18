package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.ResettingGeneratedWorldTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsResetWorldScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   static final ResourceLocation SLOT_FRAME_SPRITE = new ResourceLocation("widget/slot_frame");
   private static final Component CREATE_REALM_TITLE = Component.translatable("mco.selectServer.create");
   private static final Component CREATE_REALM_SUBTITLE = Component.translatable("mco.selectServer.create.subtitle");
   private static final Component CREATE_WORLD_TITLE = Component.translatable("mco.configure.world.switch.slot");
   private static final Component CREATE_WORLD_SUBTITLE = Component.translatable("mco.configure.world.switch.slot.subtitle");
   private static final Component RESET_WORLD_TITLE = Component.translatable("mco.reset.world.title");
   private static final Component RESET_WORLD_SUBTITLE = Component.translatable("mco.reset.world.warning");
   public static final Component CREATE_WORLD_RESET_TASK_TITLE = Component.translatable("mco.create.world.reset.title");
   private static final Component RESET_WORLD_RESET_TASK_TITLE = Component.translatable("mco.reset.world.resetting.screen.title");
   private static final Component WORLD_TEMPLATES_TITLE = Component.translatable("mco.reset.world.template");
   private static final Component ADVENTURES_TITLE = Component.translatable("mco.reset.world.adventure");
   private static final Component EXPERIENCES_TITLE = Component.translatable("mco.reset.world.experience");
   private static final Component INSPIRATION_TITLE = Component.translatable("mco.reset.world.inspiration");
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private final Component subtitle;
   private final int subtitleColor;
   private final Component resetTaskTitle;
   private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("textures/gui/realms/inspiration.png");
   WorldTemplatePaginatedList templates;
   WorldTemplatePaginatedList adventuremaps;
   WorldTemplatePaginatedList experiences;
   WorldTemplatePaginatedList inspirations;
   public final int slot;
   private final Runnable resetWorldRunnable;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   private RealmsResetWorldScreen(Screen var1, RealmsServer var2, int var3, Component var4, Component var5, int var6, Component var7, Runnable var8) {
      super(var4);
      this.lastScreen = var1;
      this.serverData = var2;
      this.slot = var3;
      this.subtitle = var5;
      this.subtitleColor = var6;
      this.resetTaskTitle = var7;
      this.resetWorldRunnable = var8;
   }

   public static RealmsResetWorldScreen forNewRealm(Screen var0, RealmsServer var1, Runnable var2) {
      return new RealmsResetWorldScreen(var0, var1, var1.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, var2);
   }

   public static RealmsResetWorldScreen forEmptySlot(Screen var0, int var1, RealmsServer var2, Runnable var3) {
      return new RealmsResetWorldScreen(var0, var2, var1, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, var3);
   }

   public static RealmsResetWorldScreen forResetSlot(Screen var0, RealmsServer var1, Runnable var2) {
      return new RealmsResetWorldScreen(var0, var1, var1.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESET_WORLD_RESET_TASK_TITLE, var2);
   }

   @Override
   public void init() {
      LinearLayout var1 = LinearLayout.vertical();
      var1.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
      var1.addChild(SpacerElement.height(3));
      var1.addChild(new StringWidget(this.subtitle, this.font).setColor(this.subtitleColor), LayoutSettings::alignHorizontallyCenter);
      this.layout.addToHeader(var1);
      (new Thread("Realms-reset-world-fetcher") {
         @Override
         public void run() {
            RealmsClient var1 = RealmsClient.create();

            try {
               WorldTemplatePaginatedList var2 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
               WorldTemplatePaginatedList var3 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
               WorldTemplatePaginatedList var4 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
               WorldTemplatePaginatedList var5 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
               RealmsResetWorldScreen.this.minecraft.execute(() -> {
                  RealmsResetWorldScreen.this.templates = var2;
                  RealmsResetWorldScreen.this.adventuremaps = var3;
                  RealmsResetWorldScreen.this.experiences = var4;
                  RealmsResetWorldScreen.this.inspirations = var5;
               });
            } catch (RealmsServiceException var6) {
               RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", var6);
            }
         }
      }).start();
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(1),
            row(0) + 10,
            RealmsResetNormalWorldScreen.TITLE,
            NEW_WORLD_LOCATION,
            var1x -> this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this::generationSelectionCallback, this.title))
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(2),
            row(0) + 10,
            RealmsSelectFileToUploadScreen.TITLE,
            UPLOAD_LOCATION,
            var1x -> this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot, this))
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(3),
            row(0) + 10,
            WORLD_TEMPLATES_TITLE,
            SURVIVAL_SPAWN_LOCATION,
            var1x -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(WORLD_TEMPLATES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates)
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(1),
            row(6) + 20,
            ADVENTURES_TITLE,
            ADVENTURE_MAP_LOCATION,
            var1x -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        ADVENTURES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps
                     )
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(2),
            row(6) + 20,
            EXPERIENCES_TITLE,
            EXPERIENCE_LOCATION,
            var1x -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        EXPERIENCES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.EXPERIENCE, this.experiences
                     )
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(3),
            row(6) + 20,
            INSPIRATION_TITLE,
            INSPIRATION_LOCATION,
            var1x -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        INSPIRATION_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.INSPIRATION, this.inspirations
                     )
                  )
         )
      );
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.layout.arrangeElements();
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.subtitle);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private int frame(int var1) {
      return this.width / 2 - 130 + (var1 - 1) * 100;
   }

   private void startTask(LongRunningTask var1) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, var1));
   }

   public void switchSlot(Runnable var1) {
      this.startTask(new SwitchSlotTask(this.serverData.id, this.slot, () -> this.minecraft.execute(var1)));
   }

   private void templateSelectionCallback(@Nullable WorldTemplate var1) {
      this.minecraft.setScreen(this);
      if (var1 != null) {
         this.resetWorld(() -> this.startTask(new ResettingTemplateWorldTask(var1, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable)));
      }
   }

   private void generationSelectionCallback(@Nullable WorldGenerationInfo var1) {
      this.minecraft.setScreen(this);
      if (var1 != null) {
         this.resetWorld(() -> this.startTask(new ResettingGeneratedWorldTask(var1, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable)));
      }
   }

   private void resetWorld(Runnable var1) {
      if (this.slot == -1) {
         var1.run();
      } else {
         this.switchSlot(var1);
      }
   }

   class FrameButton extends Button {
      private static final int WIDTH = 60;
      private static final int HEIGHT = 72;
      private static final int IMAGE_SIZE = 56;
      private final ResourceLocation image;

      FrameButton(int var2, int var3, Component var4, ResourceLocation var5, Button.OnPress var6) {
         super(var2, var3, 60, 72, var4, var6, DEFAULT_NARRATION);
         this.image = var5;
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         boolean var5 = this.isHoveredOrFocused();
         if (var5) {
            var1.setColor(0.56F, 0.56F, 0.56F, 1.0F);
         }

         int var6 = this.getX();
         int var7 = this.getY();
         var1.blit(this.image, var6 + 2, var7 + 14, 0.0F, 0.0F, 56, 56, 56, 56);
         var1.blitSprite(RealmsResetWorldScreen.SLOT_FRAME_SPRITE, var6, var7 + 12, 60, 60);
         var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         int var8 = var5 ? -6250336 : -1;
         var1.drawCenteredString(RealmsResetWorldScreen.this.font, this.getMessage(), var6 + 30, var7, var8);
      }
   }
}
