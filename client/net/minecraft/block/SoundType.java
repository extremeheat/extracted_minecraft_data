package net.minecraft.block;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class SoundType {
   public static final SoundType field_185848_a;
   public static final SoundType field_185849_b;
   public static final SoundType field_185850_c;
   public static final SoundType field_185851_d;
   public static final SoundType field_185852_e;
   public static final SoundType field_185853_f;
   public static final SoundType field_185854_g;
   public static final SoundType field_185855_h;
   public static final SoundType field_185856_i;
   public static final SoundType field_185857_j;
   public static final SoundType field_185858_k;
   public static final SoundType field_185859_l;
   public static final SoundType field_211382_m;
   public static final SoundType field_211383_n;
   public final float field_185860_m;
   public final float field_185861_n;
   private final SoundEvent field_185862_o;
   private final SoundEvent field_185863_p;
   private final SoundEvent field_185864_q;
   private final SoundEvent field_185865_r;
   private final SoundEvent field_185866_s;

   public SoundType(float var1, float var2, SoundEvent var3, SoundEvent var4, SoundEvent var5, SoundEvent var6, SoundEvent var7) {
      super();
      this.field_185860_m = var1;
      this.field_185861_n = var2;
      this.field_185862_o = var3;
      this.field_185863_p = var4;
      this.field_185864_q = var5;
      this.field_185865_r = var6;
      this.field_185866_s = var7;
   }

   public float func_185843_a() {
      return this.field_185860_m;
   }

   public float func_185847_b() {
      return this.field_185861_n;
   }

   public SoundEvent func_185845_c() {
      return this.field_185862_o;
   }

   public SoundEvent func_185844_d() {
      return this.field_185863_p;
   }

   public SoundEvent func_185841_e() {
      return this.field_185864_q;
   }

   public SoundEvent func_185846_f() {
      return this.field_185865_r;
   }

   public SoundEvent func_185842_g() {
      return this.field_185866_s;
   }

   static {
      field_185848_a = new SoundType(1.0F, 1.0F, SoundEvents.field_187881_gQ, SoundEvents.field_187897_gY, SoundEvents.field_187891_gV, SoundEvents.field_187889_gU, SoundEvents.field_187887_gT);
      field_185849_b = new SoundType(1.0F, 1.0F, SoundEvents.field_187581_bW, SoundEvents.field_187668_ca, SoundEvents.field_187587_bZ, SoundEvents.field_187585_bY, SoundEvents.field_187583_bX);
      field_185850_c = new SoundType(1.0F, 1.0F, SoundEvents.field_187571_bR, SoundEvents.field_187579_bV, SoundEvents.field_187577_bU, SoundEvents.field_187575_bT, SoundEvents.field_187573_bS);
      field_185851_d = new SoundType(1.0F, 1.0F, SoundEvents.field_187835_fT, SoundEvents.field_187902_gb, SoundEvents.field_187845_fY, SoundEvents.field_187843_fX, SoundEvents.field_187841_fW);
      field_185852_e = new SoundType(1.0F, 1.5F, SoundEvents.field_187766_dk, SoundEvents.field_187778_dq, SoundEvents.field_187772_dn, SoundEvents.field_187770_dm, SoundEvents.field_187768_dl);
      field_185853_f = new SoundType(1.0F, 1.0F, SoundEvents.field_187561_bM, SoundEvents.field_187569_bQ, SoundEvents.field_187567_bP, SoundEvents.field_187565_bO, SoundEvents.field_187563_bN);
      field_185854_g = new SoundType(1.0F, 1.0F, SoundEvents.field_187546_ae, SoundEvents.field_187554_ai, SoundEvents.field_187552_ah, SoundEvents.field_187550_ag, SoundEvents.field_187548_af);
      field_185855_h = new SoundType(1.0F, 1.0F, SoundEvents.field_187747_eB, SoundEvents.field_187755_eF, SoundEvents.field_187753_eE, SoundEvents.field_187751_eD, SoundEvents.field_187749_eC);
      field_185856_i = new SoundType(1.0F, 1.0F, SoundEvents.field_187807_fF, SoundEvents.field_187815_fJ, SoundEvents.field_187813_fI, SoundEvents.field_187811_fH, SoundEvents.field_187809_fG);
      field_185857_j = new SoundType(1.0F, 1.0F, SoundEvents.field_187641_cS, SoundEvents.field_187653_cW, SoundEvents.field_187650_cV, SoundEvents.field_187647_cU, SoundEvents.field_187644_cT);
      field_185858_k = new SoundType(0.3F, 1.0F, SoundEvents.field_187677_b, SoundEvents.field_187695_h, SoundEvents.field_187692_g, SoundEvents.field_187686_e, SoundEvents.field_187683_d);
      field_185859_l = new SoundType(1.0F, 1.0F, SoundEvents.field_187872_fl, SoundEvents.field_187888_ft, SoundEvents.field_187884_fr, SoundEvents.field_187878_fo, SoundEvents.field_187876_fn);
      field_211382_m = new SoundType(1.0F, 1.0F, SoundEvents.field_211414_dn, SoundEvents.field_211418_dr, SoundEvents.field_211417_dq, SoundEvents.field_211416_dp, SoundEvents.field_211415_do);
      field_211383_n = new SoundType(1.0F, 1.0F, SoundEvents.field_211419_ds, SoundEvents.field_211423_dw, SoundEvents.field_211422_dv, SoundEvents.field_211421_du, SoundEvents.field_211420_dt);
   }
}
