package net.minecraft.client.renderer.state;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;

public class WorldBorderRenderState {
   public double minX;
   public double maxX;
   public double minZ;
   public double maxZ;
   public int tint;
   public double alpha;

   public WorldBorderRenderState() {
      super();
   }

   public List<DistancePerDirection> closestBorder(double var1, double var3) {
      DistancePerDirection[] var5 = new DistancePerDirection[]{new DistancePerDirection(Direction.NORTH, var3 - this.minZ), new DistancePerDirection(Direction.SOUTH, this.maxZ - var3), new DistancePerDirection(Direction.WEST, var1 - this.minX), new DistancePerDirection(Direction.EAST, this.maxX - var1)};
      return Arrays.stream(var5).sorted(Comparator.comparingDouble((var0) -> var0.distance)).toList();
   }

   public void reset() {
      this.alpha = 0.0;
   }

   public static record DistancePerDirection(Direction direction, double distance) {
      final double distance;

      public DistancePerDirection(Direction var1, double var2) {
         super();
         this.direction = var1;
         this.distance = var2;
      }
   }
}
