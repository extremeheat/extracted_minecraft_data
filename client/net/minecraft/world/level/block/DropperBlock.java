package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlock extends DispenserBlock {
   private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

   public DropperBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack var1) {
      return DISPENSE_BEHAVIOUR;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DropperBlockEntity(var1, var2);
   }

   protected void dispenseFrom(ServerLevel var1, BlockPos var2) {
      BlockSourceImpl var3 = new BlockSourceImpl(var1, var2);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getEntity();
      int var5 = var4.getRandomSlot();
      if (var5 < 0) {
         var1.levelEvent(1001, var2, 0);
      } else {
         ItemStack var6 = var4.getItem(var5);
         if (!var6.isEmpty()) {
            Direction var7 = (Direction)var1.getBlockState(var2).getValue(FACING);
            Container var8 = HopperBlockEntity.getContainerAt(var1, var2.relative(var7));
            ItemStack var9;
            if (var8 == null) {
               var9 = DISPENSE_BEHAVIOUR.dispense(var3, var6);
            } else {
               var9 = HopperBlockEntity.addItem(var4, var8, var6.copy().split(1), var7.getOpposite());
               if (var9.isEmpty()) {
                  var9 = var6.copy();
                  var9.shrink(1);
               } else {
                  var9 = var6.copy();
               }
            }

            var4.setItem(var5, var9);
         }
      }
   }
}
