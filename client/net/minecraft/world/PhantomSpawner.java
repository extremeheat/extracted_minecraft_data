package net.minecraft.world;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PhantomSpawner {
   private int field_203233_a;

   public PhantomSpawner() {
      super();
   }

   public int func_203232_a(World var1, boolean var2, boolean var3) {
      if (!var2) {
         return 0;
      } else {
         Random var4 = var1.field_73012_v;
         --this.field_203233_a;
         if (this.field_203233_a > 0) {
            return 0;
         } else {
            this.field_203233_a += (60 + var4.nextInt(60)) * 20;
            if (var1.func_175657_ab() < 5 && var1.field_73011_w.func_191066_m()) {
               return 0;
            } else {
               int var5 = 0;
               Iterator var6 = var1.field_73010_i.iterator();

               while(true) {
                  DifficultyInstance var9;
                  BlockPos var13;
                  IBlockState var14;
                  IFluidState var15;
                  do {
                     BlockPos var8;
                     int var11;
                     do {
                        EntityPlayer var7;
                        do {
                           do {
                              do {
                                 if (!var6.hasNext()) {
                                    return var5;
                                 }

                                 var7 = (EntityPlayer)var6.next();
                              } while(var7.func_175149_v());

                              var8 = new BlockPos(var7);
                           } while(var1.field_73011_w.func_191066_m() && (var8.func_177956_o() < var1.func_181545_F() || !var1.func_175678_i(var8)));

                           var9 = var1.func_175649_E(var8);
                        } while(!var9.func_193845_a(var4.nextFloat() * 3.0F));

                        StatisticsManagerServer var10 = ((EntityPlayerMP)var7).func_147099_x();
                        var11 = MathHelper.func_76125_a(var10.func_77444_a(StatList.field_199092_j.func_199076_b(StatList.field_203284_n)), 1, 2147483647);
                        boolean var12 = true;
                     } while(var4.nextInt(var11) < 72000);

                     var13 = var8.func_177981_b(20 + var4.nextInt(15)).func_177965_g(-10 + var4.nextInt(21)).func_177970_e(-10 + var4.nextInt(21));
                     var14 = var1.func_180495_p(var13);
                     var15 = var1.func_204610_c(var13);
                  } while(!WorldEntitySpawner.func_206851_a(var14, var15));

                  IEntityLivingData var16 = null;
                  int var17 = 1 + var4.nextInt(var9.func_203095_a().func_151525_a() + 1);

                  for(int var18 = 0; var18 < var17; ++var18) {
                     EntityPhantom var19 = new EntityPhantom(var1);
                     var19.func_174828_a(var13, 0.0F, 0.0F);
                     var16 = var19.func_204210_a(var9, var16, (NBTTagCompound)null);
                     var1.func_72838_d(var19);
                  }

                  var5 += var17;
               }
            }
         }
      }
   }
}
