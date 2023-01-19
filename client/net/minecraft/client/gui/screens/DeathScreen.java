package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class DeathScreen extends Screen {
   private int delayTicker;
   private final Component causeOfDeath;
   private final boolean hardcore;
   private Component deathScore;
   private final List<Button> exitButtons = Lists.newArrayList();
   @Nullable
   private Button exitToTitleButton;

   public DeathScreen(@Nullable Component var1, boolean var2) {
      super(Component.translatable(var2 ? "deathScreen.title.hardcore" : "deathScreen.title"));
      this.causeOfDeath = var1;
      this.hardcore = var2;
   }

   @Override
   protected void init() {
      this.delayTicker = 0;
      this.exitButtons.clear();
      MutableComponent var1 = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
      this.exitButtons
         .add(
            this.addRenderableWidget(
               Button.builder(var1, var1x -> this.minecraft.player.respawn()).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()
            )
         );
      this.exitToTitleButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("deathScreen.titleScreen"),
               var1x -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::handleExitToTitleScreen, true)
            )
            .bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20)
            .build()
      );
      this.exitButtons.add(this.exitToTitleButton);

      for(Button var3 : this.exitButtons) {
         var3.active = false;
      }

      this.deathScore = Component.translatable("deathScreen.score")
         .append(": ")
         .append(Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void handleExitToTitleScreen() {
      if (this.hardcore) {
         this.exitToTitleScreen();
      } else {
         ConfirmScreen var1 = new ConfirmScreen(
            var1x -> {
               if (var1x) {
                  this.exitToTitleScreen();
               } else {
                  this.minecraft.player.respawn();
                  this.minecraft.setScreen(null);
               }
            },
            Component.translatable("deathScreen.quit.confirm"),
            CommonComponents.EMPTY,
            Component.translatable("deathScreen.titleScreen"),
            Component.translatable("deathScreen.respawn")
         );
         this.minecraft.setScreen(var1);
         var1.setDelay(20);
      }
   }

   private void exitToTitleScreen() {
      if (this.minecraft.level != null) {
         this.minecraft.level.disconnect();
      }

      this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
      this.minecraft.setScreen(new TitleScreen());
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.fillGradient(var1, 0, 0, this.width, this.height, 1615855616, -1602211792);
      var1.pushPose();
      var1.scale(2.0F, 2.0F, 2.0F);
      drawCenteredString(var1, this.font, this.title, this.width / 2 / 2, 30, 16777215);
      var1.popPose();
      if (this.causeOfDeath != null) {
         drawCenteredString(var1, this.font, this.causeOfDeath, this.width / 2, 85, 16777215);
      }

      drawCenteredString(var1, this.font, this.deathScore, this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && var3 > 85 && var3 < 85 + 9) {
         Style var5 = this.getClickedComponentStyleAt(var2);
         this.renderComponentHoverEffect(var1, var5, var2, var3);
      }

      super.render(var1, var2, var3, var4);
      if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
         RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
         this.blit(var1, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 182, 24, 15, 15);
      }
   }

   @Nullable
   private Style getClickedComponentStyleAt(int var1) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int var2 = this.minecraft.font.width(this.causeOfDeath);
         int var3 = this.width / 2 - var2 / 2;
         int var4 = this.width / 2 + var2 / 2;
         return var1 >= var3 && var1 <= var4 ? this.minecraft.font.getSplitter().componentStyleAtWidth(this.causeOfDeath, var1 - var3) : null;
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.causeOfDeath != null && var3 > 85.0 && var3 < (double)(85 + 9)) {
         Style var6 = this.getClickedComponentStyleAt((int)var1);
         if (var6 != null && var6.getClickEvent() != null && var6.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClicked(var6);
            return false;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      ++this.delayTicker;
      if (this.delayTicker == 20) {
         for(Button var2 : this.exitButtons) {
            var2.active = true;
         }
      }
   }
}
