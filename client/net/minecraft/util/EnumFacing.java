package net.minecraft.util;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

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
   private static final EnumFacing[] field_199792_n = values();
   private static final Map<String, EnumFacing> field_176761_p = (Map)Arrays.stream(field_199792_n).collect(Collectors.toMap(EnumFacing::func_176742_j, (var0) -> {
      return var0;
   }));
   private static final EnumFacing[] field_82609_l = (EnumFacing[])Arrays.stream(field_199792_n).sorted(Comparator.comparingInt((var0) -> {
      return var0.field_176748_g;
   })).toArray((var0) -> {
      return new EnumFacing[var0];
   });
   private static final EnumFacing[] field_176754_o = (EnumFacing[])Arrays.stream(field_199792_n).filter((var0) -> {
      return var0.func_176740_k().func_176722_c();
   }).sorted(Comparator.comparingInt((var0) -> {
      return var0.field_176760_i;
   })).toArray((var0) -> {
      return new EnumFacing[var0];
   });

   private EnumFacing(int var3, int var4, int var5, String var6, EnumFacing.AxisDirection var7, EnumFacing.Axis var8, Vec3i var9) {
      this.field_176748_g = var3;
      this.field_176760_i = var5;
      this.field_176759_h = var4;
      this.field_176757_j = var6;
      this.field_176758_k = var8;
      this.field_176755_l = var7;
      this.field_176756_m = var9;
   }

   public static EnumFacing[] func_196054_a(Entity var0) {
      float var1 = var0.func_195050_f(1.0F) * 0.017453292F;
      float var2 = -var0.func_195046_g(1.0F) * 0.017453292F;
      float var3 = MathHelper.func_76126_a(var1);
      float var4 = MathHelper.func_76134_b(var1);
      float var5 = MathHelper.func_76126_a(var2);
      float var6 = MathHelper.func_76134_b(var2);
      boolean var7 = var5 > 0.0F;
      boolean var8 = var3 < 0.0F;
      boolean var9 = var6 > 0.0F;
      float var10 = var7 ? var5 : -var5;
      float var11 = var8 ? -var3 : var3;
      float var12 = var9 ? var6 : -var6;
      float var13 = var10 * var4;
      float var14 = var12 * var4;
      EnumFacing var15 = var7 ? EAST : WEST;
      EnumFacing var16 = var8 ? UP : DOWN;
      EnumFacing var17 = var9 ? SOUTH : NORTH;
      if (var10 > var12) {
         if (var11 > var13) {
            return func_196053_a(var16, var15, var17);
         } else {
            return var14 > var11 ? func_196053_a(var15, var17, var16) : func_196053_a(var15, var16, var17);
         }
      } else if (var11 > var14) {
         return func_196053_a(var16, var17, var15);
      } else {
         return var13 > var11 ? func_196053_a(var17, var15, var16) : func_196053_a(var17, var16, var15);
      }
   }

   private static EnumFacing[] func_196053_a(EnumFacing var0, EnumFacing var1, EnumFacing var2) {
      return new EnumFacing[]{var0, var1, var2, var2.func_176734_d(), var1.func_176734_d(), var0.func_176734_d()};
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

   @Nullable
   public static EnumFacing func_176739_a(@Nullable String var0) {
      return var0 == null ? null : (EnumFacing)field_176761_p.get(var0.toLowerCase(Locale.ROOT));
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

   public static EnumFacing func_211699_a(EnumFacing.Axis var0, EnumFacing.AxisDirection var1) {
      switch(var0) {
      case X:
         return var1 == EnumFacing.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return var1 == EnumFacing.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return var1 == EnumFacing.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float func_185119_l() {
      return (float)((this.field_176760_i & 3) * 90);
   }

   public static EnumFacing func_176741_a(Random var0) {
      return values()[var0.nextInt(values().length)];
   }

   public static EnumFacing func_210769_a(double var0, double var2, double var4) {
      return func_176737_a((float)var0, (float)var2, (float)var4);
   }

   public static EnumFacing func_176737_a(float var0, float var1, float var2) {
      EnumFacing var3 = NORTH;
      float var4 = 1.4E-45F;
      EnumFacing[] var5 = field_199792_n;
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

   public static enum Plane implements Iterable<EnumFacing>, Predicate<EnumFacing> {
      HORIZONTAL(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}, new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z}),
      VERTICAL(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}, new EnumFacing.Axis[]{EnumFacing.Axis.Y});

      private final EnumFacing[] field_209387_c;
      private final EnumFacing.Axis[] field_209388_d;

      private Plane(EnumFacing[] var3, EnumFacing.Axis[] var4) {
         this.field_209387_c = var3;
         this.field_209388_d = var4;
      }

      public EnumFacing func_179518_a(Random var1) {
         return this.field_209387_c[var1.nextInt(this.field_209387_c.length)];
      }

      public boolean test(@Nullable EnumFacing var1) {
         return var1 != null && var1.func_176740_k().func_176716_d() == this;
      }

      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.field_209387_c);
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((EnumFacing)var1);
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
      X("x") {
         public int func_196052_a(int var1, int var2, int var3) {
            return var1;
         }

         public double func_196051_a(double var1, double var3, double var5) {
            return var1;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((EnumFacing)var1);
         }
      },
      Y("y") {
         public int func_196052_a(int var1, int var2, int var3) {
            return var2;
         }

         public double func_196051_a(double var1, double var3, double var5) {
            return var3;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((EnumFacing)var1);
         }
      },
      Z("z") {
         public int func_196052_a(int var1, int var2, int var3) {
            return var3;
         }

         public double func_196051_a(double var1, double var3, double var5) {
            return var5;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((EnumFacing)var1);
         }
      };

      private static final Map<String, EnumFacing.Axis> field_176725_d = (Map)Arrays.stream(values()).collect(Collectors.toMap(EnumFacing.Axis::func_176719_a, (var0) -> {
         return var0;
      }));
      private final String field_176726_e;

      private Axis(String var3) {
         this.field_176726_e = var3;
      }

      @Nullable
      public static EnumFacing.Axis func_176717_a(String var0) {
         return (EnumFacing.Axis)field_176725_d.get(var0.toLowerCase(Locale.ROOT));
      }

      public String func_176719_a() {
         return this.field_176726_e;
      }

      public boolean func_200128_b() {
         return this == Y;
      }

      public boolean func_176722_c() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.field_176726_e;
      }

      public boolean test(@Nullable EnumFacing var1) {
         return var1 != null && var1.func_176740_k() == this;
      }

      public EnumFacing.Plane func_176716_d() {
         switch(this) {
         case X:
         case Z:
            return EnumFacing.Plane.HORIZONTAL;
         case Y:
            return EnumFacing.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String func_176610_l() {
         return this.field_176726_e;
      }

      public abstract int func_196052_a(int var1, int var2, int var3);

      public abstract double func_196051_a(double var1, double var3, double var5);

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((EnumFacing)var1);
      }

      // $FF: synthetic method
      Axis(String var3, Object var4) {
         this(var3);
      }
   }
}
