package net.minecraft.world;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Containers {
   public Containers() {
      super();
   }

   public static void dropContents(final Level level, final BlockPos pos, final Container container) {
      dropContents(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), container);
   }

   public static void dropContents(final Level level, final Entity entity, final Container container) {
      dropContents(level, entity.getX(), entity.getY(), entity.getZ(), container);
   }

   private static void dropContents(final Level level, final double x, final double y, final double z, final Container container) {
      for(int i = 0; i < container.getContainerSize(); ++i) {
         dropItemStack(level, x, y, z, container.getItem(i));
      }

   }

   public static void dropContents(final Level level, final BlockPos pos, final NonNullList<ItemStack> list) {
      list.forEach((itemStack) -> dropItemStack(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack));
   }

   public static void dropItemStack(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
      double size = 0.25;
      double centerRange = 0.75;
      double halfSize = 0.125;
      RandomSource random = level.getRandom();
      double xo = Math.floor(x) + random.nextDouble() * 0.75 + 0.125;
      double yo = Math.floor(y) + random.nextDouble() * 0.75;
      double zo = Math.floor(z) + random.nextDouble() * 0.75 + 0.125;
      Collection<LivingBlock> created = LivingBlock.createStack(level, BlockPos.containing(xo, yo, zo), (Entity)null, itemStack);
      float pow = 0.05F;

      for(LivingBlock block : created) {
         block.setDeltaMovement(random.triangle(0.0, 0.11485000171139836), random.triangle(0.2, 0.11485000171139836), random.triangle(0.0, 0.11485000171139836));
      }

   }

   public static void updateNeighboursAfterDestroy(final BlockState state, final Level level, final BlockPos pos) {
      level.updateNeighbourForOutputSignal(pos, state.getBlock());
   }
}
