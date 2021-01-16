package net.minecraft.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;

public class ShulkerSharedHelper {
   public static AABB openBoundingBox(BlockPos var0, Direction var1) {
      return Shapes.block().bounds().expandTowards((double)(0.5F * (float)var1.getStepX()), (double)(0.5F * (float)var1.getStepY()), (double)(0.5F * (float)var1.getStepZ())).contract((double)var1.getStepX(), (double)var1.getStepY(), (double)var1.getStepZ()).move(var0.relative(var1));
   }
}
