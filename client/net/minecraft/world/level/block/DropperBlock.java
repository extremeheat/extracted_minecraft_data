package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.DispenseBehavior;
import net.minecraft.core.dispenser.DispenseBehaviorIds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlock extends DispenserBlock {
   public DropperBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected List<DispenseBehavior> getDispenseMethods(final ServerLevel level, final ItemStack itemStack) {
      return (List)level.registryAccess().getOrThrow(DispenseBehaviorIds.DROPPER).value();
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new DropperBlockEntity(worldPosition, blockState);
   }

   protected BlockEntityType<? extends DispenserBlockEntity> getBlockEntityType() {
      return BlockEntityTypes.DROPPER;
   }
}
