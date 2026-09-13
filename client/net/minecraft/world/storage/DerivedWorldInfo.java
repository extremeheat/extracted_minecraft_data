package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo field_76115_a;

   public DerivedWorldInfo(WorldInfo var1) {
      super();
      this.field_76115_a = var1;
   }

   @Override
   public NBTTagCompound func_76066_a() {
      return this.field_76115_a.func_76066_a();
   }

   @Override
   public NBTTagCompound func_76082_a(NBTTagCompound var1) {
      return this.field_76115_a.func_76082_a(var1);
   }

   @Override
   public long func_76063_b() {
      return this.field_76115_a.func_76063_b();
   }

   @Override
   public int func_76079_c() {
      return this.field_76115_a.func_76079_c();
   }

   @Override
   public int func_76075_d() {
      return this.field_76115_a.func_76075_d();
   }

   @Override
   public int func_76074_e() {
      return this.field_76115_a.func_76074_e();
   }

   @Override
   public long func_82573_f() {
      return this.field_76115_a.func_82573_f();
   }

   @Override
   public long func_76073_f() {
      return this.field_76115_a.func_76073_f();
   }

   @Override
   public long func_76092_g() {
      return this.field_76115_a.func_76092_g();
   }

   @Override
   public NBTTagCompound func_76072_h() {
      return this.field_76115_a.func_76072_h();
   }

   @Override
   public int func_76076_i() {
      return this.field_76115_a.func_76076_i();
   }

   @Override
   public String func_76065_j() {
      return this.field_76115_a.func_76065_j();
   }

   @Override
   public int func_76088_k() {
      return this.field_76115_a.func_76088_k();
   }

   @Override
   public long func_76057_l() {
      return this.field_76115_a.func_76057_l();
   }

   @Override
   public boolean func_76061_m() {
      return this.field_76115_a.func_76061_m();
   }

   @Override
   public int func_76071_n() {
      return this.field_76115_a.func_76071_n();
   }

   @Override
   public boolean func_76059_o() {
      return this.field_76115_a.func_76059_o();
   }

   @Override
   public int func_76083_p() {
      return this.field_76115_a.func_76083_p();
   }

   @Override
   public WorldSettings$GameType func_76077_q() {
      return this.field_76115_a.func_76077_q();
   }

   @Override
   public void func_76058_a(int var1) {
   }

   @Override
   public void func_76056_b(int var1) {
   }

   @Override
   public void func_76087_c(int var1) {
   }

   @Override
   public void func_82572_b(long var1) {
   }

   @Override
   public void func_76068_b(long var1) {
   }

   @Override
   public void func_76081_a(int var1, int var2, int var3) {
   }

   @Override
   public void func_76062_a(String var1) {
   }

   @Override
   public void func_76078_e(int var1) {
   }

   @Override
   public void func_76069_a(boolean var1) {
   }

   @Override
   public void func_76090_f(int var1) {
   }

   @Override
   public void func_76084_b(boolean var1) {
   }

   @Override
   public void func_76080_g(int var1) {
   }

   @Override
   public boolean func_76089_r() {
      return this.field_76115_a.func_76089_r();
   }

   @Override
   public boolean func_76093_s() {
      return this.field_76115_a.func_76093_s();
   }

   @Override
   public WorldType func_76067_t() {
      return this.field_76115_a.func_76067_t();
   }

   @Override
   public void func_76085_a(WorldType var1) {
   }

   @Override
   public boolean func_76086_u() {
      return this.field_76115_a.func_76086_u();
   }

   @Override
   public boolean func_76070_v() {
      return this.field_76115_a.func_76070_v();
   }

   @Override
   public void func_76091_d(boolean var1) {
   }

   @Override
   public GameRules func_82574_x() {
      return this.field_76115_a.func_82574_x();
   }
}
