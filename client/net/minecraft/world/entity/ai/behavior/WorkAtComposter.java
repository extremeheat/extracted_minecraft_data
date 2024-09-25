package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WorkAtComposter extends WorkAtPoi {
   private static final List<Item> COMPOSTABLE_ITEMS = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS);

   public WorkAtComposter() {
      super();
   }

   @Override
   protected void useWorkstation(ServerLevel var1, Villager var2) {
      Optional var3 = var2.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (!var3.isEmpty()) {
         GlobalPos var4 = (GlobalPos)var3.get();
         BlockState var5 = var1.getBlockState(var4.pos());
         if (var5.is(Blocks.COMPOSTER)) {
            this.makeBread(var1, var2);
            this.compostItems(var1, var2, var4, var5);
         }
      }
   }

   private void compostItems(ServerLevel var1, Villager var2, GlobalPos var3, BlockState var4) {
      BlockPos var5 = var3.pos();
      if (var4.getValue(ComposterBlock.LEVEL) == 8) {
         var4 = ComposterBlock.extractProduce(var2, var4, var1, var5);
      }

      int var6 = 20;
      byte var7 = 10;
      int[] var8 = new int[COMPOSTABLE_ITEMS.size()];
      SimpleContainer var9 = var2.getInventory();
      int var10 = var9.getContainerSize();
      BlockState var11 = var4;

      for (int var12 = var10 - 1; var12 >= 0 && var6 > 0; var12--) {
         ItemStack var13 = var9.getItem(var12);
         int var14 = COMPOSTABLE_ITEMS.indexOf(var13.getItem());
         if (var14 != -1) {
            int var15 = var13.getCount();
            int var16 = var8[var14] + var15;
            var8[var14] = var16;
            int var17 = Math.min(Math.min(var16 - 10, var6), var15);
            if (var17 > 0) {
               var6 -= var17;

               for (int var18 = 0; var18 < var17; var18++) {
                  var11 = ComposterBlock.insertItem(var2, var11, var1, var13, var5);
                  if (var11.getValue(ComposterBlock.LEVEL) == 7) {
                     this.spawnComposterFillEffects(var1, var4, var5, var11);
                     return;
                  }
               }
            }
         }
      }

      this.spawnComposterFillEffects(var1, var4, var5, var11);
   }

   private void spawnComposterFillEffects(ServerLevel var1, BlockState var2, BlockPos var3, BlockState var4) {
      var1.levelEvent(1500, var3, var4 != var2 ? 1 : 0);
   }

   private void makeBread(ServerLevel var1, Villager var2) {
      SimpleContainer var3 = var2.getInventory();
      if (var3.countItem(Items.BREAD) <= 36) {
         int var4 = var3.countItem(Items.WHEAT);
         byte var5 = 3;
         byte var6 = 3;
         int var7 = Math.min(3, var4 / 3);
         if (var7 != 0) {
            int var8 = var7 * 3;
            var3.removeItemType(Items.WHEAT, var8);
            ItemStack var9 = var3.addItem(new ItemStack(Items.BREAD, var7));
            if (!var9.isEmpty()) {
               var2.spawnAtLocation(var1, var9, 0.5F);
            }
         }
      }
   }
}