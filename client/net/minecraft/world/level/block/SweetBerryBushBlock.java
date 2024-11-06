package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetBerryBushBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<SweetBerryBushBlock> CODEC = simpleCodec(SweetBerryBushBlock::new);
   private static final float HURT_SPEED_THRESHOLD = 0.003F;
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE;
   private static final VoxelShape SAPLING_SHAPE;
   private static final VoxelShape MID_GROWTH_SHAPE;

   public MapCodec<SweetBerryBushBlock> codec() {
      return CODEC;
   }

   public SweetBerryBushBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Integer)var1.getValue(AGE) == 0) {
         return SAPLING_SHAPE;
      } else {
         return (Integer)var1.getValue(AGE) < 3 ? MID_GROWTH_SHAPE : super.getShape(var1, var2, var3, var4);
      }
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(AGE) < 3;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = (Integer)var1.getValue(AGE);
      if (var5 < 3 && var4.nextInt(5) == 0 && var2.getRawBrightness(var3.above(), 0) >= 9) {
         BlockState var6 = (BlockState)var1.setValue(AGE, var5 + 1);
         var2.setBlock(var3, var6, 2);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var6));
      }

   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4 instanceof LivingEntity && var4.getType() != EntityType.FOX && var4.getType() != EntityType.BEE) {
         var4.makeStuckInBlock(var1, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
         if (var2 instanceof ServerLevel) {
            ServerLevel var5 = (ServerLevel)var2;
            if ((Integer)var1.getValue(AGE) != 0) {
               Vec3 var6 = var4.isControlledByClient() ? var4.getKnownMovement() : var4.oldPosition().subtract(var4.position());
               if (var6.horizontalDistanceSqr() > 0.0) {
                  double var7 = Math.abs(var6.x());
                  double var9 = Math.abs(var6.z());
                  if (var7 >= 0.003000000026077032 || var9 >= 0.003000000026077032) {
                     var4.hurtServer(var5, var2.damageSources().sweetBerryBush(), 1.0F);
                  }
               }

               return;
            }
         }

      }
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      int var8 = (Integer)var2.getValue(AGE);
      boolean var9 = var8 == 3;
      return (InteractionResult)(!var9 && var1.is(Items.BONE_MEAL) ? InteractionResult.PASS : super.useItemOn(var1, var2, var3, var4, var5, var6, var7));
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      int var6 = (Integer)var1.getValue(AGE);
      boolean var7 = var6 == 3;
      if (var6 > 1) {
         int var8 = 1 + var2.random.nextInt(2);
         popResource(var2, var3, new ItemStack(Items.SWEET_BERRIES, var8 + (var7 ? 1 : 0)));
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + var2.random.nextFloat() * 0.4F);
         BlockState var9 = (BlockState)var1.setValue(AGE, 1);
         var2.setBlock(var3, var9, 2);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var4, var9));
         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(var1, var2, var3, var4, var5);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return (Integer)var3.getValue(AGE) < 3;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = Math.min(3, (Integer)var4.getValue(AGE) + 1);
      var1.setBlock(var3, (BlockState)var4.setValue(AGE, var5), 2);
   }

   static {
      AGE = BlockStateProperties.AGE_3;
      SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
      MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   }
}
