package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class MinecartDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final EntityType<? extends AbstractMinecart> entityType;

   public MinecartDispenseItemBehavior(EntityType<? extends AbstractMinecart> var1) {
      super();
      this.entityType = var1;
   }

   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = (Direction)var1.state().getValue(DispenserBlock.FACING);
      ServerLevel var4 = var1.level();
      Vec3 var5 = var1.center();
      double var6 = var5.x() + (double)var3.getStepX() * 1.125;
      double var8 = Math.floor(var5.y()) + (double)var3.getStepY();
      double var10 = var5.z() + (double)var3.getStepZ() * 1.125;
      BlockPos var12 = var1.pos().relative(var3);
      BlockState var13 = var4.getBlockState(var12);
      double var14;
      if (var13.is(BlockTags.RAILS)) {
         if (getRailShape(var13).isSlope()) {
            var14 = 0.6;
         } else {
            var14 = 0.1;
         }
      } else {
         if (!var13.isAir()) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         BlockState var16 = var4.getBlockState(var12.below());
         if (!var16.is(BlockTags.RAILS)) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         if (var3 != Direction.DOWN && getRailShape(var16).isSlope()) {
            var14 = -0.4;
         } else {
            var14 = -0.9;
         }
      }

      Vec3 var18 = new Vec3(var6, var8 + var14, var10);
      AbstractMinecart var17 = AbstractMinecart.createMinecart(var4, var18.x, var18.y, var18.z, this.entityType, EntitySpawnReason.DISPENSER, var2, (Player)null);
      if (var17 != null) {
         var4.addFreshEntity(var17);
         var2.shrink(1);
      }

      return var2;
   }

   private static RailShape getRailShape(BlockState var0) {
      Block var2 = var0.getBlock();
      RailShape var10000;
      if (var2 instanceof BaseRailBlock var1) {
         var10000 = (RailShape)var0.getValue(var1.getShapeProperty());
      } else {
         var10000 = RailShape.NORTH_SOUTH;
      }

      return var10000;
   }

   protected void playSound(BlockSource var1) {
      var1.level().levelEvent(1000, var1.pos(), 0);
   }
}
