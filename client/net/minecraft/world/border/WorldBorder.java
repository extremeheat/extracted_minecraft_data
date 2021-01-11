package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

public class WorldBorder {
   private final List<IBorderListener> field_177758_a = Lists.newArrayList();
   private double field_177756_b = 0.0D;
   private double field_177757_c = 0.0D;
   private double field_177754_d = 6.0E7D;
   private double field_177755_e;
   private long field_177752_f;
   private long field_177753_g;
   private int field_177762_h;
   private double field_177763_i;
   private double field_177760_j;
   private int field_177761_k;
   private int field_177759_l;

   public WorldBorder() {
      super();
      this.field_177755_e = this.field_177754_d;
      this.field_177762_h = 29999984;
      this.field_177763_i = 0.2D;
      this.field_177760_j = 5.0D;
      this.field_177761_k = 15;
      this.field_177759_l = 5;
   }

   public boolean func_177746_a(BlockPos var1) {
      return (double)(var1.func_177958_n() + 1) > this.func_177726_b() && (double)var1.func_177958_n() < this.func_177728_d() && (double)(var1.func_177952_p() + 1) > this.func_177736_c() && (double)var1.func_177952_p() < this.func_177733_e();
   }

   public boolean func_177730_a(ChunkCoordIntPair var1) {
      return (double)var1.func_180332_e() > this.func_177726_b() && (double)var1.func_180334_c() < this.func_177728_d() && (double)var1.func_180330_f() > this.func_177736_c() && (double)var1.func_180333_d() < this.func_177733_e();
   }

   public boolean func_177743_a(AxisAlignedBB var1) {
      return var1.field_72336_d > this.func_177726_b() && var1.field_72340_a < this.func_177728_d() && var1.field_72334_f > this.func_177736_c() && var1.field_72339_c < this.func_177733_e();
   }

   public double func_177745_a(Entity var1) {
      return this.func_177729_b(var1.field_70165_t, var1.field_70161_v);
   }

   public double func_177729_b(double var1, double var3) {
      double var5 = var3 - this.func_177736_c();
      double var7 = this.func_177733_e() - var3;
      double var9 = var1 - this.func_177726_b();
      double var11 = this.func_177728_d() - var1;
      double var13 = Math.min(var9, var11);
      var13 = Math.min(var13, var5);
      return Math.min(var13, var7);
   }

   public EnumBorderStatus func_177734_a() {
      if (this.field_177755_e < this.field_177754_d) {
         return EnumBorderStatus.SHRINKING;
      } else {
         return this.field_177755_e > this.field_177754_d ? EnumBorderStatus.GROWING : EnumBorderStatus.STATIONARY;
      }
   }

   public double func_177726_b() {
      double var1 = this.func_177731_f() - this.func_177741_h() / 2.0D;
      if (var1 < (double)(-this.field_177762_h)) {
         var1 = (double)(-this.field_177762_h);
      }

      return var1;
   }

   public double func_177736_c() {
      double var1 = this.func_177721_g() - this.func_177741_h() / 2.0D;
      if (var1 < (double)(-this.field_177762_h)) {
         var1 = (double)(-this.field_177762_h);
      }

      return var1;
   }

   public double func_177728_d() {
      double var1 = this.func_177731_f() + this.func_177741_h() / 2.0D;
      if (var1 > (double)this.field_177762_h) {
         var1 = (double)this.field_177762_h;
      }

      return var1;
   }

   public double func_177733_e() {
      double var1 = this.func_177721_g() + this.func_177741_h() / 2.0D;
      if (var1 > (double)this.field_177762_h) {
         var1 = (double)this.field_177762_h;
      }

      return var1;
   }

   public double func_177731_f() {
      return this.field_177756_b;
   }

   public double func_177721_g() {
      return this.field_177757_c;
   }

   public void func_177739_c(double var1, double var3) {
      this.field_177756_b = var1;
      this.field_177757_c = var3;
      Iterator var5 = this.func_177735_k().iterator();

      while(var5.hasNext()) {
         IBorderListener var6 = (IBorderListener)var5.next();
         var6.func_177693_a(this, var1, var3);
      }

   }

   public double func_177741_h() {
      if (this.func_177734_a() != EnumBorderStatus.STATIONARY) {
         double var1 = (double)((float)(System.currentTimeMillis() - this.field_177753_g) / (float)(this.field_177752_f - this.field_177753_g));
         if (var1 < 1.0D) {
            return this.field_177754_d + (this.field_177755_e - this.field_177754_d) * var1;
         }

         this.func_177750_a(this.field_177755_e);
      }

      return this.field_177754_d;
   }

   public long func_177732_i() {
      return this.func_177734_a() != EnumBorderStatus.STATIONARY ? this.field_177752_f - System.currentTimeMillis() : 0L;
   }

   public double func_177751_j() {
      return this.field_177755_e;
   }

   public void func_177750_a(double var1) {
      this.field_177754_d = var1;
      this.field_177755_e = var1;
      this.field_177752_f = System.currentTimeMillis();
      this.field_177753_g = this.field_177752_f;
      Iterator var3 = this.func_177735_k().iterator();

      while(var3.hasNext()) {
         IBorderListener var4 = (IBorderListener)var3.next();
         var4.func_177694_a(this, var1);
      }

   }

   public void func_177738_a(double var1, double var3, long var5) {
      this.field_177754_d = var1;
      this.field_177755_e = var3;
      this.field_177753_g = System.currentTimeMillis();
      this.field_177752_f = this.field_177753_g + var5;
      Iterator var7 = this.func_177735_k().iterator();

      while(var7.hasNext()) {
         IBorderListener var8 = (IBorderListener)var7.next();
         var8.func_177692_a(this, var1, var3, var5);
      }

   }

   protected List<IBorderListener> func_177735_k() {
      return Lists.newArrayList(this.field_177758_a);
   }

   public void func_177737_a(IBorderListener var1) {
      this.field_177758_a.add(var1);
   }

   public void func_177725_a(int var1) {
      this.field_177762_h = var1;
   }

   public int func_177722_l() {
      return this.field_177762_h;
   }

   public double func_177742_m() {
      return this.field_177760_j;
   }

   public void func_177724_b(double var1) {
      this.field_177760_j = var1;
      Iterator var3 = this.func_177735_k().iterator();

      while(var3.hasNext()) {
         IBorderListener var4 = (IBorderListener)var3.next();
         var4.func_177695_c(this, var1);
      }

   }

   public double func_177727_n() {
      return this.field_177763_i;
   }

   public void func_177744_c(double var1) {
      this.field_177763_i = var1;
      Iterator var3 = this.func_177735_k().iterator();

      while(var3.hasNext()) {
         IBorderListener var4 = (IBorderListener)var3.next();
         var4.func_177696_b(this, var1);
      }

   }

   public double func_177749_o() {
      return this.field_177752_f == this.field_177753_g ? 0.0D : Math.abs(this.field_177754_d - this.field_177755_e) / (double)(this.field_177752_f - this.field_177753_g);
   }

   public int func_177740_p() {
      return this.field_177761_k;
   }

   public void func_177723_b(int var1) {
      this.field_177761_k = var1;
      Iterator var2 = this.func_177735_k().iterator();

      while(var2.hasNext()) {
         IBorderListener var3 = (IBorderListener)var2.next();
         var3.func_177691_a(this, var1);
      }

   }

   public int func_177748_q() {
      return this.field_177759_l;
   }

   public void func_177747_c(int var1) {
      this.field_177759_l = var1;
      Iterator var2 = this.func_177735_k().iterator();

      while(var2.hasNext()) {
         IBorderListener var3 = (IBorderListener)var2.next();
         var3.func_177690_b(this, var1);
      }

   }
}
