package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class WorldBorder {
   private final List<IBorderListener> field_177758_a = Lists.newArrayList();
   private double field_177763_i = 0.2D;
   private double field_177760_j = 5.0D;
   private int field_177761_k = 15;
   private int field_177759_l = 5;
   private double field_177756_b;
   private double field_177757_c;
   private int field_177762_h = 29999984;
   private WorldBorder.IBorderInfo field_212674_i = new WorldBorder.StationaryBorderInfo(6.0E7D);

   public WorldBorder() {
      super();
   }

   public boolean func_177746_a(BlockPos var1) {
      return (double)(var1.func_177958_n() + 1) > this.func_177726_b() && (double)var1.func_177958_n() < this.func_177728_d() && (double)(var1.func_177952_p() + 1) > this.func_177736_c() && (double)var1.func_177952_p() < this.func_177733_e();
   }

   public boolean func_177730_a(ChunkPos var1) {
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
      return this.field_212674_i.func_212655_i();
   }

   public double func_177726_b() {
      return this.field_212674_i.func_212658_a();
   }

   public double func_177736_c() {
      return this.field_212674_i.func_212656_c();
   }

   public double func_177728_d() {
      return this.field_212674_i.func_212654_b();
   }

   public double func_177733_e() {
      return this.field_212674_i.func_212648_d();
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
      this.field_212674_i.func_212653_k();
      Iterator var5 = this.func_177735_k().iterator();

      while(var5.hasNext()) {
         IBorderListener var6 = (IBorderListener)var5.next();
         var6.func_177693_a(this, var1, var3);
      }

   }

   public double func_177741_h() {
      return this.field_212674_i.func_212647_e();
   }

   public long func_177732_i() {
      return this.field_212674_i.func_212657_g();
   }

   public double func_177751_j() {
      return this.field_212674_i.func_212650_h();
   }

   public void func_177750_a(double var1) {
      this.field_212674_i = new WorldBorder.StationaryBorderInfo(var1);
      Iterator var3 = this.func_177735_k().iterator();

      while(var3.hasNext()) {
         IBorderListener var4 = (IBorderListener)var3.next();
         var4.func_177694_a(this, var1);
      }

   }

   public void func_177738_a(double var1, double var3, long var5) {
      this.field_212674_i = (WorldBorder.IBorderInfo)(var1 != var3 ? new WorldBorder.MovingBorderInfo(var1, var3, var5) : new WorldBorder.StationaryBorderInfo(var3));
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
      this.field_212674_i.func_212652_j();
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
      return this.field_212674_i.func_212649_f();
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

   public void func_212673_r() {
      this.field_212674_i = this.field_212674_i.func_212651_l();
   }

   class StationaryBorderInfo implements WorldBorder.IBorderInfo {
      private final double field_212667_b;
      private double field_212668_c;
      private double field_212669_d;
      private double field_212670_e;
      private double field_212671_f;

      public StationaryBorderInfo(double var2) {
         super();
         this.field_212667_b = var2;
         this.func_212665_m();
      }

      public double func_212658_a() {
         return this.field_212668_c;
      }

      public double func_212654_b() {
         return this.field_212670_e;
      }

      public double func_212656_c() {
         return this.field_212669_d;
      }

      public double func_212648_d() {
         return this.field_212671_f;
      }

      public double func_212647_e() {
         return this.field_212667_b;
      }

      public EnumBorderStatus func_212655_i() {
         return EnumBorderStatus.STATIONARY;
      }

      public double func_212649_f() {
         return 0.0D;
      }

      public long func_212657_g() {
         return 0L;
      }

      public double func_212650_h() {
         return this.field_212667_b;
      }

      private void func_212665_m() {
         this.field_212668_c = Math.max(WorldBorder.this.func_177731_f() - this.field_212667_b / 2.0D, (double)(-WorldBorder.this.field_177762_h));
         this.field_212669_d = Math.max(WorldBorder.this.func_177721_g() - this.field_212667_b / 2.0D, (double)(-WorldBorder.this.field_177762_h));
         this.field_212670_e = Math.min(WorldBorder.this.func_177731_f() + this.field_212667_b / 2.0D, (double)WorldBorder.this.field_177762_h);
         this.field_212671_f = Math.min(WorldBorder.this.func_177721_g() + this.field_212667_b / 2.0D, (double)WorldBorder.this.field_177762_h);
      }

      public void func_212652_j() {
         this.func_212665_m();
      }

      public void func_212653_k() {
         this.func_212665_m();
      }

      public WorldBorder.IBorderInfo func_212651_l() {
         return this;
      }
   }

   class MovingBorderInfo implements WorldBorder.IBorderInfo {
      private final double field_212660_b;
      private final double field_212661_c;
      private final long field_212662_d;
      private final long field_212663_e;
      private final double field_212664_f;

      private MovingBorderInfo(double var2, double var4, long var6) {
         super();
         this.field_212660_b = var2;
         this.field_212661_c = var4;
         this.field_212664_f = (double)var6;
         this.field_212663_e = Util.func_211177_b();
         this.field_212662_d = this.field_212663_e + var6;
      }

      public double func_212658_a() {
         return Math.max(WorldBorder.this.func_177731_f() - this.func_212647_e() / 2.0D, (double)(-WorldBorder.this.field_177762_h));
      }

      public double func_212656_c() {
         return Math.max(WorldBorder.this.func_177721_g() - this.func_212647_e() / 2.0D, (double)(-WorldBorder.this.field_177762_h));
      }

      public double func_212654_b() {
         return Math.min(WorldBorder.this.func_177731_f() + this.func_212647_e() / 2.0D, (double)WorldBorder.this.field_177762_h);
      }

      public double func_212648_d() {
         return Math.min(WorldBorder.this.func_177721_g() + this.func_212647_e() / 2.0D, (double)WorldBorder.this.field_177762_h);
      }

      public double func_212647_e() {
         double var1 = (double)(Util.func_211177_b() - this.field_212663_e) / this.field_212664_f;
         return var1 < 1.0D ? this.field_212660_b + (this.field_212661_c - this.field_212660_b) * var1 : this.field_212661_c;
      }

      public double func_212649_f() {
         return Math.abs(this.field_212660_b - this.field_212661_c) / (double)(this.field_212662_d - this.field_212663_e);
      }

      public long func_212657_g() {
         return this.field_212662_d - Util.func_211177_b();
      }

      public double func_212650_h() {
         return this.field_212661_c;
      }

      public EnumBorderStatus func_212655_i() {
         return this.field_212661_c < this.field_212660_b ? EnumBorderStatus.SHRINKING : EnumBorderStatus.GROWING;
      }

      public void func_212653_k() {
      }

      public void func_212652_j() {
      }

      public WorldBorder.IBorderInfo func_212651_l() {
         return (WorldBorder.IBorderInfo)(this.func_212657_g() <= 0L ? WorldBorder.this.new StationaryBorderInfo(this.field_212661_c) : this);
      }

      // $FF: synthetic method
      MovingBorderInfo(double var2, double var4, long var6, Object var8) {
         this(var2, var4, var6);
      }
   }

   interface IBorderInfo {
      double func_212658_a();

      double func_212654_b();

      double func_212656_c();

      double func_212648_d();

      double func_212647_e();

      double func_212649_f();

      long func_212657_g();

      double func_212650_h();

      EnumBorderStatus func_212655_i();

      void func_212652_j();

      void func_212653_k();

      WorldBorder.IBorderInfo func_212651_l();
   }
}
