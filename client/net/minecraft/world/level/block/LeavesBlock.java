package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LeavesBlock extends Block {
   public static final IntegerProperty DISTANCE;
   public static final BooleanProperty PERSISTENT;
   protected static boolean renderCutout;

   public LeavesBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false));
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(DISTANCE) == 7 && !(Boolean)var1.getValue(PERSISTENT);
   }

   public void randomTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (!(Boolean)var1.getValue(PERSISTENT) && (Integer)var1.getValue(DISTANCE) == 7) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }

   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      var2.setBlock(var3, updateDistance(var1, var2, var3), 3);
   }

   public int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      int var7 = getDistanceAt(var3) + 1;
      if (var7 != 1 || (Integer)var1.getValue(DISTANCE) != var7) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var5 = null;

      try {
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            var4.set((Vec3i)var2).move(var9);
            var3 = Math.min(var3, getDistanceAt(var1.getBlockState(var4)) + 1);
            if (var3 == 1) {
               break;
            }
         }
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               var4.close();
            }
         }

      }

      return (BlockState)var0.setValue(DISTANCE, var3);
   }

   private static int getDistanceAt(BlockState var0) {
      if (BlockTags.LOGS.contains(var0.getBlock())) {
         return 0;
      } else {
         return var0.getBlock() instanceof LeavesBlock ? (Integer)var0.getValue(DISTANCE) : 7;
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var2.isRainingAt(var3.above())) {
         if (var4.nextInt(15) == 1) {
            BlockPos var5 = var3.below();
            BlockState var6 = var2.getBlockState(var5);
            if (!var6.canOcclude() || !var6.isFaceSturdy(var2, var5, Direction.UP)) {
               double var7 = (double)((float)var3.getX() + var4.nextFloat());
               double var9 = (double)var3.getY() - 0.05D;
               double var11 = (double)((float)var3.getZ() + var4.nextFloat());
               var2.addParticle(ParticleTypes.DRIPPING_WATER, var7, var9, var11, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }

   public static void setFancy(boolean var0) {
      renderCutout = var0;
   }

   public boolean canOcclude(BlockState var1) {
      return false;
   }

   public BlockLayer getRenderLayer() {
      return renderCutout ? BlockLayer.CUTOUT_MIPPED : BlockLayer.SOLID;
   }

   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType<?> var4) {
      return var4 == EntityType.OCELOT || var4 == EntityType.PARROT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, PERSISTENT);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return updateDistance((BlockState)this.defaultBlockState().setValue(PERSISTENT, true), var1.getLevel(), var1.getClickedPos());
   }

   static {
      DISTANCE = BlockStateProperties.DISTANCE;
      PERSISTENT = BlockStateProperties.PERSISTENT;
   }
}
