package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCauldronBlock extends Block {
   private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape SHAPE;
   private final Map<Item, CauldronInteraction> interactions;

   public AbstractCauldronBlock(BlockBehaviour.Properties var1, Map<Item, CauldronInteraction> var2) {
      super(var1);
      this.interactions = var2;
   }

   protected double getContentHeight(BlockState var1) {
      return 0.0D;
   }

   protected boolean isEntityInsideContent(BlockState var1, BlockPos var2, Entity var3) {
      return var3.getY() < (double)var2.getY() + this.getContentHeight(var1) && var3.getBoundingBox().maxY > (double)var2.getY() + 0.25D;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      CauldronInteraction var8 = (CauldronInteraction)this.interactions.get(var7.getItem());
      return var8.interact(var1, var2, var3, var4, var5, var7);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return INSIDE;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
   }
}
