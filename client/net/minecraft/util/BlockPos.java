package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i {
   public static final BlockPos field_177992_a = new BlockPos(0, 0, 0);
   private static final int field_177990_b = 1 + MathHelper.func_151239_c(MathHelper.func_151236_b(30000000));
   private static final int field_177991_c;
   private static final int field_177989_d;
   private static final int field_177987_f;
   private static final int field_177988_g;
   private static final long field_177994_h;
   private static final long field_177995_i;
   private static final long field_177993_j;

   public BlockPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public BlockPos(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   public BlockPos(Entity var1) {
      this(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
   }

   public BlockPos(Vec3 var1) {
      this(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public BlockPos(Vec3i var1) {
      this(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p());
   }

   public BlockPos func_177963_a(double var1, double var3, double var5) {
      return var1 == 0.0D && var3 == 0.0D && var5 == 0.0D ? this : new BlockPos((double)this.func_177958_n() + var1, (double)this.func_177956_o() + var3, (double)this.func_177952_p() + var5);
   }

   public BlockPos func_177982_a(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0 ? this : new BlockPos(this.func_177958_n() + var1, this.func_177956_o() + var2, this.func_177952_p() + var3);
   }

   public BlockPos func_177971_a(Vec3i var1) {
      return var1.func_177958_n() == 0 && var1.func_177956_o() == 0 && var1.func_177952_p() == 0 ? this : new BlockPos(this.func_177958_n() + var1.func_177958_n(), this.func_177956_o() + var1.func_177956_o(), this.func_177952_p() + var1.func_177952_p());
   }

   public BlockPos func_177973_b(Vec3i var1) {
      return var1.func_177958_n() == 0 && var1.func_177956_o() == 0 && var1.func_177952_p() == 0 ? this : new BlockPos(this.func_177958_n() - var1.func_177958_n(), this.func_177956_o() - var1.func_177956_o(), this.func_177952_p() - var1.func_177952_p());
   }

   public BlockPos func_177984_a() {
      return this.func_177981_b(1);
   }

   public BlockPos func_177981_b(int var1) {
      return this.func_177967_a(EnumFacing.UP, var1);
   }

   public BlockPos func_177977_b() {
      return this.func_177979_c(1);
   }

   public BlockPos func_177979_c(int var1) {
      return this.func_177967_a(EnumFacing.DOWN, var1);
   }

   public BlockPos func_177978_c() {
      return this.func_177964_d(1);
   }

   public BlockPos func_177964_d(int var1) {
      return this.func_177967_a(EnumFacing.NORTH, var1);
   }

   public BlockPos func_177968_d() {
      return this.func_177970_e(1);
   }

   public BlockPos func_177970_e(int var1) {
      return this.func_177967_a(EnumFacing.SOUTH, var1);
   }

   public BlockPos func_177976_e() {
      return this.func_177985_f(1);
   }

   public BlockPos func_177985_f(int var1) {
      return this.func_177967_a(EnumFacing.WEST, var1);
   }

   public BlockPos func_177974_f() {
      return this.func_177965_g(1);
   }

   public BlockPos func_177965_g(int var1) {
      return this.func_177967_a(EnumFacing.EAST, var1);
   }

   public BlockPos func_177972_a(EnumFacing var1) {
      return this.func_177967_a(var1, 1);
   }

   public BlockPos func_177967_a(EnumFacing var1, int var2) {
      return var2 == 0 ? this : new BlockPos(this.func_177958_n() + var1.func_82601_c() * var2, this.func_177956_o() + var1.func_96559_d() * var2, this.func_177952_p() + var1.func_82599_e() * var2);
   }

   public BlockPos func_177955_d(Vec3i var1) {
      return new BlockPos(this.func_177956_o() * var1.func_177952_p() - this.func_177952_p() * var1.func_177956_o(), this.func_177952_p() * var1.func_177958_n() - this.func_177958_n() * var1.func_177952_p(), this.func_177958_n() * var1.func_177956_o() - this.func_177956_o() * var1.func_177958_n());
   }

   public long func_177986_g() {
      return ((long)this.func_177958_n() & field_177994_h) << field_177988_g | ((long)this.func_177956_o() & field_177995_i) << field_177987_f | ((long)this.func_177952_p() & field_177993_j) << 0;
   }

   public static BlockPos func_177969_a(long var0) {
      int var2 = (int)(var0 << 64 - field_177988_g - field_177990_b >> 64 - field_177990_b);
      int var3 = (int)(var0 << 64 - field_177987_f - field_177989_d >> 64 - field_177989_d);
      int var4 = (int)(var0 << 64 - field_177991_c >> 64 - field_177991_c);
      return new BlockPos(var2, var3, var4);
   }

   public static Iterable<BlockPos> func_177980_a(BlockPos var0, BlockPos var1) {
      final BlockPos var2 = new BlockPos(Math.min(var0.func_177958_n(), var1.func_177958_n()), Math.min(var0.func_177956_o(), var1.func_177956_o()), Math.min(var0.func_177952_p(), var1.func_177952_p()));
      final BlockPos var3 = new BlockPos(Math.max(var0.func_177958_n(), var1.func_177958_n()), Math.max(var0.func_177956_o(), var1.func_177956_o()), Math.max(var0.func_177952_p(), var1.func_177952_p()));
      return new Iterable<BlockPos>() {
         public Iterator<BlockPos> iterator() {
            return new AbstractIterator<BlockPos>() {
               private BlockPos field_179309_b = null;

               protected BlockPos computeNext() {
                  if (this.field_179309_b == null) {
                     this.field_179309_b = var2;
                     return this.field_179309_b;
                  } else if (this.field_179309_b.equals(var3)) {
                     return (BlockPos)this.endOfData();
                  } else {
                     int var1 = this.field_179309_b.func_177958_n();
                     int var2x = this.field_179309_b.func_177956_o();
                     int var3x = this.field_179309_b.func_177952_p();
                     if (var1 < var3.func_177958_n()) {
                        ++var1;
                     } else if (var2x < var3.func_177956_o()) {
                        var1 = var2.func_177958_n();
                        ++var2x;
                     } else if (var3x < var3.func_177952_p()) {
                        var1 = var2.func_177958_n();
                        var2x = var2.func_177956_o();
                        ++var3x;
                     }

                     this.field_179309_b = new BlockPos(var1, var2x, var3x);
                     return this.field_179309_b;
                  }
               }

               // $FF: synthetic method
               protected Object computeNext() {
                  return this.computeNext();
               }
            };
         }
      };
   }

   public static Iterable<BlockPos.MutableBlockPos> func_177975_b(BlockPos var0, BlockPos var1) {
      final BlockPos var2 = new BlockPos(Math.min(var0.func_177958_n(), var1.func_177958_n()), Math.min(var0.func_177956_o(), var1.func_177956_o()), Math.min(var0.func_177952_p(), var1.func_177952_p()));
      final BlockPos var3 = new BlockPos(Math.max(var0.func_177958_n(), var1.func_177958_n()), Math.max(var0.func_177956_o(), var1.func_177956_o()), Math.max(var0.func_177952_p(), var1.func_177952_p()));
      return new Iterable<BlockPos.MutableBlockPos>() {
         public Iterator<BlockPos.MutableBlockPos> iterator() {
            return new AbstractIterator<BlockPos.MutableBlockPos>() {
               private BlockPos.MutableBlockPos field_179314_b = null;

               protected BlockPos.MutableBlockPos computeNext() {
                  if (this.field_179314_b == null) {
                     this.field_179314_b = new BlockPos.MutableBlockPos(var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
                     return this.field_179314_b;
                  } else if (this.field_179314_b.equals(var3)) {
                     return (BlockPos.MutableBlockPos)this.endOfData();
                  } else {
                     int var1 = this.field_179314_b.func_177958_n();
                     int var2x = this.field_179314_b.func_177956_o();
                     int var3x = this.field_179314_b.func_177952_p();
                     if (var1 < var3.func_177958_n()) {
                        ++var1;
                     } else if (var2x < var3.func_177956_o()) {
                        var1 = var2.func_177958_n();
                        ++var2x;
                     } else if (var3x < var3.func_177952_p()) {
                        var1 = var2.func_177958_n();
                        var2x = var2.func_177956_o();
                        ++var3x;
                     }

                     this.field_179314_b.field_177997_b = var1;
                     this.field_179314_b.field_177998_c = var2x;
                     this.field_179314_b.field_177996_d = var3x;
                     return this.field_179314_b;
                  }
               }

               // $FF: synthetic method
               protected Object computeNext() {
                  return this.computeNext();
               }
            };
         }
      };
   }

   // $FF: synthetic method
   public Vec3i func_177955_d(Vec3i var1) {
      return this.func_177955_d(var1);
   }

   static {
      field_177991_c = field_177990_b;
      field_177989_d = 64 - field_177990_b - field_177991_c;
      field_177987_f = 0 + field_177991_c;
      field_177988_g = field_177987_f + field_177989_d;
      field_177994_h = (1L << field_177990_b) - 1L;
      field_177995_i = (1L << field_177989_d) - 1L;
      field_177993_j = (1L << field_177991_c) - 1L;
   }

   public static final class MutableBlockPos extends BlockPos {
      private int field_177997_b;
      private int field_177998_c;
      private int field_177996_d;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(int var1, int var2, int var3) {
         super(0, 0, 0);
         this.field_177997_b = var1;
         this.field_177998_c = var2;
         this.field_177996_d = var3;
      }

      public int func_177958_n() {
         return this.field_177997_b;
      }

      public int func_177956_o() {
         return this.field_177998_c;
      }

      public int func_177952_p() {
         return this.field_177996_d;
      }

      public BlockPos.MutableBlockPos func_181079_c(int var1, int var2, int var3) {
         this.field_177997_b = var1;
         this.field_177998_c = var2;
         this.field_177996_d = var3;
         return this;
      }

      // $FF: synthetic method
      public Vec3i func_177955_d(Vec3i var1) {
         return super.func_177955_d(var1);
      }
   }
}
