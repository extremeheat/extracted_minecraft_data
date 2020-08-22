package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DeathScreen extends Screen {
   private int delayTicker;
   private final Component causeOfDeath;
   private final boolean hardcore;

   public DeathScreen(@Nullable Component var1, boolean var2) {
      super(new TranslatableComponent(var2 ? "deathScreen.title.hardcore" : "deathScreen.title", new Object[0]));
      this.causeOfDeath = var1;
      this.hardcore = var2;
   }

   protected void init() {
      this.delayTicker = 0;
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? I18n.get("deathScreen.spectate") : I18n.get("deathScreen.respawn"), (var1x) -> {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }));
      Button var1 = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, I18n.get("deathScreen.titleScreen"), (var1x) -> {
         if (this.hardcore) {
            this.exitToTitleScreen();
         } else {
            ConfirmScreen var2 = new ConfirmScreen(this::confirmResult, new TranslatableComponent("deathScreen.quit.confirm", new Object[0]), new TextComponent(""), I18n.get("deathScreen.titleScreen"), I18n.get("deathScreen.respawn"));
            this.minecraft.setScreen(var2);
            var2.setDelay(20);
         }
      }));
      if (!this.hardcore && this.minecraft.getUser() == null) {
         var1.active = false;
      }

      AbstractWidget var3;
      for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); var3.active = false) {
         var3 = (AbstractWidget)var2.next();
      }

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

      this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
      this.minecraft.setScreen(new TitleScreen());
   }

   public void render(int var1, int var2, float var3) {
      this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2 / 2, 30, 16777215);
      RenderSystem.popMatrix();
      if (this.causeOfDeath != null) {
         this.drawCenteredString(this.font, this.causeOfDeath.getColoredString(), this.width / 2, 85, 16777215);
      }

      this.drawCenteredString(this.font, I18n.get("deathScreen.score") + ": " + ChatFormatting.YELLOW + this.minecraft.player.getScore(), this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && var2 > 85) {
         this.font.getClass();
         if (var2 < 85 + 9) {
            Component var4 = this.getClickedComponentAt(var1);
            if (var4 != null && var4.getStyle().getHoverEvent() != null) {
               this.renderComponentHoverEffect(var4, var1, var2);
            }
         }
      }

      super.render(var1, var2, var3);
   }

   @Nullable
   public Component getClickedComponentAt(int var1) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int var2 = this.minecraft.font.width(this.causeOfDeath.getColoredString());
         int var3 = this.width / 2 - var2 / 2;
         int var4 = this.width / 2 + var2 / 2;
         int var5 = var3;
         if (var1 >= var3 && var1 <= var4) {
            Iterator var6 = this.causeOfDeath.iterator();

            Component var7;
            do {
               if (!var6.hasNext()) {
                  return null;
               }

               var7 = (Component)var6.next();
               var5 += this.minecraft.font.width(ComponentRenderUtils.stripColor(var7.getContents(), false));
            } while(var5 <= var1);

            return var7;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.causeOfDeath != null && var3 > 85.0D) {
         this.font.getClass();
         if (var3 < (double)(85 + 9)) {
            Component var6 = this.getClickedComponentAt((int)var1);
            if (var6 != null && var6.getStyle().getClickEvent() != null && var6.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
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
      AbstractWidget var2;
      if (this.delayTicker == 20) {
         for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (AbstractWidget)var1.next();
         }
      }

   }
}
