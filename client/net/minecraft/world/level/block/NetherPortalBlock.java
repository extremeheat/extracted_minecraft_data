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
   public static final EnumProperty<Direction.Axis> AXIS;
   private static final Logger LOGGER;
   protected static final int AABB_OFFSET = 2;
   protected static final VoxelShape X_AXIS_AABB;
   protected static final VoxelShape Z_AXIS_AABB;

   public MapCodec<NetherPortalBlock> codec() {
      return CODEC;
   }

   public NetherPortalBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction.Axis)var1.getValue(AXIS)) {
         case Z:
            return Z_AXIS_AABB;
         case X:
         default:
            return X_AXIS_AABB;
      }
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.dimensionType().natural() && var2.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && var4.nextInt(2000) < var2.getDifficulty().getId()) {
         while(var2.getBlockState(var3).is(this)) {
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

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = var2.getAxis();
      Direction.Axis var8 = (Direction.Axis)var1.getValue(AXIS);
      boolean var9 = var8 != var7 && var7.isHorizontal();
      return !var9 && !var3.is(this) && !(new PortalShape(var4, var5, var8)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.canChangeDimensions()) {
         var4.setAsInsidePortal(this, var3);
      }

   }

   public int getPortalTransitionTime(ServerLevel var1, Entity var2) {
      if (var2 instanceof Player var3) {
         return Math.max(1, var1.getGameRules().getInt(var3.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY));
      } else {
         return 0;
      }
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3) {
      ResourceKey var4 = var1.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
      ServerLevel var5 = var1.getServer().getLevel(var4);
      boolean var6 = var5.dimension() == Level.NETHER;
      WorldBorder var7 = var5.getWorldBorder();
      double var8 = DimensionType.getTeleportationScale(var1.dimensionType(), var5.dimensionType());
      BlockPos var10 = var7.clampToBounds(var2.getX() * var8, var2.getY(), var2.getZ() * var8);
      return this.getExitPortal(var5, var2, var3, var10, var6, var7);
   }

   @Nullable
   private DimensionTransition getExitPortal(ServerLevel var1, Entity var2, BlockPos var3, BlockPos var4, boolean var5, WorldBorder var6) {
      Optional var7 = var1.getPortalForcer().findPortalAround(var4, var5, var6);
      if (var7.isEmpty()) {
         Direction.Axis var8 = (Direction.Axis)var2.level().getBlockState(var3).getOptionalValue(AXIS).orElse(Direction.Axis.X);
         Optional var9 = var1.getPortalForcer().createPortal(var4, var8);
         if (var9.isEmpty()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
            return null;
         } else {
            return getDimensionTransitionFromExit(var2, var3, (BlockUtil.FoundRectangle)var9.get(), var1);
         }
      } else {
         return (DimensionTransition)var7.map((var3x) -> {
            return getDimensionTransitionFromExit(var2, var3, var3x, var1);
         }).orElse((Object)null);
      }
   }

   private static DimensionTransition getDimensionTransitionFromExit(Entity var0, BlockPos var1, BlockUtil.FoundRectangle var2, ServerLevel var3) {
      BlockState var6 = var0.level().getBlockState(var1);
      Direction.Axis var4;
      Vec3 var5;
      if (var6.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
         var4 = (Direction.Axis)var6.getValue(BlockStateProperties.HORIZONTAL_AXIS);
         BlockUtil.FoundRectangle var7 = BlockUtil.getLargestRectangleAround(var1, var4, 21, Direction.Axis.Y, 21, (var2x) -> {
            return var0.level().getBlockState(var2x) == var6;
         });
         var5 = var0.getRelativePortalPosition(var4, var7);
      } else {
         var4 = Direction.Axis.X;
         var5 = new Vec3(0.5, 0.0, 0.0);
      }

      return createDimensionTransition(var3, var2, var4, var5, var0, var0.getDeltaMovement(), var0.getYRot(), var0.getXRot());
   }

   private static DimensionTransition createDimensionTransition(ServerLevel var0, BlockUtil.FoundRectangle var1, Direction.Axis var2, Vec3 var3, Entity var4, Vec3 var5, float var6, float var7) {
      BlockPos var8 = var1.minCorner;
      BlockState var9 = var0.getBlockState(var8);
      Direction.Axis var10 = (Direction.Axis)var9.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
      double var11 = (double)var1.axis1Size;
      double var13 = (double)var1.axis2Size;
      EntityDimensions var15 = var4.getDimensions(var4.getPose());
      int var16 = var2 == var10 ? 0 : 90;
      Vec3 var17 = var2 == var10 ? var5 : new Vec3(var5.z, var5.y, -var5.x);
      double var18 = (double)var15.width() / 2.0 + (var11 - (double)var15.width()) * var3.x();
      double var20 = (var13 - (double)var15.height()) * var3.y();
      double var22 = 0.5 + var3.z();
      boolean var24 = var10 == Direction.Axis.X;
      Vec3 var25 = new Vec3((double)var8.getX() + (var24 ? var18 : var22), (double)var8.getY() + var20, (double)var8.getZ() + (var24 ? var22 : var18));
      Vec3 var26 = PortalShape.findCollisionFreePosition(var25, var0, var4, var15);
      return new DimensionTransition(var0, var26, var17, var6 + (float)var16, var7);
   }

   public Portal.Transition getLocalTransition() {
      return Portal.Transition.CONFUSION;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(100) == 0) {
         var2.playLocalSound((double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, var4.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var5 = 0; var5 < 4; ++var5) {
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

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)var1.getValue(AXIS)) {
               case Z -> {
                  return (BlockState)var1.setValue(AXIS, Direction.Axis.X);
               }
               case X -> {
                  return (BlockState)var1.setValue(AXIS, Direction.Axis.Z);
               }
               default -> {
                  return var1;
               }
            }
         default:
            return var1;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS);
   }

   static {
      AXIS = BlockStateProperties.HORIZONTAL_AXIS;
      LOGGER = LogUtils.getLogger();
      X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
      Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
   }
}
