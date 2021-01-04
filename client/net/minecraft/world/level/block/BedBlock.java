package net.minecraft.world.level.block;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
   public static final EnumProperty<BedPart> PART;
   public static final BooleanProperty OCCUPIED;
   protected static final VoxelShape BASE;
   protected static final VoxelShape LEG_NORTH_WEST;
   protected static final VoxelShape LEG_SOUTH_WEST;
   protected static final VoxelShape LEG_NORTH_EAST;
   protected static final VoxelShape LEG_SOUTH_EAST;
   protected static final VoxelShape NORTH_SHAPE;
   protected static final VoxelShape SOUTH_SHAPE;
   protected static final VoxelShape WEST_SHAPE;
   protected static final VoxelShape EAST_SHAPE;
   private final DyeColor color;

   public BedBlock(DyeColor var1, Block.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
   }

   public MaterialColor getMapColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getValue(PART) == BedPart.FOOT ? this.color.getMaterialColor() : MaterialColor.WOOL;
   }

   @Nullable
   public static Direction getBedOrientation(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.getBlock() instanceof BedBlock ? (Direction)var2.getValue(FACING) : null;
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return true;
      } else {
         if (var1.getValue(PART) != BedPart.HEAD) {
            var3 = var3.relative((Direction)var1.getValue(FACING));
            var1 = var2.getBlockState(var3);
            if (var1.getBlock() != this) {
               return true;
            }
         }

         if (var2.dimension.mayRespawn() && var2.getBiome(var3) != Biomes.NETHER) {
            if ((Boolean)var1.getValue(OCCUPIED)) {
               var4.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied", new Object[0]), true);
               return true;
            } else {
               var4.startSleepInBed(var3).ifLeft((var1x) -> {
                  if (var1x != null) {
                     var4.displayClientMessage(var1x.getMessage(), true);
                  }

               });
               return true;
            }
         } else {
            var2.removeBlock(var3, false);
            BlockPos var7 = var3.relative(((Direction)var1.getValue(FACING)).getOpposite());
            if (var2.getBlockState(var7).getBlock() == this) {
               var2.removeBlock(var7, false);
            }

            var2.explode((Entity)null, DamageSource.netherBedExplosion(), (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
            return true;
         }
      }
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      super.fallOn(var1, var2, var3, var4 * 0.5F);
   }

   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      if (var2.isSneaking()) {
         super.updateEntityAfterFallOn(var1, var2);
      } else {
         Vec3 var3 = var2.getDeltaMovement();
         if (var3.y < 0.0D) {
            double var4 = var2 instanceof LivingEntity ? 1.0D : 0.8D;
            var2.setDeltaMovement(var3.x, -var3.y * 0.6600000262260437D * var4, var3.z);
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == getNeighbourDirection((BedPart)var1.getValue(PART), (Direction)var1.getValue(FACING))) {
         return var3.getBlock() == this && var3.getValue(PART) != var1.getValue(PART) ? (BlockState)var1.setValue(OCCUPIED, var3.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   private static Direction getNeighbourDirection(BedPart var0, Direction var1) {
      return var0 == BedPart.FOOT ? var1 : var1.getOpposite();
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, Blocks.AIR.defaultBlockState(), var5, var6);
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      BedPart var5 = (BedPart)var3.getValue(PART);
      BlockPos var6 = var2.relative(getNeighbourDirection(var5, (Direction)var3.getValue(FACING)));
      BlockState var7 = var1.getBlockState(var6);
      if (var7.getBlock() == this && var7.getValue(PART) != var5) {
         var1.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
         var1.levelEvent(var4, 2001, var6, Block.getId(var7));
         if (!var1.isClientSide && !var4.isCreative()) {
            ItemStack var8 = var4.getMainHandItem();
            dropResources(var3, var1, var2, (BlockEntity)null, var4, var8);
            dropResources(var7, var1, var6, (BlockEntity)null, var4, var8);
         }

         var4.awardStat(Stats.BLOCK_MINED.get(this));
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getHorizontalDirection();
      BlockPos var3 = var1.getClickedPos();
      BlockPos var4 = var3.relative(var2);
      return var1.getLevel().getBlockState(var4).canBeReplaced(var1) ? (BlockState)this.defaultBlockState().setValue(FACING, var2) : null;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      Direction var6 = var1.getValue(PART) == BedPart.HEAD ? var5 : var5.getOpposite();
      switch(var6) {
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      default:
         return EAST_SHAPE;
      }
   }

   public boolean hasCustomBreakingProgress(BlockState var1) {
      return true;
   }

   public static Optional<Vec3> findStandUpPosition(EntityType<?> var0, LevelReader var1, BlockPos var2, int var3) {
      Direction var4 = (Direction)var1.getBlockState(var2).getValue(FACING);
      int var5 = var2.getX();
      int var6 = var2.getY();
      int var7 = var2.getZ();

      for(int var8 = 0; var8 <= 1; ++var8) {
         int var9 = var5 - var4.getStepX() * var8 - 1;
         int var10 = var7 - var4.getStepZ() * var8 - 1;
         int var11 = var9 + 2;
         int var12 = var10 + 2;

         for(int var13 = var9; var13 <= var11; ++var13) {
            for(int var14 = var10; var14 <= var12; ++var14) {
               BlockPos var15 = new BlockPos(var13, var6, var14);
               Optional var16 = getStandingLocationAtOrBelow(var0, var1, var15);
               if (var16.isPresent()) {
                  if (var3 <= 0) {
                     return var16;
                  }

                  --var3;
               }
            }
         }
      }

      return Optional.empty();
   }

   protected static Optional<Vec3> getStandingLocationAtOrBelow(EntityType<?> var0, LevelReader var1, BlockPos var2) {
      VoxelShape var3 = var1.getBlockState(var2).getCollisionShape(var1, var2);
      if (var3.max(Direction.Axis.Y) > 0.4375D) {
         return Optional.empty();
      } else {
         BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(var2);

         while(var4.getY() >= 0 && var2.getY() - var4.getY() <= 2 && var1.getBlockState(var4).getCollisionShape(var1, var4).isEmpty()) {
            var4.move(Direction.DOWN);
         }

         VoxelShape var5 = var1.getBlockState(var4).getCollisionShape(var1, var4);
         if (var5.isEmpty()) {
            return Optional.empty();
         } else {
            double var6 = (double)var4.getY() + var5.max(Direction.Axis.Y) + 2.0E-7D;
            if ((double)var2.getY() - var6 > 2.0D) {
               return Optional.empty();
            } else {
               float var8 = var0.getWidth() / 2.0F;
               Vec3 var9 = new Vec3((double)var4.getX() + 0.5D, var6, (double)var4.getZ() + 0.5D);
               return var1.noCollision(new AABB(var9.x - (double)var8, var9.y, var9.z - (double)var8, var9.x + (double)var8, var9.y + (double)var0.getHeight(), var9.z + (double)var8)) ? Optional.of(var9) : Optional.empty();
            }
         }
      }
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, PART, OCCUPIED);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new BedBlockEntity(this.color);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      if (!var1.isClientSide) {
         BlockPos var6 = var2.relative((Direction)var3.getValue(FACING));
         var1.setBlock(var6, (BlockState)var3.setValue(PART, BedPart.HEAD), 3);
         var1.blockUpdated(var2, Blocks.AIR);
         var3.updateNeighbourShapes(var1, var2, 3);
      }

   }

   public DyeColor getColor() {
      return this.color;
   }

   public long getSeed(BlockState var1, BlockPos var2) {
      BlockPos var3 = var2.relative((Direction)var1.getValue(FACING), var1.getValue(PART) == BedPart.HEAD ? 0 : 1);
      return Mth.getSeed(var3.getX(), var2.getY(), var3.getZ());
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      PART = BlockStateProperties.BED_PART;
      OCCUPIED = BlockStateProperties.OCCUPIED;
      BASE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
      LEG_NORTH_WEST = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
      LEG_SOUTH_WEST = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
      LEG_NORTH_EAST = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
      LEG_SOUTH_EAST = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
      NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
      SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
      WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
      EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
   }
}
