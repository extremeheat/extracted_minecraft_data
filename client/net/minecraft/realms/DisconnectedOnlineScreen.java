package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.IChatComponent;

public class DisconnectedOnlineScreen extends RealmsScreen {
   private String title;
   private IChatComponent reason;
   private List lines;
   private final RealmsScreen parent;

   public DisconnectedOnlineScreen(RealmsScreen var1, String var2, IChatComponent var3) {
      super();
      this.parent = var1;
      this.title = getLocalizedString(var2);
      this.reason = var3;
   }

   @Override
   public void init() {
      this.buttonsClear();
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")));
      this.lines = this.fontSplit(this.reason.func_150254_d(), this.width() - 50);
   }

   @Override
   public void keyPressed(char var1, int var2) {
      if (var2 == 1) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void buttonClicked(RealmsButton var1) {
      if (var1.id() == 0) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - 50, 11184810);
      int var4 = this.height() / 2 - 30;
      if (this.lines != null) {
         for(String var6 : this.lines) {
            this.drawCenteredString(var6, this.width() / 2, var4, 16777215);
            var4 += this.fontLineHeight();
         }
      }

      super.render(var1, var2, var3);
   }
}
