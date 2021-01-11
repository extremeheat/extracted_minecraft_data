package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
   private static final UUID field_110189_bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier field_110190_br;
   private int field_70837_d;
   private int field_70838_e;
   private UUID field_175459_bn;

   public EntityPigZombie(World var1) {
      super(var1);
      this.field_70178_ae = true;
   }

   public void func_70604_c(EntityLivingBase var1) {
      super.func_70604_c(var1);
      if (var1 != null) {
         this.field_175459_bn = var1.func_110124_au();
      }

   }

   protected void func_175456_n() {
      this.field_70715_bh.func_75776_a(1, new EntityPigZombie.AIHurtByAggressor(this));
      this.field_70715_bh.func_75776_a(2, new EntityPigZombie.AITargetAggressor(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(field_110186_bp).func_111128_a(0.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0D);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
   }

   protected void func_70619_bc() {
      IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      if (this.func_175457_ck()) {
         if (!this.func_70631_g_() && !var1.func_180374_a(field_110190_br)) {
            var1.func_111121_a(field_110190_br);
         }

         --this.field_70837_d;
      } else if (var1.func_180374_a(field_110190_br)) {
         var1.func_111124_b(field_110190_br);
      }

      if (this.field_70838_e > 0 && --this.field_70838_e == 0) {
         this.func_85030_a("mob.zombiepig.zpigangry", this.func_70599_aP() * 2.0F, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.field_70837_d > 0 && this.field_175459_bn != null && this.func_70643_av() == null) {
         EntityPlayer var2 = this.field_70170_p.func_152378_a(this.field_175459_bn);
         this.func_70604_c(var2);
         this.field_70717_bb = var2;
         this.field_70718_bc = this.func_142015_aE();
      }

      super.func_70619_bc();
   }

   public boolean func_70601_bi() {
      return this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL;
   }

   public boolean func_70058_J() {
      return this.field_70170_p.func_72917_a(this.func_174813_aQ(), this) && this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty() && !this.field_70170_p.func_72953_d(this.func_174813_aQ());
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74777_a("Anger", (short)this.field_70837_d);
      if (this.field_175459_bn != null) {
         var1.func_74778_a("HurtBy", this.field_175459_bn.toString());
      } else {
         var1.func_74778_a("HurtBy", "");
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70837_d = var1.func_74765_d("Anger");
      String var2 = var1.func_74779_i("HurtBy");
      if (var2.length() > 0) {
         this.field_175459_bn = UUID.fromString(var2);
         EntityPlayer var3 = this.field_70170_p.func_152378_a(this.field_175459_bn);
         this.func_70604_c(var3);
         if (var3 != null) {
            this.field_70717_bb = var3;
            this.field_70718_bc = this.func_142015_aE();
         }
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         Entity var3 = var1.func_76346_g();
         if (var3 instanceof EntityPlayer) {
            this.func_70835_c(var3);
         }

         return super.func_70097_a(var1, var2);
      }
   }

   private void func_70835_c(Entity var1) {
      this.field_70837_d = 400 + this.field_70146_Z.nextInt(400);
      this.field_70838_e = this.field_70146_Z.nextInt(40);
      if (var1 instanceof EntityLivingBase) {
         this.func_70604_c((EntityLivingBase)var1);
      }

   }

   public boolean func_175457_ck() {
      return this.field_70837_d > 0;
   }

   protected String func_70639_aQ() {
      return "mob.zombiepig.zpig";
   }

   protected String func_70621_aR() {
      return "mob.zombiepig.zpighurt";
   }

   protected String func_70673_aS() {
      return "mob.zombiepig.zpigdeath";
   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(2 + var2);

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151078_bh, 1);
      }

      var3 = this.field_70146_Z.nextInt(2 + var2);

      for(var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151074_bl, 1);
      }

   }

   public boolean func_70085_c(EntityPlayer var1) {
      return false;
   }

   protected void func_82164_bB() {
      this.func_145779_a(Items.field_151043_k, 1);
   }

   protected void func_180481_a(DifficultyInstance var1) {
      this.func_70062_b(0, new ItemStack(Items.field_151010_B));
   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      super.func_180482_a(var1, var2);
      this.func_82229_g(false);
      return var2;
   }

   static {
      field_110190_br = (new AttributeModifier(field_110189_bq, "Attacking speed boost", 0.05D, 0)).func_111168_a(false);
   }

   static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AITargetAggressor(EntityPigZombie var1) {
         super(var1, EntityPlayer.class, true);
      }

      public boolean func_75250_a() {
         return ((EntityPigZombie)this.field_75299_d).func_175457_ck() && super.func_75250_a();
      }
   }

   static class AIHurtByAggressor extends EntityAIHurtByTarget {
      public AIHurtByAggressor(EntityPigZombie var1) {
         super(var1, true);
      }

      protected void func_179446_a(EntityCreature var1, EntityLivingBase var2) {
         super.func_179446_a(var1, var2);
         if (var1 instanceof EntityPigZombie) {
            ((EntityPigZombie)var1).func_70835_c(var2);
         }

      }
   }
}
