package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock extends Block {
   private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
   public static final float AABB_SIZE = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block content;

   public FlowerPotBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.content = var1;
      POTTED_BY_CONTENT.put(var1, this);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      Item var8 = var7.getItem();
      BlockState var9 = (var8 instanceof BlockItem ? (Block)POTTED_BY_CONTENT.getOrDefault(((BlockItem)var8).getBlock(), Blocks.AIR) : Blocks.AIR).defaultBlockState();
      boolean var10 = var9.is(Blocks.AIR);
      boolean var11 = this.isEmpty();
      if (var10 != var11) {
         if (var11) {
            var2.setBlock(var3, var9, 3);
            var4.awardStat(Stats.POT_FLOWER);
            if (!var4.getAbilities().instabuild) {
               var7.shrink(1);
            }
         } else {
            ItemStack var12 = new ItemStack(this.content);
            if (var7.isEmpty()) {
               var4.setItemInHand(var5, var12);
            } else if (!var4.addItem(var12)) {
               var4.drop(var12, false);
            }

            var2.setBlock(var3, Blocks.FLOWER_POT.defaultBlockState(), 3);
         }

         var2.gameEvent(var4, GameEvent.BLOCK_CHANGE, var3);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.CONSUME;
      }
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return this.isEmpty() ? super.getCloneItemStack(var1, var2, var3) : new ItemStack(this.content);
   }

   private boolean isEmpty() {
      return this.content == Blocks.AIR;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public Block getContent() {
      return this.content;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
