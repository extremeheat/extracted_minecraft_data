package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDonkey extends AbstractChestHorse {
   public EntityDonkey(World var1) {
      super(EntityType.field_200798_l, var1);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191190_H;
   }

   protected SoundEvent func_184639_G() {
      super.func_184639_G();
      return SoundEvents.field_187580_av;
   }

   protected SoundEvent func_184615_bR() {
      super.func_184615_bR();
      return SoundEvents.field_187586_ay;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      super.func_184601_bQ(var1);
      return SoundEvents.field_187588_az;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (!(var1 instanceof EntityDonkey) && !(var1 instanceof EntityHorse)) {
         return false;
      } else {
         return this.func_110200_cJ() && ((AbstractHorse)var1).func_110200_cJ();
      }
   }

   public EntityAgeable func_90011_a(EntityAgeable var1) {
      Object var2 = var1 instanceof EntityHorse ? new EntityMule(this.field_70170_p) : new EntityDonkey(this.field_70170_p);
      this.func_190681_a(var1, (AbstractHorse)var2);
      return (EntityAgeable)var2;
   }
}
