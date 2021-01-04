package net.minecraft.world.level.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;
   protected static final VoxelShape X_AXIS_AABB;
   protected static final VoxelShape Z_AXIS_AABB;

   public NetherPortalBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((Direction.Axis)var1.getValue(AXIS)) {
      case Z:
         return Z_AXIS_AABB;
      case X:
      default:
         return X_AXIS_AABB;
      }
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var2.dimension.isNaturalDimension() && var2.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && var4.nextInt(2000) < var2.getDifficulty().getId()) {
         while(var2.getBlockState(var3).getBlock() == this) {
            var3 = var3.below();
         }

         if (var2.getBlockState(var3).isValidSpawn(var2, var3, EntityType.ZOMBIE_PIGMAN)) {
            Entity var5 = EntityType.ZOMBIE_PIGMAN.spawn(var2, (CompoundTag)null, (Component)null, (Player)null, var3.above(), MobSpawnType.STRUCTURE, false, false);
            if (var5 != null) {
               var5.changingDimensionDelay = var5.getDimensionChangingDelay();
            }
         }
      }

   }

   public boolean trySpawnPortal(LevelAccessor var1, BlockPos var2) {
      NetherPortalBlock.PortalShape var3 = this.isPortal(var1, var2);
      if (var3 != null) {
         var3.createPortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public NetherPortalBlock.PortalShape isPortal(LevelAccessor var1, BlockPos var2) {
      NetherPortalBlock.PortalShape var3 = new NetherPortalBlock.PortalShape(var1, var2, Direction.Axis.X);
      if (var3.isValid() && var3.numPortalBlocks == 0) {
         return var3;
      } else {
         NetherPortalBlock.PortalShape var4 = new NetherPortalBlock.PortalShape(var1, var2, Direction.Axis.Z);
         return var4.isValid() && var4.numPortalBlocks == 0 ? var4 : null;
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = var2.getAxis();
      Direction.Axis var8 = (Direction.Axis)var1.getValue(AXIS);
      boolean var9 = var8 != var7 && var7.isHorizontal();
      return !var9 && var3.getBlock() != this && !(new NetherPortalBlock.PortalShape(var4, var5, var8)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var4.isPassenger() && !var4.isVehicle() && var4.canChangeDimensions()) {
         var4.handleInsidePortal(var3);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var4.nextInt(100) == 0) {
         var2.playLocalSound((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, var4.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)((float)var3.getX() + var4.nextFloat());
         double var8 = (double)((float)var3.getY() + var4.nextFloat());
         double var10 = (double)((float)var3.getZ() + var4.nextFloat());
         double var12 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var14 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         int var18 = var4.nextInt(2) * 2 - 1;
         if (var2.getBlockState(var3.west()).getBlock() != this && var2.getBlockState(var3.east()).getBlock() != this) {
            var6 = (double)var3.getX() + 0.5D + 0.25D * (double)var18;
            var12 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)var3.getZ() + 0.5D + 0.25D * (double)var18;
            var16 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         }

         var2.addParticle(ParticleTypes.PORTAL, var6, var8, var10, var12, var14, var16);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)var1.getValue(AXIS)) {
         case Z:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.X);
         case X:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.Z);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS);
   }

   public BlockPattern.BlockPatternMatch getPortalShape(LevelAccessor var1, BlockPos var2) {
      Direction.Axis var3 = Direction.Axis.Z;
      NetherPortalBlock.PortalShape var4 = new NetherPortalBlock.PortalShape(var1, var2, Direction.Axis.X);
      LoadingCache var5 = BlockPattern.createLevelCache(var1, true);
      if (!var4.isValid()) {
         var3 = Direction.Axis.X;
         var4 = new NetherPortalBlock.PortalShape(var1, var2, Direction.Axis.Z);
      }

      if (!var4.isValid()) {
         return new BlockPattern.BlockPatternMatch(var2, Direction.NORTH, Direction.UP, var5, 1, 1, 1);
      } else {
         int[] var6 = new int[Direction.AxisDirection.values().length];
         Direction var7 = var4.rightDir.getCounterClockWise();
         BlockPos var8 = var4.bottomLeft.above(var4.getHeight() - 1);
         Direction.AxisDirection[] var9 = Direction.AxisDirection.values();
         int var10 = var9.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            Direction.AxisDirection var12 = var9[var11];
            BlockPattern.BlockPatternMatch var13 = new BlockPattern.BlockPatternMatch(var7.getAxisDirection() == var12 ? var8 : var8.relative(var4.rightDir, var4.getWidth() - 1), Direction.get(var12, var3), Direction.UP, var5, var4.getWidth(), var4.getHeight(), 1);

            for(int var14 = 0; var14 < var4.getWidth(); ++var14) {
               for(int var15 = 0; var15 < var4.getHeight(); ++var15) {
                  BlockInWorld var16 = var13.getBlock(var14, var15, 1);
                  if (!var16.getState().isAir()) {
                     ++var6[var12.ordinal()];
                  }
               }
            }
         }

         Direction.AxisDirection var17 = Direction.AxisDirection.POSITIVE;
         Direction.AxisDirection[] var18 = Direction.AxisDirection.values();
         var11 = var18.length;

         for(int var19 = 0; var19 < var11; ++var19) {
            Direction.AxisDirection var20 = var18[var19];
            if (var6[var20.ordinal()] < var6[var17.ordinal()]) {
               var17 = var20;
            }
         }

         return new BlockPattern.BlockPatternMatch(var7.getAxisDirection() == var17 ? var8 : var8.relative(var4.rightDir, var4.getWidth() - 1), Direction.get(var17, var3), Direction.UP, var5, var4.getWidth(), var4.getHeight(), 1);
      }
   }

   static {
      AXIS = BlockStateProperties.HORIZONTAL_AXIS;
      X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   }

   public static class PortalShape {
      private final LevelAccessor level;
      private final Direction.Axis axis;
      private final Direction rightDir;
      private final Direction leftDir;
      private int numPortalBlocks;
      @Nullable
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public PortalShape(LevelAccessor var1, BlockPos var2, Direction.Axis var3) {
         super();
         this.level = var1;
         this.axis = var3;
         if (var3 == Direction.Axis.X) {
            this.leftDir = Direction.EAST;
            this.rightDir = Direction.WEST;
         } else {
            this.leftDir = Direction.NORTH;
            this.rightDir = Direction.SOUTH;
         }

         for(BlockPos var4 = var2; var2.getY() > var4.getY() - 21 && var2.getY() > 0 && this.isEmpty(var1.getBlockState(var2.below())); var2 = var2.below()) {
         }

         int var5 = this.getDistanceUntilEdge(var2, this.leftDir) - 1;
         if (var5 >= 0) {
            this.bottomLeft = var2.relative(this.leftDir, var5);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos var1, Direction var2) {
         int var3;
         for(var3 = 0; var3 < 22; ++var3) {
            BlockPos var4 = var1.relative(var2, var3);
            if (!this.isEmpty(this.level.getBlockState(var4)) || this.level.getBlockState(var4.below()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block var5 = this.level.getBlockState(var1.relative(var2, var3)).getBlock();
         return var5 == Blocks.OBSIDIAN ? var3 : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         int var1;
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(var1 = 0; var1 < this.width; ++var1) {
               BlockPos var2 = this.bottomLeft.relative(this.rightDir, var1).above(this.height);
               BlockState var3 = this.level.getBlockState(var2);
               if (!this.isEmpty(var3)) {
                  break label56;
               }

               Block var4 = var3.getBlock();
               if (var4 == Blocks.NETHER_PORTAL) {
                  ++this.numPortalBlocks;
               }

               if (var1 == 0) {
                  var4 = this.level.getBlockState(var2.relative(this.leftDir)).getBlock();
                  if (var4 != Blocks.OBSIDIAN) {
                     break label56;
                  }
               } else if (var1 == this.width - 1) {
                  var4 = this.level.getBlockState(var2.relative(this.rightDir)).getBlock();
                  if (var4 != Blocks.OBSIDIAN) {
                     break label56;
                  }
               }
            }
         }

         for(var1 = 0; var1 < this.width; ++var1) {
            if (this.level.getBlockState(this.bottomLeft.relative(this.rightDir, var1).above(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean isEmpty(BlockState var1) {
         Block var2 = var1.getBlock();
         return var1.isAir() || var2 == Blocks.FIRE || var2 == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void createPortalBlocks() {
         for(int var1 = 0; var1 < this.width; ++var1) {
            BlockPos var2 = this.bottomLeft.relative(this.rightDir, var1);

            for(int var3 = 0; var3 < this.height; ++var3) {
               this.level.setBlock(var2.above(var3), (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis), 18);
            }
         }

      }

      private boolean hasAllPortalBlocks() {
         return this.numPortalBlocks >= this.width * this.height;
      }

      public boolean isComplete() {
         return this.isValid() && this.hasAllPortalBlocks();
      }
   }
}
