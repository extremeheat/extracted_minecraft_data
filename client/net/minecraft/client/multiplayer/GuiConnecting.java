package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger field_146372_a = new AtomicInteger(0);
   private static final Logger field_146370_f = LogManager.getLogger();
   private NetworkManager field_146371_g;
   private boolean field_146373_h;
   private final GuiScreen field_146374_i;

   public GuiConnecting(GuiScreen var1, Minecraft var2, ServerData var3) {
      super();
      this.field_146297_k = var2;
      this.field_146374_i = var1;
      ServerAddress var4 = ServerAddress.func_78860_a(var3.field_78845_b);
      var2.func_71403_a(null);
      var2.func_71351_a(var3);
      this.func_146367_a(var4.func_78861_a(), var4.func_78864_b());
   }

   public GuiConnecting(GuiScreen var1, Minecraft var2, String var3, int var4) {
      super();
      this.field_146297_k = var2;
      this.field_146374_i = var1;
      var2.func_71403_a(null);
      this.func_146367_a(var3, var4);
   }

   private void func_146367_a(String var1, int var2) {
      field_146370_f.info("Connecting to " + var1 + ", " + var2);
      new GuiConnecting$1(this, "Server Connector #" + field_146372_a.incrementAndGet(), var1, var2).start();
   }

   @Override
   public void func_73876_c() {
      if (this.field_146371_g != null) {
         if (this.field_146371_g.func_150724_d()) {
            this.field_146371_g.func_74428_b();
         } else if (this.field_146371_g.func_150730_f() != null) {
            this.field_146371_g.func_150729_e().func_147231_a(this.field_146371_g.func_150730_f());
         }
      }
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 2 + 50, I18n.func_135052_a("gui.cancel")));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0) {
         this.field_146373_h = true;
         if (this.field_146371_g != null) {
            this.field_146371_g.func_150718_a(new ChatComponentText("Aborted"));
         }

         this.field_146297_k.func_147108_a(this.field_146374_i);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      if (this.field_146371_g == null) {
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("connect.connecting"), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
      } else {
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("connect.authorizing"), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }
}
