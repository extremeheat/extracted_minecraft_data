package net.minecraft.world.storage;

import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public class WorldSummary implements Comparable<WorldSummary> {
   private final String field_75797_a;
   private final String field_75795_b;
   private final long field_75796_c;
   private final long field_75793_d;
   private final boolean field_75794_e;
   private final GameType field_75791_f;
   private final boolean field_75792_g;
   private final boolean field_75798_h;
   private final String field_186358_i;
   private final int field_186359_j;
   private final boolean field_186360_k;
   private final WorldType field_202843_l;

   public WorldSummary(WorldInfo var1, String var2, String var3, long var4, boolean var6) {
      super();
      this.field_75797_a = var2;
      this.field_75795_b = var3;
      this.field_75796_c = var1.func_76057_l();
      this.field_75793_d = var4;
      this.field_75791_f = var1.func_76077_q();
      this.field_75794_e = var6;
      this.field_75792_g = var1.func_76093_s();
      this.field_75798_h = var1.func_76086_u();
      this.field_186358_i = var1.func_186346_M();
      this.field_186359_j = var1.func_186344_K();
      this.field_186360_k = var1.func_186343_L();
      this.field_202843_l = var1.func_76067_t();
   }

   public String func_75786_a() {
      return this.field_75797_a;
   }

   public String func_75788_b() {
      return this.field_75795_b;
   }

   public long func_207744_c() {
      return this.field_75793_d;
   }

   public boolean func_75785_d() {
      return this.field_75794_e;
   }

   public long func_75784_e() {
      return this.field_75796_c;
   }

   public int compareTo(WorldSummary var1) {
      if (this.field_75796_c < var1.field_75796_c) {
         return 1;
      } else {
         return this.field_75796_c > var1.field_75796_c ? -1 : this.field_75797_a.compareTo(var1.field_75797_a);
      }
   }

   public GameType func_75790_f() {
      return this.field_75791_f;
   }

   public boolean func_75789_g() {
      return this.field_75792_g;
   }

   public boolean func_75783_h() {
      return this.field_75798_h;
   }

   public ITextComponent func_200538_i() {
      return (ITextComponent)(StringUtils.func_151246_b(this.field_186358_i) ? new TextComponentTranslation("selectWorld.versionUnknown", new Object[0]) : new TextComponentString(this.field_186358_i));
   }

   public boolean func_186355_l() {
      return this.func_186356_m() || this.func_197731_n() || this.func_202842_n();
   }

   public boolean func_186356_m() {
      return this.field_186359_j > 1631;
   }

   public boolean func_202842_n() {
      return this.field_202843_l == WorldType.field_180271_f && this.field_186359_j < 1466;
   }

   public boolean func_197731_n() {
      return this.field_186359_j < 1631;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((WorldSummary)var1);
   }
}
