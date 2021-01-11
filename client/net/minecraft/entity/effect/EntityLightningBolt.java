package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {
   private int field_70262_b;
   public long field_70264_a;
   private int field_70263_c;

   public EntityLightningBolt(World var1, double var2, double var4, double var6) {
      super(var1);
      this.func_70012_b(var2, var4, var6, 0.0F, 0.0F);
      this.field_70262_b = 2;
      this.field_70264_a = this.field_70146_Z.nextLong();
      this.field_70263_c = this.field_70146_Z.nextInt(3) + 1;
      BlockPos var8 = new BlockPos(this);
      if (!var1.field_72995_K && var1.func_82736_K().func_82766_b("doFireTick") && (var1.func_175659_aa() == EnumDifficulty.NORMAL || var1.func_175659_aa() == EnumDifficulty.HARD) && var1.func_175697_a(var8, 10)) {
         if (var1.func_180495_p(var8).func_177230_c().func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_176196_c(var1, var8)) {
            var1.func_175656_a(var8, Blocks.field_150480_ab.func_176223_P());
         }

         for(int var9 = 0; var9 < 4; ++var9) {
            BlockPos var10 = var8.func_177982_a(this.field_70146_Z.nextInt(3) - 1, this.field_70146_Z.nextInt(3) - 1, this.field_70146_Z.nextInt(3) - 1);
            if (var1.func_180495_p(var10).func_177230_c().func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_176196_c(var1, var10)) {
               var1.func_175656_a(var10, Blocks.field_150480_ab.func_176223_P());
            }
         }
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70262_b == 2) {
         this.field_70170_p.func_72908_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, "ambient.weather.thunder", 10000.0F, 0.8F + this.field_70146_Z.nextFloat() * 0.2F);
         this.field_70170_p.func_72908_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, "random.explode", 2.0F, 0.5F + this.field_70146_Z.nextFloat() * 0.2F);
      }

      --this.field_70262_b;
      if (this.field_70262_b < 0) {
         if (this.field_70263_c == 0) {
            this.func_70106_y();
         } else if (this.field_70262_b < -this.field_70146_Z.nextInt(10)) {
            --this.field_70263_c;
            this.field_70262_b = 1;
            this.field_70264_a = this.field_70146_Z.nextLong();
            BlockPos var1 = new BlockPos(this);
            if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("doFireTick") && this.field_70170_p.func_175697_a(var1, 10) && this.field_70170_p.func_180495_p(var1).func_177230_c().func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_176196_c(this.field_70170_p, var1)) {
               this.field_70170_p.func_175656_a(var1, Blocks.field_150480_ab.func_176223_P());
            }
         }
      }

      if (this.field_70262_b >= 0) {
         if (this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_175702_c(2);
         } else {
            double var6 = 3.0D;
            List var3 = this.field_70170_p.func_72839_b(this, new AxisAlignedBB(this.field_70165_t - var6, this.field_70163_u - var6, this.field_70161_v - var6, this.field_70165_t + var6, this.field_70163_u + 6.0D + var6, this.field_70161_v + var6));

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               Entity var5 = (Entity)var3.get(var4);
               var5.func_70077_a(this);
            }
         }
      }

   }

   protected void func_70088_a() {
   }

   protected void func_70037_a(NBTTagCompound var1) {
   }

   protected void func_70014_b(NBTTagCompound var1) {
   }
}
