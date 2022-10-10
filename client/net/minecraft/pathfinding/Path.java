package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class Path {
   private final PathPoint[] field_75884_a;
   private PathPoint[] field_186312_b = new PathPoint[0];
   private PathPoint[] field_186313_c = new PathPoint[0];
   private PathPoint field_186314_d;
   private int field_75882_b;
   private int field_75883_c;

   public Path(PathPoint[] var1) {
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

   @Nullable
   public PathPoint func_75870_c() {
      return this.field_75883_c > 0 ? this.field_75884_a[this.field_75883_c - 1] : null;
   }

   public PathPoint func_75877_a(int var1) {
      return this.field_75884_a[var1];
   }

   public void func_186309_a(int var1, PathPoint var2) {
      this.field_75884_a[var1] = var2;
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

   public Vec3d func_75881_a(Entity var1, int var2) {
      double var3 = (double)this.field_75884_a[var2].field_75839_a + (double)((int)(var1.field_70130_N + 1.0F)) * 0.5D;
      double var5 = (double)this.field_75884_a[var2].field_75837_b;
      double var7 = (double)this.field_75884_a[var2].field_75838_c + (double)((int)(var1.field_70130_N + 1.0F)) * 0.5D;
      return new Vec3d(var3, var5, var7);
   }

   public Vec3d func_75878_a(Entity var1) {
      return this.func_75881_a(var1, this.field_75882_b);
   }

   public Vec3d func_186310_f() {
      PathPoint var1 = this.field_75884_a[this.field_75882_b];
      return new Vec3d((double)var1.field_75839_a, (double)var1.field_75837_b, (double)var1.field_75838_c);
   }

   public boolean func_75876_a(Path var1) {
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

   public PathPoint[] func_189966_g() {
      return this.field_186312_b;
   }

   public PathPoint[] func_189965_h() {
      return this.field_186313_c;
   }

   @Nullable
   public PathPoint func_189964_i() {
      return this.field_186314_d;
   }

   public static Path func_186311_b(PacketBuffer var0) {
      int var1 = var0.readInt();
      PathPoint var2 = PathPoint.func_186282_b(var0);
      PathPoint[] var3 = new PathPoint[var0.readInt()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = PathPoint.func_186282_b(var0);
      }

      PathPoint[] var7 = new PathPoint[var0.readInt()];

      for(int var5 = 0; var5 < var7.length; ++var5) {
         var7[var5] = PathPoint.func_186282_b(var0);
      }

      PathPoint[] var8 = new PathPoint[var0.readInt()];

      for(int var6 = 0; var6 < var8.length; ++var6) {
         var8[var6] = PathPoint.func_186282_b(var0);
      }

      Path var9 = new Path(var3);
      var9.field_186312_b = var7;
      var9.field_186313_c = var8;
      var9.field_186314_d = var2;
      var9.field_75882_b = var1;
      return var9;
   }
}
