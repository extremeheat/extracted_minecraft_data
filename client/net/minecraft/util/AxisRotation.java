package net.minecraft.util;

public enum AxisRotation {
   NONE {
      public int func_197517_a(int var1, int var2, int var3, EnumFacing.Axis var4) {
         return var4.func_196052_a(var1, var2, var3);
      }

      public EnumFacing.Axis func_197513_a(EnumFacing.Axis var1) {
         return var1;
      }

      public AxisRotation func_197514_a() {
         return this;
      }
   },
   FORWARD {
      public int func_197517_a(int var1, int var2, int var3, EnumFacing.Axis var4) {
         return var4.func_196052_a(var3, var1, var2);
      }

      public EnumFacing.Axis func_197513_a(EnumFacing.Axis var1) {
         return field_197521_d[Math.floorMod(var1.ordinal() + 1, 3)];
      }

      public AxisRotation func_197514_a() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int func_197517_a(int var1, int var2, int var3, EnumFacing.Axis var4) {
         return var4.func_196052_a(var2, var3, var1);
      }

      public EnumFacing.Axis func_197513_a(EnumFacing.Axis var1) {
         return field_197521_d[Math.floorMod(var1.ordinal() - 1, 3)];
      }

      public AxisRotation func_197514_a() {
         return FORWARD;
      }
   };

   public static final EnumFacing.Axis[] field_197521_d = EnumFacing.Axis.values();
   public static final AxisRotation[] field_197522_e = values();

   private AxisRotation() {
   }

   public abstract int func_197517_a(int var1, int var2, int var3, EnumFacing.Axis var4);

   public abstract EnumFacing.Axis func_197513_a(EnumFacing.Axis var1);

   public abstract AxisRotation func_197514_a();

   public static AxisRotation func_197516_a(EnumFacing.Axis var0, EnumFacing.Axis var1) {
      return field_197522_e[Math.floorMod(var1.ordinal() - var0.ordinal(), 3)];
   }

   // $FF: synthetic method
   AxisRotation(Object var3) {
      this();
   }
}
