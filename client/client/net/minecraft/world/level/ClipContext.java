package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClipContext {
   private final Vec3 from;
   private final Vec3 to;
   private final ClipContext.Block block;
   private final ClipContext.Fluid fluid;
   private final CollisionContext collisionContext;

   public ClipContext(Vec3 var1, Vec3 var2, ClipContext.Block var3, ClipContext.Fluid var4, Entity var5) {
      this(var1, var2, var3, var4, CollisionContext.of(var5));
   }

   public ClipContext(Vec3 var1, Vec3 var2, ClipContext.Block var3, ClipContext.Fluid var4, CollisionContext var5) {
      super();
      this.from = var1;
      this.to = var2;
      this.block = var3;
      this.fluid = var4;
      this.collisionContext = var5;
   }

   public Vec3 getTo() {
      return this.to;
   }

   public Vec3 getFrom() {
      return this.from;
   }

   public VoxelShape getBlockShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.block.get(var1, var2, var3, this.collisionContext);
   }

   public VoxelShape getFluidShape(FluidState var1, BlockGetter var2, BlockPos var3) {
      return this.fluid.canPick(var1) ? var1.getShape(var2, var3) : Shapes.empty();
   }

   public static enum Block implements ClipContext.ShapeGetter {
      COLLIDER(BlockBehaviour.BlockStateBase::getCollisionShape),
      OUTLINE(BlockBehaviour.BlockStateBase::getShape),
      VISUAL(BlockBehaviour.BlockStateBase::getVisualShape),
      FALLDAMAGE_RESETTING((var0, var1, var2, var3) -> var0.is(BlockTags.FALL_DAMAGE_RESETTING) ? Shapes.block() : Shapes.empty());

      private final ClipContext.ShapeGetter shapeGetter;

      private Block(final ClipContext.ShapeGetter param3) {
         this.shapeGetter = nullxx;
      }

      @Override
      public VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
         return this.shapeGetter.get(var1, var2, var3, var4);
      }
   }

   public static enum Fluid {
      NONE(var0 -> false),
      SOURCE_ONLY(FluidState::isSource),
      ANY(var0 -> !var0.isEmpty()),
      WATER(var0 -> var0.is(FluidTags.WATER));

      private final Predicate<FluidState> canPick;

      private Fluid(final Predicate<FluidState> param3) {
         this.canPick = nullxx;
      }

      public boolean canPick(FluidState var1) {
         return this.canPick.test(var1);
      }
   }

   public interface ShapeGetter {
      VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4);
   }
}
