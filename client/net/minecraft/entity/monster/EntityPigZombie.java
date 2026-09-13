package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
   private static final UUID field_110189_bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier field_110190_br = new AttributeModifier(field_110189_bq, "Attacking speed boost", 0.45, 0).func_111168_a(false);
   private int field_70837_d;
   private int field_70838_e;
   private Entity field_110191_bu;

   public EntityPigZombie(World var1) {
      super(var1);
      this.field_70178_ae = true;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(field_110186_bp).func_111128_a(0.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0);
   }

   @Override
   protected boolean func_70650_aV() {
      return false;
   }

   @Override
   public void func_70071_h_() {
      if (this.field_110191_bu != this.field_70789_a && !this.field_70170_p.field_72995_K) {
         IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var1.func_111124_b(field_110190_br);
         if (this.field_70789_a != null) {
            var1.func_111121_a(field_110190_br);
         }
      }

      this.field_110191_bu = this.field_70789_a;
      if (this.field_70838_e > 0 && --this.field_70838_e == 0) {
         this.func_85030_a(
            "mob.zombiepig.zpigangry", this.func_70599_aP() * 2.0F, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) * 1.8F
         );
      }

      super.func_70071_h_();
   }

   @Override
   public boolean func_70601_bi() {
      return this.field_70170_p.field_73013_u != EnumDifficulty.PEACEFUL
         && this.field_70170_p.func_72855_b(this.field_70121_D)
         && this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty()
         && !this.field_70170_p.func_72953_d(this.field_70121_D);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74777_a("Anger", (short)this.field_70837_d);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70837_d = var1.func_74765_d("Anger");
   }

   @Override
   protected Entity func_70782_k() {
      return this.field_70837_d == 0 ? null : super.func_70782_k();
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         Entity var3 = var1.func_76346_g();
         if (var3 instanceof EntityPlayer) {
            List var4 = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(32.0, 32.0, 32.0));

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = (Entity)var4.get(var5);
               if (var6 instanceof EntityPigZombie) {
                  EntityPigZombie var7 = (EntityPigZombie)var6;
                  var7.func_70835_c(var3);
               }
            }

            this.func_70835_c(var3);
         }

         return super.func_70097_a(var1, var2);
      }
   }

   private void func_70835_c(Entity var1) {
      this.field_70789_a = var1;
      this.field_70837_d = 400 + this.field_70146_Z.nextInt(400);
      this.field_70838_e = this.field_70146_Z.nextInt(40);
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.zombiepig.zpig";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.zombiepig.zpighurt";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.zombiepig.zpigdeath";
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(2 + var2);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151078_bh, 1);
      }

      var3 = this.field_70146_Z.nextInt(2 + var2);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.func_145779_a(Items.field_151074_bl, 1);
      }
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      return false;
   }

   @Override
   protected void func_70600_l(int var1) {
      this.func_145779_a(Items.field_151043_k, 1);
   }

   @Override
   protected void func_82164_bB() {
      this.func_70062_b(0, new ItemStack(Items.field_151010_B));
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      super.func_110161_a(var1);
      this.func_82229_g(false);
      return var1;
   }
}
