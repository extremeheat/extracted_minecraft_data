package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CaveVines {
   VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   BooleanProperty BERRIES = BlockStateProperties.BERRIES;

   static InteractionResult use(BlockState var0, Level var1, BlockPos var2) {
      if ((Boolean)var0.getValue(BERRIES)) {
         Block.popResource(var1, var2, new ItemStack(Items.GLOW_BERRIES, 1));
         float var3 = Mth.randomBetween(var1.random, 0.8F, 1.2F);
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, var3);
         var1.setBlock(var2, (BlockState)var0.setValue(BERRIES, false), 2);
         return InteractionResult.sidedSuccess(var1.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   static boolean hasGlowBerries(BlockState var0) {
      return var0.hasProperty(BERRIES) && (Boolean)var0.getValue(BERRIES);
   }

   static ToIntFunction<BlockState> emission(int var0) {
      return (var1) -> {
         return (Boolean)var1.getValue(BlockStateProperties.BERRIES) ? var0 : 0;
      };
   }
}
