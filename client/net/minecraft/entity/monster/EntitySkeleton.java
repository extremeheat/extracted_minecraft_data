package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkeleton extends AbstractSkeleton {
   public EntitySkeleton(World var1) {
      super(EntityType.field_200741_ag, var1);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186385_aj;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187854_fc;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187864_fh;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187856_fd;
   }

   SoundEvent func_190727_o() {
      return SoundEvents.field_187868_fj;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76346_g() instanceof EntityCreeper) {
         EntityCreeper var2 = (EntityCreeper)var1.func_76346_g();
         if (var2.func_70830_n() && var2.func_70650_aV()) {
            var2.func_175493_co();
            this.func_199703_a(Items.field_196182_dv);
         }
      }

   }

   protected EntityArrow func_190726_a(float var1) {
      ItemStack var2 = this.func_184582_a(EntityEquipmentSlot.OFFHAND);
      if (var2.func_77973_b() == Items.field_185166_h) {
         EntitySpectralArrow var4 = new EntitySpectralArrow(this.field_70170_p, this);
         var4.func_190547_a(this, var1);
         return var4;
      } else {
         EntityArrow var3 = super.func_190726_a(var1);
         if (var2.func_77973_b() == Items.field_185167_i && var3 instanceof EntityTippedArrow) {
            ((EntityTippedArrow)var3).func_184555_a(var2);
         }

         return var3;
      }
   }
}
