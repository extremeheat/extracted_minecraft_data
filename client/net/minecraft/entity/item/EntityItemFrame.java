package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {
   private static final Logger field_195052_c = LogManager.getLogger();
   private static final DataParameter<ItemStack> field_184525_c;
   private static final DataParameter<Integer> field_184526_d;
   private float field_82337_e = 1.0F;

   public EntityItemFrame(World var1) {
      super(EntityType.field_200766_F, var1);
   }

   public EntityItemFrame(World var1, BlockPos var2, EnumFacing var3) {
      super(EntityType.field_200766_F, var1, var2);
      this.func_174859_a(var3);
   }

   public float func_70047_e() {
      return 0.0F;
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(field_184525_c, ItemStack.field_190927_a);
      this.func_184212_Q().func_187214_a(field_184526_d, 0);
   }

   protected void func_174859_a(EnumFacing var1) {
      Validate.notNull(var1);
      this.field_174860_b = var1;
      if (var1.func_176740_k().func_176722_c()) {
         this.field_70125_A = 0.0F;
         this.field_70177_z = (float)(this.field_174860_b.func_176736_b() * 90);
      } else {
         this.field_70125_A = (float)(-90 * var1.func_176743_c().func_179524_a());
         this.field_70177_z = 0.0F;
      }

      this.field_70127_C = this.field_70125_A;
      this.field_70126_B = this.field_70177_z;
      this.func_174856_o();
   }

   protected void func_174856_o() {
      if (this.field_174860_b != null) {
         double var1 = 0.46875D;
         this.field_70165_t = (double)this.field_174861_a.func_177958_n() + 0.5D - (double)this.field_174860_b.func_82601_c() * 0.46875D;
         this.field_70163_u = (double)this.field_174861_a.func_177956_o() + 0.5D - (double)this.field_174860_b.func_96559_d() * 0.46875D;
         this.field_70161_v = (double)this.field_174861_a.func_177952_p() + 0.5D - (double)this.field_174860_b.func_82599_e() * 0.46875D;
         double var3 = (double)this.func_82329_d();
         double var5 = (double)this.func_82330_g();
         double var7 = (double)this.func_82329_d();
         EnumFacing.Axis var9 = this.field_174860_b.func_176740_k();
         switch(var9) {
         case X:
            var3 = 1.0D;
            break;
         case Y:
            var5 = 1.0D;
            break;
         case Z:
            var7 = 1.0D;
         }

         var3 /= 32.0D;
         var5 /= 32.0D;
         var7 /= 32.0D;
         this.func_174826_a(new AxisAlignedBB(this.field_70165_t - var3, this.field_70163_u - var5, this.field_70161_v - var7, this.field_70165_t + var3, this.field_70163_u + var5, this.field_70161_v + var7));
      }
   }

   public boolean func_70518_d() {
      if (!this.field_70170_p.func_195586_b(this, this.func_174813_aQ())) {
         return false;
      } else {
         IBlockState var1 = this.field_70170_p.func_180495_p(this.field_174861_a.func_177972_a(this.field_174860_b.func_176734_d()));
         return var1.func_185904_a().func_76220_a() || this.field_174860_b.func_176740_k().func_176722_c() && BlockRedstoneDiode.func_185546_B(var1) ? this.field_70170_p.func_175674_a(this, this.func_174813_aQ(), field_184524_c).isEmpty() : false;
      }
   }

   public float func_70111_Y() {
      return 0.0F;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (!var1.func_94541_c() && !this.func_82335_i().func_190926_b()) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_146065_b(var1.func_76346_g(), false);
            this.func_184185_a(SoundEvents.field_187629_cO, 1.0F, 1.0F);
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
      var3 *= 64.0D * func_184183_bd();
      return var1 < var3 * var3;
   }

   public void func_110128_b(@Nullable Entity var1) {
      this.func_184185_a(SoundEvents.field_187623_cM, 1.0F, 1.0F);
      this.func_146065_b(var1, true);
   }

   public void func_184523_o() {
      this.func_184185_a(SoundEvents.field_187626_cN, 1.0F, 1.0F);
   }

   public void func_146065_b(@Nullable Entity var1, boolean var2) {
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         ItemStack var3 = this.func_82335_i();
         this.func_82334_a(ItemStack.field_190927_a);
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var4 = (EntityPlayer)var1;
            if (var4.field_71075_bZ.field_75098_d) {
               this.func_110131_b(var3);
               return;
            }
         }

         if (var2) {
            this.func_199703_a(Items.field_151160_bD);
         }

         if (!var3.func_190926_b() && this.field_70146_Z.nextFloat() < this.field_82337_e) {
            var3 = var3.func_77946_l();
            this.func_110131_b(var3);
            this.func_199701_a_(var3);
         }

      }
   }

   private void func_110131_b(ItemStack var1) {
      if (var1.func_77973_b() == Items.field_151098_aY) {
         MapData var2 = ItemMap.func_195950_a(var1, this.field_70170_p);
         var2.func_212441_a(this.field_174861_a, this.func_145782_y());
      }

      var1.func_82842_a((EntityItemFrame)null);
   }

   public ItemStack func_82335_i() {
      return (ItemStack)this.func_184212_Q().func_187225_a(field_184525_c);
   }

   public void func_82334_a(ItemStack var1) {
      this.func_174864_a(var1, true);
   }

   private void func_174864_a(ItemStack var1, boolean var2) {
      if (!var1.func_190926_b()) {
         var1 = var1.func_77946_l();
         var1.func_190920_e(1);
         var1.func_82842_a(this);
      }

      this.func_184212_Q().func_187227_b(field_184525_c, var1);
      if (!var1.func_190926_b()) {
         this.func_184185_a(SoundEvents.field_187620_cL, 1.0F, 1.0F);
      }

      if (var2 && this.field_174861_a != null) {
         this.field_70170_p.func_175666_e(this.field_174861_a, Blocks.field_150350_a);
      }

   }

   public void func_184206_a(DataParameter<?> var1) {
      if (var1.equals(field_184525_c)) {
         ItemStack var2 = this.func_82335_i();
         if (!var2.func_190926_b() && var2.func_82836_z() != this) {
            var2.func_82842_a(this);
         }
      }

   }

   public int func_82333_j() {
      return (Integer)this.func_184212_Q().func_187225_a(field_184526_d);
   }

   public void func_82336_g(int var1) {
      this.func_174865_a(var1, true);
   }

   private void func_174865_a(int var1, boolean var2) {
      this.func_184212_Q().func_187227_b(field_184526_d, var1 % 8);
      if (var2 && this.field_174861_a != null) {
         this.field_70170_p.func_175666_e(this.field_174861_a, Blocks.field_150350_a);
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (!this.func_82335_i().func_190926_b()) {
         var1.func_74782_a("Item", this.func_82335_i().func_77955_b(new NBTTagCompound()));
         var1.func_74774_a("ItemRotation", (byte)this.func_82333_j());
         var1.func_74776_a("ItemDropChance", this.field_82337_e);
      }

      var1.func_74774_a("Facing", (byte)this.field_174860_b.func_176745_a());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      NBTTagCompound var2 = var1.func_74775_l("Item");
      if (var2 != null && !var2.isEmpty()) {
         ItemStack var3 = ItemStack.func_199557_a(var2);
         if (var3.func_190926_b()) {
            field_195052_c.warn("Unable to load item from: {}", var2);
         }

         this.func_174864_a(var3, false);
         this.func_174865_a(var1.func_74771_c("ItemRotation"), false);
         if (var1.func_150297_b("ItemDropChance", 99)) {
            this.field_82337_e = var1.func_74760_g("ItemDropChance");
         }
      }

      this.func_174859_a(EnumFacing.func_82600_a(var1.func_74771_c("Facing")));
   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_82335_i().func_190926_b()) {
            if (!var3.func_190926_b()) {
               this.func_82334_a(var3);
               if (!var1.field_71075_bZ.field_75098_d) {
                  var3.func_190918_g(1);
               }
            }
         } else {
            this.func_184185_a(SoundEvents.field_187632_cP, 1.0F, 1.0F);
            this.func_82336_g(this.func_82333_j() + 1);
         }
      }

      return true;
   }

   public int func_174866_q() {
      return this.func_82335_i().func_190926_b() ? 0 : this.func_82333_j() % 8 + 1;
   }

   static {
      field_184525_c = EntityDataManager.func_187226_a(EntityItemFrame.class, DataSerializers.field_187196_f);
      field_184526_d = EntityDataManager.func_187226_a(EntityItemFrame.class, DataSerializers.field_187192_b);
   }
}
