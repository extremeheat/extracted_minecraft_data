package net.minecraft.world.item.enchantment;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class DamageEnchantment extends Enchantment {
   private final Optional<TagKey<EntityType<?>>> targets;

   public DamageEnchantment(Enchantment.EnchantmentDefinition var1, Optional<TagKey<EntityType<?>>> var2) {
      super(var1);
      this.targets = var2;
   }

   @Override
   public float getDamageBonus(int var1, @Nullable EntityType<?> var2) {
      if (this.targets.isEmpty()) {
         return 1.0F + (float)Math.max(0, var1 - 1) * 0.5F;
      } else {
         return var2 != null && var2.is(this.targets.get()) ? (float)var1 * 2.5F : 0.0F;
      }
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return !(var1 instanceof DamageEnchantment);
   }

   @Override
   public void doPostAttack(LivingEntity var1, Entity var2, int var3) {
      if (this.targets.isPresent()
         && var2 instanceof LivingEntity var4
         && this.targets.get() == EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS
         && var3 > 0
         && var4.getType().is(this.targets.get())) {
         int var5 = 20 + var1.getRandom().nextInt(10 * var3);
         var4.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, var5, 3));
      }
   }
}
