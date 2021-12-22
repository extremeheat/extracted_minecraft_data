package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DeathScreen extends Screen {
   private int delayTicker;
   private final Component causeOfDeath;
   private final boolean hardcore;
   private Component deathScore;
   private final List<Button> exitButtons = Lists.newArrayList();

   public DeathScreen(@Nullable Component var1, boolean var2) {
      super(new TranslatableComponent(var2 ? "deathScreen.title.hardcore" : "deathScreen.title"));
      this.causeOfDeath = var1;
      this.hardcore = var2;
   }

   protected void init() {
      this.delayTicker = 0;
      this.exitButtons.clear();
      this.exitButtons.add((Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? new TranslatableComponent("deathScreen.spectate") : new TranslatableComponent("deathScreen.respawn"), (var1x) -> {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      })));
      this.exitButtons.add((Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslatableComponent("deathScreen.titleScreen"), (var1x) -> {
         if (this.hardcore) {
            this.exitToTitleScreen();
         } else {
            ConfirmScreen var2 = new ConfirmScreen(this::confirmResult, new TranslatableComponent("deathScreen.quit.confirm"), TextComponent.EMPTY, new TranslatableComponent("deathScreen.titleScreen"), new TranslatableComponent("deathScreen.respawn"));
            this.minecraft.setScreen(var2);
            var2.setDelay(20);
         }
      })));

      Button var2;
      for(Iterator var1 = this.exitButtons.iterator(); var1.hasNext(); var2.active = false) {
         var2 = (Button)var1.next();
      }

      this.deathScore = (new TranslatableComponent("deathScreen.score")).append(": ").append((Component)(new TextComponent(Integer.toString(this.minecraft.player.getScore()))).withStyle(ChatFormatting.YELLOW));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void confirmResult(boolean var1) {
      if (var1) {
         this.exitToTitleScreen();
      } else {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }

   }

   private void exitToTitleScreen() {
      if (this.minecraft.level != null) {
         this.minecraft.level.disconnect();
      }

      this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
      this.minecraft.setScreen(new TitleScreen());
   }

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
      if (this.causeOfDeath != null && var3 > 85) {
         Objects.requireNonNull(this.font);
         if (var3 < 85 + 9) {
            Style var5 = this.getClickedComponentStyleAt(var2);
            this.renderComponentHoverEffect(var1, var5, var2, var3);
         }
      }

      super.render(var1, var2, var3, var4);
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
      if (this.causeOfDeath != null && var3 > 85.0D) {
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
      Button var2;
      if (this.delayTicker == 20) {
         for(Iterator var1 = this.exitButtons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (Button)var1.next();
         }
      }

   }
}
