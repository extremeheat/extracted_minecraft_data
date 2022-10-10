package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

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

   protected void func_73866_w_() {
      this.func_189646_b(new GuiButton(101, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("lanServer.start")) {
         public void func_194829_a(double var1, double var3) {
            GuiShareToLan.this.field_146297_k.func_147108_a((GuiScreen)null);
            int var5 = HttpUtil.func_76181_a();
            TextComponentTranslation var6;
            if (GuiShareToLan.this.field_146297_k.func_71401_C().func_195565_a(GameType.func_77142_a(GuiShareToLan.this.field_146599_h), GuiShareToLan.this.field_146600_i, var5)) {
               var6 = new TextComponentTranslation("commands.publish.started", new Object[]{var5});
            } else {
               var6 = new TextComponentTranslation("commands.publish.failed", new Object[0]);
            }

            GuiShareToLan.this.field_146297_k.field_71456_v.func_146158_b().func_146227_a(var6);
         }
      });
      this.func_189646_b(new GuiButton(102, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiShareToLan.this.field_146297_k.func_147108_a(GuiShareToLan.this.field_146598_a);
         }
      });
      this.field_146597_g = this.func_189646_b(new GuiButton(104, this.field_146294_l / 2 - 155, 100, 150, 20, I18n.func_135052_a("selectWorld.gameMode")) {
         public void func_194829_a(double var1, double var3) {
            if ("spectator".equals(GuiShareToLan.this.field_146599_h)) {
               GuiShareToLan.this.field_146599_h = "creative";
            } else if ("creative".equals(GuiShareToLan.this.field_146599_h)) {
               GuiShareToLan.this.field_146599_h = "adventure";
            } else if ("adventure".equals(GuiShareToLan.this.field_146599_h)) {
               GuiShareToLan.this.field_146599_h = "survival";
            } else {
               GuiShareToLan.this.field_146599_h = "spectator";
            }

            GuiShareToLan.this.func_146595_g();
         }
      });
      this.field_146596_f = this.func_189646_b(new GuiButton(103, this.field_146294_l / 2 + 5, 100, 150, 20, I18n.func_135052_a("selectWorld.allowCommands")) {
         public void func_194829_a(double var1, double var3) {
            GuiShareToLan.this.field_146600_i = !GuiShareToLan.this.field_146600_i;
            GuiShareToLan.this.func_146595_g();
         }
      });
      this.func_146595_g();
   }

   private void func_146595_g() {
      this.field_146597_g.field_146126_j = I18n.func_135052_a("selectWorld.gameMode") + ": " + I18n.func_135052_a("selectWorld.gameMode." + this.field_146599_h);
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

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("lanServer.title"), this.field_146294_l / 2, 50, 16777215);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("lanServer.otherPlayers"), this.field_146294_l / 2, 82, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
