package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   protected EntityMob(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_70728_aV = 5;
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   public void func_70636_d() {
      this.func_82168_bl();
      float var1 = this.func_70013_c();
      if (var1 > 0.5F) {
         this.field_70708_bq += 2;
      }

      super.func_70636_d();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }

   }

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_187593_cC;
   }

   protected SoundEvent func_184181_aa() {
      return SoundEvents.field_187591_cB;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return this.func_180431_b(var1) ? false : super.func_70097_a(var1, var2);
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187741_cz;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187738_cy;
   }

   protected SoundEvent func_184588_d(int var1) {
      return var1 > 4 ? SoundEvents.field_187735_cx : SoundEvents.field_187589_cA;
   }

   public float func_205022_a(BlockPos var1, IWorldReaderBase var2) {
      return 0.5F - var2.func_205052_D(var1);
   }

   protected boolean func_70814_o() {
      BlockPos var1 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
      if (this.field_70170_p.func_175642_b(EnumLightType.SKY, var1) > this.field_70146_Z.nextInt(32)) {
         return false;
      } else {
         int var2 = this.field_70170_p.func_72911_I() ? this.field_70170_p.func_205049_d(var1, 10) : this.field_70170_p.func_201696_r(var1);
         return var2 <= this.field_70146_Z.nextInt(8);
      }
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      return var1.func_175659_aa() != EnumDifficulty.PEACEFUL && this.func_70814_o() && super.func_205020_a(var1, var2);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
   }

   protected boolean func_146066_aG() {
      return true;
   }

   public boolean func_191990_c(EntityPlayer var1) {
      return true;
   }
}
