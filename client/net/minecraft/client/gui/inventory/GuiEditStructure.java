package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiEditStructure extends GuiScreen {
   private static final Logger field_189845_a = LogManager.getLogger();
   private final TileEntityStructure field_189846_f;
   private Mirror field_189847_g;
   private Rotation field_189848_h;
   private StructureMode field_189849_i;
   private boolean field_189850_r;
   private boolean field_189851_s;
   private boolean field_189852_t;
   private GuiTextField field_189853_u;
   private GuiTextField field_189854_v;
   private GuiTextField field_189855_w;
   private GuiTextField field_189856_x;
   private GuiTextField field_189857_y;
   private GuiTextField field_189858_z;
   private GuiTextField field_189825_A;
   private GuiTextField field_189826_B;
   private GuiTextField field_189827_C;
   private GuiTextField field_189828_D;
   private GuiButton field_189829_E;
   private GuiButton field_189830_F;
   private GuiButton field_189831_G;
   private GuiButton field_189832_H;
   private GuiButton field_189833_I;
   private GuiButton field_189834_J;
   private GuiButton field_189835_K;
   private GuiButton field_189836_L;
   private GuiButton field_189837_M;
   private GuiButton field_189838_N;
   private GuiButton field_189839_O;
   private GuiButton field_189840_P;
   private GuiButton field_189841_Q;
   private GuiButton field_189842_R;
   private final List<GuiTextField> field_189843_S;
   private final DecimalFormat field_189844_T;

   public GuiEditStructure(TileEntityStructure var1) {
      super();
      this.field_189847_g = Mirror.NONE;
      this.field_189848_h = Rotation.NONE;
      this.field_189849_i = StructureMode.DATA;
      this.field_189843_S = Lists.newArrayList();
      this.field_189844_T = new DecimalFormat("0.0###");
      this.field_189846_f = var1;
      this.field_189844_T.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public void func_73876_c() {
      this.field_189853_u.func_146178_a();
      this.field_189854_v.func_146178_a();
      this.field_189855_w.func_146178_a();
      this.field_189856_x.func_146178_a();
      this.field_189857_y.func_146178_a();
      this.field_189858_z.func_146178_a();
      this.field_189825_A.func_146178_a();
      this.field_189826_B.func_146178_a();
      this.field_189827_C.func_146178_a();
      this.field_189828_D.func_146178_a();
   }

   private void func_195275_h() {
      if (this.func_210143_a(TileEntityStructure.UpdateCommand.UPDATE_DATA)) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
      }

   }

   private void func_195272_i() {
      this.field_189846_f.func_184411_a(this.field_189847_g);
      this.field_189846_f.func_184408_a(this.field_189848_h);
      this.field_189846_f.func_184405_a(this.field_189849_i);
      this.field_189846_f.func_184406_a(this.field_189850_r);
      this.field_189846_f.func_189703_e(this.field_189851_s);
      this.field_189846_f.func_189710_f(this.field_189852_t);
      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_189829_E = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 4 - 150, 210, 150, 20, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.func_195275_h();
         }
      });
      this.field_189830_F = this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 + 4, 210, 150, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.func_195272_i();
         }
      });
      this.field_189831_G = this.func_189646_b(new GuiButton(9, this.field_146294_l / 2 + 4 + 100, 185, 50, 20, I18n.func_135052_a("structure_block.button.save")) {
         public void func_194829_a(double var1, double var3) {
            if (GuiEditStructure.this.field_189846_f.func_189700_k() == StructureMode.SAVE) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.SAVE_AREA);
               GuiEditStructure.this.field_146297_k.func_147108_a((GuiScreen)null);
            }

         }
      });
      this.field_189832_H = this.func_189646_b(new GuiButton(10, this.field_146294_l / 2 + 4 + 100, 185, 50, 20, I18n.func_135052_a("structure_block.button.load")) {
         public void func_194829_a(double var1, double var3) {
            if (GuiEditStructure.this.field_189846_f.func_189700_k() == StructureMode.LOAD) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.LOAD_AREA);
               GuiEditStructure.this.field_146297_k.func_147108_a((GuiScreen)null);
            }

         }
      });
      this.field_189837_M = this.func_189646_b(new GuiButton(18, this.field_146294_l / 2 - 4 - 150, 185, 50, 20, "MODE") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_189724_l();
            GuiEditStructure.this.func_189823_j();
         }
      });
      this.field_189838_N = this.func_189646_b(new GuiButton(19, this.field_146294_l / 2 + 4 + 100, 120, 50, 20, I18n.func_135052_a("structure_block.button.detect_size")) {
         public void func_194829_a(double var1, double var3) {
            if (GuiEditStructure.this.field_189846_f.func_189700_k() == StructureMode.SAVE) {
               GuiEditStructure.this.func_210143_a(TileEntityStructure.UpdateCommand.SCAN_AREA);
               GuiEditStructure.this.field_146297_k.func_147108_a((GuiScreen)null);
            }

         }
      });
      this.field_189839_O = this.func_189646_b(new GuiButton(20, this.field_146294_l / 2 + 4 + 100, 160, 50, 20, "ENTITIES") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_184406_a(!GuiEditStructure.this.field_189846_f.func_189713_m());
            GuiEditStructure.this.func_189822_a();
         }
      });
      this.field_189840_P = this.func_189646_b(new GuiButton(21, this.field_146294_l / 2 - 20, 185, 40, 20, "MIRROR") {
         public void func_194829_a(double var1, double var3) {
            switch(GuiEditStructure.this.field_189846_f.func_189716_h()) {
            case NONE:
               GuiEditStructure.this.field_189846_f.func_184411_a(Mirror.LEFT_RIGHT);
               break;
            case LEFT_RIGHT:
               GuiEditStructure.this.field_189846_f.func_184411_a(Mirror.FRONT_BACK);
               break;
            case FRONT_BACK:
               GuiEditStructure.this.field_189846_f.func_184411_a(Mirror.NONE);
            }

            GuiEditStructure.this.func_189816_h();
         }
      });
      this.field_189841_Q = this.func_189646_b(new GuiButton(22, this.field_146294_l / 2 + 4 + 100, 80, 50, 20, "SHOWAIR") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_189703_e(!GuiEditStructure.this.field_189846_f.func_189707_H());
            GuiEditStructure.this.func_189814_f();
         }
      });
      this.field_189842_R = this.func_189646_b(new GuiButton(23, this.field_146294_l / 2 + 4 + 100, 80, 50, 20, "SHOWBB") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_189710_f(!GuiEditStructure.this.field_189846_f.func_189721_I());
            GuiEditStructure.this.func_189815_g();
         }
      });
      this.field_189833_I = this.func_189646_b(new GuiButton(11, this.field_146294_l / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_184408_a(Rotation.NONE);
            GuiEditStructure.this.func_189824_i();
         }
      });
      this.field_189834_J = this.func_189646_b(new GuiButton(12, this.field_146294_l / 2 - 1 - 40 - 20, 185, 40, 20, "90") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_184408_a(Rotation.CLOCKWISE_90);
            GuiEditStructure.this.func_189824_i();
         }
      });
      this.field_189835_K = this.func_189646_b(new GuiButton(13, this.field_146294_l / 2 + 1 + 20, 185, 40, 20, "180") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_184408_a(Rotation.CLOCKWISE_180);
            GuiEditStructure.this.func_189824_i();
         }
      });
      this.field_189836_L = this.func_189646_b(new GuiButton(14, this.field_146294_l / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270") {
         public void func_194829_a(double var1, double var3) {
            GuiEditStructure.this.field_189846_f.func_184408_a(Rotation.COUNTERCLOCKWISE_90);
            GuiEditStructure.this.func_189824_i();
         }
      });
      this.field_189843_S.clear();
      this.field_189853_u = new GuiTextField(2, this.field_146289_q, this.field_146294_l / 2 - 152, 40, 300, 20) {
         public boolean charTyped(char var1, int var2) {
            return !GuiEditStructure.func_208402_b(this.func_146179_b(), var1, this.func_146198_h()) ? false : super.charTyped(var1, var2);
         }
      };
      this.field_189853_u.func_146203_f(64);
      this.field_189853_u.func_146180_a(this.field_189846_f.func_189715_d());
      this.field_189843_S.add(this.field_189853_u);
      BlockPos var1 = this.field_189846_f.func_189711_e();
      this.field_189854_v = new GuiTextField(3, this.field_146289_q, this.field_146294_l / 2 - 152, 80, 80, 20);
      this.field_189854_v.func_146203_f(15);
      this.field_189854_v.func_146180_a(Integer.toString(var1.func_177958_n()));
      this.field_189843_S.add(this.field_189854_v);
      this.field_189855_w = new GuiTextField(4, this.field_146289_q, this.field_146294_l / 2 - 72, 80, 80, 20);
      this.field_189855_w.func_146203_f(15);
      this.field_189855_w.func_146180_a(Integer.toString(var1.func_177956_o()));
      this.field_189843_S.add(this.field_189855_w);
      this.field_189856_x = new GuiTextField(5, this.field_146289_q, this.field_146294_l / 2 + 8, 80, 80, 20);
      this.field_189856_x.func_146203_f(15);
      this.field_189856_x.func_146180_a(Integer.toString(var1.func_177952_p()));
      this.field_189843_S.add(this.field_189856_x);
      BlockPos var2 = this.field_189846_f.func_189717_g();
      this.field_189857_y = new GuiTextField(6, this.field_146289_q, this.field_146294_l / 2 - 152, 120, 80, 20);
      this.field_189857_y.func_146203_f(15);
      this.field_189857_y.func_146180_a(Integer.toString(var2.func_177958_n()));
      this.field_189843_S.add(this.field_189857_y);
      this.field_189858_z = new GuiTextField(7, this.field_146289_q, this.field_146294_l / 2 - 72, 120, 80, 20);
      this.field_189858_z.func_146203_f(15);
      this.field_189858_z.func_146180_a(Integer.toString(var2.func_177956_o()));
      this.field_189843_S.add(this.field_189858_z);
      this.field_189825_A = new GuiTextField(8, this.field_146289_q, this.field_146294_l / 2 + 8, 120, 80, 20);
      this.field_189825_A.func_146203_f(15);
      this.field_189825_A.func_146180_a(Integer.toString(var2.func_177952_p()));
      this.field_189843_S.add(this.field_189825_A);
      this.field_189826_B = new GuiTextField(15, this.field_146289_q, this.field_146294_l / 2 - 152, 120, 80, 20);
      this.field_189826_B.func_146203_f(15);
      this.field_189826_B.func_146180_a(this.field_189844_T.format((double)this.field_189846_f.func_189702_n()));
      this.field_189843_S.add(this.field_189826_B);
      this.field_189827_C = new GuiTextField(16, this.field_146289_q, this.field_146294_l / 2 - 72, 120, 80, 20);
      this.field_189827_C.func_146203_f(31);
      this.field_189827_C.func_146180_a(Long.toString(this.field_189846_f.func_189719_o()));
      this.field_189843_S.add(this.field_189827_C);
      this.field_189828_D = new GuiTextField(17, this.field_146289_q, this.field_146294_l / 2 - 152, 120, 240, 20);
      this.field_189828_D.func_146203_f(128);
      this.field_189828_D.func_146180_a(this.field_189846_f.func_189708_j());
      this.field_189843_S.add(this.field_189828_D);
      this.field_195124_j.addAll(this.field_189843_S);
      this.field_189847_g = this.field_189846_f.func_189716_h();
      this.func_189816_h();
      this.field_189848_h = this.field_189846_f.func_189726_i();
      this.func_189824_i();
      this.field_189849_i = this.field_189846_f.func_189700_k();
      this.func_189823_j();
      this.field_189850_r = this.field_189846_f.func_189713_m();
      this.func_189822_a();
      this.field_189851_s = this.field_189846_f.func_189707_H();
      this.func_189814_f();
      this.field_189852_t = this.field_189846_f.func_189721_I();
      this.func_189815_g();
      this.field_189853_u.func_146195_b(true);
      this.func_195073_a(this.field_189853_u);
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_189853_u.func_146179_b();
      String var5 = this.field_189854_v.func_146179_b();
      String var6 = this.field_189855_w.func_146179_b();
      String var7 = this.field_189856_x.func_146179_b();
      String var8 = this.field_189857_y.func_146179_b();
      String var9 = this.field_189858_z.func_146179_b();
      String var10 = this.field_189825_A.func_146179_b();
      String var11 = this.field_189826_B.func_146179_b();
      String var12 = this.field_189827_C.func_146179_b();
      String var13 = this.field_189828_D.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.field_189853_u.func_146180_a(var4);
      this.field_189854_v.func_146180_a(var5);
      this.field_189855_w.func_146180_a(var6);
      this.field_189856_x.func_146180_a(var7);
      this.field_189857_y.func_146180_a(var8);
      this.field_189858_z.func_146180_a(var9);
      this.field_189825_A.func_146180_a(var10);
      this.field_189826_B.func_146180_a(var11);
      this.field_189827_C.func_146180_a(var12);
      this.field_189828_D.func_146180_a(var13);
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   private void func_189822_a() {
      boolean var1 = !this.field_189846_f.func_189713_m();
      if (var1) {
         this.field_189839_O.field_146126_j = I18n.func_135052_a("options.on");
      } else {
         this.field_189839_O.field_146126_j = I18n.func_135052_a("options.off");
      }

   }

   private void func_189814_f() {
      boolean var1 = this.field_189846_f.func_189707_H();
      if (var1) {
         this.field_189841_Q.field_146126_j = I18n.func_135052_a("options.on");
      } else {
         this.field_189841_Q.field_146126_j = I18n.func_135052_a("options.off");
      }

   }

   private void func_189815_g() {
      boolean var1 = this.field_189846_f.func_189721_I();
      if (var1) {
         this.field_189842_R.field_146126_j = I18n.func_135052_a("options.on");
      } else {
         this.field_189842_R.field_146126_j = I18n.func_135052_a("options.off");
      }

   }

   private void func_189816_h() {
      Mirror var1 = this.field_189846_f.func_189716_h();
      switch(var1) {
      case NONE:
         this.field_189840_P.field_146126_j = "|";
         break;
      case LEFT_RIGHT:
         this.field_189840_P.field_146126_j = "< >";
         break;
      case FRONT_BACK:
         this.field_189840_P.field_146126_j = "^ v";
      }

   }

   private void func_189824_i() {
      this.field_189833_I.field_146124_l = true;
      this.field_189834_J.field_146124_l = true;
      this.field_189835_K.field_146124_l = true;
      this.field_189836_L.field_146124_l = true;
      switch(this.field_189846_f.func_189726_i()) {
      case NONE:
         this.field_189833_I.field_146124_l = false;
         break;
      case CLOCKWISE_180:
         this.field_189835_K.field_146124_l = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.field_189836_L.field_146124_l = false;
         break;
      case CLOCKWISE_90:
         this.field_189834_J.field_146124_l = false;
      }

   }

   private void func_189823_j() {
      this.field_189853_u.func_146195_b(false);
      this.field_189854_v.func_146195_b(false);
      this.field_189855_w.func_146195_b(false);
      this.field_189856_x.func_146195_b(false);
      this.field_189857_y.func_146195_b(false);
      this.field_189858_z.func_146195_b(false);
      this.field_189825_A.func_146195_b(false);
      this.field_189826_B.func_146195_b(false);
      this.field_189827_C.func_146195_b(false);
      this.field_189828_D.func_146195_b(false);
      this.field_189853_u.func_146189_e(false);
      this.field_189853_u.func_146195_b(false);
      this.field_189854_v.func_146189_e(false);
      this.field_189855_w.func_146189_e(false);
      this.field_189856_x.func_146189_e(false);
      this.field_189857_y.func_146189_e(false);
      this.field_189858_z.func_146189_e(false);
      this.field_189825_A.func_146189_e(false);
      this.field_189826_B.func_146189_e(false);
      this.field_189827_C.func_146189_e(false);
      this.field_189828_D.func_146189_e(false);
      this.field_189831_G.field_146125_m = false;
      this.field_189832_H.field_146125_m = false;
      this.field_189838_N.field_146125_m = false;
      this.field_189839_O.field_146125_m = false;
      this.field_189840_P.field_146125_m = false;
      this.field_189833_I.field_146125_m = false;
      this.field_189834_J.field_146125_m = false;
      this.field_189835_K.field_146125_m = false;
      this.field_189836_L.field_146125_m = false;
      this.field_189841_Q.field_146125_m = false;
      this.field_189842_R.field_146125_m = false;
      switch(this.field_189846_f.func_189700_k()) {
      case SAVE:
         this.field_189853_u.func_146189_e(true);
         this.field_189854_v.func_146189_e(true);
         this.field_189855_w.func_146189_e(true);
         this.field_189856_x.func_146189_e(true);
         this.field_189857_y.func_146189_e(true);
         this.field_189858_z.func_146189_e(true);
         this.field_189825_A.func_146189_e(true);
         this.field_189831_G.field_146125_m = true;
         this.field_189838_N.field_146125_m = true;
         this.field_189839_O.field_146125_m = true;
         this.field_189841_Q.field_146125_m = true;
         break;
      case LOAD:
         this.field_189853_u.func_146189_e(true);
         this.field_189854_v.func_146189_e(true);
         this.field_189855_w.func_146189_e(true);
         this.field_189856_x.func_146189_e(true);
         this.field_189826_B.func_146189_e(true);
         this.field_189827_C.func_146189_e(true);
         this.field_189832_H.field_146125_m = true;
         this.field_189839_O.field_146125_m = true;
         this.field_189840_P.field_146125_m = true;
         this.field_189833_I.field_146125_m = true;
         this.field_189834_J.field_146125_m = true;
         this.field_189835_K.field_146125_m = true;
         this.field_189836_L.field_146125_m = true;
         this.field_189842_R.field_146125_m = true;
         this.func_189824_i();
         break;
      case CORNER:
         this.field_189853_u.func_146189_e(true);
         break;
      case DATA:
         this.field_189828_D.func_146189_e(true);
      }

      this.field_189837_M.field_146126_j = I18n.func_135052_a("structure_block.mode." + this.field_189846_f.func_189700_k().func_176610_l());
   }

   private boolean func_210143_a(TileEntityStructure.UpdateCommand var1) {
      BlockPos var2 = new BlockPos(this.func_189817_c(this.field_189854_v.func_146179_b()), this.func_189817_c(this.field_189855_w.func_146179_b()), this.func_189817_c(this.field_189856_x.func_146179_b()));
      BlockPos var3 = new BlockPos(this.func_189817_c(this.field_189857_y.func_146179_b()), this.func_189817_c(this.field_189858_z.func_146179_b()), this.func_189817_c(this.field_189825_A.func_146179_b()));
      float var4 = this.func_189819_b(this.field_189826_B.func_146179_b());
      long var5 = this.func_189821_a(this.field_189827_C.func_146179_b());
      this.field_146297_k.func_147114_u().func_147297_a(new CPacketUpdateStructureBlock(this.field_189846_f.func_174877_v(), var1, this.field_189846_f.func_189700_k(), this.field_189853_u.func_146179_b(), var2, var3, this.field_189846_f.func_189716_h(), this.field_189846_f.func_189726_i(), this.field_189828_D.func_146179_b(), this.field_189846_f.func_189713_m(), this.field_189846_f.func_189707_H(), this.field_189846_f.func_189721_I(), var4, var5));
      return true;
   }

   private long func_189821_a(String var1) {
      try {
         return Long.valueOf(var1);
      } catch (NumberFormatException var3) {
         return 0L;
      }
   }

   private float func_189819_b(String var1) {
      try {
         return Float.valueOf(var1);
      } catch (NumberFormatException var3) {
         return 1.0F;
      }
   }

   private int func_189817_c(String var1) {
      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   public void func_195122_V_() {
      this.func_195272_i();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         Iterator var6 = this.field_189843_S.iterator();

         while(var6.hasNext()) {
            GuiTextField var7 = (GuiTextField)var6.next();
            var7.func_146195_b(this.getFocused() == var7);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 258) {
         if (var1 != 257 && var1 != 335) {
            return super.keyPressed(var1, var2, var3);
         } else {
            this.func_195275_h();
            return true;
         }
      } else {
         GuiTextField var4 = null;
         GuiTextField var5 = null;
         Iterator var6 = this.field_189843_S.iterator();

         GuiTextField var7;
         while(var6.hasNext()) {
            var7 = (GuiTextField)var6.next();
            if (var4 != null && var7.func_146176_q()) {
               var5 = var7;
               break;
            }

            if (var7.func_146206_l() && var7.func_146176_q()) {
               var4 = var7;
            }
         }

         if (var4 != null && var5 == null) {
            var6 = this.field_189843_S.iterator();

            while(var6.hasNext()) {
               var7 = (GuiTextField)var6.next();
               if (var7.func_146176_q() && var7 != var4) {
                  var5 = var7;
                  break;
               }
            }
         }

         if (var5 != null && var5 != var4) {
            var4.func_146195_b(false);
            var5.func_146195_b(true);
            this.func_195073_a(var5);
         }

         return true;
      }
   }

   private static boolean func_208402_b(String var0, char var1, int var2) {
      int var3 = var0.indexOf(58);
      int var4 = var0.indexOf(47);
      if (var1 == ':') {
         return (var4 == -1 || var2 <= var4) && var3 == -1;
      } else if (var1 == '/') {
         return var2 > var3;
      } else {
         return var1 == '_' || var1 == '-' || var1 >= 'a' && var1 <= 'z' || var1 >= '0' && var1 <= '9' || var1 == '.';
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      StructureMode var4 = this.field_189846_f.func_189700_k();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a(Blocks.field_185779_df.func_149739_a()), this.field_146294_l / 2, 10, 16777215);
      if (var4 != StructureMode.DATA) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("structure_block.structure_name"), this.field_146294_l / 2 - 153, 30, 10526880);
         this.field_189853_u.func_195608_a(var1, var2, var3);
      }

      String var5;
      int var6;
      if (var4 == StructureMode.LOAD || var4 == StructureMode.SAVE) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("structure_block.position"), this.field_146294_l / 2 - 153, 70, 10526880);
         this.field_189854_v.func_195608_a(var1, var2, var3);
         this.field_189855_w.func_195608_a(var1, var2, var3);
         this.field_189856_x.func_195608_a(var1, var2, var3);
         var5 = I18n.func_135052_a("structure_block.include_entities");
         var6 = this.field_146289_q.func_78256_a(var5);
         this.func_73731_b(this.field_146289_q, var5, this.field_146294_l / 2 + 154 - var6, 150, 10526880);
      }

      if (var4 == StructureMode.SAVE) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("structure_block.size"), this.field_146294_l / 2 - 153, 110, 10526880);
         this.field_189857_y.func_195608_a(var1, var2, var3);
         this.field_189858_z.func_195608_a(var1, var2, var3);
         this.field_189825_A.func_195608_a(var1, var2, var3);
         var5 = I18n.func_135052_a("structure_block.detect_size");
         var6 = this.field_146289_q.func_78256_a(var5);
         this.func_73731_b(this.field_146289_q, var5, this.field_146294_l / 2 + 154 - var6, 110, 10526880);
         String var7 = I18n.func_135052_a("structure_block.show_air");
         int var8 = this.field_146289_q.func_78256_a(var7);
         this.func_73731_b(this.field_146289_q, var7, this.field_146294_l / 2 + 154 - var8, 70, 10526880);
      }

      if (var4 == StructureMode.LOAD) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("structure_block.integrity"), this.field_146294_l / 2 - 153, 110, 10526880);
         this.field_189826_B.func_195608_a(var1, var2, var3);
         this.field_189827_C.func_195608_a(var1, var2, var3);
         var5 = I18n.func_135052_a("structure_block.show_boundingbox");
         var6 = this.field_146289_q.func_78256_a(var5);
         this.func_73731_b(this.field_146289_q, var5, this.field_146294_l / 2 + 154 - var6, 70, 10526880);
      }

      if (var4 == StructureMode.DATA) {
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("structure_block.custom_data"), this.field_146294_l / 2 - 153, 110, 10526880);
         this.field_189828_D.func_195608_a(var1, var2, var3);
      }

      var5 = "structure_block.mode_info." + var4.func_176610_l();
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a(var5), this.field_146294_l / 2 - 153, 174, 10526880);
      super.func_73863_a(var1, var2, var3);
   }

   public boolean func_73868_f() {
      return false;
   }
}
