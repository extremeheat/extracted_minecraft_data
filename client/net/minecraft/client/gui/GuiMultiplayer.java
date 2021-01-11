package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
   private static final Logger field_146802_a = LogManager.getLogger();
   private final OldServerPinger field_146797_f = new OldServerPinger();
   private GuiScreen field_146798_g;
   private ServerSelectionList field_146803_h;
   private ServerList field_146804_i;
   private GuiButton field_146810_r;
   private GuiButton field_146809_s;
   private GuiButton field_146808_t;
   private boolean field_146807_u;
   private boolean field_146806_v;
   private boolean field_146805_w;
   private boolean field_146813_x;
   private String field_146812_y;
   private ServerData field_146811_z;
   private LanServerDetector.LanServerList field_146799_A;
   private LanServerDetector.ThreadLanServerFind field_146800_B;
   private boolean field_146801_C;

   public GuiMultiplayer(GuiScreen var1) {
      super();
      this.field_146798_g = var1;
   }

   public void func_73866_w_() {
      Keyboard.enableRepeatEvents(true);
      this.field_146292_n.clear();
      if (!this.field_146801_C) {
         this.field_146801_C = true;
         this.field_146804_i = new ServerList(this.field_146297_k);
         this.field_146804_i.func_78853_a();
         this.field_146799_A = new LanServerDetector.LanServerList();

         try {
            this.field_146800_B = new LanServerDetector.ThreadLanServerFind(this.field_146799_A);
            this.field_146800_B.start();
         } catch (Exception var2) {
            field_146802_a.warn("Unable to start LAN server detection: " + var2.getMessage());
         }

         this.field_146803_h = new ServerSelectionList(this, this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 64, 36);
         this.field_146803_h.func_148195_a(this.field_146804_i);
      } else {
         this.field_146803_h.func_148122_a(this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 64);
      }

      this.func_146794_g();
   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_146803_h.func_178039_p();
   }

   public void func_146794_g() {
      this.field_146292_n.add(this.field_146810_r = new GuiButton(7, this.field_146294_l / 2 - 154, this.field_146295_m - 28, 70, 20, I18n.func_135052_a("selectServer.edit")));
      this.field_146292_n.add(this.field_146808_t = new GuiButton(2, this.field_146294_l / 2 - 74, this.field_146295_m - 28, 70, 20, I18n.func_135052_a("selectServer.delete")));
      this.field_146292_n.add(this.field_146809_s = new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("selectServer.select")));
      this.field_146292_n.add(new GuiButton(4, this.field_146294_l / 2 - 50, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("selectServer.direct")));
      this.field_146292_n.add(new GuiButton(3, this.field_146294_l / 2 + 4 + 50, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("selectServer.add")));
      this.field_146292_n.add(new GuiButton(8, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 70, 20, I18n.func_135052_a("selectServer.refresh")));
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 4 + 76, this.field_146295_m - 28, 75, 20, I18n.func_135052_a("gui.cancel")));
      this.func_146790_a(this.field_146803_h.func_148193_k());
   }

   public void func_73876_c() {
      super.func_73876_c();
      if (this.field_146799_A.func_77553_a()) {
         List var1 = this.field_146799_A.func_77554_c();
         this.field_146799_A.func_77552_b();
         this.field_146803_h.func_148194_a(var1);
      }

      this.field_146797_f.func_147223_a();
   }

   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
      if (this.field_146800_B != null) {
         this.field_146800_B.interrupt();
         this.field_146800_B = null;
      }

      this.field_146797_f.func_147226_b();
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         GuiListExtended.IGuiListEntry var2 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
         if (var1.field_146127_k == 2 && var2 instanceof ServerListEntryNormal) {
            String var9 = ((ServerListEntryNormal)var2).func_148296_a().field_78847_a;
            if (var9 != null) {
               this.field_146807_u = true;
               String var4 = I18n.func_135052_a("selectServer.deleteQuestion");
               String var5 = "'" + var9 + "' " + I18n.func_135052_a("selectServer.deleteWarning");
               String var6 = I18n.func_135052_a("selectServer.deleteButton");
               String var7 = I18n.func_135052_a("gui.cancel");
               GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, this.field_146803_h.func_148193_k());
               this.field_146297_k.func_147108_a(var8);
            }
         } else if (var1.field_146127_k == 1) {
            this.func_146796_h();
         } else if (var1.field_146127_k == 4) {
            this.field_146813_x = true;
            this.field_146297_k.func_147108_a(new GuiScreenServerList(this, this.field_146811_z = new ServerData(I18n.func_135052_a("selectServer.defaultName"), "", false)));
         } else if (var1.field_146127_k == 3) {
            this.field_146806_v = true;
            this.field_146297_k.func_147108_a(new GuiScreenAddServer(this, this.field_146811_z = new ServerData(I18n.func_135052_a("selectServer.defaultName"), "", false)));
         } else if (var1.field_146127_k == 7 && var2 instanceof ServerListEntryNormal) {
            this.field_146805_w = true;
            ServerData var3 = ((ServerListEntryNormal)var2).func_148296_a();
            this.field_146811_z = new ServerData(var3.field_78847_a, var3.field_78845_b, false);
            this.field_146811_z.func_152583_a(var3);
            this.field_146297_k.func_147108_a(new GuiScreenAddServer(this, this.field_146811_z));
         } else if (var1.field_146127_k == 0) {
            this.field_146297_k.func_147108_a(this.field_146798_g);
         } else if (var1.field_146127_k == 8) {
            this.func_146792_q();
         }

      }
   }

   private void func_146792_q() {
      this.field_146297_k.func_147108_a(new GuiMultiplayer(this.field_146798_g));
   }

   public void func_73878_a(boolean var1, int var2) {
      GuiListExtended.IGuiListEntry var3 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
      if (this.field_146807_u) {
         this.field_146807_u = false;
         if (var1 && var3 instanceof ServerListEntryNormal) {
            this.field_146804_i.func_78851_b(this.field_146803_h.func_148193_k());
            this.field_146804_i.func_78855_b();
            this.field_146803_h.func_148192_c(-1);
            this.field_146803_h.func_148195_a(this.field_146804_i);
         }

         this.field_146297_k.func_147108_a(this);
      } else if (this.field_146813_x) {
         this.field_146813_x = false;
         if (var1) {
            this.func_146791_a(this.field_146811_z);
         } else {
            this.field_146297_k.func_147108_a(this);
         }
      } else if (this.field_146806_v) {
         this.field_146806_v = false;
         if (var1) {
            this.field_146804_i.func_78849_a(this.field_146811_z);
            this.field_146804_i.func_78855_b();
            this.field_146803_h.func_148192_c(-1);
            this.field_146803_h.func_148195_a(this.field_146804_i);
         }

         this.field_146297_k.func_147108_a(this);
      } else if (this.field_146805_w) {
         this.field_146805_w = false;
         if (var1 && var3 instanceof ServerListEntryNormal) {
            ServerData var4 = ((ServerListEntryNormal)var3).func_148296_a();
            var4.field_78847_a = this.field_146811_z.field_78847_a;
            var4.field_78845_b = this.field_146811_z.field_78845_b;
            var4.func_152583_a(this.field_146811_z);
            this.field_146804_i.func_78855_b();
            this.field_146803_h.func_148195_a(this.field_146804_i);
         }

         this.field_146297_k.func_147108_a(this);
      }

   }

   protected void func_73869_a(char var1, int var2) {
      int var3 = this.field_146803_h.func_148193_k();
      GuiListExtended.IGuiListEntry var4 = var3 < 0 ? null : this.field_146803_h.func_148180_b(var3);
      if (var2 == 63) {
         this.func_146792_q();
      } else {
         if (var3 >= 0) {
            if (var2 == 200) {
               if (func_146272_n()) {
                  if (var3 > 0 && var4 instanceof ServerListEntryNormal) {
                     this.field_146804_i.func_78857_a(var3, var3 - 1);
                     this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                     this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                     this.field_146803_h.func_148195_a(this.field_146804_i);
                  }
               } else if (var3 > 0) {
                  this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                  this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                  if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan) {
                     if (this.field_146803_h.func_148193_k() > 0) {
                        this.func_146790_a(this.field_146803_h.func_148127_b() - 1);
                        this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                     } else {
                        this.func_146790_a(-1);
                     }
                  }
               } else {
                  this.func_146790_a(-1);
               }
            } else if (var2 == 208) {
               if (func_146272_n()) {
                  if (var3 < this.field_146804_i.func_78856_c() - 1) {
                     this.field_146804_i.func_78857_a(var3, var3 + 1);
                     this.func_146790_a(var3 + 1);
                     this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                     this.field_146803_h.func_148195_a(this.field_146804_i);
                  }
               } else if (var3 < this.field_146803_h.func_148127_b()) {
                  this.func_146790_a(this.field_146803_h.func_148193_k() + 1);
                  this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                  if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan) {
                     if (this.field_146803_h.func_148193_k() < this.field_146803_h.func_148127_b() - 1) {
                        this.func_146790_a(this.field_146803_h.func_148127_b() + 1);
                        this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                     } else {
                        this.func_146790_a(-1);
                     }
                  }
               } else {
                  this.func_146790_a(-1);
               }
            } else if (var2 != 28 && var2 != 156) {
               super.func_73869_a(var1, var2);
            } else {
               this.func_146284_a((GuiButton)this.field_146292_n.get(2));
            }
         } else {
            super.func_73869_a(var1, var2);
         }

      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_146812_y = null;
      this.func_146276_q_();
      this.field_146803_h.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.title"), this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(var1, var2, var3);
      if (this.field_146812_y != null) {
         this.func_146283_a(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), var1, var2);
      }

   }

   public void func_146796_h() {
      GuiListExtended.IGuiListEntry var1 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
      if (var1 instanceof ServerListEntryNormal) {
         this.func_146791_a(((ServerListEntryNormal)var1).func_148296_a());
      } else if (var1 instanceof ServerListEntryLanDetected) {
         LanServerDetector.LanServer var2 = ((ServerListEntryLanDetected)var1).func_148289_a();
         this.func_146791_a(new ServerData(var2.func_77487_a(), var2.func_77488_b(), true));
      }

   }

   private void func_146791_a(ServerData var1) {
      this.field_146297_k.func_147108_a(new GuiConnecting(this, this.field_146297_k, var1));
   }

   public void func_146790_a(int var1) {
      this.field_146803_h.func_148192_c(var1);
      GuiListExtended.IGuiListEntry var2 = var1 < 0 ? null : this.field_146803_h.func_148180_b(var1);
      this.field_146809_s.field_146124_l = false;
      this.field_146810_r.field_146124_l = false;
      this.field_146808_t.field_146124_l = false;
      if (var2 != null && !(var2 instanceof ServerListEntryLanScan)) {
         this.field_146809_s.field_146124_l = true;
         if (var2 instanceof ServerListEntryNormal) {
            this.field_146810_r.field_146124_l = true;
            this.field_146808_t.field_146124_l = true;
         }
      }

   }

   public OldServerPinger func_146789_i() {
      return this.field_146797_f;
   }

   public void func_146793_a(String var1) {
      this.field_146812_y = var1;
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      this.field_146803_h.func_148179_a(var1, var2, var3);
   }

   protected void func_146286_b(int var1, int var2, int var3) {
      super.func_146286_b(var1, var2, var3);
      this.field_146803_h.func_148181_b(var1, var2, var3);
   }

   public ServerList func_146795_p() {
      return this.field_146804_i;
   }

   public boolean func_175392_a(ServerListEntryNormal var1, int var2) {
      return var2 > 0;
   }

   public boolean func_175394_b(ServerListEntryNormal var1, int var2) {
      return var2 < this.field_146804_i.func_78856_c() - 1;
   }

   public void func_175391_a(ServerListEntryNormal var1, int var2, boolean var3) {
      int var4 = var3 ? 0 : var2 - 1;
      this.field_146804_i.func_78857_a(var2, var4);
      if (this.field_146803_h.func_148193_k() == var2) {
         this.func_146790_a(var4);
      }

      this.field_146803_h.func_148195_a(this.field_146804_i);
   }

   public void func_175393_b(ServerListEntryNormal var1, int var2, boolean var3) {
      int var4 = var3 ? this.field_146804_i.func_78856_c() - 1 : var2 + 1;
      this.field_146804_i.func_78857_a(var2, var4);
      if (this.field_146803_h.func_148193_k() == var2) {
         this.func_146790_a(var4);
      }

      this.field_146803_h.func_148195_a(this.field_146804_i);
   }
}
