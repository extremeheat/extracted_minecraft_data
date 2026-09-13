package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   public EntitySnowman(World var1) {
      super(var1);
      this.func_70105_a(0.4F, 1.8F);
      this.func_70661_as().func_75491_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0F));
      this.field_70714_bg.func_75776_a(2, new EntityAIWander(this, 1.0));
      this.field_70714_bg.func_75776_a(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(4, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.field_82192_a));
   }

   @Override
   public boolean func_70650_aV() {
      return true;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(4.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224);
   }

   @Override
   public void func_70636_d() {
      super.func_70636_d();
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70163_u);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.func_70026_G()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      if (this.field_70170_p.func_72807_a(var1, var3).func_150564_a(var1, var2, var3) > 1.0F) {
         this.func_70097_a(DamageSource.field_76370_b, 1.0F);
      }

      for(int var4 = 0; var4 < 4; ++var4) {
         var1 = MathHelper.func_76128_c(this.field_70165_t + (double)((float)(var4 % 2 * 2 - 1) * 0.25F));
         var2 = MathHelper.func_76128_c(this.field_70163_u);
         var3 = MathHelper.func_76128_c(this.field_70161_v + (double)((float)(var4 / 2 % 2 * 2 - 1) * 0.25F));
         if (this.field_70170_p.func_147439_a(var1, var2, var3).func_149688_o() == Material.field_151579_a
            && this.field_70170_p.func_72807_a(var1, var3).func_150564_a(var1, var2, var3) < 0.8F
            && Blocks.field_150431_aC.func_149742_c(this.field_70170_p, var1, var2, var3)) {
            this.field_70170_p.func_147449_b(var1, var2, var3, Blocks.field_150431_aC);
         }
      }
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151126_ay;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(16);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151126_ay, 1);
      }
   }

   @Override
   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntitySnowball var3 = new EntitySnowball(this.field_70170_p, this);
      double var4 = var1.field_70165_t - this.field_70165_t;
      double var6 = var1.field_70163_u + (double)var1.func_70047_e() - 1.100000023841858 - var3.field_70163_u;
      double var8 = var1.field_70161_v - this.field_70161_v;
      float var10 = MathHelper.func_76133_a(var4 * var4 + var8 * var8) * 0.2F;
      var3.func_70186_c(var4, var6 + (double)var10, var8, 1.6F, 12.0F);
      this.func_85030_a("random.bow", 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }
}
