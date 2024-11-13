package net.minecraft.world.level.levelgen;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Column {
   public Column() {
      super();
   }

   public static Range around(int var0, int var1) {
      return new Range(var0 - 1, var1 + 1);
   }

   public static Range inside(int var0, int var1) {
      return new Range(var0, var1);
   }

   public static Column below(int var0) {
      return new Ray(var0, false);
   }

   public static Column fromHighest(int var0) {
      return new Ray(var0 + 1, false);
   }

   public static Column above(int var0) {
      return new Ray(var0, true);
   }

   public static Column fromLowest(int var0) {
      return new Ray(var0 - 1, true);
   }

   public static Column line() {
      return Column.Line.INSTANCE;
   }

   public static Column create(OptionalInt var0, OptionalInt var1) {
      if (var0.isPresent() && var1.isPresent()) {
         return inside(var0.getAsInt(), var1.getAsInt());
      } else if (var0.isPresent()) {
         return above(var0.getAsInt());
      } else {
         return var1.isPresent() ? below(var1.getAsInt()) : line();
      }
   }

   public abstract OptionalInt getCeiling();

   public abstract OptionalInt getFloor();

   public abstract OptionalInt getHeight();

   public Column withFloor(OptionalInt var1) {
      return create(var1, this.getCeiling());
   }

   public Column withCeiling(OptionalInt var1) {
      return create(this.getFloor(), var1);
   }

   public static Optional<Column> scan(LevelSimulatedReader var0, BlockPos var1, int var2, Predicate<BlockState> var3, Predicate<BlockState> var4) {
      BlockPos.MutableBlockPos var5 = var1.mutable();
      if (!var0.isStateAtPosition(var1, var3)) {
         return Optional.empty();
      } else {
         int var6 = var1.getY();
         OptionalInt var7 = scanDirection(var0, var2, var3, var4, var5, var6, Direction.UP);
         OptionalInt var8 = scanDirection(var0, var2, var3, var4, var5, var6, Direction.DOWN);
         return Optional.of(create(var8, var7));
      }
   }

   private static OptionalInt scanDirection(LevelSimulatedReader var0, int var1, Predicate<BlockState> var2, Predicate<BlockState> var3, BlockPos.MutableBlockPos var4, int var5, Direction var6) {
      var4.setY(var5);

      for(int var7 = 1; var7 < var1 && var0.isStateAtPosition(var4, var2); ++var7) {
         var4.move(var6);
      }

      return var0.isStateAtPosition(var4, var3) ? OptionalInt.of(var4.getY()) : OptionalInt.empty();
   }

   public static final class Range extends Column {
      private final int floor;
      private final int ceiling;

      protected Range(int var1, int var2) {
         super();
         this.floor = var1;
         this.ceiling = var2;
         if (this.height() < 0) {
            throw new IllegalArgumentException("Column of negative height: " + String.valueOf(this));
         }
      }

      public OptionalInt getCeiling() {
         return OptionalInt.of(this.ceiling);
      }

      public OptionalInt getFloor() {
         return OptionalInt.of(this.floor);
      }

      public OptionalInt getHeight() {
         return OptionalInt.of(this.height());
      }

      public int ceiling() {
         return this.ceiling;
      }

      public int floor() {
         return this.floor;
      }

      public int height() {
         return this.ceiling - this.floor - 1;
      }

      public String toString() {
         return "C(" + this.ceiling + "-" + this.floor + ")";
      }
   }

   public static final class Line extends Column {
      static final Line INSTANCE = new Line();

      private Line() {
         super();
      }

      public OptionalInt getCeiling() {
         return OptionalInt.empty();
      }

      public OptionalInt getFloor() {
         return OptionalInt.empty();
      }

      public OptionalInt getHeight() {
         return OptionalInt.empty();
      }

      public String toString() {
         return "C(-)";
      }
   }

   public static final class Ray extends Column {
      private final int edge;
      private final boolean pointingUp;

      public Ray(int var1, boolean var2) {
         super();
         this.edge = var1;
         this.pointingUp = var2;
      }

      public OptionalInt getCeiling() {
         return this.pointingUp ? OptionalInt.empty() : OptionalInt.of(this.edge);
      }

      public OptionalInt getFloor() {
         return this.pointingUp ? OptionalInt.of(this.edge) : OptionalInt.empty();
      }

      public OptionalInt getHeight() {
         return OptionalInt.empty();
      }

      public String toString() {
         return this.pointingUp ? "C(" + this.edge + "-)" : "C(-" + this.edge + ")";
      }
   }
}
