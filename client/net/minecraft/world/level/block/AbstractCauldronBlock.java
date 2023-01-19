package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCauldronBlock extends Block {
   private static final int SIDE_THICKNESS = 2;
   private static final int LEG_WIDTH = 4;
   private static final int LEG_HEIGHT = 3;
   private static final int LEG_DEPTH = 2;
   protected static final int FLOOR_LEVEL = 4;
   private static final VoxelShape INSIDE = box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
   protected static final VoxelShape SHAPE = Shapes.join(
      Shapes.block(),
      Shapes.or(box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE),
      BooleanOp.ONLY_FIRST
   );
   private final Map<Item, CauldronInteraction> interactions;

   public AbstractCauldronBlock(BlockBehaviour.Properties var1, Map<Item, CauldronInteraction> var2) {
      super(var1);
      this.interactions = var2;
   }

   protected double getContentHeight(BlockState var1) {
      return 0.0;
   }

   protected boolean isEntityInsideContent(BlockState var1, BlockPos var2, Entity var3) {
      return var3.getY() < (double)var2.getY() + this.getContentHeight(var1) && var3.getBoundingBox().maxY > (double)var2.getY() + 0.25;
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      CauldronInteraction var8 = this.interactions.get(var7.getItem());
      return var8.interact(var1, var2, var3, var4, var5, var7);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return INSIDE;
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public abstract boolean isFull(BlockState var1);

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = PointedDripstoneBlock.findStalactiteTipAboveCauldron(var2, var3);
      if (var5 != null) {
         Fluid var6 = PointedDripstoneBlock.getCauldronFillFluidType(var2, var5);
         if (var6 != Fluids.EMPTY && this.canReceiveStalactiteDrip(var6)) {
            this.receiveStalactiteDrip(var1, var2, var3, var6);
         }
      }
   }

   protected boolean canReceiveStalactiteDrip(Fluid var1) {
      return false;
   }

   protected void receiveStalactiteDrip(BlockState var1, Level var2, BlockPos var3, Fluid var4) {
   }
}
