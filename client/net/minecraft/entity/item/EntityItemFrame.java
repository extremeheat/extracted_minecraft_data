package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging {
   private float field_82337_e = 1.0F;

   public EntityItemFrame(World var1) {
      super(var1);
   }

   public EntityItemFrame(World var1, BlockPos var2, EnumFacing var3) {
      super(var1, var2);
      this.func_174859_a(var3);
   }

   protected void func_70088_a() {
      this.func_70096_w().func_82709_a(8, 5);
      this.func_70096_w().func_75682_a(9, (byte)0);
   }

   public float func_70111_Y() {
      return 0.0F;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (!var1.func_94541_c() && this.func_82335_i() != null) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_146065_b(var1.func_76346_g(), false);
            this.func_82334_a((ItemStack)null);
         }

         return true;
      } else {
         return super.func_70097_a(var1, var2);
      }
   }

   public int func_82329_d() {
      return 12;
   }

   public int func_82330_g() {
      return 12;
   }

   public boolean func_70112_a(double var1) {
      double var3 = 16.0D;
      var3 *= 64.0D * this.field_70155_l;
      return var1 < var3 * var3;
   }

   public void func_110128_b(Entity var1) {
      this.func_146065_b(var1, true);
   }

   public void func_146065_b(Entity var1, boolean var2) {
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         ItemStack var3 = this.func_82335_i();
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var4 = (EntityPlayer)var1;
            if (var4.field_71075_bZ.field_75098_d) {
               this.func_110131_b(var3);
               return;
            }
         }

         if (var2) {
            this.func_70099_a(new ItemStack(Items.field_151160_bD), 0.0F);
         }

         if (var3 != null && this.field_70146_Z.nextFloat() < this.field_82337_e) {
            var3 = var3.func_77946_l();
            this.func_110131_b(var3);
            this.func_70099_a(var3, 0.0F);
         }

      }
   }

   private void func_110131_b(ItemStack var1) {
      if (var1 != null) {
         if (var1.func_77973_b() == Items.field_151098_aY) {
            MapData var2 = ((ItemMap)var1.func_77973_b()).func_77873_a(var1, this.field_70170_p);
            var2.field_76203_h.remove("frame-" + this.func_145782_y());
         }

         var1.func_82842_a((EntityItemFrame)null);
      }
   }

   public ItemStack func_82335_i() {
      return this.func_70096_w().func_82710_f(8);
   }

   public void func_82334_a(ItemStack var1) {
      this.func_174864_a(var1, true);
   }

   private void func_174864_a(ItemStack var1, boolean var2) {
      if (var1 != null) {
         var1 = var1.func_77946_l();
         var1.field_77994_a = 1;
         var1.func_82842_a(this);
      }

      this.func_70096_w().func_75692_b(8, var1);
      this.func_70096_w().func_82708_h(8);
      if (var2 && this.field_174861_a != null) {
         this.field_70170_p.func_175666_e(this.field_174861_a, Blocks.field_150350_a);
      }

   }

   public int func_82333_j() {
      return this.func_70096_w().func_75683_a(9);
   }

   public void func_82336_g(int var1) {
      this.func_174865_a(var1, true);
   }

   private void func_174865_a(int var1, boolean var2) {
      this.func_70096_w().func_75692_b(9, (byte)(var1 % 8));
      if (var2 && this.field_174861_a != null) {
         this.field_70170_p.func_175666_e(this.field_174861_a, Blocks.field_150350_a);
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      if (this.func_82335_i() != null) {
         var1.func_74782_a("Item", this.func_82335_i().func_77955_b(new NBTTagCompound()));
         var1.func_74774_a("ItemRotation", (byte)this.func_82333_j());
         var1.func_74776_a("ItemDropChance", this.field_82337_e);
      }

      super.func_70014_b(var1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      NBTTagCompound var2 = var1.func_74775_l("Item");
      if (var2 != null && !var2.func_82582_d()) {
         this.func_174864_a(ItemStack.func_77949_a(var2), false);
         this.func_174865_a(var1.func_74771_c("ItemRotation"), false);
         if (var1.func_150297_b("ItemDropChance", 99)) {
            this.field_82337_e = var1.func_74760_g("ItemDropChance");
         }

         if (var1.func_74764_b("Direction")) {
            this.func_174865_a(this.func_82333_j() * 2, false);
         }
      }

      super.func_70037_a(var1);
   }

   public boolean func_130002_c(EntityPlayer var1) {
      if (this.func_82335_i() == null) {
         ItemStack var2 = var1.func_70694_bm();
         if (var2 != null && !this.field_70170_p.field_72995_K) {
            this.func_82334_a(var2);
            if (!var1.field_71075_bZ.field_75098_d && --var2.field_77994_a <= 0) {
               var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
            }
         }
      } else if (!this.field_70170_p.field_72995_K) {
         this.func_82336_g(this.func_82333_j() + 1);
      }

      return true;
   }

   public int func_174866_q() {
      return this.func_82335_i() == null ? 0 : this.func_82333_j() % 8 + 1;
   }
}
