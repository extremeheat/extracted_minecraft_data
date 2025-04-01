package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MineRevisitorBlock extends Block {
   public static final MapCodec<MineRevisitorBlock> CODEC = simpleCodec(MineRevisitorBlock::new);

   public MapCodec<? extends MineRevisitorBlock> codec() {
      return CODEC;
   }

   protected MineRevisitorBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.has(DataComponents.MINE_COMPLETED) && var1.has(DataComponents.DIMENSION_ID)) {
         ResourceKey var8 = (ResourceKey)var1.get(DataComponents.DIMENSION_ID);
         if (var8 != null) {
            MineTravellingBlock.createBlock(var3, var4.above(), var8, true);
            var1.consume(1, var5);
            if (var5 instanceof ServerPlayer) {
               ServerPlayer var9 = (ServerPlayer)var5;
               CriteriaTriggers.MINE_REVISITOR_ACTIVATED.trigger(var9);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
   }
}
