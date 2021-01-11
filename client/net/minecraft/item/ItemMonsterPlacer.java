package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
   public ItemMonsterPlacer() {
      super();
      this.func_77627_a(true);
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public String func_77653_i(ItemStack var1) {
      String var2 = ("" + StatCollector.func_74838_a(this.func_77658_a() + ".name")).trim();
      String var3 = EntityList.func_75617_a(var1.func_77960_j());
      if (var3 != null) {
         var2 = var2 + " " + StatCollector.func_74838_a("entity." + var3 + ".name");
      }

      return var2;
   }

   public int func_82790_a(ItemStack var1, int var2) {
      EntityList.EntityEggInfo var3 = (EntityList.EntityEggInfo)EntityList.field_75627_a.get(var1.func_77960_j());
      if (var3 != null) {
         return var2 == 0 ? var3.field_75611_b : var3.field_75612_c;
      } else {
         return 16777215;
      }
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.field_72995_K) {
         return true;
      } else if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         if (var9.func_177230_c() == Blocks.field_150474_ac) {
            TileEntity var10 = var3.func_175625_s(var4);
            if (var10 instanceof TileEntityMobSpawner) {
               MobSpawnerBaseLogic var11 = ((TileEntityMobSpawner)var10).func_145881_a();
               var11.func_98272_a(EntityList.func_75617_a(var1.func_77960_j()));
               var10.func_70296_d();
               var3.func_175689_h(var4);
               if (!var2.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }

               return true;
            }
         }

         var4 = var4.func_177972_a(var5);
         double var13 = 0.0D;
         if (var5 == EnumFacing.UP && var9 instanceof BlockFence) {
            var13 = 0.5D;
         }

         Entity var12 = func_77840_a(var3, var1.func_77960_j(), (double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o() + var13, (double)var4.func_177952_p() + 0.5D);
         if (var12 != null) {
            if (var12 instanceof EntityLivingBase && var1.func_82837_s()) {
               var12.func_96094_a(var1.func_82833_r());
            }

            if (!var2.field_71075_bZ.field_75098_d) {
               --var1.field_77994_a;
            }
         }

         return true;
      }
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var2.field_72995_K) {
         return var1;
      } else {
         MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
         if (var4 == null) {
            return var1;
         } else {
            if (var4.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos var5 = var4.func_178782_a();
               if (!var2.func_175660_a(var3, var5)) {
                  return var1;
               }

               if (!var3.func_175151_a(var5, var4.field_178784_b, var1)) {
                  return var1;
               }

               if (var2.func_180495_p(var5).func_177230_c() instanceof BlockLiquid) {
                  Entity var6 = func_77840_a(var2, var1.func_77960_j(), (double)var5.func_177958_n() + 0.5D, (double)var5.func_177956_o() + 0.5D, (double)var5.func_177952_p() + 0.5D);
                  if (var6 != null) {
                     if (var6 instanceof EntityLivingBase && var1.func_82837_s()) {
                        ((EntityLiving)var6).func_96094_a(var1.func_82833_r());
                     }

                     if (!var3.field_71075_bZ.field_75098_d) {
                        --var1.field_77994_a;
                     }

                     var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
                  }
               }
            }

            return var1;
         }
      }
   }

   public static Entity func_77840_a(World var0, int var1, double var2, double var4, double var6) {
      if (!EntityList.field_75627_a.containsKey(var1)) {
         return null;
      } else {
         Entity var8 = null;

         for(int var9 = 0; var9 < 1; ++var9) {
            var8 = EntityList.func_75616_a(var1, var0);
            if (var8 instanceof EntityLivingBase) {
               EntityLiving var10 = (EntityLiving)var8;
               var8.func_70012_b(var2, var4, var6, MathHelper.func_76142_g(var0.field_73012_v.nextFloat() * 360.0F), 0.0F);
               var10.field_70759_as = var10.field_70177_z;
               var10.field_70761_aq = var10.field_70177_z;
               var10.func_180482_a(var0.func_175649_E(new BlockPos(var10)), (IEntityLivingData)null);
               var0.func_72838_d(var8);
               var10.func_70642_aH();
            }
         }

         return var8;
      }
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      Iterator var4 = EntityList.field_75627_a.values().iterator();

      while(var4.hasNext()) {
         EntityList.EntityEggInfo var5 = (EntityList.EntityEggInfo)var4.next();
         var3.add(new ItemStack(var1, 1, var5.field_75613_a));
      }

   }
}
