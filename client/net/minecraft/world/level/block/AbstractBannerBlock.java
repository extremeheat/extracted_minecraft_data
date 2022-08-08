package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
   private final DyeColor color;

   protected AbstractBannerBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BannerBlockEntity(var1, var2, this.color);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      if (var1.isClientSide) {
         var1.getBlockEntity(var2, BlockEntityType.BANNER).ifPresent((var1x) -> {
            var1x.fromItem(var5);
         });
      } else if (var5.hasCustomHoverName()) {
         var1.getBlockEntity(var2, BlockEntityType.BANNER).ifPresent((var1x) -> {
            var1x.setCustomName(var5.getHoverName());
         });
      }

   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      return var4 instanceof BannerBlockEntity ? ((BannerBlockEntity)var4).getItem() : super.getCloneItemStack(var1, var2, var3);
   }

   public DyeColor getColor() {
      return this.color;
   }
}
