package net.minecraft.client.gui.screens;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DisconnectedScreen extends Screen {
   private final Component reason;
   private List<String> lines;
   private final Screen parent;
   private int textHeight;

   public DisconnectedScreen(Screen var1, String var2, Component var3) {
      super(new TranslatableComponent(var2, new Object[0]));
      this.parent = var1;
      this.reason = var3;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.lines = this.font.split(this.reason.getColoredString(), this.width - 50);
      int var10001 = this.lines.size();
      this.font.getClass();
      this.textHeight = var10001 * 9;
      int var10003 = this.width / 2 - 100;
      int var10004 = this.height / 2 + this.textHeight / 2;
      this.font.getClass();
      this.addButton(new Button(var10003, Math.min(var10004 + 9, this.height - 30), 200, 20, I18n.get("gui.toMenu"), (var1) -> {
         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      Font var10001 = this.font;
      String var10002 = this.title.getColoredString();
      int var10003 = this.width / 2;
      int var10004 = this.height / 2 - this.textHeight / 2;
      this.font.getClass();
      this.drawCenteredString(var10001, var10002, var10003, var10004 - 9 * 2, 11184810);
      int var4 = this.height / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for(Iterator var5 = this.lines.iterator(); var5.hasNext(); var4 += 9) {
            String var6 = (String)var5.next();
            this.drawCenteredString(this.font, var6, this.width / 2, var4, 16777215);
            this.font.getClass();
         }
      }

      super.render(var1, var2, var3);
   }
}
