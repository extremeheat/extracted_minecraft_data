package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DropItemDispenseBehavior implements DispenseBehavior {
   protected static final int DEFAULT_ACCURACY = 6;
   public static final DropItemDispenseBehavior INSTANCE = new DropItemDispenseBehavior();
   public static final MapCodec<DropItemDispenseBehavior> MAP_CODEC;

   private DropItemDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ItemStack itemStack = dispensed.split(1);
      spawnItem(source, itemStack);
      return DispenseBehavior.DispenseResult.success(dispensed);
   }

   public static Direction spawnItem(final BlockSource source, final ItemStack itemStack) {
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      Position position = DispenserBlock.getDispensePosition(source);
      spawnItem(source.level(), itemStack, 6, direction, position);
      return direction;
   }

   public static void spawnItem(final Level level, final ItemStack itemStack, final int accuracy, final Direction direction, final Position position) {
      double spawnX = position.x();
      double spawnY = position.y();
      double spawnZ = position.z();
      if (direction.getAxis() == Direction.Axis.Y) {
         spawnY -= 0.125;
      } else {
         spawnY -= 0.15625;
      }

      ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, itemStack);
      RandomSource random = level.getRandom();
      double pow = random.nextDouble() * 0.1 + 0.2;
      itemEntity.setDeltaMovement(random.triangle((double)direction.getStepX() * pow, 0.0172275 * (double)accuracy), random.triangle(0.2, 0.0172275 * (double)accuracy), random.triangle((double)direction.getStepZ() * pow, 0.0172275 * (double)accuracy));
      level.addFreshEntity(itemEntity);
   }

   public MapCodec<DropItemDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
