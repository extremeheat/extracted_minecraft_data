package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.ResettingWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsResetWorldScreen extends RealmsScreenWithCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private Component title;
   private Component subtitle;
   private Component buttonTitle;
   private int subtitleColor;
   private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
   private WorldTemplatePaginatedList templates;
   private WorldTemplatePaginatedList adventuremaps;
   private WorldTemplatePaginatedList experiences;
   private WorldTemplatePaginatedList inspirations;
   public int slot;
   private RealmsResetWorldScreen.ResetType typeToReset;
   private RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset;
   private WorldTemplate worldTemplateToReset;
   @Nullable
   private Component resetTitle;
   private final Runnable resetWorldRunnable;
   private final Runnable callback;

   public RealmsResetWorldScreen(Screen var1, RealmsServer var2, Runnable var3, Runnable var4) {
      super();
      this.title = new TranslatableComponent("mco.reset.world.title");
      this.subtitle = new TranslatableComponent("mco.reset.world.warning");
      this.buttonTitle = CommonComponents.GUI_CANCEL;
      this.subtitleColor = 16711680;
      this.slot = -1;
      this.typeToReset = RealmsResetWorldScreen.ResetType.NONE;
      this.lastScreen = var1;
      this.serverData = var2;
      this.resetWorldRunnable = var3;
      this.callback = var4;
   }

   public RealmsResetWorldScreen(Screen var1, RealmsServer var2, Component var3, Component var4, int var5, Component var6, Runnable var7, Runnable var8) {
      this(var1, var2, var7, var8);
      this.title = var3;
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

   public void init() {
      this.addButton(new Button(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      (new Thread("Realms-reset-world-fetcher") {
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
      this.titleLabel = (RealmsLabel)this.addWidget(new RealmsLabel(this.title, this.width / 2, 7, 16777215));
      this.subtitleLabel = (RealmsLabel)this.addWidget(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(1), row(0) + 10, new TranslatableComponent("mco.reset.world.generate"), NEW_WORLD_LOCATION, (var1) -> {
         this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this, this.title));
      }));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(2), row(0) + 10, new TranslatableComponent("mco.reset.world.upload"), UPLOAD_LOCATION, (var1) -> {
         RealmsSelectFileToUploadScreen var2 = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback);
         this.minecraft.setScreen(var2);
      }));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(3), row(0) + 10, new TranslatableComponent("mco.reset.world.template"), SURVIVAL_SPAWN_LOCATION, (var1) -> {
         RealmsSelectWorldTemplateScreen var2 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.NORMAL, this.templates);
         var2.setTitle(new TranslatableComponent("mco.reset.world.template"));
         this.minecraft.setScreen(var2);
      }));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(1), row(6) + 20, new TranslatableComponent("mco.reset.world.adventure"), ADVENTURE_MAP_LOCATION, (var1) -> {
         RealmsSelectWorldTemplateScreen var2 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps);
         var2.setTitle(new TranslatableComponent("mco.reset.world.adventure"));
         this.minecraft.setScreen(var2);
      }));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(2), row(6) + 20, new TranslatableComponent("mco.reset.world.experience"), EXPERIENCE_LOCATION, (var1) -> {
         RealmsSelectWorldTemplateScreen var2 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.EXPERIENCE, this.experiences);
         var2.setTitle(new TranslatableComponent("mco.reset.world.experience"));
         this.minecraft.setScreen(var2);
      }));
      this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(3), row(6) + 20, new TranslatableComponent("mco.reset.world.inspiration"), INSPIRATION_LOCATION, (var1) -> {
         RealmsSelectWorldTemplateScreen var2 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.INSPIRATION, this.inspirations);
         var2.setTitle(new TranslatableComponent("mco.reset.world.inspiration"));
         this.minecraft.setScreen(var2);
      }));
      this.narrateLabels();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.titleLabel.render(this, var1);
      this.subtitleLabel.render(this, var1);
      super.render(var1, var2, var3, var4);
   }

   private void drawFrame(PoseStack var1, int var2, int var3, Component var4, ResourceLocation var5, boolean var6, boolean var7) {
      this.minecraft.getTextureManager().bind(var5);
      if (var6) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(var1, var2 + 2, var3 + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      this.minecraft.getTextureManager().bind(SLOT_FRAME_LOCATION);
      if (var6) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(var1, var2, var3 + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      int var8 = var6 ? 10526880 : 16777215;
      drawCenteredString(var1, this.font, var4, var2 + 30, var3, var8);
   }

   protected void callback(@Nullable WorldTemplate var1) {
      if (var1 != null) {
         if (this.slot == -1) {
            this.resetWorldWithTemplate(var1);
         } else {
            switch(var1.type) {
            case WORLD_TEMPLATE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
               break;
            case ADVENTUREMAP:
               this.typeToReset = RealmsResetWorldScreen.ResetType.ADVENTURE;
               break;
            case EXPERIENCE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.EXPERIENCE;
               break;
            case INSPIRATION:
               this.typeToReset = RealmsResetWorldScreen.ResetType.INSPIRATION;
            }

            this.worldTemplateToReset = var1;
            this.switchSlot();
         }

      }
   }

   private void switchSlot() {
      this.switchSlot(() -> {
         switch(this.typeToReset) {
         case ADVENTURE:
         case SURVIVAL_SPAWN:
         case EXPERIENCE:
         case INSPIRATION:
            if (this.worldTemplateToReset != null) {
               this.resetWorldWithTemplate(this.worldTemplateToReset);
            }
            break;
         case GENERATE:
            if (this.worldInfoToReset != null) {
               this.triggerResetWorld(this.worldInfoToReset);
            }
         }

      });
   }

   public void switchSlot(Runnable var1) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, this.slot, var1)));
   }

   public void resetWorldWithTemplate(WorldTemplate var1) {
      this.resetWorld((String)null, var1, -1, true);
   }

   private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo var1) {
      this.resetWorld(var1.seed, (WorldTemplate)null, var1.levelType, var1.generateStructures);
   }

   private void resetWorld(@Nullable String var1, @Nullable WorldTemplate var2, int var3, boolean var4) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ResettingWorldTask(var1, var2, var3, var4, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
   }

   public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo var1) {
      if (this.slot == -1) {
         this.triggerResetWorld(var1);
      } else {
         this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
         this.worldInfoToReset = var1;
         this.switchSlot();
      }

   }

   class FrameButton extends Button {
      private final ResourceLocation image;

      public FrameButton(int var2, int var3, Component var4, ResourceLocation var5, Button.OnPress var6) {
         super(var2, var3, 60, 72, var4, var6);
         this.image = var5;
      }

      public void renderButton(PoseStack var1, int var2, int var3, float var4) {
         RealmsResetWorldScreen.this.drawFrame(var1, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver((double)var2, (double)var3));
      }
   }

   public static class ResetWorldInfo {
      private final String seed;
      private final int levelType;
      private final boolean generateStructures;

      public ResetWorldInfo(String var1, int var2, boolean var3) {
         super();
         this.seed = var1;
         this.levelType = var2;
         this.generateStructures = var3;
      }
   }

   static enum ResetType {
      NONE,
      GENERATE,
      UPLOAD,
      ADVENTURE,
      SURVIVAL_SPAWN,
      EXPERIENCE,
      INSPIRATION;

      private ResetType() {
      }
   }
}
