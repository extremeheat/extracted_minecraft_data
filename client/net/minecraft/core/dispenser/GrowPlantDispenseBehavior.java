package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class GrowPlantDispenseBehavior implements DispenseBehavior {
   public static final GrowPlantDispenseBehavior INSTANCE = new GrowPlantDispenseBehavior();
   public static final MapCodec<GrowPlantDispenseBehavior> MAP_CODEC;

   private GrowPlantDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      BlockPos target = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
      if (!BoneMealItem.growCrop(dispensed, level, target) && !BoneMealItem.growWaterPlant(dispensed, level, target, (Direction)null)) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         level.levelEvent(1505, target, 15);
         return DispenseBehavior.DispenseResult.success(dispensed);
      }
   }

   public MapCodec<GrowPlantDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
