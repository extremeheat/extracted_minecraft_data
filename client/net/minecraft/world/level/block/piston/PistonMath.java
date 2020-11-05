package net.minecraft.world.level.block.piston;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class PistonMath {
   public static AABB getMovementArea(AABB var0, Direction var1, double var2) {
      double var4 = var2 * (double)var1.getAxisDirection().getStep();
      double var6 = Math.min(var4, 0.0D);
      double var8 = Math.max(var4, 0.0D);
      switch(var1) {
      case WEST:
         return new AABB(var0.minX + var6, var0.minY, var0.minZ, var0.minX + var8, var0.maxY, var0.maxZ);
      case EAST:
         return new AABB(var0.maxX + var6, var0.minY, var0.minZ, var0.maxX + var8, var0.maxY, var0.maxZ);
      case DOWN:
         return new AABB(var0.minX, var0.minY + var6, var0.minZ, var0.maxX, var0.minY + var8, var0.maxZ);
      case UP:
      default:
         return new AABB(var0.minX, var0.maxY + var6, var0.minZ, var0.maxX, var0.maxY + var8, var0.maxZ);
      case NORTH:
         return new AABB(var0.minX, var0.minY, var0.minZ + var6, var0.maxX, var0.maxY, var0.minZ + var8);
      case SOUTH:
         return new AABB(var0.minX, var0.minY, var0.maxZ + var6, var0.maxX, var0.maxY, var0.maxZ + var8);
      }
   }
}
