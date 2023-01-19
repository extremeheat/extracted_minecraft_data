package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsResetWorldScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private Component subtitle = Component.translatable("mco.reset.world.warning");
   private Component buttonTitle = CommonComponents.GUI_CANCEL;
   private int subtitleColor = 16711680;
   private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
   WorldTemplatePaginatedList templates;
   WorldTemplatePaginatedList adventuremaps;
   WorldTemplatePaginatedList experiences;
   WorldTemplatePaginatedList inspirations;
   public int slot = -1;
   private Component resetTitle = Component.translatable("mco.reset.world.resetting.screen.title");
   private final Runnable resetWorldRunnable;
   private final Runnable callback;

   public RealmsResetWorldScreen(Screen var1, RealmsServer var2, Component var3, Runnable var4, Runnable var5) {
      super(var3);
      this.lastScreen = var1;
      this.serverData = var2;
      this.resetWorldRunnable = var4;
      this.callback = var5;
   }

   public RealmsResetWorldScreen(Screen var1, RealmsServer var2, Runnable var3, Runnable var4) {
      this(var1, var2, Component.translatable("mco.reset.world.title"), var3, var4);
   }

   public RealmsResetWorldScreen(Screen var1, RealmsServer var2, Component var3, Component var4, int var5, Component var6, Runnable var7, Runnable var8) {
      this(var1, var2, var3, var7, var8);
      this.subtitle = var4;
      this.subtitleColor = var5;
      this.buttonTitle = var6;
   }

   public void setSlot(int var1) {
      this.slot = var1;
   }

   public void setResetTitle(Component var1) {
      this.resetTitle = var1;
   }

   @Override
   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, var1 -> this.minecraft.setScreen(this.lastScreen)));
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
      this.addLabel(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(1),
            row(0) + 10,
            Component.translatable("mco.reset.world.generate"),
            NEW_WORLD_LOCATION,
            var1 -> this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this::generationSelectionCallback, this.title))
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(2),
            row(0) + 10,
            Component.translatable("mco.reset.world.upload"),
            UPLOAD_LOCATION,
            var1 -> this.minecraft
                  .setScreen(
                     new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback)
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(3),
            row(0) + 10,
            Component.translatable("mco.reset.world.template"),
            SURVIVAL_SPAWN_LOCATION,
            var1 -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        Component.translatable("mco.reset.world.template"), this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates
                     )
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(1),
            row(6) + 20,
            Component.translatable("mco.reset.world.adventure"),
            ADVENTURE_MAP_LOCATION,
            var1 -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        Component.translatable("mco.reset.world.adventure"),
                        this::templateSelectionCallback,
                        RealmsServer.WorldType.ADVENTUREMAP,
                        this.adventuremaps
                     )
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(2),
            row(6) + 20,
            Component.translatable("mco.reset.world.experience"),
            EXPERIENCE_LOCATION,
            var1 -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        Component.translatable("mco.reset.world.experience"),
                        this::templateSelectionCallback,
                        RealmsServer.WorldType.EXPERIENCE,
                        this.experiences
                     )
                  )
         )
      );
      this.addRenderableWidget(
         new RealmsResetWorldScreen.FrameButton(
            this.frame(3),
            row(6) + 20,
            Component.translatable("mco.reset.world.inspiration"),
            INSPIRATION_LOCATION,
            var1 -> this.minecraft
                  .setScreen(
                     new RealmsSelectWorldTemplateScreen(
                        Component.translatable("mco.reset.world.inspiration"),
                        this::templateSelectionCallback,
                        RealmsServer.WorldType.INSPIRATION,
                        this.inspirations
                     )
                  )
         )
      );
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private int frame(int var1) {
      return this.width / 2 - 130 + (var1 - 1) * 100;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 7, 16777215);
      super.render(var1, var2, var3, var4);
   }

   void drawFrame(PoseStack var1, int var2, int var3, Component var4, ResourceLocation var5, boolean var6, boolean var7) {
      RenderSystem.setShaderTexture(0, var5);
      if (var6) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(var1, var2 + 2, var3 + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
      if (var6) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(var1, var2, var3 + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      int var8 = var6 ? 10526880 : 16777215;
      drawCenteredString(var1, this.font, var4, var2 + 30, var3, var8);
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
         this.resetWorld(() -> this.startTask(new ResettingTemplateWorldTask(var1, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
      }
   }

   private void generationSelectionCallback(@Nullable WorldGenerationInfo var1) {
      this.minecraft.setScreen(this);
      if (var1 != null) {
         this.resetWorld(() -> this.startTask(new ResettingGeneratedWorldTask(var1, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
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
      private final ResourceLocation image;

      public FrameButton(int var2, int var3, Component var4, ResourceLocation var5, Button.OnPress var6) {
         super(var2, var3, 60, 72, var4, var6);
         this.image = var5;
      }

      @Override
      public void renderButton(PoseStack var1, int var2, int var3, float var4) {
         RealmsResetWorldScreen.this.drawFrame(
            var1, this.x, this.y, this.getMessage(), this.image, this.isHoveredOrFocused(), this.isMouseOver((double)var2, (double)var3)
         );
      }
   }
}
