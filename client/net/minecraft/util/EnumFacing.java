package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int field_176748_g;
   private final int field_176759_h;
   private final int field_176760_i;
   private final String field_176757_j;
   private final EnumFacing.Axis field_176758_k;
   private final EnumFacing.AxisDirection field_176755_l;
   private final Vec3i field_176756_m;
   private static final EnumFacing[] field_82609_l = new EnumFacing[6];
   private static final EnumFacing[] field_176754_o = new EnumFacing[4];
   private static final Map<String, EnumFacing> field_176761_p = Maps.newHashMap();

   private EnumFacing(int var3, int var4, int var5, String var6, EnumFacing.AxisDirection var7, EnumFacing.Axis var8, Vec3i var9) {
      this.field_176748_g = var3;
      this.field_176760_i = var5;
      this.field_176759_h = var4;
      this.field_176757_j = var6;
      this.field_176758_k = var8;
      this.field_176755_l = var7;
      this.field_176756_m = var9;
   }

   public int func_176745_a() {
      return this.field_176748_g;
   }

   public int func_176736_b() {
      return this.field_176760_i;
   }

   public EnumFacing.AxisDirection func_176743_c() {
      return this.field_176755_l;
   }

   public EnumFacing func_176734_d() {
      return func_82600_a(this.field_176759_h);
   }

   public EnumFacing func_176732_a(EnumFacing.Axis var1) {
      switch(var1) {
      case X:
         if (this != WEST && this != EAST) {
            return this.func_176744_n();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.func_176746_e();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.func_176738_p();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + var1);
      }
   }

   public EnumFacing func_176746_e() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   private EnumFacing func_176744_n() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   private EnumFacing func_176738_p() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   public EnumFacing func_176735_f() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int func_82601_c() {
      return this.field_176758_k == EnumFacing.Axis.X ? this.field_176755_l.func_179524_a() : 0;
   }

   public int func_96559_d() {
      return this.field_176758_k == EnumFacing.Axis.Y ? this.field_176755_l.func_179524_a() : 0;
   }

   public int func_82599_e() {
      return this.field_176758_k == EnumFacing.Axis.Z ? this.field_176755_l.func_179524_a() : 0;
   }

   public String func_176742_j() {
      return this.field_176757_j;
   }

   public EnumFacing.Axis func_176740_k() {
      return this.field_176758_k;
   }

   public static EnumFacing func_176739_a(String var0) {
      return var0 == null ? null : (EnumFacing)field_176761_p.get(var0.toLowerCase());
   }

   public static EnumFacing func_82600_a(int var0) {
      return field_82609_l[MathHelper.func_76130_a(var0 % field_82609_l.length)];
   }

   public static EnumFacing func_176731_b(int var0) {
      return field_176754_o[MathHelper.func_76130_a(var0 % field_176754_o.length)];
   }

   public static EnumFacing func_176733_a(double var0) {
      return func_176731_b(MathHelper.func_76128_c(var0 / 90.0D + 0.5D) & 3);
   }

   public static EnumFacing func_176741_a(Random var0) {
      return values()[var0.nextInt(values().length)];
   }

   public static EnumFacing func_176737_a(float var0, float var1, float var2) {
      EnumFacing var3 = NORTH;
      float var4 = 1.4E-45F;
      EnumFacing[] var5 = values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         float var9 = var0 * (float)var8.field_176756_m.func_177958_n() + var1 * (float)var8.field_176756_m.func_177956_o() + var2 * (float)var8.field_176756_m.func_177952_p();
         if (var9 > var4) {
            var4 = var9;
            var3 = var8;
         }
      }

      return var3;
   }

   public String toString() {
      return this.field_176757_j;
   }

   public String func_176610_l() {
      return this.field_176757_j;
   }

   public static EnumFacing func_181076_a(EnumFacing.AxisDirection var0, EnumFacing.Axis var1) {
      EnumFacing[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         if (var5.func_176743_c() == var0 && var5.func_176740_k() == var1) {
            return var5;
         }
      }

      throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
   }

   public Vec3i func_176730_m() {
      return this.field_176756_m;
   }

   static {
      EnumFacing[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumFacing var3 = var0[var2];
         field_82609_l[var3.field_176748_g] = var3;
         if (var3.func_176740_k().func_176722_c()) {
            field_176754_o[var3.field_176760_i] = var3;
         }

         field_176761_p.put(var3.func_176742_j().toLowerCase(), var3);
      }

   }

   public static enum Plane implements Predicate<EnumFacing>, Iterable<EnumFacing> {
      HORIZONTAL,
      VERTICAL;

      private Plane() {
      }

      public EnumFacing[] func_179516_a() {
         switch(this) {
         case HORIZONTAL:
            return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case VERTICAL:
            return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public EnumFacing func_179518_a(Random var1) {
         EnumFacing[] var2 = this.func_179516_a();
         return var2[var1.nextInt(var2.length)];
      }

      public boolean apply(EnumFacing var1) {
         return var1 != null && var1.func_176740_k().func_176716_d() == this;
      }

      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.func_179516_a());
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EnumFacing)var1);
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int field_179528_c;
      private final String field_179525_d;

      private AxisDirection(int var3, String var4) {
         this.field_179528_c = var3;
         this.field_179525_d = var4;
      }

      public int func_179524_a() {
         return this.field_179528_c;
      }

      public String toString() {
         return this.field_179525_d;
      }
   }

   public static enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("x", EnumFacing.Plane.HORIZONTAL),
      Y("y", EnumFacing.Plane.VERTICAL),
      Z("z", EnumFacing.Plane.HORIZONTAL);

      private static final Map<String, EnumFacing.Axis> field_176725_d = Maps.newHashMap();
      private final String field_176726_e;
      private final EnumFacing.Plane field_176723_f;

      private Axis(String var3, EnumFacing.Plane var4) {
         this.field_176726_e = var3;
         this.field_176723_f = var4;
      }

      public static EnumFacing.Axis func_176717_a(String var0) {
         return var0 == null ? null : (EnumFacing.Axis)field_176725_d.get(var0.toLowerCase());
      }

      public String func_176719_a() {
         return this.field_176726_e;
      }

      public boolean func_176720_b() {
         return this.field_176723_f == EnumFacing.Plane.VERTICAL;
      }

      public boolean func_176722_c() {
         return this.field_176723_f == EnumFacing.Plane.HORIZONTAL;
      }

      public String toString() {
         return this.field_176726_e;
      }

      public boolean apply(EnumFacing var1) {
         return var1 != null && var1.func_176740_k() == this;
      }

      public EnumFacing.Plane func_176716_d() {
         return this.field_176723_f;
      }

      public String func_176610_l() {
         return this.field_176726_e;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EnumFacing)var1);
      }

      static {
         EnumFacing.Axis[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            EnumFacing.Axis var3 = var0[var2];
            field_176725_d.put(var3.func_176719_a().toLowerCase(), var3);
         }

      }
   }
}
