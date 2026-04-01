package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DefaultDispenseItemBehavior implements DispenseItemBehavior {
   private static final int DEFAULT_ACCURACY = 6;

   public DefaultDispenseItemBehavior() {
      super();
   }

   public final ItemStack dispense(final DispenseSource source, final ItemStack dispensed) {
      ItemStack result = this.execute(source, dispensed);
      this.playSound(source);
      this.playAnimation(source, source.direction());
      return result;
   }

   protected ItemStack execute(final DispenseSource source, final ItemStack dispensed) {
      Direction direction = source.direction();
      Position position = DispenserBlock.getDispensePosition(source);
      ItemStack itemStack = dispensed.split(1);
      spawnItem(source.level(), itemStack, 6, direction, position);
      return dispensed;
   }

   public static void spawnItem(final Level level, final ItemStack itemStack, final int accuracy, final Direction direction, final Position position) {
      LivingBlock itemEntity = LivingBlock.createAt(level, BlockPos.containing(position), itemStack);
      if (itemEntity != null) {
         RandomSource random = level.getRandom();
         double pow = random.nextDouble() * 0.1 + 0.2;
         itemEntity.setDeltaMovement(random.triangle((double)direction.getStepX() * pow, 0.0172275 * (double)accuracy), random.triangle(0.2, 0.0172275 * (double)accuracy), random.triangle((double)direction.getStepZ() * pow, 0.0172275 * (double)accuracy));
      }
   }

   protected void playSound(final DispenseSource source) {
      playDefaultSound(source.level(), source.pos());
   }

   protected void playAnimation(final DispenseSource source, final Direction direction) {
      playDefaultAnimation(source.level(), source.pos(), direction);
   }

   public static void playDefaultSound(final Level level, final BlockPos pos) {
      level.levelEvent(1000, pos, 0);
   }

   public static void playDefaultAnimation(final Level level, final BlockPos pos, final Direction direction) {
      level.levelEvent(2000, pos, direction.get3DDataValue());
   }

   protected ItemStack consumeWithRemainder(final DispenseSource source, final ItemStack dispensed, final ItemStack remainder) {
      dispensed.shrink(1);
      if (dispensed.isEmpty()) {
         return remainder;
      } else {
         this.addToInventoryOrDispense(source, remainder);
         return dispensed;
      }
   }

   private void addToInventoryOrDispense(final DispenseSource source, final ItemStack itemStack) {
      ItemStack remainder = source.insertItem(itemStack);
      if (!remainder.isEmpty()) {
         Direction direction = source.direction();
         spawnItem(source.level(), remainder, 6, direction, DispenserBlock.getDispensePosition(source));
         playDefaultSound(source.level(), source.pos());
         playDefaultAnimation(source.level(), source.pos(), direction);
      }
   }
}
