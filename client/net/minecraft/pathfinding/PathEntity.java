package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {
   private final PathPoint[] field_75884_a;
   private int field_75882_b;
   private int field_75883_c;

   public PathEntity(PathPoint[] var1) {
      super();
      this.field_75884_a = var1;
      this.field_75883_c = var1.length;
   }

   public void func_75875_a() {
      ++this.field_75882_b;
   }

   public boolean func_75879_b() {
      return this.field_75882_b >= this.field_75883_c;
   }

   public PathPoint func_75870_c() {
      return this.field_75883_c > 0 ? this.field_75884_a[this.field_75883_c - 1] : null;
   }

   public PathPoint func_75877_a(int var1) {
      return this.field_75884_a[var1];
   }

   public int func_75874_d() {
      return this.field_75883_c;
   }

   public void func_75871_b(int var1) {
      this.field_75883_c = var1;
   }

   public int func_75873_e() {
      return this.field_75882_b;
   }

   public void func_75872_c(int var1) {
      this.field_75882_b = var1;
   }

   public Vec3 func_75881_a(Entity var1, int var2) {
      double var3 = (double)this.field_75884_a[var2].field_75839_a + (double)((int)(var1.field_70130_N + 1.0F)) * 0.5D;
      double var5 = (double)this.field_75884_a[var2].field_75837_b;
      double var7 = (double)this.field_75884_a[var2].field_75838_c + (double)((int)(var1.field_70130_N + 1.0F)) * 0.5D;
      return new Vec3(var3, var5, var7);
   }

   public Vec3 func_75878_a(Entity var1) {
      return this.func_75881_a(var1, this.field_75882_b);
   }

   public boolean func_75876_a(PathEntity var1) {
      if (var1 == null) {
         return false;
      } else if (var1.field_75884_a.length != this.field_75884_a.length) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.field_75884_a.length; ++var2) {
            if (this.field_75884_a[var2].field_75839_a != var1.field_75884_a[var2].field_75839_a || this.field_75884_a[var2].field_75837_b != var1.field_75884_a[var2].field_75837_b || this.field_75884_a[var2].field_75838_c != var1.field_75884_a[var2].field_75838_c) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_75880_b(Vec3 var1) {
      PathPoint var2 = this.func_75870_c();
      if (var2 == null) {
         return false;
      } else {
         return var2.field_75839_a == (int)var1.field_72450_a && var2.field_75838_c == (int)var1.field_72449_c;
      }
   }
}
