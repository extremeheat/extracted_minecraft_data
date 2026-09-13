package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
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
      if (!var1.field_72995_K
         && var1.func_82736_K().func_82766_b("doFireTick")
         && (var1.field_73013_u == EnumDifficulty.NORMAL || var1.field_73013_u == EnumDifficulty.HARD)
         && var1.func_72873_a(MathHelper.func_76128_c(var2), MathHelper.func_76128_c(var4), MathHelper.func_76128_c(var6), 10)) {
         int var8 = MathHelper.func_76128_c(var2);
         int var9 = MathHelper.func_76128_c(var4);
         int var10 = MathHelper.func_76128_c(var6);
         if (var1.func_147439_a(var8, var9, var10).func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_149742_c(var1, var8, var9, var10)
            )
          {
            var1.func_147449_b(var8, var9, var10, Blocks.field_150480_ab);
         }

         for(int var12 = 0; var12 < 4; ++var12) {
            var9 = MathHelper.func_76128_c(var2) + this.field_70146_Z.nextInt(3) - 1;
            var10 = MathHelper.func_76128_c(var4) + this.field_70146_Z.nextInt(3) - 1;
            int var11 = MathHelper.func_76128_c(var6) + this.field_70146_Z.nextInt(3) - 1;
            if (var1.func_147439_a(var9, var10, var11).func_149688_o() == Material.field_151579_a
               && Blocks.field_150480_ab.func_149742_c(var1, var9, var10, var11)) {
               var1.func_147449_b(var9, var10, var11, Blocks.field_150480_ab);
            }
         }
      }
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70262_b == 2) {
         this.field_70170_p
            .func_72908_a(
               this.field_70165_t, this.field_70163_u, this.field_70161_v, "ambient.weather.thunder", 10000.0F, 0.8F + this.field_70146_Z.nextFloat() * 0.2F
            );
         this.field_70170_p
            .func_72908_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, "random.explode", 2.0F, 0.5F + this.field_70146_Z.nextFloat() * 0.2F);
      }

      --this.field_70262_b;
      if (this.field_70262_b < 0) {
         if (this.field_70263_c == 0) {
            this.func_70106_y();
         } else if (this.field_70262_b < -this.field_70146_Z.nextInt(10)) {
            --this.field_70263_c;
            this.field_70262_b = 1;
            this.field_70264_a = this.field_70146_Z.nextLong();
            if (!this.field_70170_p.field_72995_K
               && this.field_70170_p.func_82736_K().func_82766_b("doFireTick")
               && this.field_70170_p
                  .func_72873_a(
                     MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v), 10
                  )) {
               int var1 = MathHelper.func_76128_c(this.field_70165_t);
               int var2 = MathHelper.func_76128_c(this.field_70163_u);
               int var3 = MathHelper.func_76128_c(this.field_70161_v);
               if (this.field_70170_p.func_147439_a(var1, var2, var3).func_149688_o() == Material.field_151579_a
                  && Blocks.field_150480_ab.func_149742_c(this.field_70170_p, var1, var2, var3)) {
                  this.field_70170_p.func_147449_b(var1, var2, var3, Blocks.field_150480_ab);
               }
            }
         }
      }

      if (this.field_70262_b >= 0) {
         if (this.field_70170_p.field_72995_K) {
            this.field_70170_p.field_73016_r = 2;
         } else {
            double var6 = 3.0;
            List var7 = this.field_70170_p
               .func_72839_b(
                  this,
                  AxisAlignedBB.func_72330_a(
                     this.field_70165_t - var6,
                     this.field_70163_u - var6,
                     this.field_70161_v - var6,
                     this.field_70165_t + var6,
                     this.field_70163_u + 6.0 + var6,
                     this.field_70161_v + var6
                  )
               );

            for(int var4 = 0; var4 < var7.size(); ++var4) {
               Entity var5 = (Entity)var7.get(var4);
               var5.func_70077_a(this);
            }
         }
      }
   }

   @Override
   protected void func_70088_a() {
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
   }
}
