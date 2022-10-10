package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World var1) {
      super(EntityType.field_200768_H, var1);
   }

   public EntityLeashKnot(World var1, BlockPos var2) {
      super(EntityType.field_200768_H, var1, var2);
      this.func_70107_b((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D);
      float var3 = 0.125F;
      float var4 = 0.1875F;
      float var5 = 0.25F;
      this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.1875D, this.field_70163_u - 0.25D + 0.125D, this.field_70161_v - 0.1875D, this.field_70165_t + 0.1875D, this.field_70163_u + 0.25D + 0.125D, this.field_70161_v + 0.1875D));
      this.field_98038_p = true;
   }

   public void func_70107_b(double var1, double var3, double var5) {
      super.func_70107_b((double)MathHelper.func_76128_c(var1) + 0.5D, (double)MathHelper.func_76128_c(var3) + 0.5D, (double)MathHelper.func_76128_c(var5) + 0.5D);
   }

   protected void func_174856_o() {
      this.field_70165_t = (double)this.field_174861_a.func_177958_n() + 0.5D;
      this.field_70163_u = (double)this.field_174861_a.func_177956_o() + 0.5D;
      this.field_70161_v = (double)this.field_174861_a.func_177952_p() + 0.5D;
   }

   public void func_174859_a(EnumFacing var1) {
   }

   public int func_82329_d() {
      return 9;
   }

   public int func_82330_g() {
      return 9;
   }

   public float func_70047_e() {
      return -0.0625F;
   }

   public boolean func_70112_a(double var1) {
      return var1 < 1024.0D;
   }

   public void func_110128_b(@Nullable Entity var1) {
      this.func_184185_a(SoundEvents.field_187746_da, 1.0F, 1.0F);
   }

   public void func_70014_b(NBTTagCompound var1) {
   }

   public void func_70037_a(NBTTagCompound var1) {
   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (this.field_70170_p.field_72995_K) {
         return true;
      } else {
         boolean var3 = false;
         double var4 = 7.0D;
         List var6 = this.field_70170_p.func_72872_a(EntityLiving.class, new AxisAlignedBB(this.field_70165_t - 7.0D, this.field_70163_u - 7.0D, this.field_70161_v - 7.0D, this.field_70165_t + 7.0D, this.field_70163_u + 7.0D, this.field_70161_v + 7.0D));
         Iterator var7 = var6.iterator();

         EntityLiving var8;
         while(var7.hasNext()) {
            var8 = (EntityLiving)var7.next();
            if (var8.func_110167_bD() && var8.func_110166_bE() == var1) {
               var8.func_110162_b(this, true);
               var3 = true;
            }
         }

         if (!var3) {
            this.func_70106_y();
            if (var1.field_71075_bZ.field_75098_d) {
               var7 = var6.iterator();

               while(var7.hasNext()) {
                  var8 = (EntityLiving)var7.next();
                  if (var8.func_110167_bD() && var8.func_110166_bE() == this) {
                     var8.func_110160_i(true, false);
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean func_70518_d() {
      return this.field_70170_p.func_180495_p(this.field_174861_a).func_177230_c() instanceof BlockFence;
   }

   public static EntityLeashKnot func_174862_a(World var0, BlockPos var1) {
      EntityLeashKnot var2 = new EntityLeashKnot(var0, var1);
      var0.func_72838_d(var2);
      var2.func_184523_o();
      return var2;
   }

   @Nullable
   public static EntityLeashKnot func_174863_b(World var0, BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      List var5 = var0.func_72872_a(EntityLeashKnot.class, new AxisAlignedBB((double)var2 - 1.0D, (double)var3 - 1.0D, (double)var4 - 1.0D, (double)var2 + 1.0D, (double)var3 + 1.0D, (double)var4 + 1.0D));
      Iterator var6 = var5.iterator();

      EntityLeashKnot var7;
      do {
         if (!var6.hasNext()) {
            return null;
         }

         var7 = (EntityLeashKnot)var6.next();
      } while(!var7.func_174857_n().equals(var1));

      return var7;
   }

   public void func_184523_o() {
      this.func_184185_a(SoundEvents.field_187748_db, 1.0F, 1.0F);
   }
}
