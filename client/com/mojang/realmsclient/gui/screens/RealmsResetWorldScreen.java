package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.worldupload.RealmsCreateWorldFlow;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class RealmsResetWorldScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component CREATE_REALM_TITLE = Component.translatable("mco.selectServer.create");
   private static final Component CREATE_REALM_SUBTITLE = Component.translatable("mco.selectServer.create.subtitle");
   private static final Component CREATE_WORLD_TITLE = Component.translatable("mco.configure.world.switch.slot");
   private static final Component CREATE_WORLD_SUBTITLE = Component.translatable("mco.configure.world.switch.slot.subtitle");
   private static final Component GENERATE_NEW_WORLD = Component.translatable("mco.reset.world.generate");
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
   private static final ResourceLocation UPLOAD_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/inspiration.png");
   WorldTemplatePaginatedList templates;
   WorldTemplatePaginatedList adventuremaps;
   WorldTemplatePaginatedList experiences;
   WorldTemplatePaginatedList inspirations;
   public final int slot;
   @Nullable
   private final RealmCreationTask realmCreationTask;
   private final Runnable resetWorldRunnable;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   private RealmsResetWorldScreen(Screen var1, RealmsServer var2, int var3, Component var4, Component var5, int var6, Component var7, Runnable var8) {
      this(var1, var2, var3, var4, var5, var6, var7, null, var8);
   }

   public RealmsResetWorldScreen(
      Screen var1, RealmsServer var2, int var3, Component var4, Component var5, int var6, Component var7, @Nullable RealmCreationTask var8, Runnable var9
   ) {
      super(var4);
      this.lastScreen = var1;
      this.serverData = var2;
      this.slot = var3;
      this.subtitle = var5;
      this.subtitleColor = var6;
      this.resetTaskTitle = var7;
      this.realmCreationTask = var8;
      this.resetWorldRunnable = var9;
   }

   public static RealmsResetWorldScreen forNewRealm(Screen var0, RealmsServer var1, RealmCreationTask var2, Runnable var3) {
      return new RealmsResetWorldScreen(
         var0, var1, var1.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, var2, var3
      );
   }

   public static RealmsResetWorldScreen forEmptySlot(Screen var0, int var1, RealmsServer var2, Runnable var3) {
      return new RealmsResetWorldScreen(var0, var2, var1, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, var3);
   }

   public static RealmsResetWorldScreen forResetSlot(Screen var0, RealmsServer var1, Runnable var2) {
      return new RealmsResetWorldScreen(var0, var1, var1.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESET_WORLD_RESET_TASK_TITLE, var2);
   }

   @Override
   public void init() {
      LinearLayout var1 = this.layout.addToHeader(LinearLayout.vertical());
      var1.defaultCellSetting().padding(9 / 3);
      var1.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
      var1.addChild(new StringWidget(this.subtitle, this.font).setColor(this.subtitleColor), LayoutSettings::alignHorizontallyCenter);
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
      GridLayout var2 = this.layout.addToContents(new GridLayout());
      GridLayout.RowHelper var3 = var2.createRowHelper(3);
      var3.defaultCellSetting().paddingHorizontal(16);
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
            GENERATE_NEW_WORLD,
            NEW_WORLD_LOCATION,
            var1x -> RealmsCreateWorldFlow.createWorld(this.minecraft, this.lastScreen, this, this.slot, this.serverData, this.realmCreationTask)
         )
      );
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
            RealmsSelectFileToUploadScreen.TITLE,
            UPLOAD_LOCATION,
            var1x -> this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.realmCreationTask, this.serverData.id, this.slot, this))
         )
      );
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
            WORLD_TEMPLATES_TITLE,
            SURVIVAL_SPAWN_LOCATION,
            var1x -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(WORLD_TEMPLATES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates)
                  )
         )
      );
      var3.addChild(SpacerElement.height(16), 3);
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
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
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
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
      var3.addChild(
         new RealmsResetWorldScreen.FrameButton(
            this.minecraft.font,
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
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
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

   private void templateSelectionCallback(@Nullable WorldTemplate var1) {
      this.minecraft.setScreen(this);
      if (var1 != null) {
         this.runResetTasks(new ResettingTemplateWorldTask(var1, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable));
      }

      RealmsMainScreen.refreshServerList();
   }

   private void runResetTasks(LongRunningTask var1) {
      ArrayList var2 = new ArrayList();
      if (this.realmCreationTask != null) {
         var2.add(this.realmCreationTask);
      }

      if (this.slot != this.serverData.activeSlot) {
         var2.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {
         }));
      }

      var2.add(var1);
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2.toArray(new LongRunningTask[0])));
   }

   class FrameButton extends Button {
      private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
      private static final int FRAME_SIZE = 60;
      private static final int FRAME_WIDTH = 2;
      private static final int IMAGE_SIZE = 56;
      private final ResourceLocation image;

      FrameButton(final Font nullx, final Component nullxx, final ResourceLocation nullxxx, final Button.OnPress nullxxxx) {
         super(0, 0, 60, 60 + 9, nullxx, nullxxxx, DEFAULT_NARRATION);
         this.image = nullxxx;
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         boolean var5 = this.isHoveredOrFocused();
         int var6 = -1;
         if (var5) {
            var6 = ARGB.colorFromFloat(1.0F, 0.56F, 0.56F, 0.56F);
         }

         int var7 = this.getX();
         int var8 = this.getY();
         var1.blit(RenderType::guiTextured, this.image, var7 + 2, var8 + 2, 0.0F, 0.0F, 56, 56, 56, 56, 56, 56, var6);
         var1.blitSprite(RenderType::guiTextured, SLOT_FRAME_SPRITE, var7, var8, 60, 60, var6);
         int var9 = var5 ? -6250336 : -1;
         var1.drawCenteredString(RealmsResetWorldScreen.this.font, this.getMessage(), var7 + 28, var8 - 14, var9);
      }
   }
}
