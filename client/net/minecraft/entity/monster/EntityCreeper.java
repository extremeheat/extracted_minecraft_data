package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob {
   private int field_70834_e;
   private int field_70833_d;
   private int field_82225_f = 30;
   private int field_82226_g = 3;
   private int field_175494_bm = 0;

   public EntityCreeper(World var1) {
      super(var1);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAICreeperSwell(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 0.8D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false, new Class[0]));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
   }

   public int func_82143_as() {
      return this.func_70638_az() == null ? 3 : 3 + (int)(this.func_110143_aJ() - 1.0F);
   }

   public void func_180430_e(float var1, float var2) {
      super.func_180430_e(var1, var2);
      this.field_70833_d = (int)((float)this.field_70833_d + var1 * 1.5F);
      if (this.field_70833_d > this.field_82225_f - 5) {
         this.field_70833_d = this.field_82225_f - 5;
      }

   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, -1);
      this.field_70180_af.func_75682_a(17, (byte)0);
      this.field_70180_af.func_75682_a(18, (byte)0);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_70180_af.func_75683_a(17) == 1) {
         var1.func_74757_a("powered", true);
      }

      var1.func_74777_a("Fuse", (short)this.field_82225_f);
      var1.func_74774_a("ExplosionRadius", (byte)this.field_82226_g);
      var1.func_74757_a("ignited", this.func_146078_ca());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70180_af.func_75692_b(17, (byte)(var1.func_74767_n("powered") ? 1 : 0));
      if (var1.func_150297_b("Fuse", 99)) {
         this.field_82225_f = var1.func_74765_d("Fuse");
      }

      if (var1.func_150297_b("ExplosionRadius", 99)) {
         this.field_82226_g = var1.func_74771_c("ExplosionRadius");
      }

      if (var1.func_74767_n("ignited")) {
         this.func_146079_cb();
      }

   }

   public void func_70071_h_() {
      if (this.func_70089_S()) {
         this.field_70834_e = this.field_70833_d;
         if (this.func_146078_ca()) {
            this.func_70829_a(1);
         }

         int var1 = this.func_70832_p();
         if (var1 > 0 && this.field_70833_d == 0) {
            this.func_85030_a("creeper.primed", 1.0F, 0.5F);
         }

         this.field_70833_d += var1;
         if (this.field_70833_d < 0) {
            this.field_70833_d = 0;
         }

         if (this.field_70833_d >= this.field_82225_f) {
            this.field_70833_d = this.field_82225_f;
            this.func_146077_cc();
         }
      }

      super.func_70071_h_();
   }

   protected String func_70621_aR() {
      return "mob.creeper.say";
   }

   protected String func_70673_aS() {
      return "mob.creeper.death";
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76346_g() instanceof EntitySkeleton) {
         int var2 = Item.func_150891_b(Items.field_151096_cd);
         int var3 = Item.func_150891_b(Items.field_151084_co);
         int var4 = var2 + this.field_70146_Z.nextInt(var3 - var2 + 1);
         this.func_145779_a(Item.func_150899_d(var4), 1);
      } else if (var1.func_76346_g() instanceof EntityCreeper && var1.func_76346_g() != this && ((EntityCreeper)var1.func_76346_g()).func_70830_n() && ((EntityCreeper)var1.func_76346_g()).func_70650_aV()) {
         ((EntityCreeper)var1.func_76346_g()).func_175493_co();
         this.func_70099_a(new ItemStack(Items.field_151144_bL, 1, 4), 0.0F);
      }

   }

   public boolean func_70652_k(Entity var1) {
      return true;
   }

   public boolean func_70830_n() {
      return this.field_70180_af.func_75683_a(17) == 1;
   }

   public float func_70831_j(float var1) {
      return ((float)this.field_70834_e + (float)(this.field_70833_d - this.field_70834_e) * var1) / (float)(this.field_82225_f - 2);
   }

   protected Item func_146068_u() {
      return Items.field_151016_H;
   }

   public int func_70832_p() {
      return this.field_70180_af.func_75683_a(16);
   }

   public void func_70829_a(int var1) {
      this.field_70180_af.func_75692_b(16, (byte)var1);
   }

   public void func_70077_a(EntityLightningBolt var1) {
      super.func_70077_a(var1);
      this.field_70180_af.func_75692_b(17, (byte)1);
   }

   protected boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && var2.func_77973_b() == Items.field_151033_d) {
         this.field_70170_p.func_72908_a(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D, "fire.ignite", 1.0F, this.field_70146_Z.nextFloat() * 0.4F + 0.8F);
         var1.func_71038_i();
         if (!this.field_70170_p.field_72995_K) {
            this.func_146079_cb();
            var2.func_77972_a(1, var1);
            return true;
         }
      }

      return super.func_70085_c(var1);
   }

   private void func_146077_cc() {
      if (!this.field_70170_p.field_72995_K) {
         boolean var1 = this.field_70170_p.func_82736_K().func_82766_b("mobGriefing");
         float var2 = this.func_70830_n() ? 2.0F : 1.0F;
         this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float)this.field_82226_g * var2, var1);
         this.func_70106_y();
      }

   }

   public boolean func_146078_ca() {
      return this.field_70180_af.func_75683_a(18) != 0;
   }

   public void func_146079_cb() {
      this.field_70180_af.func_75692_b(18, (byte)1);
   }

   public boolean func_70650_aV() {
      return this.field_175494_bm < 1 && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot");
   }

   public void func_175493_co() {
      ++this.field_175494_bm;
   }
}
