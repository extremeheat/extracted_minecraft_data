package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TestInstanceBlock extends BaseEntityBlock implements GameMasterBlock {
   public static final MapCodec<TestInstanceBlock> CODEC = simpleCodec(TestInstanceBlock::new);

   public TestInstanceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TestInstanceBlockEntity(var1, var2);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof TestInstanceBlockEntity var7) {
         if (!var4.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
         } else {
            if (var4.getCommandSenderWorld().isClientSide) {
               var4.openTestInstanceBlock(var7);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected MapCodec<TestInstanceBlock> codec() {
      return CODEC;
   }
}
