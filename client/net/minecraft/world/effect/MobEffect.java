package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MobEffect {
   private final Map<Attribute, AttributeModifierTemplate> attributeModifiers = Maps.newHashMap();
   private final MobEffectCategory category;
   private final int color;
   @Nullable
   private String descriptionId;
   private Supplier<MobEffectInstance.FactorData> factorDataFactory = () -> null;
   private final Holder.Reference<MobEffect> builtInRegistryHolder = BuiltInRegistries.MOB_EFFECT.createIntrusiveHolder(this);

   protected MobEffect(MobEffectCategory var1, int var2) {
      super();
      this.category = var1;
      this.color = var2;
   }

   public Optional<MobEffectInstance.FactorData> createFactorData() {
      return Optional.ofNullable(this.factorDataFactory.get());
   }

   public void applyEffectTick(LivingEntity var1, int var2) {
   }

   public void applyInstantenousEffect(@Nullable Entity var1, @Nullable Entity var2, LivingEntity var3, int var4, double var5) {
      this.applyEffectTick(var3, var4);
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return false;
   }

   public void onEffectStarted(LivingEntity var1, int var2) {
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
      this.attributeModifiers.put(var1, new MobEffect.MobEffectAttributeModifierTemplate(UUID.fromString(var2), var3, var5));
      return this;
   }

   public MobEffect setFactorDataFactory(Supplier<MobEffectInstance.FactorData> var1) {
      this.factorDataFactory = var1;
      return this;
   }

   public Map<Attribute, AttributeModifierTemplate> getAttributeModifiers() {
      return this.attributeModifiers;
   }

   public void removeAttributeModifiers(AttributeMap var1) {
      for(Entry var3 : this.attributeModifiers.entrySet()) {
         AttributeInstance var4 = var1.getInstance((Attribute)var3.getKey());
         if (var4 != null) {
            var4.removeModifier(((AttributeModifierTemplate)var3.getValue()).getAttributeModifierId());
         }
      }
   }

   public void addAttributeModifiers(AttributeMap var1, int var2) {
      for(Entry var4 : this.attributeModifiers.entrySet()) {
         AttributeInstance var5 = var1.getInstance((Attribute)var4.getKey());
         if (var5 != null) {
            var5.removeModifier(((AttributeModifierTemplate)var4.getValue()).getAttributeModifierId());
            var5.addPermanentModifier(((AttributeModifierTemplate)var4.getValue()).create(var2));
         }
      }
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }

   @Deprecated
   public Holder.Reference<MobEffect> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   class MobEffectAttributeModifierTemplate implements AttributeModifierTemplate {
      private final UUID id;
      private final double amount;
      private final AttributeModifier.Operation operation;

      public MobEffectAttributeModifierTemplate(UUID var2, double var3, AttributeModifier.Operation var5) {
         super();
         this.id = var2;
         this.amount = var3;
         this.operation = var5;
      }

      @Override
      public UUID getAttributeModifierId() {
         return this.id;
      }

      @Override
      public AttributeModifier create(int var1) {
         return new AttributeModifier(this.id, MobEffect.this.getDescriptionId() + " " + var1, this.amount * (double)(var1 + 1), this.operation);
      }
   }
}
