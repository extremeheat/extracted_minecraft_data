package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class TransformWhenIntersectingBehavior<T> implements LivingBlockBehavior {
   protected static final int REEVALUATION_TICKS = 20;
   private final Block intersectWith;
   private final T transformInto;
   private int lastTriggeredTick;

   public TransformWhenIntersectingBehavior(final Block intersectWith, final T transformInto) {
      super();
      this.intersectWith = intersectWith;
      this.transformInto = transformInto;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 20 < entity.tickCount;
   }

   public final boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      return this.checkInside(entity, level);
   }

   public boolean checkInside(final LivingBlock entity, final ServerLevel level) {
      for(BlockPos pos : BlockPos.betweenClosed(entity.getBoundingBox())) {
         BlockState state = level.getBlockState(pos);
         boolean isBlockOrFluidSource = state.getFluidState() == Fluids.EMPTY.defaultFluidState() || state.getFluidState().isSource();
         if (isBlockOrFluidSource && state.is(this.intersectWith) && this.isColliding(entity, level, pos, state)) {
            entity.discard();
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            LivingBlock newLivingBlock = this.createNewLivingBlock(level);
            if (newLivingBlock != null) {
               newLivingBlock.snapTo(entity.position());
               level.addFreshEntity(newLivingBlock);
               ServerPlayer owner = entity.getAttributablePlayer();
               if (owner != null) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(owner, newLivingBlock);
               }
            }
            break;
         }
      }

      return false;
   }

   public abstract @Nullable LivingBlock createNewLivingBlock(ServerLevel level);

   public T getTransformInto() {
      return this.transformInto;
   }

   protected boolean isColliding(final Entity entity, final ServerLevel level, final BlockPos pos, final BlockState state) {
      VoxelShape movedBlockShape = state.getCollisionShape(level, pos, CollisionContext.of(entity, true)).move((Vec3i)pos);
      return Shapes.joinIsNotEmpty(movedBlockShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
   }

   public static LivingBlockBehaviorType transformWhenIntersecting(final Block intersectWith, final Block transformInto) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new TransformIntoBlockState(intersectWith, transformInto.defaultBlockState())));
   }

   public static LivingBlockBehaviorType transformWhenIntersecting(final Block intersectWith, final ItemStack transformIntoItem) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new TransformIntoItemStack(intersectWith, transformIntoItem)));
   }

   public static class TransformIntoBlockState extends TransformWhenIntersectingBehavior<BlockState> {
      protected TransformIntoBlockState(final Block intersectWith, final BlockState transformInto) {
         super(intersectWith, transformInto);
      }

      public @Nullable LivingBlock createNewLivingBlock(final ServerLevel level) {
         return LivingBlock.create(level, (BlockState)((BlockState)this.getTransformInto()));
      }
   }

   public static class TransformIntoItemStack extends TransformWhenIntersectingBehavior<ItemStack> {
      protected TransformIntoItemStack(final Block intersectWith, final ItemStack transformInto) {
         super(intersectWith, transformInto);
      }

      public @Nullable LivingBlock createNewLivingBlock(final ServerLevel level) {
         return LivingBlock.create(level, (ItemStack)((ItemStack)this.getTransformInto()));
      }
   }

   public static class TransformBucketIntoPowderSnowBucket extends TransformIntoItemStack {
      public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType(TransformBucketIntoPowderSnowBucket::new);

      private TransformBucketIntoPowderSnowBucket() {
         super(Blocks.POWDER_SNOW, new ItemStack(Items.POWDER_SNOW_BUCKET));
      }

      protected boolean isColliding(final Entity entity, final ServerLevel level, final BlockPos pos, final BlockState state) {
         VoxelShape movedBlockShape = state.getEntityInsideCollisionShape(level, pos, entity).move((Vec3i)pos);
         return Shapes.joinIsNotEmpty(movedBlockShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
      }
   }
}
