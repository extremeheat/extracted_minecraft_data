package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartItem extends Item {
   private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      public ItemStack execute(BlockSource var1, ItemStack var2) {
         Direction var3 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
         ServerLevel var4 = var1.getLevel();
         double var5 = var1.x() + (double)var3.getStepX() * 1.125D;
         double var7 = Math.floor(var1.y()) + (double)var3.getStepY();
         double var9 = var1.z() + (double)var3.getStepZ() * 1.125D;
         BlockPos var11 = var1.getPos().relative(var3);
         BlockState var12 = var4.getBlockState(var11);
         RailShape var13 = var12.getBlock() instanceof BaseRailBlock ? (RailShape)var12.getValue(((BaseRailBlock)var12.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double var14;
         if (var12.is(BlockTags.RAILS)) {
            if (var13.isAscending()) {
               var14 = 0.6D;
            } else {
               var14 = 0.1D;
            }
         } else {
            if (!var12.isAir() || !var4.getBlockState(var11.below()).is(BlockTags.RAILS)) {
               return this.defaultDispenseItemBehavior.dispense(var1, var2);
            }

            BlockState var16 = var4.getBlockState(var11.below());
            RailShape var17 = var16.getBlock() instanceof BaseRailBlock ? (RailShape)var16.getValue(((BaseRailBlock)var16.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (var3 != Direction.DOWN && var17.isAscending()) {
               var14 = -0.4D;
            } else {
               var14 = -0.9D;
            }
         }

         AbstractMinecart var18 = AbstractMinecart.createMinecart(var4, var5, var7 + var14, var9, ((MinecartItem)var2.getItem()).type);
         if (var2.hasCustomHoverName()) {
            var18.setCustomName(var2.getHoverName());
         }

         var4.addFreshEntity(var18);
         var2.shrink(1);
         return var2;
      }

      protected void playSound(BlockSource var1) {
         var1.getLevel().levelEvent(1000, var1.getPos(), 0);
      }
   };
   private final AbstractMinecart.Type type;

   public MinecartItem(AbstractMinecart.Type var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.is(BlockTags.RAILS)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack var5 = var1.getItemInHand();
         if (!var2.isClientSide) {
            RailShape var6 = var4.getBlock() instanceof BaseRailBlock ? (RailShape)var4.getValue(((BaseRailBlock)var4.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double var7 = 0.0D;
            if (var6.isAscending()) {
               var7 = 0.5D;
            }

            AbstractMinecart var9 = AbstractMinecart.createMinecart(var2, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.0625D + var7, (double)var3.getZ() + 0.5D, this.type);
            if (var5.hasCustomHoverName()) {
               var9.setCustomName(var5.getHoverName());
            }

            var2.addFreshEntity(var9);
         }

         var5.shrink(1);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }
}
