package net.minecraft.client.gui;

import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;

public class GuiCreateWorld extends GuiScreen {
   private final GuiScreen field_146332_f;
   private GuiTextField field_146333_g;
   private GuiTextField field_146335_h;
   private String field_146336_i;
   private String field_146342_r = "survival";
   private String field_175300_s;
   private boolean field_146341_s = true;
   private boolean field_146340_t;
   private boolean field_146339_u;
   private boolean field_146338_v;
   private boolean field_146337_w;
   private boolean field_146345_x;
   private boolean field_146344_y;
   private GuiButton field_195355_B;
   private GuiButton field_146343_z;
   private GuiButton field_146324_A;
   private GuiButton field_146325_B;
   private GuiButton field_146326_C;
   private GuiButton field_146320_D;
   private GuiButton field_146321_E;
   private GuiButton field_146322_F;
   private String field_146323_G;
   private String field_146328_H;
   private String field_146329_I;
   private String field_146330_J;
   private int field_146331_K;
   public NBTTagCompound field_146334_a = new NBTTagCompound();
   private static final String[] field_146327_L = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public GuiCreateWorld(GuiScreen var1) {
      super();
      this.field_146332_f = var1;
      this.field_146329_I = "";
      this.field_146330_J = I18n.func_135052_a("selectWorld.newWorld");
   }

   public void func_73876_c() {
      this.field_146333_g.func_146178_a();
      this.field_146335_h.func_146178_a();
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_195355_B = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("selectWorld.create")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.func_195352_j();
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.field_146297_k.func_147108_a(GuiCreateWorld.this.field_146332_f);
         }
      });
      this.field_146343_z = this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 - 75, 115, 150, 20, I18n.func_135052_a("selectWorld.gameMode")) {
         public void func_194829_a(double var1, double var3) {
            if ("survival".equals(GuiCreateWorld.this.field_146342_r)) {
               if (!GuiCreateWorld.this.field_146339_u) {
                  GuiCreateWorld.this.field_146340_t = false;
               }

               GuiCreateWorld.this.field_146337_w = false;
               GuiCreateWorld.this.field_146342_r = "hardcore";
               GuiCreateWorld.this.field_146337_w = true;
               GuiCreateWorld.this.field_146321_E.field_146124_l = false;
               GuiCreateWorld.this.field_146326_C.field_146124_l = false;
               GuiCreateWorld.this.func_146319_h();
            } else if ("hardcore".equals(GuiCreateWorld.this.field_146342_r)) {
               if (!GuiCreateWorld.this.field_146339_u) {
                  GuiCreateWorld.this.field_146340_t = true;
               }

               GuiCreateWorld.this.field_146337_w = false;
               GuiCreateWorld.this.field_146342_r = "creative";
               GuiCreateWorld.this.func_146319_h();
               GuiCreateWorld.this.field_146337_w = false;
               GuiCreateWorld.this.field_146321_E.field_146124_l = true;
               GuiCreateWorld.this.field_146326_C.field_146124_l = true;
            } else {
               if (!GuiCreateWorld.this.field_146339_u) {
                  GuiCreateWorld.this.field_146340_t = false;
               }

               GuiCreateWorld.this.field_146342_r = "survival";
               GuiCreateWorld.this.func_146319_h();
               GuiCreateWorld.this.field_146321_E.field_146124_l = true;
               GuiCreateWorld.this.field_146326_C.field_146124_l = true;
               GuiCreateWorld.this.field_146337_w = false;
            }

            GuiCreateWorld.this.func_146319_h();
         }
      });
      this.field_146324_A = this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 - 75, 187, 150, 20, I18n.func_135052_a("selectWorld.moreWorldOptions")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.func_146315_i();
         }
      });
      this.field_146325_B = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 - 155, 100, 150, 20, I18n.func_135052_a("selectWorld.mapFeatures")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.field_146341_s = !GuiCreateWorld.this.field_146341_s;
            GuiCreateWorld.this.func_146319_h();
         }
      });
      this.field_146325_B.field_146125_m = false;
      this.field_146326_C = this.func_189646_b(new GuiButton(7, this.field_146294_l / 2 + 5, 151, 150, 20, I18n.func_135052_a("selectWorld.bonusItems")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.field_146338_v = !GuiCreateWorld.this.field_146338_v;
            GuiCreateWorld.this.func_146319_h();
         }
      });
      this.field_146326_C.field_146125_m = false;
      this.field_146320_D = this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 + 5, 100, 150, 20, I18n.func_135052_a("selectWorld.mapType")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.field_146331_K++;
            if (GuiCreateWorld.this.field_146331_K >= WorldType.field_77139_a.length) {
               GuiCreateWorld.this.field_146331_K = 0;
            }

            while(!GuiCreateWorld.this.func_175299_g()) {
               GuiCreateWorld.this.field_146331_K++;
               if (GuiCreateWorld.this.field_146331_K >= WorldType.field_77139_a.length) {
                  GuiCreateWorld.this.field_146331_K = 0;
               }
            }

            GuiCreateWorld.this.field_146334_a = new NBTTagCompound();
            GuiCreateWorld.this.func_146319_h();
            GuiCreateWorld.this.func_146316_a(GuiCreateWorld.this.field_146344_y);
         }
      });
      this.field_146320_D.field_146125_m = false;
      this.field_146321_E = this.func_189646_b(new GuiButton(6, this.field_146294_l / 2 - 155, 151, 150, 20, I18n.func_135052_a("selectWorld.allowCommands")) {
         public void func_194829_a(double var1, double var3) {
            GuiCreateWorld.this.field_146339_u = true;
            GuiCreateWorld.this.field_146340_t = !GuiCreateWorld.this.field_146340_t;
            GuiCreateWorld.this.func_146319_h();
         }
      });
      this.field_146321_E.field_146125_m = false;
      this.field_146322_F = this.func_189646_b(new GuiButton(8, this.field_146294_l / 2 + 5, 120, 150, 20, I18n.func_135052_a("selectWorld.customizeType")) {
         public void func_194829_a(double var1, double var3) {
            if (WorldType.field_77139_a[GuiCreateWorld.this.field_146331_K] == WorldType.field_77138_c) {
               GuiCreateWorld.this.field_146297_k.func_147108_a(new GuiCreateFlatWorld(GuiCreateWorld.this, GuiCreateWorld.this.field_146334_a));
            }

            if (WorldType.field_77139_a[GuiCreateWorld.this.field_146331_K] == WorldType.field_205394_h) {
               GuiCreateWorld.this.field_146297_k.func_147108_a(new GuiCreateBuffetWorld(GuiCreateWorld.this, GuiCreateWorld.this.field_146334_a));
            }

         }
      });
      this.field_146322_F.field_146125_m = false;
      this.field_146333_g = new GuiTextField(9, this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
      this.field_146333_g.func_146195_b(true);
      this.field_146333_g.func_146180_a(this.field_146330_J);
      this.field_146335_h = new GuiTextField(10, this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
      this.field_146335_h.func_146180_a(this.field_146329_I);
      this.func_146316_a(this.field_146344_y);
      this.func_146314_g();
      this.func_146319_h();
   }

   private void func_146314_g() {
      this.field_146336_i = this.field_146333_g.func_146179_b().trim();
      char[] var1 = SharedConstants.field_71567_b;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1[var3];
         this.field_146336_i = this.field_146336_i.replace(var4, '_');
      }

      if (StringUtils.isEmpty(this.field_146336_i)) {
         this.field_146336_i = "World";
      }

      this.field_146336_i = func_146317_a(this.field_146297_k.func_71359_d(), this.field_146336_i);
   }

   private void func_146319_h() {
      this.field_146343_z.field_146126_j = I18n.func_135052_a("selectWorld.gameMode") + ": " + I18n.func_135052_a("selectWorld.gameMode." + this.field_146342_r);
      this.field_146323_G = I18n.func_135052_a("selectWorld.gameMode." + this.field_146342_r + ".line1");
      this.field_146328_H = I18n.func_135052_a("selectWorld.gameMode." + this.field_146342_r + ".line2");
      this.field_146325_B.field_146126_j = I18n.func_135052_a("selectWorld.mapFeatures") + " ";
      StringBuilder var10000;
      GuiButton var10002;
      if (this.field_146341_s) {
         var10000 = new StringBuilder();
         var10002 = this.field_146325_B;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.on")).toString();
      } else {
         var10000 = new StringBuilder();
         var10002 = this.field_146325_B;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.off")).toString();
      }

      this.field_146326_C.field_146126_j = I18n.func_135052_a("selectWorld.bonusItems") + " ";
      if (this.field_146338_v && !this.field_146337_w) {
         var10000 = new StringBuilder();
         var10002 = this.field_146326_C;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.on")).toString();
      } else {
         var10000 = new StringBuilder();
         var10002 = this.field_146326_C;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.off")).toString();
      }

      this.field_146320_D.field_146126_j = I18n.func_135052_a("selectWorld.mapType") + " " + I18n.func_135052_a(WorldType.field_77139_a[this.field_146331_K].func_77128_b());
      this.field_146321_E.field_146126_j = I18n.func_135052_a("selectWorld.allowCommands") + " ";
      if (this.field_146340_t && !this.field_146337_w) {
         var10000 = new StringBuilder();
         var10002 = this.field_146321_E;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.on")).toString();
      } else {
         var10000 = new StringBuilder();
         var10002 = this.field_146321_E;
         var10002.field_146126_j = var10000.append(var10002.field_146126_j).append(I18n.func_135052_a("options.off")).toString();
      }

   }

   public static String func_146317_a(ISaveFormat var0, String var1) {
      var1 = var1.replaceAll("[\\./\"]", "_");
      String[] var2 = field_146327_L;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var1.equalsIgnoreCase(var5)) {
            var1 = "_" + var1 + "_";
         }
      }

      while(var0.func_75803_c(var1) != null) {
         var1 = var1 + "-";
      }

      return var1;
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   private void func_195352_j() {
      this.field_146297_k.func_147108_a((GuiScreen)null);
      if (!this.field_146345_x) {
         this.field_146345_x = true;
         long var1 = (new Random()).nextLong();
         String var3 = this.field_146335_h.func_146179_b();
         if (!StringUtils.isEmpty(var3)) {
            try {
               long var4 = Long.parseLong(var3);
               if (var4 != 0L) {
                  var1 = var4;
               }
            } catch (NumberFormatException var6) {
               var1 = (long)var3.hashCode();
            }
         }

         WorldSettings var7 = new WorldSettings(var1, GameType.func_77142_a(this.field_146342_r), this.field_146341_s, this.field_146337_w, WorldType.field_77139_a[this.field_146331_K]);
         var7.func_205390_a((JsonElement)Dynamic.convert(NBTDynamicOps.field_210820_a, JsonOps.INSTANCE, this.field_146334_a));
         if (this.field_146338_v && !this.field_146337_w) {
            var7.func_77159_a();
         }

         if (this.field_146340_t && !this.field_146337_w) {
            var7.func_77166_b();
         }

         this.field_146297_k.func_71371_a(this.field_146336_i, this.field_146333_g.func_146179_b().trim(), var7);
      }
   }

   private boolean func_175299_g() {
      WorldType var1 = WorldType.field_77139_a[this.field_146331_K];
      if (var1 != null && var1.func_77126_d()) {
         return var1 == WorldType.field_180272_g ? func_146272_n() : true;
      } else {
         return false;
      }
   }

   private void func_146315_i() {
      this.func_146316_a(!this.field_146344_y);
   }

   private void func_146316_a(boolean var1) {
      this.field_146344_y = var1;
      if (WorldType.field_77139_a[this.field_146331_K] == WorldType.field_180272_g) {
         this.field_146343_z.field_146125_m = !this.field_146344_y;
         this.field_146343_z.field_146124_l = false;
         if (this.field_175300_s == null) {
            this.field_175300_s = this.field_146342_r;
         }

         this.field_146342_r = "spectator";
         this.field_146325_B.field_146125_m = false;
         this.field_146326_C.field_146125_m = false;
         this.field_146320_D.field_146125_m = this.field_146344_y;
         this.field_146321_E.field_146125_m = false;
         this.field_146322_F.field_146125_m = false;
      } else {
         this.field_146343_z.field_146125_m = !this.field_146344_y;
         this.field_146343_z.field_146124_l = true;
         if (this.field_175300_s != null) {
            this.field_146342_r = this.field_175300_s;
            this.field_175300_s = null;
         }

         this.field_146325_B.field_146125_m = this.field_146344_y && WorldType.field_77139_a[this.field_146331_K] != WorldType.field_180271_f;
         this.field_146326_C.field_146125_m = this.field_146344_y;
         this.field_146320_D.field_146125_m = this.field_146344_y;
         this.field_146321_E.field_146125_m = this.field_146344_y;
         this.field_146322_F.field_146125_m = this.field_146344_y && WorldType.field_77139_a[this.field_146331_K].func_205393_e();
      }

      this.func_146319_h();
      if (this.field_146344_y) {
         this.field_146324_A.field_146126_j = I18n.func_135052_a("gui.done");
      } else {
         this.field_146324_A.field_146126_j = I18n.func_135052_a("selectWorld.moreWorldOptions");
      }

   }

   public boolean charTyped(char var1, int var2) {
      if (this.field_146333_g.func_146206_l() && !this.field_146344_y) {
         this.field_146333_g.charTyped(var1, var2);
         this.field_146330_J = this.field_146333_g.func_146179_b();
         this.field_195355_B.field_146124_l = !this.field_146333_g.func_146179_b().isEmpty();
         this.func_146314_g();
         return true;
      } else if (this.field_146335_h.func_146206_l() && this.field_146344_y) {
         this.field_146335_h.charTyped(var1, var2);
         this.field_146329_I = this.field_146335_h.func_146179_b();
         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.field_146333_g.func_146206_l() && !this.field_146344_y) {
         this.field_146333_g.keyPressed(var1, var2, var3);
         this.field_146330_J = this.field_146333_g.func_146179_b();
         this.field_195355_B.field_146124_l = !this.field_146333_g.func_146179_b().isEmpty();
         this.func_146314_g();
      } else if (this.field_146335_h.func_146206_l() && this.field_146344_y) {
         this.field_146335_h.keyPressed(var1, var2, var3);
         this.field_146329_I = this.field_146335_h.func_146179_b();
      }

      if (this.field_195355_B.field_146124_l && (var1 == 257 || var1 == 335)) {
         this.func_195352_j();
      }

      return true;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.field_146344_y ? this.field_146335_h.mouseClicked(var1, var3, var5) : this.field_146333_g.mouseClicked(var1, var3, var5);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("selectWorld.create"), this.field_146294_l / 2, 20, -1);
      if (this.field_146344_y) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.enterSeed"), this.field_146294_l / 2 - 100, 47, -6250336);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.seedInfo"), this.field_146294_l / 2 - 100, 85, -6250336);
         if (this.field_146325_B.field_146125_m) {
            this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.mapFeatures.info"), this.field_146294_l / 2 - 150, 122, -6250336);
         }

         if (this.field_146321_E.field_146125_m) {
            this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.allowCommands.info"), this.field_146294_l / 2 - 150, 172, -6250336);
         }

         this.field_146335_h.func_195608_a(var1, var2, var3);
         if (WorldType.field_77139_a[this.field_146331_K].func_151357_h()) {
            this.field_146289_q.func_78279_b(I18n.func_135052_a(WorldType.field_77139_a[this.field_146331_K].func_151359_c()), this.field_146320_D.field_146128_h + 2, this.field_146320_D.field_146129_i + 22, this.field_146320_D.func_146117_b(), 10526880);
         }
      } else {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.enterName"), this.field_146294_l / 2 - 100, 47, -6250336);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.resultFolder") + " " + this.field_146336_i, this.field_146294_l / 2 - 100, 85, -6250336);
         this.field_146333_g.func_195608_a(var1, var2, var3);
         this.func_73732_a(this.field_146289_q, this.field_146323_G, this.field_146294_l / 2, 137, -6250336);
         this.func_73732_a(this.field_146289_q, this.field_146328_H, this.field_146294_l / 2, 149, -6250336);
      }

      super.func_73863_a(var1, var2, var3);
   }

   public void func_146318_a(WorldInfo var1) {
      this.field_146330_J = I18n.func_135052_a("selectWorld.newWorld.copyOf", var1.func_76065_j());
      this.field_146329_I = var1.func_76063_b() + "";
      WorldType var2 = var1.func_76067_t() == WorldType.field_180271_f ? WorldType.field_77137_b : var1.func_76067_t();
      this.field_146331_K = var2.func_82747_f();
      this.field_146334_a = var1.func_211027_A();
      this.field_146341_s = var1.func_76089_r();
      this.field_146340_t = var1.func_76086_u();
      if (var1.func_76093_s()) {
         this.field_146342_r = "hardcore";
      } else if (var1.func_76077_q().func_77144_e()) {
         this.field_146342_r = "survival";
      } else if (var1.func_76077_q().func_77145_d()) {
         this.field_146342_r = "creative";
      }

   }
}
