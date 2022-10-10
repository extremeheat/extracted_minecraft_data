package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {
   protected Block field_175506_bl;
   private int field_70881_d;
   private UUID field_146084_br;

   protected EntityAnimal(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_175506_bl = Blocks.field_196658_i;
   }

   protected void func_70619_bc() {
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      super.func_70619_bc();
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      if (this.field_70881_d > 0) {
         --this.field_70881_d;
         if (this.field_70881_d % 10 == 0) {
            double var1 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_195594_a(Particles.field_197633_z, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var1, var3, var5);
         }
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.field_70881_d = 0;
         return super.func_70097_a(var1, var2);
      }
   }

   public float func_205022_a(BlockPos var1, IWorldReaderBase var2) {
      return var2.func_180495_p(var1.func_177977_b()).func_177230_c() == this.field_175506_bl ? 10.0F : var2.func_205052_D(var1) - 0.5F;
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("InLove", this.field_70881_d);
      if (this.field_146084_br != null) {
         var1.func_186854_a("LoveCause", this.field_146084_br);
      }

   }

   public double func_70033_W() {
      return 0.14D;
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70881_d = var1.func_74762_e("InLove");
      this.field_146084_br = var1.func_186855_b("LoveCause") ? var1.func_186857_a("LoveCause") : null;
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      int var3 = MathHelper.func_76128_c(this.field_70165_t);
      int var4 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var5 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var6 = new BlockPos(var3, var4, var5);
      return var1.func_180495_p(var6.func_177977_b()).func_177230_c() == this.field_175506_bl && var1.func_201669_a(var6, 0) > 8 && super.func_205020_a(var1, var2);
   }

   public int func_70627_aG() {
      return 120;
   }

   public boolean func_70692_ba() {
      return false;
   }

   protected int func_70693_a(EntityPlayer var1) {
      return 1 + this.field_70170_p.field_73012_v.nextInt(3);
   }

   public boolean func_70877_b(ItemStack var1) {
      return var1.func_77973_b() == Items.field_151015_O;
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (this.func_70877_b(var3)) {
         if (this.func_70874_b() == 0 && this.func_204701_dC()) {
            this.func_175505_a(var1, var3);
            this.func_146082_f(var1);
            return true;
         }

         if (this.func_70631_g_()) {
            this.func_175505_a(var1, var3);
            this.func_175501_a((int)((float)(-this.func_70874_b() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.func_184645_a(var1, var2);
   }

   protected void func_175505_a(EntityPlayer var1, ItemStack var2) {
      if (!var1.field_71075_bZ.field_75098_d) {
         var2.func_190918_g(1);
      }

   }

   public boolean func_204701_dC() {
      return this.field_70881_d <= 0;
   }

   public void func_146082_f(@Nullable EntityPlayer var1) {
      this.field_70881_d = 600;
      if (var1 != null) {
         this.field_146084_br = var1.func_110124_au();
      }

      this.field_70170_p.func_72960_a(this, (byte)18);
   }

   public void func_204700_e(int var1) {
      this.field_70881_d = var1;
   }

   @Nullable
   public EntityPlayerMP func_191993_do() {
      if (this.field_146084_br == null) {
         return null;
      } else {
         EntityPlayer var1 = this.field_70170_p.func_152378_a(this.field_146084_br);
         return var1 instanceof EntityPlayerMP ? (EntityPlayerMP)var1 : null;
      }
   }

   public boolean func_70880_s() {
      return this.field_70881_d > 0;
   }

   public void func_70875_t() {
      this.field_70881_d = 0;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         return this.func_70880_s() && var1.func_70880_s();
      }
   }

   public void func_70103_a(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var7 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_195594_a(Particles.field_197633_z, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var3, var5, var7);
         }
      } else {
         super.func_70103_a(var1);
      }

   }
}
