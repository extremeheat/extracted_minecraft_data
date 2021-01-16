package net.minecraft.world.item.enchantment;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.ARMOR_FEET, var2);
   }

   public int getMinCost(int var1) {
      return var1 * 10;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 15;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 2;
   }

   public static void onEntityMoved(LivingEntity var0, Level var1, BlockPos var2, int var3) {
      if (var0.isOnGround()) {
         BlockState var4 = Blocks.FROSTED_ICE.defaultBlockState();
         float var5 = (float)Math.min(16, 2 + var3);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
         Iterator var7 = BlockPos.betweenClosed(var2.offset((double)(-var5), -1.0D, (double)(-var5)), var2.offset((double)var5, -1.0D, (double)var5)).iterator();

         while(var7.hasNext()) {
            BlockPos var8 = (BlockPos)var7.next();
            if (var8.closerThan(var0.position(), (double)var5)) {
               var6.set(var8.getX(), var8.getY() + 1, var8.getZ());
               BlockState var9 = var1.getBlockState(var6);
               if (var9.isAir()) {
                  BlockState var10 = var1.getBlockState(var8);
                  if (var10.getMaterial() == Material.WATER && (Integer)var10.getValue(LiquidBlock.LEVEL) == 0 && var4.canSurvive(var1, var8) && var1.isUnobstructed(var4, var8, CollisionContext.empty())) {
                     var1.setBlockAndUpdate(var8, var4);
                     var1.getBlockTicks().scheduleTick(var8, Blocks.FROSTED_ICE, Mth.nextInt(var0.getRandom(), 60, 120));
                  }
               }
            }
         }

      }
   }

   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.DEPTH_STRIDER;
   }
}
