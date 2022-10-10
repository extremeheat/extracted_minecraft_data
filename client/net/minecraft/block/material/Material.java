package net.minecraft.block.material;

public final class Material {
   public static final Material field_151579_a;
   public static final Material field_189963_J;
   public static final Material field_151567_E;
   public static final Material field_151593_r;
   public static final Material field_151585_k;
   public static final Material field_203243_f;
   public static final Material field_151582_l;
   public static final Material field_204868_h;
   public static final Material field_151586_h;
   public static final Material field_203244_i;
   public static final Material field_151587_i;
   public static final Material field_151597_y;
   public static final Material field_151581_o;
   public static final Material field_151594_q;
   public static final Material field_151569_G;
   public static final Material field_151591_t;
   public static final Material field_151571_B;
   public static final Material field_151578_c;
   public static final Material field_151577_b;
   public static final Material field_151598_x;
   public static final Material field_151595_p;
   public static final Material field_151583_m;
   public static final Material field_151575_d;
   public static final Material field_151580_n;
   public static final Material field_151590_u;
   public static final Material field_151584_j;
   public static final Material field_151592_s;
   public static final Material field_151588_w;
   public static final Material field_151570_A;
   public static final Material field_151576_e;
   public static final Material field_151573_f;
   public static final Material field_151596_z;
   public static final Material field_151574_g;
   public static final Material field_175972_I;
   public static final Material field_76233_E;
   public static final Material field_151589_v;
   public static final Material field_151572_C;
   public static final Material field_151566_D;
   public static final Material field_151568_F;
   private final MaterialColor field_76234_F;
   private final EnumPushReaction field_76242_K;
   private final boolean field_200521_M;
   private final boolean field_76235_G;
   private final boolean field_76241_J;
   private final boolean field_200523_P;
   private final boolean field_200524_Q;
   private final boolean field_76239_H;
   private final boolean field_200525_S;

   public Material(MaterialColor var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, EnumPushReaction var9) {
      super();
      this.field_76234_F = var1;
      this.field_200523_P = var2;
      this.field_200525_S = var3;
      this.field_200521_M = var4;
      this.field_200524_Q = var5;
      this.field_76241_J = var6;
      this.field_76235_G = var7;
      this.field_76239_H = var8;
      this.field_76242_K = var9;
   }

   public boolean func_76224_d() {
      return this.field_200523_P;
   }

   public boolean func_76220_a() {
      return this.field_200525_S;
   }

   public boolean func_76230_c() {
      return this.field_200521_M;
   }

   public boolean func_76217_h() {
      return this.field_76235_G;
   }

   public boolean func_76222_j() {
      return this.field_76239_H;
   }

   public boolean func_76218_k() {
      return this.field_200524_Q;
   }

   public boolean func_76229_l() {
      return this.field_76241_J;
   }

   public EnumPushReaction func_186274_m() {
      return this.field_76242_K;
   }

   public MaterialColor func_151565_r() {
      return this.field_76234_F;
   }

   static {
      field_151579_a = (new Material.Builder(MaterialColor.field_151660_b)).func_200508_c().func_200505_j().func_200502_b().func_200509_f().func_200506_i();
      field_189963_J = (new Material.Builder(MaterialColor.field_151660_b)).func_200508_c().func_200505_j().func_200502_b().func_200509_f().func_200506_i();
      field_151567_E = (new Material.Builder(MaterialColor.field_151660_b)).func_200508_c().func_200505_j().func_200502_b().func_200503_h().func_200506_i();
      field_151593_r = (new Material.Builder(MaterialColor.field_151659_e)).func_200508_c().func_200505_j().func_200502_b().func_200504_e().func_200506_i();
      field_151585_k = (new Material.Builder(MaterialColor.field_151669_i)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200506_i();
      field_203243_f = (new Material.Builder(MaterialColor.field_151662_n)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200506_i();
      field_151582_l = (new Material.Builder(MaterialColor.field_151669_i)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200504_e().func_200506_i();
      field_204868_h = (new Material.Builder(MaterialColor.field_151662_n)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200506_i();
      field_151586_h = (new Material.Builder(MaterialColor.field_151662_n)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200507_a().func_200506_i();
      field_203244_i = (new Material.Builder(MaterialColor.field_151662_n)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200507_a().func_200506_i();
      field_151587_i = (new Material.Builder(MaterialColor.field_151656_f)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200507_a().func_200506_i();
      field_151597_y = (new Material.Builder(MaterialColor.field_151666_j)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200510_d().func_200506_i();
      field_151581_o = (new Material.Builder(MaterialColor.field_151660_b)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200509_f().func_200506_i();
      field_151594_q = (new Material.Builder(MaterialColor.field_151660_b)).func_200508_c().func_200505_j().func_200502_b().func_200511_g().func_200506_i();
      field_151569_G = (new Material.Builder(MaterialColor.field_151659_e)).func_200508_c().func_200505_j().func_200511_g().func_200510_d().func_200506_i();
      field_151591_t = (new Material.Builder(MaterialColor.field_151660_b)).func_200506_i();
      field_151571_B = (new Material.Builder(MaterialColor.field_151667_k)).func_200506_i();
      field_151578_c = (new Material.Builder(MaterialColor.field_151664_l)).func_200506_i();
      field_151577_b = (new Material.Builder(MaterialColor.field_151661_c)).func_200506_i();
      field_151598_x = (new Material.Builder(MaterialColor.field_151657_g)).func_200506_i();
      field_151595_p = (new Material.Builder(MaterialColor.field_151658_d)).func_200506_i();
      field_151583_m = (new Material.Builder(MaterialColor.field_151673_t)).func_200506_i();
      field_151575_d = (new Material.Builder(MaterialColor.field_151663_o)).func_200504_e().func_200506_i();
      field_151580_n = (new Material.Builder(MaterialColor.field_151659_e)).func_200504_e().func_200506_i();
      field_151590_u = (new Material.Builder(MaterialColor.field_151656_f)).func_200504_e().func_200505_j().func_200506_i();
      field_151584_j = (new Material.Builder(MaterialColor.field_151669_i)).func_200504_e().func_200505_j().func_200511_g().func_200506_i();
      field_151592_s = (new Material.Builder(MaterialColor.field_151660_b)).func_200505_j().func_200506_i();
      field_151588_w = (new Material.Builder(MaterialColor.field_151657_g)).func_200505_j().func_200506_i();
      field_151570_A = (new Material.Builder(MaterialColor.field_151669_i)).func_200505_j().func_200511_g().func_200506_i();
      field_151576_e = (new Material.Builder(MaterialColor.field_151665_m)).func_200510_d().func_200506_i();
      field_151573_f = (new Material.Builder(MaterialColor.field_151668_h)).func_200510_d().func_200506_i();
      field_151596_z = (new Material.Builder(MaterialColor.field_151666_j)).func_200510_d().func_200506_i();
      field_151574_g = (new Material.Builder(MaterialColor.field_151668_h)).func_200510_d().func_200503_h().func_200506_i();
      field_175972_I = (new Material.Builder(MaterialColor.field_151660_b)).func_200510_d().func_200503_h().func_200506_i();
      field_76233_E = (new Material.Builder(MaterialColor.field_151665_m)).func_200503_h().func_200506_i();
      field_151589_v = (new Material.Builder(MaterialColor.field_151669_i)).func_200511_g().func_200506_i();
      field_151572_C = (new Material.Builder(MaterialColor.field_151669_i)).func_200511_g().func_200506_i();
      field_151566_D = (new Material.Builder(MaterialColor.field_151669_i)).func_200511_g().func_200506_i();
      field_151568_F = (new Material.Builder(MaterialColor.field_151660_b)).func_200511_g().func_200506_i();
   }

   public static class Builder {
      private EnumPushReaction field_200512_a;
      private boolean field_200513_b;
      private boolean field_200514_c;
      private boolean field_200515_d;
      private boolean field_200516_e;
      private boolean field_200517_f;
      private boolean field_200518_g;
      private final MaterialColor field_200519_h;
      private boolean field_200520_i;

      public Builder(MaterialColor var1) {
         super();
         this.field_200512_a = EnumPushReaction.NORMAL;
         this.field_200513_b = true;
         this.field_200515_d = true;
         this.field_200518_g = true;
         this.field_200520_i = true;
         this.field_200519_h = var1;
      }

      public Material.Builder func_200507_a() {
         this.field_200516_e = true;
         return this;
      }

      public Material.Builder func_200502_b() {
         this.field_200518_g = false;
         return this;
      }

      public Material.Builder func_200508_c() {
         this.field_200513_b = false;
         return this;
      }

      private Material.Builder func_200505_j() {
         this.field_200520_i = false;
         return this;
      }

      protected Material.Builder func_200510_d() {
         this.field_200515_d = false;
         return this;
      }

      protected Material.Builder func_200504_e() {
         this.field_200514_c = true;
         return this;
      }

      public Material.Builder func_200509_f() {
         this.field_200517_f = true;
         return this;
      }

      protected Material.Builder func_200511_g() {
         this.field_200512_a = EnumPushReaction.DESTROY;
         return this;
      }

      protected Material.Builder func_200503_h() {
         this.field_200512_a = EnumPushReaction.BLOCK;
         return this;
      }

      public Material func_200506_i() {
         return new Material(this.field_200519_h, this.field_200516_e, this.field_200518_g, this.field_200513_b, this.field_200520_i, this.field_200515_d, this.field_200514_c, this.field_200517_f, this.field_200512_a);
      }
   }
}
