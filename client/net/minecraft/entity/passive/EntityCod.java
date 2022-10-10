package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityCod extends AbstractGroupFish {
   public EntityCod(World var1) {
      super(EntityType.field_203780_j, var1);
      this.func_70105_a(0.5F, 0.3F);
   }

   protected ItemStack func_203707_dx() {
      return new ItemStack(Items.field_203797_aN);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_203811_aE;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_203815_ax;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_203816_ay;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_203813_aA;
   }

   protected SoundEvent func_203701_dz() {
      return SoundEvents.field_203818_az;
   }
}
