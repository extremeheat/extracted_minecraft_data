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

   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      this.setSuccess(false);
      Item var3 = var2.getItem();
      if (var3 instanceof BlockItem) {
         Direction var4 = (Direction)var1.state().getValue(DispenserBlock.FACING);
         BlockPos var5 = var1.pos().relative(var4);
         Direction var6 = var1.level().isEmptyBlock(var5.below()) ? var4 : Direction.UP;

         try {
            this.setSuccess(((BlockItem)var3).place(new DirectionalPlaceContext(var1.level(), var5, var4, var2, var6)).consumesAction());
         } catch (Exception var8) {
            LOGGER.error("Error trying to place shulker box at {}", var5, var8);
         }
      }

      return var2;
   }
}
