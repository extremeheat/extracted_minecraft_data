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

public class EntitySalmon extends AbstractGroupFish {
   public EntitySalmon(World var1) {
      super(EntityType.field_203778_ae, var1);
      this.func_70105_a(0.7F, 0.4F);
   }

   public int func_203704_dv() {
      return 5;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_203810_aD;
   }

   protected ItemStack func_203707_dx() {
      return new ItemStack(Items.field_203796_aM);
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_203820_gM;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_203821_gN;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_203823_gP;
   }

   protected SoundEvent func_203701_dz() {
      return SoundEvents.field_203822_gO;
   }
}
