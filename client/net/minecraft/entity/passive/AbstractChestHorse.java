package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class AbstractChestHorse extends AbstractHorse {
   private static final DataParameter<Boolean> field_190698_bG;

   protected AbstractChestHorse(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_190688_bE = false;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_190698_bG, false);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)this.func_110267_cL());
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.17499999701976776D);
      this.func_110148_a(field_110271_bv).func_111128_a(0.5D);
   }

   public boolean func_190695_dh() {
      return (Boolean)this.field_70180_af.func_187225_a(field_190698_bG);
   }

   public void func_110207_m(boolean var1) {
      this.field_70180_af.func_187227_b(field_190698_bG, var1);
   }

   protected int func_190686_di() {
      return this.func_190695_dh() ? 17 : super.func_190686_di();
   }

   public double func_70042_X() {
      return super.func_70042_X() - 0.25D;
   }

   protected SoundEvent func_184785_dv() {
      super.func_184785_dv();
      return SoundEvents.field_187582_aw;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (this.func_190695_dh()) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_199703_a(Blocks.field_150486_ae);
         }

         this.func_110207_m(false);
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("ChestedHorse", this.func_190695_dh());
      if (this.func_190695_dh()) {
         NBTTagList var2 = new NBTTagList();

         for(int var3 = 2; var3 < this.field_110296_bG.func_70302_i_(); ++var3) {
            ItemStack var4 = this.field_110296_bG.func_70301_a(var3);
            if (!var4.func_190926_b()) {
               NBTTagCompound var5 = new NBTTagCompound();
               var5.func_74774_a("Slot", (byte)var3);
               var4.func_77955_b(var5);
               var2.add((INBTBase)var5);
            }
         }

         var1.func_74782_a("Items", var2);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_110207_m(var1.func_74767_n("ChestedHorse"));
      if (this.func_190695_dh()) {
         NBTTagList var2 = var1.func_150295_c("Items", 10);
         this.func_110226_cD();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            NBTTagCompound var4 = var2.func_150305_b(var3);
            int var5 = var4.func_74771_c("Slot") & 255;
            if (var5 >= 2 && var5 < this.field_110296_bG.func_70302_i_()) {
               this.field_110296_bG.func_70299_a(var5, ItemStack.func_199557_a(var4));
            }
         }
      }

      this.func_110232_cE();
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (var1 == 499) {
         if (this.func_190695_dh() && var2.func_190926_b()) {
            this.func_110207_m(false);
            this.func_110226_cD();
            return true;
         }

         if (!this.func_190695_dh() && var2.func_77973_b() == Blocks.field_150486_ae.func_199767_j()) {
            this.func_110207_m(true);
            this.func_110226_cD();
            return true;
         }
      }

      return super.func_174820_d(var1, var2);
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (var3.func_77973_b() instanceof ItemSpawnEgg) {
         return super.func_184645_a(var1, var2);
      } else {
         if (!this.func_70631_g_()) {
            if (this.func_110248_bS() && var1.func_70093_af()) {
               this.func_110199_f(var1);
               return true;
            }

            if (this.func_184207_aI()) {
               return super.func_184645_a(var1, var2);
            }
         }

         if (!var3.func_190926_b()) {
            boolean var4 = this.func_190678_b(var1, var3);
            if (!var4) {
               if (!this.func_110248_bS() || var3.func_77973_b() == Items.field_151057_cb) {
                  if (var3.func_111282_a(var1, this, var2)) {
                     return true;
                  } else {
                     this.func_190687_dF();
                     return true;
                  }
               }

               if (!this.func_190695_dh() && var3.func_77973_b() == Blocks.field_150486_ae.func_199767_j()) {
                  this.func_110207_m(true);
                  this.func_190697_dk();
                  var4 = true;
                  this.func_110226_cD();
               }

               if (!this.func_70631_g_() && !this.func_110257_ck() && var3.func_77973_b() == Items.field_151141_av) {
                  this.func_110199_f(var1);
                  return true;
               }
            }

            if (var4) {
               if (!var1.field_71075_bZ.field_75098_d) {
                  var3.func_190918_g(1);
               }

               return true;
            }
         }

         if (this.func_70631_g_()) {
            return super.func_184645_a(var1, var2);
         } else {
            this.func_110237_h(var1);
            return true;
         }
      }
   }

   protected void func_190697_dk() {
      this.func_184185_a(SoundEvents.field_187584_ax, 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
   }

   public int func_190696_dl() {
      return 5;
   }

   static {
      field_190698_bG = EntityDataManager.func_187226_a(AbstractChestHorse.class, DataSerializers.field_187198_h);
   }
}
