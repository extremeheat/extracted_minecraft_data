package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class PlaceBoatDispenseBehavior implements DispenseBehavior {
   public static final PlaceBoatDispenseBehavior INSTANCE = new PlaceBoatDispenseBehavior();
   public static final MapCodec<PlaceBoatDispenseBehavior> MAP_CODEC;

   private PlaceBoatDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      EntityType<?> type = TypedEntityData.getType(dispensed);
      if (type == null) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
         ServerLevel level = source.level();
         Vec3 center = source.center();
         double justOutsideDispenser = 0.5625 + (double)type.getWidth() / 2.0;
         double spawnX = center.x() + (double)direction.getStepX() * justOutsideDispenser;
         double spawnY = center.y() + (double)((float)direction.getStepY() * 1.125F);
         double spawnZ = center.z() + (double)direction.getStepZ() * justOutsideDispenser;
         BlockPos frontPos = source.pos().relative(direction);
         double yOffset;
         if (level.getFluidState(frontPos).is(FluidTags.WATER)) {
            yOffset = 1.0;
         } else {
            if (!level.getBlockState(frontPos).isAir() || !level.getFluidState(frontPos.below()).is(FluidTags.WATER)) {
               return DispenseBehavior.DispenseResult.failure();
            }

            yOffset = 0.0;
         }

         Entity var19 = type.create(level, (EntitySpawnReason)EntitySpawnReason.DISPENSER);
         if (var19 instanceof AbstractBoat) {
            AbstractBoat boat = (AbstractBoat)var19;
            boat.setInitialPos(spawnX, spawnY + yOffset, spawnZ);
            EntityType.createDefaultStackConfig(level, dispensed, (LivingEntity)null).apply(boat);
            boat.setYRot(direction.toYRot());
            level.addFreshEntity(boat);
            dispensed.shrink(1);
            return DispenseBehavior.DispenseResult.success(dispensed);
         } else {
            return DispenseBehavior.DispenseResult.failure();
         }
      }
   }

   public MapCodec<PlaceBoatDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
