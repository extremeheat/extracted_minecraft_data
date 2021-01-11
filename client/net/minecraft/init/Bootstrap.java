package net.minecraft.init;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   private static final PrintStream field_179872_a;
   private static boolean field_151355_a;
   private static final Logger field_179871_c;

   public static boolean func_179869_a() {
      return field_151355_a;
   }

   static void func_151353_a() {
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151032_g, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2) {
            EntityArrow var3 = new EntityArrow(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
            var3.field_70251_a = 1;
            return var3;
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151110_aK, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2) {
            return new EntityEgg(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151126_ay, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2) {
            return new EntitySnowball(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151062_by, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2) {
            return new EntityExpBottle(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }

         protected float func_82498_a() {
            return super.func_82498_a() * 0.5F;
         }

         protected float func_82500_b() {
            return super.func_82500_b() * 1.25F;
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151068_bn, new IBehaviorDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82482_a(IBlockSource var1, final ItemStack var2) {
            return ItemPotion.func_77831_g(var2.func_77960_j()) ? (new BehaviorProjectileDispense() {
               protected IProjectile func_82499_a(World var1, IPosition var2x) {
                  return new EntityPotion(var1, var2x.func_82615_a(), var2x.func_82617_b(), var2x.func_82616_c(), var2.func_77946_l());
               }

               protected float func_82498_a() {
                  return super.func_82498_a() * 0.5F;
               }

               protected float func_82500_b() {
                  return super.func_82500_b() * 1.25F;
               }
            }).func_82482_a(var1, var2) : this.field_150843_b.func_82482_a(var1, var2);
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151063_bx, new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
            double var4 = var1.func_82615_a() + (double)var3.func_82601_c();
            double var6 = (double)((float)var1.func_180699_d().func_177956_o() + 0.2F);
            double var8 = var1.func_82616_c() + (double)var3.func_82599_e();
            Entity var10 = ItemMonsterPlacer.func_77840_a(var1.func_82618_k(), var2.func_77960_j(), var4, var6, var8);
            if (var10 instanceof EntityLivingBase && var2.func_82837_s()) {
               ((EntityLiving)var10).func_96094_a(var2.func_82833_r());
            }

            var2.func_77979_a(1);
            return var2;
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151152_bP, new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
            double var4 = var1.func_82615_a() + (double)var3.func_82601_c();
            double var6 = (double)((float)var1.func_180699_d().func_177956_o() + 0.2F);
            double var8 = var1.func_82616_c() + (double)var3.func_82599_e();
            EntityFireworkRocket var10 = new EntityFireworkRocket(var1.func_82618_k(), var4, var6, var8, var2);
            var1.func_82618_k().func_72838_d(var10);
            var2.func_77979_a(1);
            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            var1.func_82618_k().func_175718_b(1002, var1.func_180699_d(), 0);
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151059_bz, new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
            IPosition var4 = BlockDispenser.func_149939_a(var1);
            double var5 = var4.func_82615_a() + (double)((float)var3.func_82601_c() * 0.3F);
            double var7 = var4.func_82617_b() + (double)((float)var3.func_96559_d() * 0.3F);
            double var9 = var4.func_82616_c() + (double)((float)var3.func_82599_e() * 0.3F);
            World var11 = var1.func_82618_k();
            Random var12 = var11.field_73012_v;
            double var13 = var12.nextGaussian() * 0.05D + (double)var3.func_82601_c();
            double var15 = var12.nextGaussian() * 0.05D + (double)var3.func_96559_d();
            double var17 = var12.nextGaussian() * 0.05D + (double)var3.func_82599_e();
            var11.func_72838_d(new EntitySmallFireball(var11, var5, var7, var9, var13, var15, var17));
            var2.func_77979_a(1);
            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            var1.func_82618_k().func_175718_b(1009, var1.func_180699_d(), 0);
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151124_az, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
            World var4 = var1.func_82618_k();
            double var5 = var1.func_82615_a() + (double)((float)var3.func_82601_c() * 1.125F);
            double var7 = var1.func_82617_b() + (double)((float)var3.func_96559_d() * 1.125F);
            double var9 = var1.func_82616_c() + (double)((float)var3.func_82599_e() * 1.125F);
            BlockPos var11 = var1.func_180699_d().func_177972_a(var3);
            Material var12 = var4.func_180495_p(var11).func_177230_c().func_149688_o();
            double var13;
            if (Material.field_151586_h.equals(var12)) {
               var13 = 1.0D;
            } else {
               if (!Material.field_151579_a.equals(var12) || !Material.field_151586_h.equals(var4.func_180495_p(var11.func_177977_b()).func_177230_c().func_149688_o())) {
                  return this.field_150842_b.func_82482_a(var1, var2);
               }

               var13 = 0.0D;
            }

            EntityBoat var15 = new EntityBoat(var4, var5, var7 + var13, var9);
            var4.func_72838_d(var15);
            var2.func_77979_a(1);
            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
         }
      });
      BehaviorDefaultDispenseItem var0 = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            ItemBucket var3 = (ItemBucket)var2.func_77973_b();
            BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
            if (var3.func_180616_a(var1.func_82618_k(), var4)) {
               var2.func_150996_a(Items.field_151133_ar);
               var2.field_77994_a = 1;
               return var2;
            } else {
               return this.field_150841_b.func_82482_a(var1, var2);
            }
         }
      };
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151129_at, var0);
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151131_as, var0);
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151133_ar, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_82618_k();
            BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
            IBlockState var5 = var3.func_180495_p(var4);
            Block var6 = var5.func_177230_c();
            Material var7 = var6.func_149688_o();
            Item var8;
            if (Material.field_151586_h.equals(var7) && var6 instanceof BlockLiquid && (Integer)var5.func_177229_b(BlockLiquid.field_176367_b) == 0) {
               var8 = Items.field_151131_as;
            } else {
               if (!Material.field_151587_i.equals(var7) || !(var6 instanceof BlockLiquid) || (Integer)var5.func_177229_b(BlockLiquid.field_176367_b) != 0) {
                  return super.func_82487_b(var1, var2);
               }

               var8 = Items.field_151129_at;
            }

            var3.func_175698_g(var4);
            if (--var2.field_77994_a == 0) {
               var2.func_150996_a(var8);
               var2.field_77994_a = 1;
            } else if (((TileEntityDispenser)var1.func_150835_j()).func_146019_a(new ItemStack(var8)) < 0) {
               this.field_150840_b.func_82482_a(var1, new ItemStack(var8));
            }

            return var2;
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151033_d, new BehaviorDefaultDispenseItem() {
         private boolean field_150839_b = true;

         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_82618_k();
            BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
            if (var3.func_175623_d(var4)) {
               var3.func_175656_a(var4, Blocks.field_150480_ab.func_176223_P());
               if (var2.func_96631_a(1, var3.field_73012_v)) {
                  var2.field_77994_a = 0;
               }
            } else if (var3.func_180495_p(var4).func_177230_c() == Blocks.field_150335_W) {
               Blocks.field_150335_W.func_176206_d(var3, var4, Blocks.field_150335_W.func_176223_P().func_177226_a(BlockTNT.field_176246_a, true));
               var3.func_175698_g(var4);
            } else {
               this.field_150839_b = false;
            }

            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            if (this.field_150839_b) {
               var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
            } else {
               var1.func_82618_k().func_175718_b(1001, var1.func_180699_d(), 0);
            }

         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151100_aR, new BehaviorDefaultDispenseItem() {
         private boolean field_150838_b = true;

         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            if (EnumDyeColor.WHITE == EnumDyeColor.func_176766_a(var2.func_77960_j())) {
               World var3 = var1.func_82618_k();
               BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
               if (ItemDye.func_179234_a(var2, var3, var4)) {
                  if (!var3.field_72995_K) {
                     var3.func_175718_b(2005, var4, 0);
                  }
               } else {
                  this.field_150838_b = false;
               }

               return var2;
            } else {
               return super.func_82487_b(var1, var2);
            }
         }

         protected void func_82485_a(IBlockSource var1) {
            if (this.field_150838_b) {
               var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
            } else {
               var1.func_82618_k().func_175718_b(1001, var1.func_180699_d(), 0);
            }

         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Item.func_150898_a(Blocks.field_150335_W), new BehaviorDefaultDispenseItem() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_82618_k();
            BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
            EntityTNTPrimed var5 = new EntityTNTPrimed(var3, (double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o(), (double)var4.func_177952_p() + 0.5D, (EntityLivingBase)null);
            var3.func_72838_d(var5);
            var3.func_72956_a(var5, "game.tnt.primed", 1.0F, 1.0F);
            --var2.field_77994_a;
            return var2;
         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151144_bL, new BehaviorDefaultDispenseItem() {
         private boolean field_179240_b = true;

         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_82618_k();
            EnumFacing var4 = BlockDispenser.func_149937_b(var1.func_82620_h());
            BlockPos var5 = var1.func_180699_d().func_177972_a(var4);
            BlockSkull var6 = Blocks.field_150465_bP;
            if (var3.func_175623_d(var5) && var6.func_176415_b(var3, var5, var2)) {
               if (!var3.field_72995_K) {
                  var3.func_180501_a(var5, var6.func_176223_P().func_177226_a(BlockSkull.field_176418_a, EnumFacing.UP), 3);
                  TileEntity var7 = var3.func_175625_s(var5);
                  if (var7 instanceof TileEntitySkull) {
                     if (var2.func_77960_j() == 3) {
                        GameProfile var8 = null;
                        if (var2.func_77942_o()) {
                           NBTTagCompound var9 = var2.func_77978_p();
                           if (var9.func_150297_b("SkullOwner", 10)) {
                              var8 = NBTUtil.func_152459_a(var9.func_74775_l("SkullOwner"));
                           } else if (var9.func_150297_b("SkullOwner", 8)) {
                              String var10 = var9.func_74779_i("SkullOwner");
                              if (!StringUtils.func_151246_b(var10)) {
                                 var8 = new GameProfile((UUID)null, var10);
                              }
                           }
                        }

                        ((TileEntitySkull)var7).func_152106_a(var8);
                     } else {
                        ((TileEntitySkull)var7).func_152107_a(var2.func_77960_j());
                     }

                     ((TileEntitySkull)var7).func_145903_a(var4.func_176734_d().func_176736_b() * 4);
                     Blocks.field_150465_bP.func_180679_a(var3, var5, (TileEntitySkull)var7);
                  }

                  --var2.field_77994_a;
               }
            } else {
               this.field_179240_b = false;
            }

            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            if (this.field_179240_b) {
               var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
            } else {
               var1.func_82618_k().func_175718_b(1001, var1.func_180699_d(), 0);
            }

         }
      });
      BlockDispenser.field_149943_a.func_82595_a(Item.func_150898_a(Blocks.field_150423_aK), new BehaviorDefaultDispenseItem() {
         private boolean field_179241_b = true;

         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_82618_k();
            BlockPos var4 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
            BlockPumpkin var5 = (BlockPumpkin)Blocks.field_150423_aK;
            if (var3.func_175623_d(var4) && var5.func_176390_d(var3, var4)) {
               if (!var3.field_72995_K) {
                  var3.func_180501_a(var4, var5.func_176223_P(), 3);
               }

               --var2.field_77994_a;
            } else {
               this.field_179241_b = false;
            }

            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            if (this.field_179241_b) {
               var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
            } else {
               var1.func_82618_k().func_175718_b(1001, var1.func_180699_d(), 0);
            }

         }
      });
   }

   public static void func_151354_b() {
      if (!field_151355_a) {
         field_151355_a = true;
         if (field_179871_c.isDebugEnabled()) {
            func_179868_d();
         }

         Block.func_149671_p();
         BlockFire.func_149843_e();
         Item.func_150900_l();
         StatList.func_151178_a();
         func_151353_a();
      }
   }

   private static void func_179868_d() {
      System.setErr(new LoggingPrintStream("STDERR", System.err));
      System.setOut(new LoggingPrintStream("STDOUT", field_179872_a));
   }

   public static void func_179870_a(String var0) {
      field_179872_a.println(var0);
   }

   static {
      field_179872_a = System.out;
      field_151355_a = false;
      field_179871_c = LogManager.getLogger();
   }
}
