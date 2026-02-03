package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ShulkerBoxDispenseBehavior() {
      super();
   }

   protected ItemStack execute(final BlockSource source, final ItemStack dispensed) {
      this.setSuccess(false);
      Item item = dispensed.getItem();
      if (item instanceof BlockItem) {
         Direction facing = (Direction)source.state().getValue(DispenserBlock.FACING);
         BlockPos relativePos = source.pos().relative(facing);
         Direction clickedFace = source.level().isEmptyBlock(relativePos.below()) ? facing : Direction.UP;

         try {
            this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(source.level(), relativePos, facing, dispensed, clickedFace)).consumesAction());
         } catch (Exception e) {
            LOGGER.error("Error trying to place shulker box at {}", relativePos, e);
         }
      }

      return dispensed;
   }
}
