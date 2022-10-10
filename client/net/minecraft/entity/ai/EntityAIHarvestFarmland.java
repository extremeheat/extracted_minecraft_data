package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   private final EntityVillager field_179504_c;
   private boolean field_179502_d;
   private boolean field_179503_e;
   private int field_179501_f;

   public EntityAIHarvestFarmland(EntityVillager var1, double var2) {
      super(var1, var2, 16);
      this.field_179504_c = var1;
   }

   public boolean func_75250_a() {
      if (this.field_179496_a <= 0) {
         if (!this.field_179504_c.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         }

         this.field_179501_f = -1;
         this.field_179502_d = this.field_179504_c.func_175556_cs();
         this.field_179503_e = this.field_179504_c.func_175557_cr();
      }

      return super.func_75250_a();
   }

   public boolean func_75253_b() {
      return this.field_179501_f >= 0 && super.func_75253_b();
   }

   public void func_75246_d() {
      super.func_75246_d();
      this.field_179504_c.func_70671_ap().func_75650_a((double)this.field_179494_b.func_177958_n() + 0.5D, (double)(this.field_179494_b.func_177956_o() + 1), (double)this.field_179494_b.func_177952_p() + 0.5D, 10.0F, (float)this.field_179504_c.func_70646_bf());
      if (this.func_179487_f()) {
         World var1 = this.field_179504_c.field_70170_p;
         BlockPos var2 = this.field_179494_b.func_177984_a();
         IBlockState var3 = var1.func_180495_p(var2);
         Block var4 = var3.func_177230_c();
         if (this.field_179501_f == 0 && var4 instanceof BlockCrops && ((BlockCrops)var4).func_185525_y(var3)) {
            var1.func_175655_b(var2, true);
         } else if (this.field_179501_f == 1 && var3.func_196958_f()) {
            InventoryBasic var5 = this.field_179504_c.func_175551_co();

            for(int var6 = 0; var6 < var5.func_70302_i_(); ++var6) {
               ItemStack var7 = var5.func_70301_a(var6);
               boolean var8 = false;
               if (!var7.func_190926_b()) {
                  if (var7.func_77973_b() == Items.field_151014_N) {
                     var1.func_180501_a(var2, Blocks.field_150464_aj.func_176223_P(), 3);
                     var8 = true;
                  } else if (var7.func_77973_b() == Items.field_151174_bG) {
                     var1.func_180501_a(var2, Blocks.field_150469_bN.func_176223_P(), 3);
                     var8 = true;
                  } else if (var7.func_77973_b() == Items.field_151172_bF) {
                     var1.func_180501_a(var2, Blocks.field_150459_bM.func_176223_P(), 3);
                     var8 = true;
                  } else if (var7.func_77973_b() == Items.field_185163_cU) {
                     var1.func_180501_a(var2, Blocks.field_185773_cZ.func_176223_P(), 3);
                     var8 = true;
                  }
               }

               if (var8) {
                  var7.func_190918_g(1);
                  if (var7.func_190926_b()) {
                     var5.func_70299_a(var6, ItemStack.field_190927_a);
                  }
                  break;
               }
            }
         }

         this.field_179501_f = -1;
         this.field_179496_a = 10;
      }

   }

   protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2).func_177230_c();
      if (var3 == Blocks.field_150458_ak) {
         var2 = var2.func_177984_a();
         IBlockState var4 = var1.func_180495_p(var2);
         var3 = var4.func_177230_c();
         if (var3 instanceof BlockCrops && ((BlockCrops)var3).func_185525_y(var4) && this.field_179503_e && (this.field_179501_f == 0 || this.field_179501_f < 0)) {
            this.field_179501_f = 0;
            return true;
         }

         if (var4.func_196958_f() && this.field_179502_d && (this.field_179501_f == 1 || this.field_179501_f < 0)) {
            this.field_179501_f = 1;
            return true;
         }
      }

      return false;
   }
}
