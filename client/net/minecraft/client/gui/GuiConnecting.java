package net.minecraft.client.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger field_146372_a = new AtomicInteger(0);
   private static final Logger field_146370_f = LogManager.getLogger();
   private NetworkManager field_146371_g;
   private boolean field_146373_h;
   private final GuiScreen field_146374_i;
   private ITextComponent field_209515_s = new TextComponentTranslation("connect.connecting", new Object[0]);

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
      field_146370_f.info("Connecting to {}, {}", var1, var2);
      Thread var3 = new Thread("Server Connector #" + field_146372_a.incrementAndGet()) {
         public void run() {
            InetAddress var1x = null;

            try {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               var1x = InetAddress.getByName(var1);
               GuiConnecting.this.field_146371_g = NetworkManager.func_181124_a(var1x, var2, GuiConnecting.this.field_146297_k.field_71474_y.func_181148_f());
               GuiConnecting.this.field_146371_g.func_150719_a(new NetHandlerLoginClient(GuiConnecting.this.field_146371_g, GuiConnecting.this.field_146297_k, GuiConnecting.this.field_146374_i, (var1xx) -> {
                  GuiConnecting.this.func_209514_a(var1xx);
               }));
               GuiConnecting.this.field_146371_g.func_179290_a(new CPacketHandshake(var1, var2, EnumConnectionState.LOGIN));
               GuiConnecting.this.field_146371_g.func_179290_a(new CPacketLoginStart(GuiConnecting.this.field_146297_k.func_110432_I().func_148256_e()));
            } catch (UnknownHostException var4) {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               GuiConnecting.field_146370_f.error("Couldn't connect to server", var4);
               GuiConnecting.this.field_146297_k.func_152344_a(() -> {
                  GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
               });
            } catch (Exception var5) {
               if (GuiConnecting.this.field_146373_h) {
                  return;
               }

               GuiConnecting.field_146370_f.error("Couldn't connect to server", var5);
               String var3 = var1x == null ? var5.toString() : var5.toString().replaceAll(var1x + ":" + var2, "");
               GuiConnecting.this.field_146297_k.func_152344_a(() -> {
                  GuiConnecting.this.field_146297_k.func_147108_a(new GuiDisconnected(GuiConnecting.this.field_146374_i, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{var3})));
               });
            }

         }
      };
      var3.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_146370_f));
      var3.start();
   }

   private void func_209514_a(ITextComponent var1) {
      this.field_209515_s = var1;
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

   public boolean func_195120_Y_() {
      return false;
   }

   protected void func_73866_w_() {
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiConnecting.this.field_146373_h = true;
            if (GuiConnecting.this.field_146371_g != null) {
               GuiConnecting.this.field_146371_g.func_150718_a(new TextComponentTranslation("connect.aborted", new Object[0]));
            }

            GuiConnecting.this.field_146297_k.func_147108_a(GuiConnecting.this.field_146374_i);
         }
      });
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_209515_s.func_150254_d(), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
