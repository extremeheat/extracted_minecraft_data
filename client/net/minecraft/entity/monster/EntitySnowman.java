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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   public EntitySnowman(World var1) {
      super(var1);
      this.func_70105_a(0.7F, 1.9F);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
      this.field_70714_bg.func_75776_a(2, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(4, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, true, false, IMob.field_82192_a));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(4.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (!this.field_70170_p.field_72995_K) {
         int var1 = MathHelper.func_76128_c(this.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_70163_u);
         int var3 = MathHelper.func_76128_c(this.field_70161_v);
         if (this.func_70026_G()) {
            this.func_70097_a(DamageSource.field_76369_e, 1.0F);
         }

         if (this.field_70170_p.func_180494_b(new BlockPos(var1, 0, var3)).func_180626_a(new BlockPos(var1, var2, var3)) > 1.0F) {
            this.func_70097_a(DamageSource.field_76370_b, 1.0F);
         }

         for(int var4 = 0; var4 < 4; ++var4) {
            var1 = MathHelper.func_76128_c(this.field_70165_t + (double)((float)(var4 % 2 * 2 - 1) * 0.25F));
            var2 = MathHelper.func_76128_c(this.field_70163_u);
            var3 = MathHelper.func_76128_c(this.field_70161_v + (double)((float)(var4 / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos var5 = new BlockPos(var1, var2, var3);
            if (this.field_70170_p.func_180495_p(var5).func_177230_c().func_149688_o() == Material.field_151579_a && this.field_70170_p.func_180494_b(new BlockPos(var1, 0, var3)).func_180626_a(var5) < 0.8F && Blocks.field_150431_aC.func_176196_c(this.field_70170_p, var5)) {
               this.field_70170_p.func_175656_a(var5, Blocks.field_150431_aC.func_176223_P());
            }
         }
      }

   }

   protected Item func_146068_u() {
      return Items.field_151126_ay;
   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(16);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151126_ay, 1);
      }

   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntitySnowball var3 = new EntitySnowball(this.field_70170_p, this);
      double var4 = var1.field_70163_u + (double)var1.func_70047_e() - 1.100000023841858D;
      double var6 = var1.field_70165_t - this.field_70165_t;
      double var8 = var4 - var3.field_70163_u;
      double var10 = var1.field_70161_v - this.field_70161_v;
      float var12 = MathHelper.func_76133_a(var6 * var6 + var10 * var10) * 0.2F;
      var3.func_70186_c(var6, var8 + (double)var12, var10, 1.6F, 12.0F);
      this.func_85030_a("random.bow", 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }

   public float func_70047_e() {
      return 1.7F;
   }
}
