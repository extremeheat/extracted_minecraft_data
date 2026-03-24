package net.minecraft.core;

public enum AxisCycle {
   NONE {
      public int cycle(final int x, final int y, final int z, final Direction.Axis axis) {
         return axis.choose(x, y, z);
      }

      public double cycle(final double x, final double y, final double z, final Direction.Axis axis) {
         return axis.choose(x, y, z);
      }

      public Direction.Axis cycle(final Direction.Axis axis) {
         return axis;
      }

      public AxisCycle inverse() {
         return this;
      }
   },
   FORWARD {
      public int cycle(final int x, final int y, final int z, final Direction.Axis axis) {
         return axis.choose(z, x, y);
      }

      public double cycle(final double x, final double y, final double z, final Direction.Axis axis) {
         return axis.choose(z, x, y);
      }

      public Direction.Axis cycle(final Direction.Axis axis) {
         return AXIS_VALUES[Math.floorMod(axis.ordinal() + 1, 3)];
      }

      public AxisCycle inverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int cycle(final int x, final int y, final int z, final Direction.Axis axis) {
         return axis.choose(y, z, x);
      }

      public double cycle(final double x, final double y, final double z, final Direction.Axis axis) {
         return axis.choose(y, z, x);
      }

      public Direction.Axis cycle(final Direction.Axis axis) {
         return AXIS_VALUES[Math.floorMod(axis.ordinal() - 1, 3)];
      }

      public AxisCycle inverse() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   public static final AxisCycle[] VALUES = values();

   private AxisCycle() {
   }

   public abstract int cycle(final int x, final int y, final int z, final Direction.Axis axis);

   public abstract double cycle(final double x, final double y, final double z, final Direction.Axis axis);

   public abstract Direction.Axis cycle(final Direction.Axis axis);

   public abstract AxisCycle inverse();

   public static AxisCycle between(final Direction.Axis from, final Direction.Axis to) {
      return VALUES[Math.floorMod(to.ordinal() - from.ordinal(), 3)];
   }

   // $FF: synthetic method
   private static AxisCycle[] $values() {
      return new AxisCycle[]{NONE, FORWARD, BACKWARD};
   }
}
