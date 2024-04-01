package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MashedPotatoBlock extends Block {
   public static final MapCodec<MashedPotatoBlock> CODEC = simpleCodec(MashedPotatoBlock::new);
   private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9F;
   private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5F;
   public static final int MAX_HEIGHT = 8;
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
   protected static final VoxelShape[] SHAPE_BY_LAYER = SnowLayerBlock.SHAPE_BY_LAYER;

   @Override
   protected MapCodec<MashedPotatoBlock> codec() {
      return CODEC;
   }

   protected MashedPotatoBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, Integer.valueOf(1)));
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return true;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_LAYER[var1.getValue(LAYERS)];
   }

   @Override
   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return SHAPE_BY_LAYER[var1.getValue(LAYERS)];
   }

   @Override
   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   protected float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      if (var4.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
         return false;
      } else if (var4.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
         return true;
      } else {
         return Block.isFaceFull(var4.getCollisionShape(var2, var3.below()), Direction.UP) || var4.is(this) && var4.getValue(LAYERS) == 8;
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      int var3 = var1.getValue(LAYERS);
      if (!var2.getItemInHand().is(this.asItem()) || var3 >= 8) {
         return var3 == 1;
      } else if (var2.replacingClickedOnBlock()) {
         return var2.getClickedFace() == Direction.UP;
      } else {
         return true;
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(this)) {
         int var3 = var2.getValue(LAYERS);
         return var2.setValue(LAYERS, Integer.valueOf(Math.min(8, var3 + 1)));
      } else {
         return super.getStateForPlacement(var1);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LAYERS);
   }

   @Override
   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   @Override
   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!(var4 instanceof LivingEntity) || var4.getInBlockState().is(this)) {
         var4.makeStuckInBlock(var1, new Vec3(0.8999999761581421, 1.5, 0.8999999761581421));
      }

      int var5 = var1.getValue(LAYERS);
      if (var4 instanceof LivingEntity var6) {
         var6.addEffect(new MobEffectInstance(MobEffects.STICKY, 100 + var5 * 20));
      }
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      return var1.is(this.asItem()) ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var4.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         var4.getFoodData().eat(2, 0.1F);
         int var6 = var1.getValue(LAYERS);
         var2.gameEvent(var4, GameEvent.EAT, var3);
         if (var6 > 1) {
            var2.setBlock(var3, var1.setValue(LAYERS, Integer.valueOf(var6 - 1)), 3);
         } else {
            var2.removeBlock(var3, false);
            var2.gameEvent(var4, GameEvent.BLOCK_DESTROY, var3);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide());
      }
   }
}
