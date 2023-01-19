package net.minecraft.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final float AABB_OFFSET = 4.0F;
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   private final WoodType type;

   protected SignBlock(BlockBehaviour.Properties var1, WoodType var2) {
      super(var1);
      this.type = var2;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SignBlockEntity(var1, var2);
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      Item var8 = var7.getItem();
      boolean var9 = var8 instanceof DyeItem;
      boolean var10 = var7.is(Items.GLOW_INK_SAC);
      boolean var11 = var7.is(Items.INK_SAC);
      boolean var12 = (var10 || var9 || var11) && var4.getAbilities().mayBuild;
      if (var2.isClientSide) {
         return var12 ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         BlockEntity var13 = var2.getBlockEntity(var3);
         if (!(var13 instanceof SignBlockEntity)) {
            return InteractionResult.PASS;
         } else {
            SignBlockEntity var14 = (SignBlockEntity)var13;
            boolean var15 = var14.hasGlowingText();
            if ((!var10 || !var15) && (!var11 || var15)) {
               if (var12) {
                  boolean var16;
                  if (var10) {
                     var2.playSound(null, var3, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     var16 = var14.setHasGlowingText(true);
                     if (var4 instanceof ServerPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)var4, var3, var7);
                     }
                  } else if (var11) {
                     var2.playSound(null, var3, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     var16 = var14.setHasGlowingText(false);
                  } else {
                     var2.playSound(null, var3, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     var16 = var14.setColor(((DyeItem)var8).getDyeColor());
                  }

                  if (var16) {
                     if (!var4.isCreative()) {
                        var7.shrink(1);
                     }

                     var4.awardStat(Stats.ITEM_USED.get(var8));
                  }
               }

               return var14.executeClickCommands((ServerPlayer)var4) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            } else {
               return InteractionResult.PASS;
            }
         }
      }
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public WoodType type() {
      return this.type;
   }
}
