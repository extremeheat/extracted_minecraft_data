package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class DeathScreen extends Screen {
   private static final int TITLE_SCALE = 2;
   private static final Identifier DRAFT_REPORT_SPRITE = Identifier.withDefaultNamespace("icon/draft_report");
   private int delayTicker;
   private final @Nullable Component causeOfDeath;
   private final boolean hardcore;
   private final LocalPlayer player;
   private final Component deathScore;
   private final List<Button> exitButtons = Lists.newArrayList();
   private @Nullable Button exitToTitleButton;

   public DeathScreen(final @Nullable Component causeOfDeath, final boolean hardcore, final LocalPlayer player) {
      super(Component.translatable(hardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
      this.causeOfDeath = causeOfDeath;
      this.hardcore = hardcore;
      this.player = player;
      Component scoreValue = Component.literal(Integer.toString(player.getScore())).withStyle(ChatFormatting.YELLOW);
      this.deathScore = Component.translatable("deathScreen.score.value", scoreValue);
   }

   protected void init() {
      this.delayTicker = 0;
      this.exitButtons.clear();
      Component message = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
      this.exitButtons.add((Button)this.addRenderableWidget(Button.builder(message, (button) -> {
         this.player.respawn();
         button.active = false;
      }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
      this.exitToTitleButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("deathScreen.titleScreen"), (button) -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::handleExitToTitleScreen, true)).bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
      this.exitButtons.add(this.exitToTitleButton);
      this.setButtonsActive(false);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void handleExitToTitleScreen() {
      if (this.hardcore) {
         this.exitToTitleScreen();
      } else {
         ConfirmScreen confirm = new TitleConfirmScreen((result) -> {
            if (result) {
               this.exitToTitleScreen();
            } else {
               this.player.respawn();
               this.minecraft.setScreen((Screen)null);
            }

         }, Component.translatable("deathScreen.quit.confirm"), CommonComponents.EMPTY, Component.translatable("deathScreen.titleScreen"), Component.translatable("deathScreen.respawn"));
         this.minecraft.setScreen(confirm);
         confirm.setDelay(20);
      }
   }

   private void exitToTitleScreen() {
      if (this.minecraft.level != null) {
         this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
      }

      this.minecraft.disconnectWithSavingScreen();
      this.minecraft.setScreen(new TitleScreen());
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      this.visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR));
      if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DRAFT_REPORT_SPRITE, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 15, 15);
      }

   }

   private void visitText(final ActiveTextCollector output) {
      ActiveTextCollector.Parameters normalParameters = output.defaultParameters();
      int middleLine = this.width / 2;
      output.defaultParameters(normalParameters.withScale(2.0F));
      output.accept(TextAlignment.CENTER, middleLine / 2, 30, (Component)this.title);
      output.defaultParameters(normalParameters);
      if (this.causeOfDeath != null) {
         output.accept(TextAlignment.CENTER, middleLine, 85, (Component)this.causeOfDeath);
      }

      output.accept(TextAlignment.CENTER, middleLine, 100, (Component)this.deathScore);
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      extractDeathBackground(graphics, this.width, this.height);
   }

   private static void extractDeathBackground(final GuiGraphicsExtractor graphics, final int width, final int height) {
      graphics.fillGradient(0, 0, width, height, 1615855616, -1602211792);
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      ActiveTextCollector.ClickableStyleFinder finder = new ActiveTextCollector.ClickableStyleFinder(this.getFont(), (int)event.x(), (int)event.y());
      this.visitText(finder);
      Style clickedStyle = finder.result();
      if (clickedStyle != null) {
         ClickEvent var6 = clickedStyle.getClickEvent();
         if (var6 instanceof ClickEvent.OpenUrl) {
            ClickEvent.OpenUrl openUrl = (ClickEvent.OpenUrl)var6;
            return clickUrlAction(this.minecraft, this, openUrl.uri());
         }
      }

      return super.mouseClicked(event, doubleClick);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isAllowedInPortal() {
      return true;
   }

   public void tick() {
      super.tick();
      ++this.delayTicker;
      if (this.delayTicker == 20) {
         this.setButtonsActive(true);
      }

   }

   private void setButtonsActive(final boolean isActive) {
      for(Button button : this.exitButtons) {
         button.active = isActive;
      }

   }

   public static class TitleConfirmScreen extends ConfirmScreen {
      public TitleConfirmScreen(final BooleanConsumer callback, final Component title, final Component message, final Component yesButton, final Component noButton) {
         super(callback, title, message, yesButton, noButton);
      }

      public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         DeathScreen.extractDeathBackground(graphics, this.width, this.height);
      }
   }
}
