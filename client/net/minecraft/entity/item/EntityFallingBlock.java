package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
   private IBlockState field_175132_d;
   public int field_145812_b;
   public boolean field_145813_c = true;
   private boolean field_145808_f;
   private boolean field_145809_g;
   private int field_145815_h = 40;
   private float field_145816_i = 2.0F;
   public NBTTagCompound field_145810_d;

   public EntityFallingBlock(World var1) {
      super(var1);
   }

   public EntityFallingBlock(World var1, double var2, double var4, double var6, IBlockState var8) {
      super(var1);
      this.field_175132_d = var8;
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.98F);
      this.func_70107_b(var2, var4, var6);
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public void func_70071_h_() {
      Block var1 = this.field_175132_d.func_177230_c();
      if (var1.func_149688_o() == Material.field_151579_a) {
         this.func_70106_y();
      } else {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         BlockPos var2;
         if (this.field_145812_b++ == 0) {
            var2 = new BlockPos(this);
            if (this.field_70170_p.func_180495_p(var2).func_177230_c() == var1) {
               this.field_70170_p.func_175698_g(var2);
            } else if (!this.field_70170_p.field_72995_K) {
               this.func_70106_y();
               return;
            }
         }

         this.field_70181_x -= 0.03999999910593033D;
         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.9800000190734863D;
         this.field_70181_x *= 0.9800000190734863D;
         this.field_70179_y *= 0.9800000190734863D;
         if (!this.field_70170_p.field_72995_K) {
            var2 = new BlockPos(this);
            if (this.field_70122_E) {
               this.field_70159_w *= 0.699999988079071D;
               this.field_70179_y *= 0.699999988079071D;
               this.field_70181_x *= -0.5D;
               if (this.field_70170_p.func_180495_p(var2).func_177230_c() != Blocks.field_180384_M) {
                  this.func_70106_y();
                  if (!this.field_145808_f) {
                     if (this.field_70170_p.func_175716_a(var1, var2, true, EnumFacing.UP, (Entity)null, (ItemStack)null) && !BlockFalling.func_180685_d(this.field_70170_p, var2.func_177977_b()) && this.field_70170_p.func_180501_a(var2, this.field_175132_d, 3)) {
                        if (var1 instanceof BlockFalling) {
                           ((BlockFalling)var1).func_176502_a_(this.field_70170_p, var2);
                        }

                        if (this.field_145810_d != null && var1 instanceof ITileEntityProvider) {
                           TileEntity var3 = this.field_70170_p.func_175625_s(var2);
                           if (var3 != null) {
                              NBTTagCompound var4 = new NBTTagCompound();
                              var3.func_145841_b(var4);
                              Iterator var5 = this.field_145810_d.func_150296_c().iterator();

                              while(var5.hasNext()) {
                                 String var6 = (String)var5.next();
                                 NBTBase var7 = this.field_145810_d.func_74781_a(var6);
                                 if (!var6.equals("x") && !var6.equals("y") && !var6.equals("z")) {
                                    var4.func_74782_a(var6, var7.func_74737_b());
                                 }
                              }

                              var3.func_145839_a(var4);
                              var3.func_70296_d();
                           }
                        }
                     } else if (this.field_145813_c && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                        this.func_70099_a(new ItemStack(var1, 1, var1.func_180651_a(this.field_175132_d)), 0.0F);
                     }
                  }
               }
            } else if (this.field_145812_b > 100 && !this.field_70170_p.field_72995_K && (var2.func_177956_o() < 1 || var2.func_177956_o() > 256) || this.field_145812_b > 600) {
               if (this.field_145813_c && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                  this.func_70099_a(new ItemStack(var1, 1, var1.func_180651_a(this.field_175132_d)), 0.0F);
               }

               this.func_70106_y();
            }
         }

      }
   }

   public void func_180430_e(float var1, float var2) {
      Block var3 = this.field_175132_d.func_177230_c();
      if (this.field_145809_g) {
         int var4 = MathHelper.func_76123_f(var1 - 1.0F);
         if (var4 > 0) {
            ArrayList var5 = Lists.newArrayList(this.field_70170_p.func_72839_b(this, this.func_174813_aQ()));
            boolean var6 = var3 == Blocks.field_150467_bQ;
            DamageSource var7 = var6 ? DamageSource.field_82728_o : DamageSource.field_82729_p;
            Iterator var8 = var5.iterator();

            while(var8.hasNext()) {
               Entity var9 = (Entity)var8.next();
               var9.func_70097_a(var7, (float)Math.min(MathHelper.func_76141_d((float)var4 * this.field_145816_i), this.field_145815_h));
            }

            if (var6 && (double)this.field_70146_Z.nextFloat() < 0.05000000074505806D + (double)var4 * 0.05D) {
               int var10 = (Integer)this.field_175132_d.func_177229_b(BlockAnvil.field_176505_b);
               ++var10;
               if (var10 > 2) {
                  this.field_145808_f = true;
               } else {
                  this.field_175132_d = this.field_175132_d.func_177226_a(BlockAnvil.field_176505_b, var10);
               }
            }
         }
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      Block var2 = this.field_175132_d != null ? this.field_175132_d.func_177230_c() : Blocks.field_150350_a;
      ResourceLocation var3 = (ResourceLocation)Block.field_149771_c.func_177774_c(var2);
      var1.func_74778_a("Block", var3 == null ? "" : var3.toString());
      var1.func_74774_a("Data", (byte)var2.func_176201_c(this.field_175132_d));
      var1.func_74774_a("Time", (byte)this.field_145812_b);
      var1.func_74757_a("DropItem", this.field_145813_c);
      var1.func_74757_a("HurtEntities", this.field_145809_g);
      var1.func_74776_a("FallHurtAmount", this.field_145816_i);
      var1.func_74768_a("FallHurtMax", this.field_145815_h);
      if (this.field_145810_d != null) {
         var1.func_74782_a("TileEntityData", this.field_145810_d);
      }

   }

   protected void func_70037_a(NBTTagCompound var1) {
      int var2 = var1.func_74771_c("Data") & 255;
      if (var1.func_150297_b("Block", 8)) {
         this.field_175132_d = Block.func_149684_b(var1.func_74779_i("Block")).func_176203_a(var2);
      } else if (var1.func_150297_b("TileID", 99)) {
         this.field_175132_d = Block.func_149729_e(var1.func_74762_e("TileID")).func_176203_a(var2);
      } else {
         this.field_175132_d = Block.func_149729_e(var1.func_74771_c("Tile") & 255).func_176203_a(var2);
      }

      this.field_145812_b = var1.func_74771_c("Time") & 255;
      Block var3 = this.field_175132_d.func_177230_c();
      if (var1.func_150297_b("HurtEntities", 99)) {
         this.field_145809_g = var1.func_74767_n("HurtEntities");
         this.field_145816_i = var1.func_74760_g("FallHurtAmount");
         this.field_145815_h = var1.func_74762_e("FallHurtMax");
      } else if (var3 == Blocks.field_150467_bQ) {
         this.field_145809_g = true;
      }

      if (var1.func_150297_b("DropItem", 99)) {
         this.field_145813_c = var1.func_74767_n("DropItem");
      }

      if (var1.func_150297_b("TileEntityData", 10)) {
         this.field_145810_d = var1.func_74775_l("TileEntityData");
      }

      if (var3 == null || var3.func_149688_o() == Material.field_151579_a) {
         this.field_175132_d = Blocks.field_150354_m.func_176223_P();
      }

   }

   public World func_145807_e() {
      return this.field_70170_p;
   }

   public void func_145806_a(boolean var1) {
      this.field_145809_g = var1;
   }

   public boolean func_90999_ad() {
      return false;
   }

   public void func_85029_a(CrashReportCategory var1) {
      super.func_85029_a(var1);
      if (this.field_175132_d != null) {
         Block var2 = this.field_175132_d.func_177230_c();
         var1.func_71507_a("Immitating block ID", Block.func_149682_b(var2));
         var1.func_71507_a("Immitating block data", var2.func_176201_c(this.field_175132_d));
      }

   }

   public IBlockState func_175131_l() {
      return this.field_175132_d;
   }
}
