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

   public List<DistancePerDirection> closestBorder(final double x, final double z) {
      DistancePerDirection[] directions = new DistancePerDirection[]{new DistancePerDirection(Direction.NORTH, z - this.minZ), new DistancePerDirection(Direction.SOUTH, this.maxZ - z), new DistancePerDirection(Direction.WEST, x - this.minX), new DistancePerDirection(Direction.EAST, this.maxX - x)};
      return Arrays.stream(directions).sorted(Comparator.comparingDouble((d) -> d.distance)).toList();
   }

   public void reset() {
      this.alpha = 0.0;
   }

   public static record DistancePerDirection(Direction direction, double distance) {
      public DistancePerDirection {
         super();
      }
   }
}
