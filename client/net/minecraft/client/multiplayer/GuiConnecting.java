package net.minecraft.client.multiplayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
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
      var2.func_71403_a((WorldClient)null);
      var2.func_71351_a(var3);
      this.func_146367_a(var4.func_78861_a(), var4.func_78864_b());
   }

   public GuiConnecting(GuiScreen var1, Minecraft var2, String var3, int var4) {
      super();
      this.field_146297_k = var2;
      this.field_146374_i = var1;
      var2.func_71403_a((WorldClient)null);
      this.func_146367_a(var3, var4);
   }

   private void func_146367_a(final String var1, final int var2) {
      field_146370_f.info("Connecting to " + var1 + ", " + var2);
      (new Thread("Server Connector #" + field_146372_a.incrementAndGet()) {
         public void run() {
            InetAddress var1x = null;

            try {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               var1x = InetAddress.getByName(var1);
               GuiConnecting.this.field_146371_g = NetworkManager.func_181124_a(var1x, var2, GuiConnecting.this.field_146297_k.field_71474_y.func_181148_f());
               GuiConnecting.this.field_146371_g.func_150719_a(new NetHandlerLoginClient(GuiConnecting.this.field_146371_g, GuiConnecting.this.field_146297_k, GuiConnecting.this.field_146374_i));
               GuiConnecting.this.field_146371_g.func_179290_a(new C00Handshake(47, var1, var2, EnumConnectionState.LOGIN));
               GuiConnecting.this.field_146371_g.func_179290_a(new C00PacketLoginStart(GuiConnecting.this.field_146297_k.func_110432_I().func_148256_e()));
            } catch (UnknownHostException var5) {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               GuiConnecting.field_146370_f.error("Couldn't connect to server", var5);
               GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
            } catch (Exception var6) {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               GuiConnecting.field_146370_f.error("Couldn't connect to server", var6);
               String var3 = var6.toString();
               if (var1x != null) {
                  String var4 = var1x.toString() + ":" + var2;
                  var3 = var3.replaceAll(var4, "");
               }

               GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{var3})));
            }

         }
      }).start();
   }

   public void func_73876_c() {
      if (this.field_146371_g != null) {
         if (this.field_146371_g.func_150724_d()) {
            this.field_146371_g.func_74428_b();
         } else {
            this.field_146371_g.func_179293_l();
         }
      }

   }

   protected void func_73869_a(char var1, int var2) {
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.func_135052_a("gui.cancel")));
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0) {
         this.field_146373_h = true;
         if (this.field_146371_g != null) {
            this.field_146371_g.func_150718_a(new ChatComponentText("Aborted"));
         }

         this.field_146297_k.func_147108_a(this.field_146374_i);
      }

   }

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
