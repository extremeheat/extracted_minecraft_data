package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class ConfirmScreen extends Screen {
   private final Component title2;
   private final List lines;
   protected String yesButton;
   protected String noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      this(var1, var2, var3, I18n.get("gui.yes"), I18n.get("gui.no"));
   }

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3, String var4, String var5) {
      super(var2);
      this.lines = Lists.newArrayList();
      this.callback = var1;
      this.title2 = var3;
      this.yesButton = var4;
      this.noButton = var5;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.title2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, (var1) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, (var1) -> {
         this.callback.accept(false);
      }));
      this.lines.clear();
      this.lines.addAll(this.font.split(this.title2.getColoredString(), this.width - 50));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
      int var4 = 90;

      for(Iterator var5 = this.lines.iterator(); var5.hasNext(); var4 += 9) {
         String var6 = (String)var5.next();
         this.drawCenteredString(this.font, var6, this.width / 2, var4, 16777215);
         this.font.getClass();
      }

      super.render(var1, var2, var3);
   }

   public void setDelay(int var1) {
      this.delayTicker = var1;

      AbstractWidget var3;
      for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); var3.active = false) {
         var3 = (AbstractWidget)var2.next();
      }

   }

   public void tick() {
      super.tick();
      AbstractWidget var2;
      if (--this.delayTicker == 0) {
         for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (AbstractWidget)var1.next();
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }
}
