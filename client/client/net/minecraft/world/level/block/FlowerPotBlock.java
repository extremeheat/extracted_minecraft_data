package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock extends Block {
   public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("potted").forGetter(var0x -> var0x.potted), propertiesCodec())
            .apply(var0, FlowerPotBlock::new)
   );
   private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
   public static final float AABB_SIZE = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
   private final Block potted;

   @Override
   public MapCodec<FlowerPotBlock> codec() {
      return CODEC;
   }

   public FlowerPotBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.potted = var1;
      POTTED_BY_CONTENT.put(var1, this);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockState var8 = (var1.getItem() instanceof BlockItem var9 ? POTTED_BY_CONTENT.getOrDefault(var9.getBlock(), Blocks.AIR) : Blocks.AIR)
         .defaultBlockState();
      if (var8.isAir()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (!this.isEmpty()) {
         return ItemInteractionResult.CONSUME;
      } else {
         var3.setBlock(var4, var8, 3);
         var3.gameEvent(var5, GameEvent.BLOCK_CHANGE, var4);
         var5.awardStat(Stats.POT_FLOWER);
         var1.consume(1, var5);
         return ItemInteractionResult.sidedSuccess(var3.isClientSide);
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (this.isEmpty()) {
         return InteractionResult.CONSUME;
      } else {
         ItemStack var6 = new ItemStack(this.potted);
         if (!var4.addItem(var6)) {
            var4.drop(var6, false);
         }

         var2.setBlock(var3, Blocks.FLOWER_POT.defaultBlockState(), 3);
         var2.gameEvent(var4, GameEvent.BLOCK_CHANGE, var3);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return this.isEmpty() ? super.getCloneItemStack(var1, var2, var3) : new ItemStack(this.potted);
   }

   private boolean isEmpty() {
      return this.potted == Blocks.AIR;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public Block getPotted() {
      return this.potted;
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
