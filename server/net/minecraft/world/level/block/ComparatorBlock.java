package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
   public static final EnumProperty<ComparatorMode> MODE;

   public ComparatorBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(BlockState var1) {
      return 2;
   }

   protected int getOutputSignal(BlockGetter var1, BlockPos var2, BlockState var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      return var4 instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)var4).getOutputSignal() : 0;
   }

   private int calculateOutputSignal(Level var1, BlockPos var2, BlockState var3) {
      return var3.getValue(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.getInputSignal(var1, var2, var3) - this.getAlternateSignal(var1, var2, var3), 0) : this.getInputSignal(var1, var2, var3);
   }

   protected boolean shouldTurnOn(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.getInputSignal(var1, var2, var3);
      if (var4 == 0) {
         return false;
      } else {
         int var5 = this.getAlternateSignal(var1, var2, var3);
         if (var4 > var5) {
            return true;
         } else {
            return var4 == var5 && var3.getValue(MODE) == ComparatorMode.COMPARE;
         }
      }
   }

   protected int getInputSignal(Level var1, BlockPos var2, BlockState var3) {
      int var4 = super.getInputSignal(var1, var2, var3);
      Direction var5 = (Direction)var3.getValue(FACING);
      BlockPos var6 = var2.relative(var5);
      BlockState var7 = var1.getBlockState(var6);
      if (var7.hasAnalogOutputSignal()) {
         var4 = var7.getAnalogOutputSignal(var1, var6);
      } else if (var4 < 15 && var7.isRedstoneConductor(var1, var6)) {
         var6 = var6.relative(var5);
         var7 = var1.getBlockState(var6);
         ItemFrame var8 = this.getItemFrame(var1, var5, var6);
         int var9 = Math.max(var8 == null ? -2147483648 : var8.getAnalogOutput(), var7.hasAnalogOutputSignal() ? var7.getAnalogOutputSignal(var1, var6) : -2147483648);
         if (var9 != -2147483648) {
            var4 = var9;
         }
      }

      return var4;
   }

   @Nullable
   private ItemFrame getItemFrame(Level var1, Direction var2, BlockPos var3) {
      List var4 = var1.getEntitiesOfClass(ItemFrame.class, new AABB((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), (double)(var3.getX() + 1), (double)(var3.getY() + 1), (double)(var3.getZ() + 1)), (var1x) -> {
         return var1x != null && var1x.getDirection() == var2;
      });
      return var4.size() == 1 ? (ItemFrame)var4.get(0) : null;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (!var4.abilities.mayBuild) {
         return InteractionResult.PASS;
      } else {
         var1 = (BlockState)var1.cycle(MODE);
         float var7 = var1.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         var2.playSound(var4, var3, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, var7);
         var2.setBlock(var3, var1, 2);
         this.refreshOutputState(var2, var3, var1);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   protected void checkTickOnNeighbor(Level var1, BlockPos var2, BlockState var3) {
      if (!var1.getBlockTicks().willTickThisTick(var2, this)) {
         int var4 = this.calculateOutputSignal(var1, var2, var3);
         BlockEntity var5 = var1.getBlockEntity(var2);
         int var6 = var5 instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)var5).getOutputSignal() : 0;
         if (var4 != var6 || (Boolean)var3.getValue(POWERED) != this.shouldTurnOn(var1, var2, var3)) {
            TickPriority var7 = this.shouldPrioritize(var1, var2, var3) ? TickPriority.HIGH : TickPriority.NORMAL;
            var1.getBlockTicks().scheduleTick(var2, this, 2, var7);
         }

      }
   }

   private void refreshOutputState(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.calculateOutputSignal(var1, var2, var3);
      BlockEntity var5 = var1.getBlockEntity(var2);
      int var6 = 0;
      if (var5 instanceof ComparatorBlockEntity) {
         ComparatorBlockEntity var7 = (ComparatorBlockEntity)var5;
         var6 = var7.getOutputSignal();
         var7.setOutputSignal(var4);
      }

      if (var6 != var4 || var3.getValue(MODE) == ComparatorMode.COMPARE) {
         boolean var9 = this.shouldTurnOn(var1, var2, var3);
         boolean var8 = (Boolean)var3.getValue(POWERED);
         if (var8 && !var9) {
            var1.setBlock(var2, (BlockState)var3.setValue(POWERED, false), 2);
         } else if (!var8 && var9) {
            var1.setBlock(var2, (BlockState)var3.setValue(POWERED, true), 2);
         }

         this.updateNeighborsInFront(var1, var2, var3);
      }

   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.refreshOutputState(var2, var3, var1);
   }

   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      super.triggerEvent(var1, var2, var3, var4, var5);
      BlockEntity var6 = var2.getBlockEntity(var3);
      return var6 != null && var6.triggerEvent(var4, var5);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new ComparatorBlockEntity();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, MODE, POWERED);
   }

   static {
      MODE = BlockStateProperties.MODE_COMPARATOR;
   }
}
