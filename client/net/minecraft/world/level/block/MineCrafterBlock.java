package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.MineCrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.phys.BlockHitResult;

public class MineCrafterBlock extends BaseEntityBlock {
   public static final MapCodec<MineCrafterBlock> CODEC = simpleCodec(MineCrafterBlock::new);

   public MapCodec<MineCrafterBlock> codec() {
      return CODEC;
   }

   protected MineCrafterBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2 instanceof ServerLevel) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof MineCrafterBlockEntity) {
            MineCrafterBlockEntity var6 = (MineCrafterBlockEntity)var7;
            var4.openMenu(var6);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.has(DataComponents.WORLD_EFFECT_UNLOCK) && var1.has(DataComponents.WORLD_MODIFIERS)) {
         if (var3 instanceof ServerLevel) {
            ServerLevel var8 = (ServerLevel)var3;
            WorldModifiers var9 = (WorldModifiers)var1.get(DataComponents.WORLD_MODIFIERS);

            for(WorldEffect var11 : var9.effects()) {
               var8.unlockEffect(var11);
            }
         }

         var1.setCount(0);
         return InteractionResult.CONSUME;
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new MineCrafterBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.MINE_CRAFTER, MineCrafterBlockEntity::serverTick);
   }
}
