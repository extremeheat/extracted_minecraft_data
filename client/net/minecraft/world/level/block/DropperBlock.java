package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class DropperBlock extends DispenserBlock {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<DropperBlock> CODEC = simpleCodec(DropperBlock::new);
   private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

   public MapCodec<DropperBlock> codec() {
      return CODEC;
   }

   public DropperBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected DispenseItemBehavior getDispenseMethod(Level var1, ItemStack var2) {
      return DISPENSE_BEHAVIOUR;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DropperBlockEntity(var1, var2);
   }

   protected void dispenseFrom(ServerLevel var1, BlockState var2, BlockPos var3) {
      DispenserBlockEntity var4 = (DispenserBlockEntity)var1.getBlockEntity(var3, BlockEntityType.DROPPER).orElse((Object)null);
      if (var4 == null) {
         LOGGER.warn("Ignoring dispensing attempt for Dropper without matching block entity at {}", var3);
      } else {
         BlockSource var5 = new BlockSource(var1, var3, var2, var4);
         int var6 = var4.getRandomSlot(var1.random);
         if (var6 < 0) {
            var1.levelEvent(1001, var3, 0);
         } else {
            ItemStack var7 = var4.getItem(var6);
            if (!var7.isEmpty()) {
               Direction var8 = (Direction)var1.getBlockState(var3).getValue(FACING);
               Container var9 = HopperBlockEntity.getContainerAt(var1, var3.relative(var8));
               ItemStack var10;
               if (var9 == null) {
                  var10 = DISPENSE_BEHAVIOUR.dispense(var5, var7);
               } else {
                  var10 = HopperBlockEntity.addItem(var4, var9, var7.copyWithCount(1), var8.getOpposite());
                  if (var10.isEmpty()) {
                     var10 = var7.copy();
                     var10.shrink(1);
                  } else {
                     var10 = var7.copy();
                  }
               }

               var4.setItem(var6, var10);
            }
         }
      }
   }
}
