package net.minecraft.world.item.enchantment;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class DamageEnchantment extends Enchantment {
   private final int minCost;
   private final int levelCost;
   private final int levelCostSpan;
   private final Optional<TagKey<EntityType<?>>> targets;

   public DamageEnchantment(Enchantment.Rarity var1, int var2, int var3, int var4, Optional<TagKey<EntityType<?>>> var5, EquipmentSlot... var6) {
      super(var1, ItemTags.WEAPON_ENCHANTABLE, var6);
      this.minCost = var2;
      this.levelCost = var3;
      this.levelCostSpan = var4;
      this.targets = var5;
   }

   @Override
   public int getMinCost(int var1) {
      return this.minCost + (var1 - 1) * this.levelCost;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + this.levelCostSpan;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public float getDamageBonus(int var1, @Nullable EntityType<?> var2) {
      if (this.targets.isEmpty()) {
         return 1.0F + (float)Math.max(0, var1 - 1) * 0.5F;
      } else {
         return var2 != null && var2.is((TagKey<EntityType<?>>)this.targets.get()) ? (float)var1 * 2.5F : 0.0F;
      }
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return !(var1 instanceof DamageEnchantment);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void doPostAttack(LivingEntity var1, Entity var2, int var3) {
      if (this.targets.isPresent()
         && var2 instanceof LivingEntity var4
         && this.targets.get() == EntityTypeTags.ARTHROPOD
         && var3 > 0
         && var4.getType().is((TagKey<EntityType<?>>)this.targets.get())) {
         int var5 = 20 + var1.getRandom().nextInt(10 * var3);
         var4.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, var5, 3));
      }
   }
}
