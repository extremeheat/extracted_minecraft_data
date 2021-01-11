package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import java.net.IDN;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenAddServer extends GuiScreen {
   private final GuiScreen field_146310_a;
   private final ServerData field_146311_h;
   private GuiTextField field_146308_f;
   private GuiTextField field_146309_g;
   private GuiButton field_152176_i;
   private Predicate<String> field_181032_r = new Predicate<String>() {
      public boolean apply(String var1) {
         if (var1.length() == 0) {
            return true;
         } else {
            String[] var2 = var1.split(":");
            if (var2.length == 0) {
               return true;
            } else {
               try {
                  String var3 = IDN.toASCII(var2[0]);
                  return true;
               } catch (IllegalArgumentException var4) {
                  return false;
               }
            }
         }
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((String)var1);
      }
   };

   public GuiScreenAddServer(GuiScreen var1, ServerData var2) {
      super();
      this.field_146310_a = var1;
      this.field_146311_h = var2;
   }

   public void func_73876_c() {
      this.field_146309_g.func_146178_a();
      this.field_146308_f.func_146178_a();
   }

   public void func_73866_w_() {
      Keyboard.enableRepeatEvents(true);
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 18, I18n.func_135052_a("addServer.add")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 18, I18n.func_135052_a("gui.cancel")));
      this.field_146292_n.add(this.field_152176_i = new GuiButton(2, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72, I18n.func_135052_a("addServer.resourcePack") + ": " + this.field_146311_h.func_152586_b().func_152589_a().func_150254_d()));
      this.field_146309_g = new GuiTextField(0, this.field_146289_q, this.field_146294_l / 2 - 100, 66, 200, 20);
      this.field_146309_g.func_146195_b(true);
      this.field_146309_g.func_146180_a(this.field_146311_h.field_78847_a);
      this.field_146308_f = new GuiTextField(1, this.field_146289_q, this.field_146294_l / 2 - 100, 106, 200, 20);
      this.field_146308_f.func_146203_f(128);
      this.field_146308_f.func_146180_a(this.field_146311_h.field_78845_b);
      this.field_146308_f.func_175205_a(this.field_181032_r);
      ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146308_f.func_146179_b().length() > 0 && this.field_146308_f.func_146179_b().split(":").length > 0 && this.field_146309_g.func_146179_b().length() > 0;
   }

   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 2) {
            this.field_146311_h.func_152584_a(ServerData.ServerResourceMode.values()[(this.field_146311_h.func_152586_b().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
            this.field_152176_i.field_146126_j = I18n.func_135052_a("addServer.resourcePack") + ": " + this.field_146311_h.func_152586_b().func_152589_a().func_150254_d();
         } else if (var1.field_146127_k == 1) {
            this.field_146310_a.func_73878_a(false, 0);
         } else if (var1.field_146127_k == 0) {
            this.field_146311_h.field_78847_a = this.field_146309_g.func_146179_b();
            this.field_146311_h.field_78845_b = this.field_146308_f.func_146179_b();
            this.field_146310_a.func_73878_a(true, 0);
         }

      }
   }

   protected void func_73869_a(char var1, int var2) {
      this.field_146309_g.func_146201_a(var1, var2);
      this.field_146308_f.func_146201_a(var1, var2);
      if (var2 == 15) {
         this.field_146309_g.func_146195_b(!this.field_146309_g.func_146206_l());
         this.field_146308_f.func_146195_b(!this.field_146308_f.func_146206_l());
      }

      if (var2 == 28 || var2 == 156) {
         this.func_146284_a((GuiButton)this.field_146292_n.get(0));
      }

      ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146308_f.func_146179_b().length() > 0 && this.field_146308_f.func_146179_b().split(":").length > 0 && this.field_146309_g.func_146179_b().length() > 0;
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      this.field_146308_f.func_146192_a(var1, var2, var3);
      this.field_146309_g.func_146192_a(var1, var2, var3);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("addServer.title"), this.field_146294_l / 2, 17, 16777215);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("addServer.enterName"), this.field_146294_l / 2 - 100, 53, 10526880);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("addServer.enterIp"), this.field_146294_l / 2 - 100, 94, 10526880);
      this.field_146309_g.func_146194_f();
      this.field_146308_f.func_146194_f();
      super.func_73863_a(var1, var2, var3);
   }
}
