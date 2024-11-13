package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class DeathScreen extends Screen {
   private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
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

   protected void init() {
      this.delayTicker = 0;
      this.exitButtons.clear();
      MutableComponent var1 = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
      this.exitButtons.add((Button)this.addRenderableWidget(Button.builder(var1, (var1x) -> {
         this.minecraft.player.respawn();
         var1x.active = false;
      }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
      this.exitToTitleButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("deathScreen.titleScreen"), (var1x) -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::handleExitToTitleScreen, true)).bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
      this.exitButtons.add(this.exitToTitleButton);
      this.setButtonsActive(false);
      this.deathScore = Component.translatable("deathScreen.score.value", Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void handleExitToTitleScreen() {
      if (this.hardcore) {
         this.exitToTitleScreen();
      } else {
         TitleConfirmScreen var1 = new TitleConfirmScreen((var1x) -> {
            if (var1x) {
               this.exitToTitleScreen();
            } else {
               this.minecraft.player.respawn();
               this.minecraft.setScreen((Screen)null);
            }

         }, Component.translatable("deathScreen.quit.confirm"), CommonComponents.EMPTY, Component.translatable("deathScreen.titleScreen"), Component.translatable("deathScreen.respawn"));
         this.minecraft.setScreen(var1);
         ((ConfirmScreen)var1).setDelay(20);
      }
   }

   private void exitToTitleScreen() {
      if (this.minecraft.level != null) {
         this.minecraft.level.disconnect();
      }

      this.minecraft.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
      this.minecraft.setScreen(new TitleScreen());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.pose().pushPose();
      var1.pose().scale(2.0F, 2.0F, 2.0F);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2 / 2, 30, 16777215);
      var1.pose().popPose();
      if (this.causeOfDeath != null) {
         var1.drawCenteredString(this.font, (Component)this.causeOfDeath, this.width / 2, 85, 16777215);
      }

      var1.drawCenteredString(this.font, (Component)this.deathScore, this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && var3 > 85) {
         Objects.requireNonNull(this.font);
         if (var3 < 85 + 9) {
            Style var5 = this.getClickedComponentStyleAt(var2);
            var1.renderComponentHoverEffect(this.font, var5, var2, var3);
         }
      }

      if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)DRAFT_REPORT_SPRITE, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 15, 15);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      renderDeathBackground(var1, this.width, this.height);
   }

   static void renderDeathBackground(GuiGraphics var0, int var1, int var2) {
      var0.fillGradient(0, 0, var1, var2, 1615855616, -1602211792);
   }

   @Nullable
   private Style getClickedComponentStyleAt(int var1) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int var2 = this.minecraft.font.width((FormattedText)this.causeOfDeath);
         int var3 = this.width / 2 - var2 / 2;
         int var4 = this.width / 2 + var2 / 2;
         return var1 >= var3 && var1 <= var4 ? this.minecraft.font.getSplitter().componentStyleAtWidth((FormattedText)this.causeOfDeath, var1 - var3) : null;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.causeOfDeath != null && var3 > 85.0) {
         Objects.requireNonNull(this.font);
         if (var3 < (double)(85 + 9)) {
            Style var6 = this.getClickedComponentStyleAt((int)var1);
            if (var6 != null && var6.getClickEvent() != null && var6.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
               this.handleComponentClicked(var6);
               return false;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      ++this.delayTicker;
      if (this.delayTicker == 20) {
         this.setButtonsActive(true);
      }

   }

   private void setButtonsActive(boolean var1) {
      for(Button var3 : this.exitButtons) {
         var3.active = var1;
      }

   }

   public static class TitleConfirmScreen extends ConfirmScreen {
      public TitleConfirmScreen(BooleanConsumer var1, Component var2, Component var3, Component var4, Component var5) {
         super(var1, var2, var3, var4, var5);
      }

      public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
         DeathScreen.renderDeathBackground(var1, this.width, this.height);
      }
   }
}
