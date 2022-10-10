package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityZombieHorse extends AbstractHorse {
   public EntityZombieHorse(World var1) {
      super(EntityType.field_200726_aE, var1);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(15.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
      this.func_110148_a(field_110271_bv).func_111128_a(this.func_110245_cM());
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEAD;
   }

   protected SoundEvent func_184639_G() {
      super.func_184639_G();
      return SoundEvents.field_187931_he;
   }

   protected SoundEvent func_184615_bR() {
      super.func_184615_bR();
      return SoundEvents.field_187932_hf;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      super.func_184601_bQ(var1);
      return SoundEvents.field_187933_hg;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186397_E;
   }

   @Nullable
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return new EntityZombieHorse(this.field_70170_p);
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (var3.func_77973_b() instanceof ItemSpawnEgg) {
         return super.func_184645_a(var1, var2);
      } else if (!this.func_110248_bS()) {
         return false;
      } else if (this.func_70631_g_()) {
         return super.func_184645_a(var1, var2);
      } else if (var1.func_70093_af()) {
         this.func_110199_f(var1);
         return true;
      } else if (this.func_184207_aI()) {
         return super.func_184645_a(var1, var2);
      } else {
         if (!var3.func_190926_b()) {
            if (!this.func_110257_ck() && var3.func_77973_b() == Items.field_151141_av) {
               this.func_110199_f(var1);
               return true;
            }

            if (var3.func_111282_a(var1, this, var2)) {
               return true;
            }
         }

         this.func_110237_h(var1);
         return true;
      }
   }

   protected void func_205714_dM() {
   }
}
