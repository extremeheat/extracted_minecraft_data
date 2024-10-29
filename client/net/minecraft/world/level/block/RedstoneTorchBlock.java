package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;

public class RedstoneTorchBlock extends BaseTorchBlock {
   public static final MapCodec<RedstoneTorchBlock> CODEC = simpleCodec(RedstoneTorchBlock::new);
   public static final BooleanProperty LIT;
   private static final Map<BlockGetter, List<Toggle>> RECENT_TOGGLES;
   public static final int RECENT_TOGGLE_TIMER = 60;
   public static final int MAX_RECENT_TOGGLES = 8;
   public static final int RESTART_DELAY = 160;
   private static final int TOGGLE_DELAY = 2;

   public MapCodec<? extends RedstoneTorchBlock> codec() {
      return CODEC;
   }

   protected RedstoneTorchBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      this.notifyNeighbors(var2, var3, var1);
   }

   private void notifyNeighbors(Level var1, BlockPos var2, BlockState var3) {
      Orientation var4 = this.randomOrientation(var1, var3);
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         var1.updateNeighborsAt(var2.relative(var8), this, ExperimentalRedstoneUtils.withFront(var4, var8));
      }

   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5) {
         this.notifyNeighbors(var2, var3, var1);
      }
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(LIT) && Direction.UP != var4 ? 15 : 0;
   }

   protected boolean hasNeighborSignal(Level var1, BlockPos var2, BlockState var3) {
      return var1.hasSignal(var2.below(), Direction.DOWN);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      boolean var5 = this.hasNeighborSignal(var2, var3, var1);
      List var6 = (List)RECENT_TOGGLES.get(var2);

      while(var6 != null && !var6.isEmpty() && var2.getGameTime() - ((Toggle)var6.get(0)).when > 60L) {
         var6.remove(0);
      }

      if ((Boolean)var1.getValue(LIT)) {
         if (var5) {
            var2.setBlock(var3, (BlockState)var1.setValue(LIT, false), 3);
            if (isToggledTooFrequently(var2, var3, true)) {
               var2.levelEvent(1502, var3, 0);
               var2.scheduleTick(var3, var2.getBlockState(var3).getBlock(), 160);
            }
         }
      } else if (!var5 && !isToggledTooFrequently(var2, var3, false)) {
         var2.setBlock(var3, (BlockState)var1.setValue(LIT, true), 3);
      }

   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if ((Boolean)var1.getValue(LIT) == this.hasNeighborSignal(var2, var3, var1) && !var2.getBlockTicks().willTickThisTick(var3, this)) {
         var2.scheduleTick(var3, this, 2);
      }

   }

   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.DOWN ? var1.getSignal(var2, var3, var4) : 0;
   }

   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(LIT)) {
         double var5 = (double)var3.getX() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         double var7 = (double)var3.getY() + 0.7 + (var4.nextDouble() - 0.5) * 0.2;
         double var9 = (double)var3.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         var2.addParticle(DustParticleOptions.REDSTONE, var5, var7, var9, 0.0, 0.0, 0.0);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT);
   }

   private static boolean isToggledTooFrequently(Level var0, BlockPos var1, boolean var2) {
      List var3 = (List)RECENT_TOGGLES.computeIfAbsent(var0, (var0x) -> {
         return Lists.newArrayList();
      });
      if (var2) {
         var3.add(new Toggle(var1.immutable(), var0.getGameTime()));
      }

      int var4 = 0;
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Toggle var6 = (Toggle)var5.next();
         if (var6.pos.equals(var1)) {
            ++var4;
            if (var4 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   protected Orientation randomOrientation(Level var1, BlockState var2) {
      return ExperimentalRedstoneUtils.initialOrientation(var1, (Direction)null, Direction.UP);
   }

   static {
      LIT = BlockStateProperties.LIT;
      RECENT_TOGGLES = new WeakHashMap();
   }

   public static class Toggle {
      final BlockPos pos;
      final long when;

      public Toggle(BlockPos var1, long var2) {
         super();
         this.pos = var1;
         this.when = var2;
      }
   }
}
