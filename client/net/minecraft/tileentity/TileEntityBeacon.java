package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBeacon extends TileEntityLockable implements ITickable, IInventory {
   public static final Potion[][] field_146009_a;
   private final List<TileEntityBeacon.BeamSegment> field_174909_f = Lists.newArrayList();
   private long field_146016_i;
   private float field_146014_j;
   private boolean field_146015_k;
   private int field_146012_l = -1;
   private int field_146013_m;
   private int field_146010_n;
   private ItemStack field_146011_o;
   private String field_146008_p;

   public TileEntityBeacon() {
      super();
   }

   public void func_73660_a() {
      if (this.field_145850_b.func_82737_E() % 80L == 0L) {
         this.func_174908_m();
      }

   }

   public void func_174908_m() {
      this.func_146003_y();
      this.func_146000_x();
   }

   private void func_146000_x() {
      if (this.field_146015_k && this.field_146012_l > 0 && !this.field_145850_b.field_72995_K && this.field_146013_m > 0) {
         double var1 = (double)(this.field_146012_l * 10 + 10);
         byte var3 = 0;
         if (this.field_146012_l >= 4 && this.field_146013_m == this.field_146010_n) {
            var3 = 1;
         }

         int var4 = this.field_174879_c.func_177958_n();
         int var5 = this.field_174879_c.func_177956_o();
         int var6 = this.field_174879_c.func_177952_p();
         AxisAlignedBB var7 = (new AxisAlignedBB((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1))).func_72314_b(var1, var1, var1).func_72321_a(0.0D, (double)this.field_145850_b.func_72800_K(), 0.0D);
         List var8 = this.field_145850_b.func_72872_a(EntityPlayer.class, var7);
         Iterator var9 = var8.iterator();

         EntityPlayer var10;
         while(var9.hasNext()) {
            var10 = (EntityPlayer)var9.next();
            var10.func_70690_d(new PotionEffect(this.field_146013_m, 180, var3, true, true));
         }

         if (this.field_146012_l >= 4 && this.field_146013_m != this.field_146010_n && this.field_146010_n > 0) {
            var9 = var8.iterator();

            while(var9.hasNext()) {
               var10 = (EntityPlayer)var9.next();
               var10.func_70690_d(new PotionEffect(this.field_146010_n, 180, 0, true, true));
            }
         }
      }

   }

   private void func_146003_y() {
      int var1 = this.field_146012_l;
      int var2 = this.field_174879_c.func_177958_n();
      int var3 = this.field_174879_c.func_177956_o();
      int var4 = this.field_174879_c.func_177952_p();
      this.field_146012_l = 0;
      this.field_174909_f.clear();
      this.field_146015_k = true;
      TileEntityBeacon.BeamSegment var5 = new TileEntityBeacon.BeamSegment(EntitySheep.func_175513_a(EnumDyeColor.WHITE));
      this.field_174909_f.add(var5);
      boolean var6 = true;
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      int var8;
      for(var8 = var3 + 1; var8 < 256; ++var8) {
         IBlockState var9 = this.field_145850_b.func_180495_p(var7.func_181079_c(var2, var8, var4));
         float[] var10;
         if (var9.func_177230_c() == Blocks.field_150399_cn) {
            var10 = EntitySheep.func_175513_a((EnumDyeColor)var9.func_177229_b(BlockStainedGlass.field_176547_a));
         } else {
            if (var9.func_177230_c() != Blocks.field_150397_co) {
               if (var9.func_177230_c().func_149717_k() >= 15 && var9.func_177230_c() != Blocks.field_150357_h) {
                  this.field_146015_k = false;
                  this.field_174909_f.clear();
                  break;
               }

               var5.func_177262_a();
               continue;
            }

            var10 = EntitySheep.func_175513_a((EnumDyeColor)var9.func_177229_b(BlockStainedGlassPane.field_176245_a));
         }

         if (!var6) {
            var10 = new float[]{(var5.func_177263_b()[0] + var10[0]) / 2.0F, (var5.func_177263_b()[1] + var10[1]) / 2.0F, (var5.func_177263_b()[2] + var10[2]) / 2.0F};
         }

         if (Arrays.equals(var10, var5.func_177263_b())) {
            var5.func_177262_a();
         } else {
            var5 = new TileEntityBeacon.BeamSegment(var10);
            this.field_174909_f.add(var5);
         }

         var6 = false;
      }

      if (this.field_146015_k) {
         for(var8 = 1; var8 <= 4; this.field_146012_l = var8++) {
            int var15 = var3 - var8;
            if (var15 < 0) {
               break;
            }

            boolean var17 = true;

            for(int var11 = var2 - var8; var11 <= var2 + var8 && var17; ++var11) {
               for(int var12 = var4 - var8; var12 <= var4 + var8; ++var12) {
                  Block var13 = this.field_145850_b.func_180495_p(new BlockPos(var11, var15, var12)).func_177230_c();
                  if (var13 != Blocks.field_150475_bE && var13 != Blocks.field_150340_R && var13 != Blocks.field_150484_ah && var13 != Blocks.field_150339_S) {
                     var17 = false;
                     break;
                  }
               }
            }

            if (!var17) {
               break;
            }
         }

         if (this.field_146012_l == 0) {
            this.field_146015_k = false;
         }
      }

      if (!this.field_145850_b.field_72995_K && this.field_146012_l == 4 && var1 < this.field_146012_l) {
         Iterator var14 = this.field_145850_b.func_72872_a(EntityPlayer.class, (new AxisAlignedBB((double)var2, (double)var3, (double)var4, (double)var2, (double)(var3 - 4), (double)var4)).func_72314_b(10.0D, 5.0D, 10.0D)).iterator();

         while(var14.hasNext()) {
            EntityPlayer var16 = (EntityPlayer)var14.next();
            var16.func_71029_a(AchievementList.field_150965_K);
         }
      }

   }

   public List<TileEntityBeacon.BeamSegment> func_174907_n() {
      return this.field_174909_f;
   }

   public float func_146002_i() {
      if (!this.field_146015_k) {
         return 0.0F;
      } else {
         int var1 = (int)(this.field_145850_b.func_82737_E() - this.field_146016_i);
         this.field_146016_i = this.field_145850_b.func_82737_E();
         if (var1 > 1) {
            this.field_146014_j -= (float)var1 / 40.0F;
            if (this.field_146014_j < 0.0F) {
               this.field_146014_j = 0.0F;
            }
         }

         this.field_146014_j += 0.025F;
         if (this.field_146014_j > 1.0F) {
            this.field_146014_j = 1.0F;
         }

         return this.field_146014_j;
      }
   }

   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      return new S35PacketUpdateTileEntity(this.field_174879_c, 3, var1);
   }

   public double func_145833_n() {
      return 65536.0D;
   }

   private int func_183001_h(int var1) {
      if (var1 >= 0 && var1 < Potion.field_76425_a.length && Potion.field_76425_a[var1] != null) {
         Potion var2 = Potion.field_76425_a[var1];
         return var2 != Potion.field_76424_c && var2 != Potion.field_76422_e && var2 != Potion.field_76429_m && var2 != Potion.field_76430_j && var2 != Potion.field_76420_g && var2 != Potion.field_76428_l ? 0 : var1;
      } else {
         return 0;
      }
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_146013_m = this.func_183001_h(var1.func_74762_e("Primary"));
      this.field_146010_n = this.func_183001_h(var1.func_74762_e("Secondary"));
      this.field_146012_l = var1.func_74762_e("Levels");
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74768_a("Primary", this.field_146013_m);
      var1.func_74768_a("Secondary", this.field_146010_n);
      var1.func_74768_a("Levels", this.field_146012_l);
   }

   public int func_70302_i_() {
      return 1;
   }

   public ItemStack func_70301_a(int var1) {
      return var1 == 0 ? this.field_146011_o : null;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (var1 == 0 && this.field_146011_o != null) {
         if (var2 >= this.field_146011_o.field_77994_a) {
            ItemStack var3 = this.field_146011_o;
            this.field_146011_o = null;
            return var3;
         } else {
            ItemStack var10000 = this.field_146011_o;
            var10000.field_77994_a -= var2;
            return new ItemStack(this.field_146011_o.func_77973_b(), var2, this.field_146011_o.func_77960_j());
         }
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (var1 == 0 && this.field_146011_o != null) {
         ItemStack var2 = this.field_146011_o;
         this.field_146011_o = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.field_146011_o = var2;
      }

   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_146008_p : "container.beacon";
   }

   public boolean func_145818_k_() {
      return this.field_146008_p != null && this.field_146008_p.length() > 0;
   }

   public void func_145999_a(String var1) {
      this.field_146008_p = var1;
   }

   public int func_70297_j_() {
      return 1;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return var2.func_77973_b() == Items.field_151166_bC || var2.func_77973_b() == Items.field_151045_i || var2.func_77973_b() == Items.field_151043_k || var2.func_77973_b() == Items.field_151042_j;
   }

   public String func_174875_k() {
      return "minecraft:beacon";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBeacon(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_146012_l;
      case 1:
         return this.field_146013_m;
      case 2:
         return this.field_146010_n;
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_146012_l = var2;
         break;
      case 1:
         this.field_146013_m = this.func_183001_h(var2);
         break;
      case 2:
         this.field_146010_n = this.func_183001_h(var2);
      }

   }

   public int func_174890_g() {
      return 3;
   }

   public void func_174888_l() {
      this.field_146011_o = null;
   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.func_174908_m();
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   static {
      field_146009_a = new Potion[][]{{Potion.field_76424_c, Potion.field_76422_e}, {Potion.field_76429_m, Potion.field_76430_j}, {Potion.field_76420_g}, {Potion.field_76428_l}};
   }

   public static class BeamSegment {
      private final float[] field_177266_a;
      private int field_177265_b;

      public BeamSegment(float[] var1) {
         super();
         this.field_177266_a = var1;
         this.field_177265_b = 1;
      }

      protected void func_177262_a() {
         ++this.field_177265_b;
      }

      public float[] func_177263_b() {
         return this.field_177266_a;
      }

      public int func_177264_c() {
         return this.field_177265_b;
      }
   }
}
