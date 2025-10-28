package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class TestBlock extends BaseEntityBlock implements GameMasterBlock {
   public static final MapCodec<TestBlock> CODEC = simpleCodec(TestBlock::new);
   public static final EnumProperty<TestBlockMode> MODE;

   public TestBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public @Nullable BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TestBlockEntity(var1, var2);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockItemStateProperties var2 = (BlockItemStateProperties)var1.getItemInHand().get(DataComponents.BLOCK_STATE);
      BlockState var3 = this.defaultBlockState();
      if (var2 != null) {
         TestBlockMode var4 = (TestBlockMode)var2.get(MODE);
         if (var4 != null) {
            var3 = (BlockState)var3.setValue(MODE, var4);
         }
      }

      return var3;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(MODE);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof TestBlockEntity var7) {
         if (!var4.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
         } else {
            if (var2.isClientSide()) {
               var4.openTestBlock(var7);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      TestBlockEntity var5 = getServerTestBlockEntity(var2, var3);
      if (var5 != null) {
         var5.reset();
      }
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      TestBlockEntity var7 = getServerTestBlockEntity(var2, var3);
      if (var7 != null) {
         if (var7.getMode() != TestBlockMode.START) {
            boolean var8 = var2.hasNeighborSignal(var3);
            boolean var9 = var7.isPowered();
            if (var8 && !var9) {
               var7.setPowered(true);
               var7.trigger();
            } else if (!var8 && var9) {
               var7.setPowered(false);
            }

         }
      }
   }

   private static @Nullable TestBlockEntity getServerTestBlockEntity(Level var0, BlockPos var1) {
      if (var0 instanceof ServerLevel var2) {
         BlockEntity var4 = var2.getBlockEntity(var1);
         if (var4 instanceof TestBlockEntity var3) {
            return var3;
         }
      }

      return null;
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (var1.getValue(MODE) != TestBlockMode.START) {
         return 0;
      } else {
         BlockEntity var5 = var2.getBlockEntity(var3);
         if (var5 instanceof TestBlockEntity) {
            TestBlockEntity var6 = (TestBlockEntity)var5;
            return var6.isPowered() ? 15 : 0;
         } else {
            return 0;
         }
      }
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      ItemStack var5 = super.getCloneItemStack(var1, var2, var3, var4);
      return setModeOnStack(var5, (TestBlockMode)var3.getValue(MODE));
   }

   public static ItemStack setModeOnStack(ItemStack var0, TestBlockMode var1) {
      var0.set(DataComponents.BLOCK_STATE, ((BlockItemStateProperties)var0.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)).with(MODE, var1));
      return var0;
   }

   protected MapCodec<TestBlock> codec() {
      return CODEC;
   }

   static {
      MODE = BlockStateProperties.TEST_BLOCK_MODE;
   }
}
