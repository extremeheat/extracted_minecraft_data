package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;

public class EntityPig extends EntityAnimal {
   private final EntityAIControlledByPlayer field_82184_d;

   public EntityPig(World var1) {
      super(var1);
      this.func_70105_a(0.9F, 0.9F);
      this.func_70661_as().func_75491_a(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.25));
      this.field_70714_bg.func_75776_a(2, this.field_82184_d = new EntityAIControlledByPlayer(this, 0.3F));
      this.field_70714_bg.func_75776_a(3, new EntityAIMate(this, 1.0));
      this.field_70714_bg.func_75776_a(4, new EntityAITempt(this, 1.2, Items.field_151146_bM, false));
      this.field_70714_bg.func_75776_a(4, new EntityAITempt(this, 1.2, Items.field_151172_bF, false));
      this.field_70714_bg.func_75776_a(5, new EntityAIFollowParent(this, 1.1));
      this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 1.0));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
   }

   @Override
   public boolean func_70650_aV() {
      return true;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25);
   }

   @Override
   protected void func_70619_bc() {
      super.func_70619_bc();
   }

   @Override
   public boolean func_82171_bF() {
      ItemStack var1 = ((EntityPlayer)this.field_70153_n).func_70694_bm();
      return var1 != null && var1.func_77973_b() == Items.field_151146_bM;
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("Saddle", this.func_70901_n());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70900_e(var1.func_74767_n("Saddle"));
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.pig.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.pig.say";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.pig.death";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.pig.step", 0.15F, 1.0F);
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      if (super.func_70085_c(var1)) {
         return true;
      } else if (!this.func_70901_n() || this.field_70170_p.field_72995_K || this.field_70153_n != null && this.field_70153_n != var1) {
         return false;
      } else {
         var1.func_70078_a(this);
         return true;
      }
   }

   @Override
   protected Item func_146068_u() {
      return this.func_70027_ad() ? Items.field_151157_am : Items.field_151147_al;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3) + 1 + this.field_70146_Z.nextInt(1 + var2);

      for(int var4 = 0; var4 < var3; ++var4) {
         if (this.func_70027_ad()) {
            this.func_145779_a(Items.field_151157_am, 1);
         } else {
            this.func_145779_a(Items.field_151147_al, 1);
         }
      }

      if (this.func_70901_n()) {
         this.func_145779_a(Items.field_151141_av, 1);
      }
   }

   public boolean func_70901_n() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_70900_e(boolean var1) {
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)1);
      } else {
         this.field_70180_af.func_75692_b(16, (byte)0);
      }
   }

   @Override
   public void func_70077_a(EntityLightningBolt var1) {
      if (!this.field_70170_p.field_72995_K) {
         EntityPigZombie var2 = new EntityPigZombie(this.field_70170_p);
         var2.func_70062_b(0, new ItemStack(Items.field_151010_B));
         var2.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_70170_p.func_72838_d(var2);
         this.func_70106_y();
      }
   }

   @Override
   protected void func_70069_a(float var1) {
      super.func_70069_a(var1);
      if (var1 > 5.0F && this.field_70153_n instanceof EntityPlayer) {
         ((EntityPlayer)this.field_70153_n).func_71029_a(AchievementList.field_76021_u);
      }
   }

   public EntityPig func_90011_a(EntityAgeable var1) {
      return new EntityPig(this.field_70170_p);
   }

   @Override
   public boolean func_70877_b(ItemStack var1) {
      return var1 != null && var1.func_77973_b() == Items.field_151172_bF;
   }

   public EntityAIControlledByPlayer func_82183_n() {
      return this.field_82184_d;
   }
}
