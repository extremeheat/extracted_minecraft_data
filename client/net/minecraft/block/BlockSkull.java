package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   protected BlockSkull() {
      super(Material.field_151594_q);
      this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public int func_149645_b() {
      return -1;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) & 7;
      switch(var5) {
         case 1:
         default:
            this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case 2:
            this.func_149676_a(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case 3:
            this.func_149676_a(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case 4:
            this.func_149676_a(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case 5:
            this.func_149676_a(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 2.5) & 3;
      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntitySkull();
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151144_bL;
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      TileEntity var5 = var1.func_147438_o(var2, var3, var4);
      return var5 != null && var5 instanceof TileEntitySkull ? ((TileEntitySkull)var5).func_145904_a() : super.func_149643_k(var1, var2, var3, var4);
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (var6.field_71075_bZ.field_75098_d) {
         var5 |= 8;
         var1.func_72921_c(var2, var3, var4, var5, 4);
      }

      super.func_149681_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if (!var1.field_72995_K) {
         if ((var6 & 8) == 0) {
            ItemStack var7 = new ItemStack(Items.field_151144_bL, 1, this.func_149643_k(var1, var2, var3, var4));
            TileEntitySkull var8 = (TileEntitySkull)var1.func_147438_o(var2, var3, var4);
            if (var8.func_145904_a() == 3 && var8.func_152108_a() != null) {
               var7.func_77982_d(new NBTTagCompound());
               NBTTagCompound var9 = new NBTTagCompound();
               NBTUtil.func_152460_a(var9, var8.func_152108_a());
               var7.func_77978_p().func_74782_a("SkullOwner", var9);
            }

            this.func_149642_a(var1, var2, var3, var4, var7);
         }

         super.func_149749_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151144_bL;
   }

   public void func_149965_a(World var1, int var2, int var3, int var4, TileEntitySkull var5) {
      if (var5.func_145904_a() == 1 && var3 >= 2 && var1.field_73013_u != EnumDifficulty.PEACEFUL && !var1.field_72995_K) {
         for(int var6 = -2; var6 <= 0; ++var6) {
            if (var1.func_147439_a(var2, var3 - 1, var4 + var6) == Blocks.field_150425_aM
               && var1.func_147439_a(var2, var3 - 1, var4 + var6 + 1) == Blocks.field_150425_aM
               && var1.func_147439_a(var2, var3 - 2, var4 + var6 + 1) == Blocks.field_150425_aM
               && var1.func_147439_a(var2, var3 - 1, var4 + var6 + 2) == Blocks.field_150425_aM
               && this.func_149966_a(var1, var2, var3, var4 + var6, 1)
               && this.func_149966_a(var1, var2, var3, var4 + var6 + 1, 1)
               && this.func_149966_a(var1, var2, var3, var4 + var6 + 2, 1)) {
               var1.func_72921_c(var2, var3, var4 + var6, 8, 2);
               var1.func_72921_c(var2, var3, var4 + var6 + 1, 8, 2);
               var1.func_72921_c(var2, var3, var4 + var6 + 2, 8, 2);
               var1.func_147465_d(var2, var3, var4 + var6, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3, var4 + var6 + 1, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3, var4 + var6 + 2, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3 - 1, var4 + var6, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3 - 1, var4 + var6 + 1, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3 - 1, var4 + var6 + 2, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3 - 2, var4 + var6 + 1, func_149729_e(0), 0, 2);
               if (!var1.field_72995_K) {
                  EntityWither var7 = new EntityWither(var1);
                  var7.func_70012_b((double)var2 + 0.5, (double)var3 - 1.45, (double)(var4 + var6) + 1.5, 90.0F, 0.0F);
                  var7.field_70761_aq = 90.0F;
                  var7.func_82206_m();
                  if (!var1.field_72995_K) {
                     for(EntityPlayer var9 : var1.func_72872_a(EntityPlayer.class, var7.field_70121_D.func_72314_b(50.0, 50.0, 50.0))) {
                        var9.func_71029_a(AchievementList.field_150963_I);
                     }
                  }

                  var1.func_72838_d(var7);
               }

               for(int var11 = 0; var11 < 120; ++var11) {
                  var1.func_72869_a(
                     "snowballpoof",
                     (double)var2 + var1.field_73012_v.nextDouble(),
                     (double)(var3 - 2) + var1.field_73012_v.nextDouble() * 3.9,
                     (double)(var4 + var6 + 1) + var1.field_73012_v.nextDouble(),
                     0.0,
                     0.0,
                     0.0
                  );
               }

               var1.func_147444_c(var2, var3, var4 + var6, func_149729_e(0));
               var1.func_147444_c(var2, var3, var4 + var6 + 1, func_149729_e(0));
               var1.func_147444_c(var2, var3, var4 + var6 + 2, func_149729_e(0));
               var1.func_147444_c(var2, var3 - 1, var4 + var6, func_149729_e(0));
               var1.func_147444_c(var2, var3 - 1, var4 + var6 + 1, func_149729_e(0));
               var1.func_147444_c(var2, var3 - 1, var4 + var6 + 2, func_149729_e(0));
               var1.func_147444_c(var2, var3 - 2, var4 + var6 + 1, func_149729_e(0));
               return;
            }
         }

         for(int var10 = -2; var10 <= 0; ++var10) {
            if (var1.func_147439_a(var2 + var10, var3 - 1, var4) == Blocks.field_150425_aM
               && var1.func_147439_a(var2 + var10 + 1, var3 - 1, var4) == Blocks.field_150425_aM
               && var1.func_147439_a(var2 + var10 + 1, var3 - 2, var4) == Blocks.field_150425_aM
               && var1.func_147439_a(var2 + var10 + 2, var3 - 1, var4) == Blocks.field_150425_aM
               && this.func_149966_a(var1, var2 + var10, var3, var4, 1)
               && this.func_149966_a(var1, var2 + var10 + 1, var3, var4, 1)
               && this.func_149966_a(var1, var2 + var10 + 2, var3, var4, 1)) {
               var1.func_72921_c(var2 + var10, var3, var4, 8, 2);
               var1.func_72921_c(var2 + var10 + 1, var3, var4, 8, 2);
               var1.func_72921_c(var2 + var10 + 2, var3, var4, 8, 2);
               var1.func_147465_d(var2 + var10, var3, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10 + 1, var3, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10 + 2, var3, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10, var3 - 1, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10 + 1, var3 - 1, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10 + 2, var3 - 1, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + var10 + 1, var3 - 2, var4, func_149729_e(0), 0, 2);
               if (!var1.field_72995_K) {
                  EntityWither var12 = new EntityWither(var1);
                  var12.func_70012_b((double)(var2 + var10) + 1.5, (double)var3 - 1.45, (double)var4 + 0.5, 0.0F, 0.0F);
                  var12.func_82206_m();
                  if (!var1.field_72995_K) {
                     for(EntityPlayer var15 : var1.func_72872_a(EntityPlayer.class, var12.field_70121_D.func_72314_b(50.0, 50.0, 50.0))) {
                        var15.func_71029_a(AchievementList.field_150963_I);
                     }
                  }

                  var1.func_72838_d(var12);
               }

               for(int var13 = 0; var13 < 120; ++var13) {
                  var1.func_72869_a(
                     "snowballpoof",
                     (double)(var2 + var10 + 1) + var1.field_73012_v.nextDouble(),
                     (double)(var3 - 2) + var1.field_73012_v.nextDouble() * 3.9,
                     (double)var4 + var1.field_73012_v.nextDouble(),
                     0.0,
                     0.0,
                     0.0
                  );
               }

               var1.func_147444_c(var2 + var10, var3, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10 + 1, var3, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10 + 2, var3, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10, var3 - 1, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10 + 1, var3 - 1, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10 + 2, var3 - 1, var4, func_149729_e(0));
               var1.func_147444_c(var2 + var10 + 1, var3 - 2, var4, func_149729_e(0));
               return;
            }
         }
      }
   }

   private boolean func_149966_a(World var1, int var2, int var3, int var4, int var5) {
      if (var1.func_147439_a(var2, var3, var4) != this) {
         return false;
      } else {
         TileEntity var6 = var1.func_147438_o(var2, var3, var4);
         if (var6 != null && var6 instanceof TileEntitySkull) {
            return ((TileEntitySkull)var6).func_145904_a() == var5;
         } else {
            return false;
         }
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150425_aM.func_149733_h(var1);
   }

   @Override
   public String func_149702_O() {
      return this.func_149641_N() + "_" + ItemSkull.field_94587_a[0];
   }
}
