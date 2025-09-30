package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface SelectableSlotContainer {
   int getRows();

   int getColumns();

   default OptionalInt getHitSlot(BlockHitResult var1, Direction var2) {
      return (OptionalInt)getRelativeHitCoordinatesForBlockFace(var1, var2).map((var1x) -> {
         int var2 = getSection(1.0F - var1x.y, this.getRows());
         int var3 = getSection(var1x.x, this.getColumns());
         return OptionalInt.of(var3 + var2 * this.getColumns());
      }).orElseGet(OptionalInt::empty);
   }

   private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult var0, Direction var1) {
      Direction var2 = var0.getDirection();
      if (var1 != var2) {
         return Optional.empty();
      } else {
         BlockPos var3 = var0.getBlockPos().relative(var2);
         Vec3 var4 = var0.getLocation().subtract((double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
         double var5 = var4.x();
         double var7 = var4.y();
         double var9 = var4.z();
         Optional var10000;
         switch (var2) {
            case NORTH:
               var10000 = Optional.of(new Vec2((float)(1.0 - var5), (float)var7));
               break;
            case SOUTH:
               var10000 = Optional.of(new Vec2((float)var5, (float)var7));
               break;
            case WEST:
               var10000 = Optional.of(new Vec2((float)var9, (float)var7));
               break;
            case EAST:
               var10000 = Optional.of(new Vec2((float)(1.0 - var9), (float)var7));
               break;
            case DOWN:
            case UP:
               var10000 = Optional.empty();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private static int getSection(float var0, int var1) {
      float var2 = var0 * 16.0F;
      float var3 = 16.0F / (float)var1;
      return Mth.clamp(Mth.floor(var2 / var3), 0, var1 - 1);
   }
}
