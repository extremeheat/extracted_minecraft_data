package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class GuiShareToLan extends GuiScreen {
   private final GuiScreen field_146598_a;
   private GuiButton field_146596_f;
   private GuiButton field_146597_g;
   private String field_146599_h = "survival";
   private boolean field_146600_i;

   public GuiShareToLan(GuiScreen var1) {
      super();
      this.field_146598_a = var1;
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(101, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("lanServer.start")));
      this.field_146292_n.add(new GuiButton(102, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")));
      this.field_146292_n.add(this.field_146597_g = new GuiButton(104, this.field_146294_l / 2 - 155, 100, 150, 20, I18n.func_135052_a("selectWorld.gameMode")));
      this.field_146292_n.add(this.field_146596_f = new GuiButton(103, this.field_146294_l / 2 + 5, 100, 150, 20, I18n.func_135052_a("selectWorld.allowCommands")));
      this.func_146595_g();
   }

   private void func_146595_g() {
      this.field_146597_g.field_146126_j = I18n.func_135052_a("selectWorld.gameMode") + " " + I18n.func_135052_a("selectWorld.gameMode." + this.field_146599_h);
      this.field_146596_f.field_146126_j = I18n.func_135052_a("selectWorld.allowCommands") + " ";
      StringBuilder var10000;
      GuiButton var10002;
      if (this.field_146600_i) {
         var10000 = new StringBuilder();
         var10002 = this.field_146596_f;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.on")).toString();
      } else {
         var10000 = new StringBuilder();
         var10002 = this.field_146596_f;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.off")).toString();
      }

   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 102) {
         this.field_146297_k.func_147108_a(this.field_146598_a);
      } else if (var1.field_146127_k == 104) {
         if (this.field_146599_h.equals("spectator")) {
            this.field_146599_h = "creative";
         } else if (this.field_146599_h.equals("creative")) {
            this.field_146599_h = "adventure";
         } else if (this.field_146599_h.equals("adventure")) {
            this.field_146599_h = "survival";
         } else {
            this.field_146599_h = "spectator";
         }

         this.func_146595_g();
      } else if (var1.field_146127_k == 103) {
         this.field_146600_i = !this.field_146600_i;
         this.func_146595_g();
      } else if (var1.field_146127_k == 101) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
         String var2 = this.field_146297_k.func_71401_C().func_71206_a(WorldSettings.GameType.func_77142_a(this.field_146599_h), this.field_146600_i);
         Object var3;
         if (var2 != null) {
            var3 = new ChatComponentTranslation("commands.publish.started", new Object[]{var2});
         } else {
            var3 = new ChatComponentText("commands.publish.failed");
         }

         this.field_146297_k.field_71456_v.func_146158_b().func_146227_a((IChatComponent)var3);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("lanServer.title"), this.field_146294_l / 2, 50, 16777215);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("lanServer.otherPlayers"), this.field_146294_l / 2, 82, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
