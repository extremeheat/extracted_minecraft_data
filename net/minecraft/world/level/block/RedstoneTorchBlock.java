package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneTorchBlock extends TorchBlock {
   public static final BooleanProperty LIT;
   private static final Map RECENT_TOGGLES;

   protected RedstoneTorchBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
   }

   public int getTickDelay(LevelReader var1) {
      return 2;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Direction[] var6 = Direction.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction var9 = var6[var8];
         var2.updateNeighborsAt(var3.relative(var9), this);
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5) {
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            var2.updateNeighborsAt(var3.relative(var9), this);
         }

      }
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(LIT) && Direction.UP != var4 ? 15 : 0;
   }

   protected boolean hasNeighborSignal(Level var1, BlockPos var2, BlockState var3) {
      return var1.hasSignal(var2.below(), Direction.DOWN);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      handleTick(var1, var2, var3, var4, this.hasNeighborSignal(var2, var3, var1));
   }

   public static void handleTick(BlockState var0, Level var1, BlockPos var2, Random var3, boolean var4) {
      List var5 = (List)RECENT_TOGGLES.get(var1);

      while(var5 != null && !var5.isEmpty() && var1.getGameTime() - ((RedstoneTorchBlock.Toggle)var5.get(0)).when > 60L) {
         var5.remove(0);
      }

      if ((Boolean)var0.getValue(LIT)) {
         if (var4) {
            var1.setBlock(var2, (BlockState)var0.setValue(LIT, false), 3);
            if (isToggledTooFrequently(var1, var2, true)) {
               var1.levelEvent(1502, var2, 0);
               var1.getBlockTicks().scheduleTick(var2, var1.getBlockState(var2).getBlock(), 160);
            }
         }
      } else if (!var4 && !isToggledTooFrequently(var1, var2, false)) {
         var1.setBlock(var2, (BlockState)var0.setValue(LIT, true), 3);
      }

   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if ((Boolean)var1.getValue(LIT) == this.hasNeighborSignal(var2, var3, var1) && !var2.getBlockTicks().willTickThisTick(var3, this)) {
         var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2));
      }

   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.DOWN ? var1.getSignal(var2, var3, var4) : 0;
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(LIT)) {
         double var5 = (double)var3.getX() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var7 = (double)var3.getY() + 0.7D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var9 = (double)var3.getZ() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         var2.addParticle(DustParticleOptions.REDSTONE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }
   }

   public int getLightEmission(BlockState var1) {
      return (Boolean)var1.getValue(LIT) ? super.getLightEmission(var1) : 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(LIT);
   }

   private static boolean isToggledTooFrequently(Level var0, BlockPos var1, boolean var2) {
      List var3 = (List)RECENT_TOGGLES.computeIfAbsent(var0, (var0x) -> {
         return Lists.newArrayList();
      });
      if (var2) {
         var3.add(new RedstoneTorchBlock.Toggle(var1.immutable(), var0.getGameTime()));
      }

      int var4 = 0;

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         RedstoneTorchBlock.Toggle var6 = (RedstoneTorchBlock.Toggle)var3.get(var5);
         if (var6.pos.equals(var1)) {
            ++var4;
            if (var4 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   static {
      LIT = BlockStateProperties.LIT;
      RECENT_TOGGLES = new WeakHashMap();
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long when;

      public Toggle(BlockPos var1, long var2) {
         this.pos = var1;
         this.when = var2;
      }
   }
}
