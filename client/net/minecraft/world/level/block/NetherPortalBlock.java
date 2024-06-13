package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class NetherPortalBlock extends Block implements Portal {
   public static final MapCodec<NetherPortalBlock> CODEC = simpleCodec(NetherPortalBlock::new);
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final int AABB_OFFSET = 2;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

   @Override
   public MapCodec<NetherPortalBlock> codec() {
      return CODEC;
   }

   public NetherPortalBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction.Axis)var1.getValue(AXIS)) {
         case Z:
            return Z_AXIS_AABB;
         case X:
         default:
            return X_AXIS_AABB;
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.dimensionType().natural() && var2.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && var4.nextInt(2000) < var2.getDifficulty().getId()) {
         while (var2.getBlockState(var3).is(this)) {
            var3 = var3.below();
         }

         if (var2.getBlockState(var3).isValidSpawn(var2, var3, EntityType.ZOMBIFIED_PIGLIN)) {
            Entity var5 = EntityType.ZOMBIFIED_PIGLIN.spawn(var2, var3.above(), MobSpawnType.STRUCTURE);
            if (var5 != null) {
               var5.setPortalCooldown();
            }
         }
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = var2.getAxis();
      Direction.Axis var8 = var1.getValue(AXIS);
      boolean var9 = var8 != var7 && var7.isHorizontal();
      return !var9 && !var3.is(this) && !new PortalShape(var4, var5, var8).isComplete()
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.canUsePortal(false)) {
         var4.setAsInsidePortal(this, var3);
      }
   }

   @Override
   public int getPortalTransitionTime(ServerLevel var1, Entity var2) {
      return var2 instanceof Player var3
         ? Math.max(
            1,
            var1.getGameRules()
               .getInt(
                  var3.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY
               )
         )
         : 0;
   }

   @Nullable
   @Override
   public DimensionTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3) {
      ResourceKey var4 = var1.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
      ServerLevel var5 = var1.getServer().getLevel(var4);
      if (var5 == null) {
         return null;
      } else {
         boolean var6 = var5.dimension() == Level.NETHER;
         WorldBorder var7 = var5.getWorldBorder();
         double var8 = DimensionType.getTeleportationScale(var1.dimensionType(), var5.dimensionType());
         BlockPos var10 = var7.clampToBounds(var2.getX() * var8, var2.getY(), var2.getZ() * var8);
         return this.getExitPortal(var5, var2, var3, var10, var6, var7);
      }
   }

   @Nullable
   private DimensionTransition getExitPortal(ServerLevel var1, Entity var2, BlockPos var3, BlockPos var4, boolean var5, WorldBorder var6) {
      Optional var7 = var1.getPortalForcer().findClosestPortalPosition(var4, var5, var6);
      BlockUtil.FoundRectangle var8;
      DimensionTransition.PostDimensionTransition var9;
      if (var7.isPresent()) {
         BlockPos var10 = (BlockPos)var7.get();
         BlockState var11 = var1.getBlockState(var10);
         var8 = BlockUtil.getLargestRectangleAround(
            var10, var11.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, var2x -> var1.getBlockState(var2x) == var11
         );
         var9 = DimensionTransition.PLAY_PORTAL_SOUND.then(var1x -> var1x.placePortalTicket(var10));
      } else {
         Direction.Axis var12 = var2.level().getBlockState(var3).getOptionalValue(AXIS).orElse(Direction.Axis.X);
         Optional var13 = var1.getPortalForcer().createPortal(var4, var12);
         if (var13.isEmpty()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
            return null;
         }

         var8 = (BlockUtil.FoundRectangle)var13.get();
         var9 = DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);
      }

      return getDimensionTransitionFromExit(var2, var3, var8, var1, var9);
   }

   private static DimensionTransition getDimensionTransitionFromExit(
      Entity var0, BlockPos var1, BlockUtil.FoundRectangle var2, ServerLevel var3, DimensionTransition.PostDimensionTransition var4
   ) {
      BlockState var7 = var0.level().getBlockState(var1);
      Direction.Axis var5;
      Vec3 var6;
      if (var7.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
         var5 = var7.getValue(BlockStateProperties.HORIZONTAL_AXIS);
         BlockUtil.FoundRectangle var8 = BlockUtil.getLargestRectangleAround(
            var1, var5, 21, Direction.Axis.Y, 21, var2x -> var0.level().getBlockState(var2x) == var7
         );
         var6 = var0.getRelativePortalPosition(var5, var8);
      } else {
         var5 = Direction.Axis.X;
         var6 = new Vec3(0.5, 0.0, 0.0);
      }

      return createDimensionTransition(var3, var2, var5, var6, var0, var0.getDeltaMovement(), var0.getYRot(), var0.getXRot(), var4);
   }

   private static DimensionTransition createDimensionTransition(
      ServerLevel var0,
      BlockUtil.FoundRectangle var1,
      Direction.Axis var2,
      Vec3 var3,
      Entity var4,
      Vec3 var5,
      float var6,
      float var7,
      DimensionTransition.PostDimensionTransition var8
   ) {
      BlockPos var9 = var1.minCorner;
      BlockState var10 = var0.getBlockState(var9);
      Direction.Axis var11 = var10.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
      double var12 = (double)var1.axis1Size;
      double var14 = (double)var1.axis2Size;
      EntityDimensions var16 = var4.getDimensions(var4.getPose());
      int var17 = var2 == var11 ? 0 : 90;
      Vec3 var18 = var2 == var11 ? var5 : new Vec3(var5.z, var5.y, -var5.x);
      double var19 = (double)var16.width() / 2.0 + (var12 - (double)var16.width()) * var3.x();
      double var21 = (var14 - (double)var16.height()) * var3.y();
      double var23 = 0.5 + var3.z();
      boolean var25 = var11 == Direction.Axis.X;
      Vec3 var26 = new Vec3((double)var9.getX() + (var25 ? var19 : var23), (double)var9.getY() + var21, (double)var9.getZ() + (var25 ? var23 : var19));
      Vec3 var27 = PortalShape.findCollisionFreePosition(var26, var0, var4, var16);
      return new DimensionTransition(var0, var27, var18, var6 + (float)var17, var7, var8);
   }

   @Override
   public Portal.Transition getLocalTransition() {
      return Portal.Transition.CONFUSION;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(100) == 0) {
         var2.playLocalSound(
            (double)var3.getX() + 0.5,
            (double)var3.getY() + 0.5,
            (double)var3.getZ() + 0.5,
            SoundEvents.PORTAL_AMBIENT,
            SoundSource.BLOCKS,
            0.5F,
            var4.nextFloat() * 0.4F + 0.8F,
            false
         );
      }

      for (int var5 = 0; var5 < 4; var5++) {
         double var6 = (double)var3.getX() + var4.nextDouble();
         double var8 = (double)var3.getY() + var4.nextDouble();
         double var10 = (double)var3.getZ() + var4.nextDouble();
         double var12 = ((double)var4.nextFloat() - 0.5) * 0.5;
         double var14 = ((double)var4.nextFloat() - 0.5) * 0.5;
         double var16 = ((double)var4.nextFloat() - 0.5) * 0.5;
         int var18 = var4.nextInt(2) * 2 - 1;
         if (!var2.getBlockState(var3.west()).is(this) && !var2.getBlockState(var3.east()).is(this)) {
            var6 = (double)var3.getX() + 0.5 + 0.25 * (double)var18;
            var12 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)var3.getZ() + 0.5 + 0.25 * (double)var18;
            var16 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         }

         var2.addParticle(ParticleTypes.PORTAL, var6, var8, var10, var12, var14, var16);
      }
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)var1.getValue(AXIS)) {
               case Z:
                  return var1.setValue(AXIS, Direction.Axis.X);
               case X:
                  return var1.setValue(AXIS, Direction.Axis.Z);
               default:
                  return var1;
            }
         default:
            return var1;
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS);
   }
}
