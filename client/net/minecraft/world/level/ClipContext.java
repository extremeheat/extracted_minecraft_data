package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
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
      super();
      this.from = var1;
      this.to = var2;
      this.block = var3;
      this.fluid = var4;
      this.collisionContext = CollisionContext.of(var5);
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

   public static enum Fluid {
      NONE((var0) -> {
         return false;
      }),
      SOURCE_ONLY(FluidState::isSource),
      ANY((var0) -> {
         return !var0.isEmpty();
      });

      private final Predicate<FluidState> canPick;

      private Fluid(Predicate<FluidState> var3) {
         this.canPick = var3;
      }

      public boolean canPick(FluidState var1) {
         return this.canPick.test(var1);
      }
   }

   public interface ShapeGetter {
      VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4);
   }

   public static enum Block implements ClipContext.ShapeGetter {
      COLLIDER(BlockBehaviour.BlockStateBase::getCollisionShape),
      OUTLINE(BlockBehaviour.BlockStateBase::getShape),
      VISUAL(BlockBehaviour.BlockStateBase::getVisualShape);

      private final ClipContext.ShapeGetter shapeGetter;

      private Block(ClipContext.ShapeGetter var3) {
         this.shapeGetter = var3;
      }

      public VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
         return this.shapeGetter.get(var1, var2, var3, var4);
      }
   }
}
