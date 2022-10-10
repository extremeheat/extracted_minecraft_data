package net.minecraft.block.material;

public class MaterialColor {
   public static final MaterialColor[] field_76281_a = new MaterialColor[64];
   public static final MaterialColor field_151660_b = new MaterialColor(0, 0);
   public static final MaterialColor field_151661_c = new MaterialColor(1, 8368696);
   public static final MaterialColor field_151658_d = new MaterialColor(2, 16247203);
   public static final MaterialColor field_151659_e = new MaterialColor(3, 13092807);
   public static final MaterialColor field_151656_f = new MaterialColor(4, 16711680);
   public static final MaterialColor field_151657_g = new MaterialColor(5, 10526975);
   public static final MaterialColor field_151668_h = new MaterialColor(6, 10987431);
   public static final MaterialColor field_151669_i = new MaterialColor(7, 31744);
   public static final MaterialColor field_151666_j = new MaterialColor(8, 16777215);
   public static final MaterialColor field_151667_k = new MaterialColor(9, 10791096);
   public static final MaterialColor field_151664_l = new MaterialColor(10, 9923917);
   public static final MaterialColor field_151665_m = new MaterialColor(11, 7368816);
   public static final MaterialColor field_151662_n = new MaterialColor(12, 4210943);
   public static final MaterialColor field_151663_o = new MaterialColor(13, 9402184);
   public static final MaterialColor field_151677_p = new MaterialColor(14, 16776437);
   public static final MaterialColor field_151676_q = new MaterialColor(15, 14188339);
   public static final MaterialColor field_151675_r = new MaterialColor(16, 11685080);
   public static final MaterialColor field_151674_s = new MaterialColor(17, 6724056);
   public static final MaterialColor field_151673_t = new MaterialColor(18, 15066419);
   public static final MaterialColor field_151672_u = new MaterialColor(19, 8375321);
   public static final MaterialColor field_151671_v = new MaterialColor(20, 15892389);
   public static final MaterialColor field_151670_w = new MaterialColor(21, 5000268);
   public static final MaterialColor field_197656_x = new MaterialColor(22, 10066329);
   public static final MaterialColor field_151679_y = new MaterialColor(23, 5013401);
   public static final MaterialColor field_151678_z = new MaterialColor(24, 8339378);
   public static final MaterialColor field_151649_A = new MaterialColor(25, 3361970);
   public static final MaterialColor field_151650_B = new MaterialColor(26, 6704179);
   public static final MaterialColor field_151651_C = new MaterialColor(27, 6717235);
   public static final MaterialColor field_151645_D = new MaterialColor(28, 10040115);
   public static final MaterialColor field_151646_E = new MaterialColor(29, 1644825);
   public static final MaterialColor field_151647_F = new MaterialColor(30, 16445005);
   public static final MaterialColor field_151648_G = new MaterialColor(31, 6085589);
   public static final MaterialColor field_151652_H = new MaterialColor(32, 4882687);
   public static final MaterialColor field_151653_I = new MaterialColor(33, 55610);
   public static final MaterialColor field_151654_J = new MaterialColor(34, 8476209);
   public static final MaterialColor field_151655_K = new MaterialColor(35, 7340544);
   public static final MaterialColor field_193561_M = new MaterialColor(36, 13742497);
   public static final MaterialColor field_193562_N = new MaterialColor(37, 10441252);
   public static final MaterialColor field_193563_O = new MaterialColor(38, 9787244);
   public static final MaterialColor field_193564_P = new MaterialColor(39, 7367818);
   public static final MaterialColor field_193565_Q = new MaterialColor(40, 12223780);
   public static final MaterialColor field_193566_R = new MaterialColor(41, 6780213);
   public static final MaterialColor field_193567_S = new MaterialColor(42, 10505550);
   public static final MaterialColor field_193568_T = new MaterialColor(43, 3746083);
   public static final MaterialColor field_197655_T = new MaterialColor(44, 8874850);
   public static final MaterialColor field_193570_V = new MaterialColor(45, 5725276);
   public static final MaterialColor field_193571_W = new MaterialColor(46, 8014168);
   public static final MaterialColor field_193572_X = new MaterialColor(47, 4996700);
   public static final MaterialColor field_193573_Y = new MaterialColor(48, 4993571);
   public static final MaterialColor field_193574_Z = new MaterialColor(49, 5001770);
   public static final MaterialColor field_193559_aa = new MaterialColor(50, 9321518);
   public static final MaterialColor field_193560_ab = new MaterialColor(51, 2430480);
   public final int field_76291_p;
   public final int field_76290_q;

   private MaterialColor(int var1, int var2) {
      super();
      if (var1 >= 0 && var1 <= 63) {
         this.field_76290_q = var1;
         this.field_76291_p = var2;
         field_76281_a[var1] = this;
      } else {
         throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
      }
   }

   public int func_151643_b(int var1) {
      short var2 = 220;
      if (var1 == 3) {
         var2 = 135;
      }

      if (var1 == 2) {
         var2 = 255;
      }

      if (var1 == 1) {
         var2 = 220;
      }

      if (var1 == 0) {
         var2 = 180;
      }

      int var3 = (this.field_76291_p >> 16 & 255) * var2 / 255;
      int var4 = (this.field_76291_p >> 8 & 255) * var2 / 255;
      int var5 = (this.field_76291_p & 255) * var2 / 255;
      return -16777216 | var5 << 16 | var4 << 8 | var3;
   }
}
