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

   default OptionalInt getHitSlot(final BlockHitResult hitResult, final Direction blockFacing) {
      return (OptionalInt)getRelativeHitCoordinatesForBlockFace(hitResult, blockFacing).map((hitCoords) -> {
         int row = getSection(1.0F - hitCoords.y, this.getRows());
         int column = getSection(hitCoords.x, this.getColumns());
         return OptionalInt.of(column + row * this.getColumns());
      }).orElseGet(OptionalInt::empty);
   }

   private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(final BlockHitResult hitResult, final Direction blockFacing) {
      Direction hitDirection = hitResult.getDirection();
      if (blockFacing != hitDirection) {
         return Optional.empty();
      } else {
         BlockPos hitBlockPos = hitResult.getBlockPos().relative(hitDirection);
         Vec3 relativeHit = hitResult.getLocation().subtract((double)hitBlockPos.getX(), (double)hitBlockPos.getY(), (double)hitBlockPos.getZ());
         double relativeX = relativeHit.x();
         double relativeY = relativeHit.y();
         double relativeZ = relativeHit.z();
         Optional var10000;
         switch (hitDirection) {
            case NORTH:
               var10000 = Optional.of(new Vec2((float)(1.0 - relativeX), (float)relativeY));
               break;
            case SOUTH:
               var10000 = Optional.of(new Vec2((float)relativeX, (float)relativeY));
               break;
            case WEST:
               var10000 = Optional.of(new Vec2((float)relativeZ, (float)relativeY));
               break;
            case EAST:
               var10000 = Optional.of(new Vec2((float)(1.0 - relativeZ), (float)relativeY));
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

   private static int getSection(final float relativeCoordinate, final int maxSections) {
      float targetedPixel = relativeCoordinate * 16.0F;
      float sectionSize = 16.0F / (float)maxSections;
      return Mth.clamp(Mth.floor(targetedPixel / sectionSize), 0, maxSections - 1);
   }
}
