package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo field_76115_a;

   public DerivedWorldInfo(WorldInfo var1) {
      super();
      this.field_76115_a = var1;
   }

   public NBTTagCompound func_76082_a(@Nullable NBTTagCompound var1) {
      return this.field_76115_a.func_76082_a(var1);
   }

   public long func_76063_b() {
      return this.field_76115_a.func_76063_b();
   }

   public int func_76079_c() {
      return this.field_76115_a.func_76079_c();
   }

   public int func_76075_d() {
      return this.field_76115_a.func_76075_d();
   }

   public int func_76074_e() {
      return this.field_76115_a.func_76074_e();
   }

   public long func_82573_f() {
      return this.field_76115_a.func_82573_f();
   }

   public long func_76073_f() {
      return this.field_76115_a.func_76073_f();
   }

   public long func_76092_g() {
      return this.field_76115_a.func_76092_g();
   }

   public NBTTagCompound func_76072_h() {
      return this.field_76115_a.func_76072_h();
   }

   public int func_202836_i() {
      return this.field_76115_a.func_202836_i();
   }

   public String func_76065_j() {
      return this.field_76115_a.func_76065_j();
   }

   public int func_76088_k() {
      return this.field_76115_a.func_76088_k();
   }

   public long func_76057_l() {
      return this.field_76115_a.func_76057_l();
   }

   public boolean func_76061_m() {
      return this.field_76115_a.func_76061_m();
   }

   public int func_76071_n() {
      return this.field_76115_a.func_76071_n();
   }

   public boolean func_76059_o() {
      return this.field_76115_a.func_76059_o();
   }

   public int func_76083_p() {
      return this.field_76115_a.func_76083_p();
   }

   public GameType func_76077_q() {
      return this.field_76115_a.func_76077_q();
   }

   public void func_76058_a(int var1) {
   }

   public void func_76056_b(int var1) {
   }

   public void func_76087_c(int var1) {
   }

   public void func_82572_b(long var1) {
   }

   public void func_76068_b(long var1) {
   }

   public void func_176143_a(BlockPos var1) {
   }

   public void func_76062_a(String var1) {
   }

   public void func_76078_e(int var1) {
   }

   public void func_76069_a(boolean var1) {
   }

   public void func_76090_f(int var1) {
   }

   public void func_76084_b(boolean var1) {
   }

   public void func_76080_g(int var1) {
   }

   public boolean func_76089_r() {
      return this.field_76115_a.func_76089_r();
   }

   public boolean func_76093_s() {
      return this.field_76115_a.func_76093_s();
   }

   public WorldType func_76067_t() {
      return this.field_76115_a.func_76067_t();
   }

   public void func_76085_a(WorldType var1) {
   }

   public boolean func_76086_u() {
      return this.field_76115_a.func_76086_u();
   }

   public void func_176121_c(boolean var1) {
   }

   public boolean func_76070_v() {
      return this.field_76115_a.func_76070_v();
   }

   public void func_76091_d(boolean var1) {
   }

   public GameRules func_82574_x() {
      return this.field_76115_a.func_82574_x();
   }

   public EnumDifficulty func_176130_y() {
      return this.field_76115_a.func_176130_y();
   }

   public void func_176144_a(EnumDifficulty var1) {
   }

   public boolean func_176123_z() {
      return this.field_76115_a.func_176123_z();
   }

   public void func_180783_e(boolean var1) {
   }

   public void func_186345_a(DimensionType var1, NBTTagCompound var2) {
      this.field_76115_a.func_186345_a(var1, var2);
   }

   public NBTTagCompound func_186347_a(DimensionType var1) {
      return this.field_76115_a.func_186347_a(var1);
   }
}
