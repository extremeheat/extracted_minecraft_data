package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH;
   public static final IntegerProperty EGGS;

   public TurtleEggBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
      this.destroyEgg(var1, var2, var3, 100);
      super.stepOn(var1, var2, var3);
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      if (!(var3 instanceof Zombie)) {
         this.destroyEgg(var1, var2, var3, 3);
      }

      super.fallOn(var1, var2, var3, var4);
   }

   private void destroyEgg(Level var1, BlockPos var2, Entity var3, int var4) {
      if (!this.canDestroyEgg(var1, var3)) {
         super.stepOn(var1, var2, var3);
      } else {
         if (!var1.isClientSide && var1.random.nextInt(var4) == 0) {
            this.decreaseEggs(var1, var2, var1.getBlockState(var2));
         }

      }
   }

   private void decreaseEggs(Level var1, BlockPos var2, BlockState var3) {
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + var1.random.nextFloat() * 0.2F);
      int var4 = (Integer)var3.getValue(EGGS);
      if (var4 <= 1) {
         var1.destroyBlock(var2, false);
      } else {
         var1.setBlock(var2, (BlockState)var3.setValue(EGGS, var4 - 1), 2);
         var1.levelEvent(2001, var2, Block.getId(var3));
      }

   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (this.shouldUpdateHatchLevel(var2) && this.onSand(var2, var3)) {
         int var5 = (Integer)var1.getValue(HATCH);
         if (var5 < 2) {
            var2.playSound((Player)null, var3, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.setBlock(var3, (BlockState)var1.setValue(HATCH, var5 + 1), 2);
         } else {
            var2.playSound((Player)null, var3, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.removeBlock(var3, false);

            for(int var6 = 0; var6 < (Integer)var1.getValue(EGGS); ++var6) {
               var2.levelEvent(2001, var3, Block.getId(var1));
               Turtle var7 = (Turtle)EntityType.TURTLE.create(var2);
               var7.setAge(-24000);
               var7.setHomePos(var3);
               var7.moveTo((double)var3.getX() + 0.3D + (double)var6 * 0.2D, (double)var3.getY(), (double)var3.getZ() + 0.3D, 0.0F, 0.0F);
               var2.addFreshEntity(var7);
            }
         }
      }

   }

   private boolean onSand(BlockGetter var1, BlockPos var2) {
      return var1.getBlockState(var2.below()).getBlock() == Blocks.SAND;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (this.onSand(var2, var3) && !var2.isClientSide) {
         var2.levelEvent(2005, var3, 0);
      }

   }

   private boolean shouldUpdateHatchLevel(Level var1) {
      float var2 = var1.getTimeOfDay(1.0F);
      if ((double)var2 < 0.69D && (double)var2 > 0.65D) {
         return true;
      } else {
         return var1.random.nextInt(500) == 0;
      }
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      this.decreaseEggs(var1, var3, var4);
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return var2.getItemInHand().getItem() == this.asItem() && (Integer)var1.getValue(EGGS) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      return var2.getBlock() == this ? (BlockState)var2.setValue(EGGS, Math.min(4, (Integer)var2.getValue(EGGS) + 1)) : super.getStateForPlacement(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Integer)var1.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(HATCH, EGGS);
   }

   private boolean canDestroyEgg(Level var1, Entity var2) {
      if (var2 instanceof Turtle) {
         return false;
      } else {
         return var2 instanceof LivingEntity && !(var2 instanceof Player) ? var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) : true;
      }
   }

   static {
      HATCH = BlockStateProperties.HATCH;
      EGGS = BlockStateProperties.EGGS;
   }
}
