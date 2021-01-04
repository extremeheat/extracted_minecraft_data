package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
   public BeaconBlock(Block.Properties var1) {
      super(var1);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new BeaconBlockEntity();
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof BeaconBlockEntity) {
            var4.openMenu((BeaconBlockEntity)var7);
            var4.awardStat(Stats.INTERACT_WITH_BEACON);
         }

         return true;
      }
   }

   public boolean isRedstoneConductor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof BeaconBlockEntity) {
            ((BeaconBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
