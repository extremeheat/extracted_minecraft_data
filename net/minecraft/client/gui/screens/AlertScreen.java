package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class AlertScreen extends Screen {
   private final Runnable callback;
   protected final Component text;
   private final List lines;
   protected final String okButton;
   private int delayTicker;

   public AlertScreen(Runnable var1, Component var2, Component var3) {
      this(var1, var2, var3, "gui.back");
   }

   public AlertScreen(Runnable var1, Component var2, Component var3, String var4) {
      super(var2);
      this.lines = Lists.newArrayList();
      this.callback = var1;
      this.text = var3;
      this.okButton = I18n.get(var4);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, (var1) -> {
         this.callback.run();
      }));
      this.lines.clear();
      this.lines.addAll(this.font.split(this.text.getColoredString(), this.width - 50));
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

   public void tick() {
      super.tick();
      AbstractWidget var2;
      if (--this.delayTicker == 0) {
         for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (AbstractWidget)var1.next();
         }
      }

   }
}
