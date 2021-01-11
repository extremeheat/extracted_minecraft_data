package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class EntityEnderCrystal extends Entity {
   public int field_70261_a;
   public int field_70260_b;

   public EntityEnderCrystal(World var1) {
      super(var1);
      this.field_70156_m = true;
      this.func_70105_a(2.0F, 2.0F);
      this.field_70260_b = 5;
      this.field_70261_a = this.field_70146_Z.nextInt(100000);
   }

   public EntityEnderCrystal(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(8, this.field_70260_b);
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      ++this.field_70261_a;
      this.field_70180_af.func_75692_b(8, this.field_70260_b);
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70163_u);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.field_70170_p.field_73011_w instanceof WorldProviderEnd && this.field_70170_p.func_180495_p(new BlockPos(var1, var2, var3)).func_177230_c() != Blocks.field_150480_ab) {
         this.field_70170_p.func_175656_a(new BlockPos(var1, var2, var3), Blocks.field_150480_ab.func_176223_P());
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
   }

   protected void func_70037_a(NBTTagCompound var1) {
   }

   public boolean func_70067_L() {
      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if (!this.field_70128_L && !this.field_70170_p.field_72995_K) {
            this.field_70260_b = 0;
            if (this.field_70260_b <= 0) {
               this.func_70106_y();
               if (!this.field_70170_p.field_72995_K) {
                  this.field_70170_p.func_72876_a((Entity)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, 6.0F, true);
               }
            }
         }

         return true;
      }
   }
}
