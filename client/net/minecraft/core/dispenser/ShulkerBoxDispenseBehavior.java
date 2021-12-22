package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {
   private static final Logger LOGGER = LogManager.getLogger();

   public ShulkerBoxDispenseBehavior() {
      super();
   }

   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      this.setSuccess(false);
      Item var3 = var2.getItem();
      if (var3 instanceof BlockItem) {
         Direction var4 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
         BlockPos var5 = var1.getPos().relative(var4);
         Direction var6 = var1.getLevel().isEmptyBlock(var5.below()) ? var4 : Direction.field_526;

         try {
            this.setSuccess(((BlockItem)var3).place(new DirectionalPlaceContext(var1.getLevel(), var5, var4, var2, var6)).consumesAction());
         } catch (Exception var8) {
            LOGGER.error("Error trying to place shulker box at {}", var5, var8);
         }
      }

      return var2;
   }
}
