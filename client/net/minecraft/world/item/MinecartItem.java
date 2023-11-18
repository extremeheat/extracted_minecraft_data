package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MinecartItem extends Item {
   private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      @Override
      public ItemStack execute(BlockSource var1, ItemStack var2) {
         Direction var3 = var1.state().getValue(DispenserBlock.FACING);
         ServerLevel var4 = var1.level();
         Vec3 var5 = var1.center();
         double var6 = var5.x() + (double)var3.getStepX() * 1.125;
         double var8 = Math.floor(var5.y()) + (double)var3.getStepY();
         double var10 = var5.z() + (double)var3.getStepZ() * 1.125;
         BlockPos var12 = var1.pos().relative(var3);
         BlockState var13 = var4.getBlockState(var12);
         RailShape var14 = var13.getBlock() instanceof BaseRailBlock
            ? var13.getValue(((BaseRailBlock)var13.getBlock()).getShapeProperty())
            : RailShape.NORTH_SOUTH;
         double var15;
         if (var13.is(BlockTags.RAILS)) {
            if (var14.isAscending()) {
               var15 = 0.6;
            } else {
               var15 = 0.1;
            }
         } else {
            if (!var13.isAir() || !var4.getBlockState(var12.below()).is(BlockTags.RAILS)) {
               return this.defaultDispenseItemBehavior.dispense(var1, var2);
            }

            BlockState var17 = var4.getBlockState(var12.below());
            RailShape var18 = var17.getBlock() instanceof BaseRailBlock
               ? var17.getValue(((BaseRailBlock)var17.getBlock()).getShapeProperty())
               : RailShape.NORTH_SOUTH;
            if (var3 != Direction.DOWN && var18.isAscending()) {
               var15 = -0.4;
            } else {
               var15 = -0.9;
            }
         }

         AbstractMinecart var19 = AbstractMinecart.createMinecart(var4, var6, var8 + var15, var10, ((MinecartItem)var2.getItem()).type);
         if (var2.hasCustomHoverName()) {
            var19.setCustomName(var2.getHoverName());
         }

         var4.addFreshEntity(var19);
         var2.shrink(1);
         return var2;
      }

      @Override
      protected void playSound(BlockSource var1) {
         var1.level().levelEvent(1000, var1.pos(), 0);
      }
   };
   final AbstractMinecart.Type type;

   public MinecartItem(AbstractMinecart.Type var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.is(BlockTags.RAILS)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack var5 = var1.getItemInHand();
         if (!var2.isClientSide) {
            RailShape var6 = var4.getBlock() instanceof BaseRailBlock
               ? var4.getValue(((BaseRailBlock)var4.getBlock()).getShapeProperty())
               : RailShape.NORTH_SOUTH;
            double var7 = 0.0;
            if (var6.isAscending()) {
               var7 = 0.5;
            }

            AbstractMinecart var9 = AbstractMinecart.createMinecart(
               var2, (double)var3.getX() + 0.5, (double)var3.getY() + 0.0625 + var7, (double)var3.getZ() + 0.5, this.type
            );
            if (var5.hasCustomHoverName()) {
               var9.setCustomName(var5.getHoverName());
            }

            var2.addFreshEntity(var9);
            var2.gameEvent(GameEvent.ENTITY_PLACE, var3, GameEvent.Context.of(var1.getPlayer(), var2.getBlockState(var3.below())));
         }

         var5.shrink(1);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }
}
