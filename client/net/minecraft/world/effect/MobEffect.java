package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class MobEffect {
   private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
   private final MobEffectCategory category;
   private final int color;
   @Nullable
   private String descriptionId;
   private Supplier<MobEffectInstance.FactorData> factorDataFactory = () -> null;

   @Nullable
   public static MobEffect byId(int var0) {
      return BuiltInRegistries.MOB_EFFECT.byId(var0);
   }

   public static int getId(MobEffect var0) {
      return BuiltInRegistries.MOB_EFFECT.getId(var0);
   }

   public static int getIdFromNullable(@Nullable MobEffect var0) {
      return BuiltInRegistries.MOB_EFFECT.getId(var0);
   }

   protected MobEffect(MobEffectCategory var1, int var2) {
      super();
      this.category = var1;
      this.color = var2;
   }

   public Optional<MobEffectInstance.FactorData> createFactorData() {
      return Optional.ofNullable(this.factorDataFactory.get());
   }

   public void applyEffectTick(LivingEntity var1, int var2) {
      if (this == MobEffects.REGENERATION) {
         if (var1.getHealth() < var1.getMaxHealth()) {
            var1.heal(1.0F);
         }
      } else if (this == MobEffects.POISON) {
         if (var1.getHealth() > 1.0F) {
            var1.hurt(var1.damageSources().magic(), 1.0F);
         }
      } else if (this == MobEffects.WITHER) {
         var1.hurt(var1.damageSources().wither(), 1.0F);
      } else if (this == MobEffects.HUNGER && var1 instanceof Player) {
         ((Player)var1).causeFoodExhaustion(0.005F * (float)(var2 + 1));
      } else if (this == MobEffects.SATURATION && var1 instanceof Player) {
         if (!var1.level().isClientSide) {
            ((Player)var1).getFoodData().eat(var2 + 1, 1.0F);
         }
      } else if ((this != MobEffects.HEAL || var1.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !var1.isInvertedHealAndHarm())) {
         if (this == MobEffects.HARM && !var1.isInvertedHealAndHarm() || this == MobEffects.HEAL && var1.isInvertedHealAndHarm()) {
            var1.hurt(var1.damageSources().magic(), (float)(6 << var2));
         }
      } else {
         var1.heal((float)Math.max(4 << var2, 0));
      }
   }

   public void applyInstantenousEffect(@Nullable Entity var1, @Nullable Entity var2, LivingEntity var3, int var4, double var5) {
      if ((this != MobEffects.HEAL || var3.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !var3.isInvertedHealAndHarm())) {
         if (this == MobEffects.HARM && !var3.isInvertedHealAndHarm() || this == MobEffects.HEAL && var3.isInvertedHealAndHarm()) {
            int var8 = (int)(var5 * (double)(6 << var4) + 0.5);
            if (var1 == null) {
               var3.hurt(var3.damageSources().magic(), (float)var8);
            } else {
               var3.hurt(var3.damageSources().indirectMagic(var1, var2), (float)var8);
            }
         } else {
            this.applyEffectTick(var3, var4);
         }
      } else {
         int var7 = (int)(var5 * (double)(4 << var4) + 0.5);
         var3.heal((float)var7);
      }
   }

   public boolean isDurationEffectTick(int var1, int var2) {
      if (this == MobEffects.REGENERATION) {
         int var5 = 50 >> var2;
         if (var5 > 0) {
            return var1 % var5 == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.POISON) {
         int var4 = 25 >> var2;
         if (var4 > 0) {
            return var1 % var4 == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.WITHER) {
         int var3 = 40 >> var2;
         if (var3 > 0) {
            return var1 % var3 == 0;
         } else {
            return true;
         }
      } else {
         return this == MobEffects.HUNGER;
      }
   }

   public boolean isInstantenous() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("effect", BuiltInRegistries.MOB_EFFECT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getDisplayName() {
      return Component.translatable(this.getDescriptionId());
   }

   public MobEffectCategory getCategory() {
      return this.category;
   }

   public int getColor() {
      return this.color;
   }

   public MobEffect addAttributeModifier(Attribute var1, String var2, double var3, AttributeModifier.Operation var5) {
      AttributeModifier var6 = new AttributeModifier(UUID.fromString(var2), this::getDescriptionId, var3, var5);
      this.attributeModifiers.put(var1, var6);
      return this;
   }

   public MobEffect setFactorDataFactory(Supplier<MobEffectInstance.FactorData> var1) {
      this.factorDataFactory = var1;
      return this;
   }

   public Map<Attribute, AttributeModifier> getAttributeModifiers() {
      return this.attributeModifiers;
   }

   public void removeAttributeModifiers(LivingEntity var1, AttributeMap var2, int var3) {
      for(Entry var5 : this.attributeModifiers.entrySet()) {
         AttributeInstance var6 = var2.getInstance((Attribute)var5.getKey());
         if (var6 != null) {
            var6.removeModifier((AttributeModifier)var5.getValue());
         }
      }
   }

   public void addAttributeModifiers(LivingEntity var1, AttributeMap var2, int var3) {
      for(Entry var5 : this.attributeModifiers.entrySet()) {
         AttributeInstance var6 = var2.getInstance((Attribute)var5.getKey());
         if (var6 != null) {
            AttributeModifier var7 = (AttributeModifier)var5.getValue();
            var6.removeModifier(var7);
            var6.addPermanentModifier(
               new AttributeModifier(var7.getId(), this.getDescriptionId() + " " + var3, this.getAttributeModifierValue(var3, var7), var7.getOperation())
            );
         }
      }
   }

   public double getAttributeModifierValue(int var1, AttributeModifier var2) {
      return var2.getAmount() * (double)(var1 + 1);
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }
}
