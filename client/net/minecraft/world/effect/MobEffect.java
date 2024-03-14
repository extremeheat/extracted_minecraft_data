package net.minecraft.world.effect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MobEffect {
   private static final int AMBIENT_ALPHA = Mth.floor(38.25F);
   private final Map<Holder<Attribute>, MobEffect.AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap();
   private final MobEffectCategory category;
   private final int color;
   private final Function<MobEffectInstance, ParticleOptions> particleFactory;
   @Nullable
   private String descriptionId;
   private int blendDurationTicks;

   protected MobEffect(MobEffectCategory var1, int var2) {
      super();
      this.category = var1;
      this.color = var2;
      this.particleFactory = var1x -> {
         int var2xx = var1x.isAmbient() ? AMBIENT_ALPHA : 255;
         return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, FastColor.ARGB32.color(var2xx, var2));
      };
   }

   protected MobEffect(MobEffectCategory var1, int var2, ParticleOptions var3) {
      super();
      this.category = var1;
      this.color = var2;
      this.particleFactory = var1x -> var3;
   }

   public int getBlendDurationTicks() {
      return this.blendDurationTicks;
   }

   public boolean applyEffectTick(LivingEntity var1, int var2) {
      return true;
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

   public MobEffect addAttributeModifier(Holder<Attribute> var1, String var2, double var3, AttributeModifier.Operation var5) {
      this.attributeModifiers.put(var1, new MobEffect.AttributeTemplate(UUID.fromString(var2), var3, var5));
      return this;
   }

   public MobEffect setBlendDuration(int var1) {
      this.blendDurationTicks = var1;
      return this;
   }

   public void createModifiers(int var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      this.attributeModifiers.forEach((var3, var4) -> var2.accept(var3, var4.create(this.getDescriptionId(), var1)));
   }

   public void removeAttributeModifiers(AttributeMap var1) {
      for(Entry var3 : this.attributeModifiers.entrySet()) {
         AttributeInstance var4 = var1.getInstance((Holder<Attribute>)var3.getKey());
         if (var4 != null) {
            var4.removeModifier(((MobEffect.AttributeTemplate)var3.getValue()).id());
         }
      }
   }

   public void addAttributeModifiers(AttributeMap var1, int var2) {
      for(Entry var4 : this.attributeModifiers.entrySet()) {
         AttributeInstance var5 = var1.getInstance((Holder<Attribute>)var4.getKey());
         if (var5 != null) {
            var5.removeModifier(((MobEffect.AttributeTemplate)var4.getValue()).id());
            var5.addPermanentModifier(((MobEffect.AttributeTemplate)var4.getValue()).create(this.getDescriptionId(), var2));
         }
      }
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }

   public ParticleOptions createParticleOptions(MobEffectInstance var1) {
      return this.particleFactory.apply(var1);
   }

   static record AttributeTemplate(UUID a, double b, AttributeModifier.Operation c) {
      private final UUID id;
      private final double amount;
      private final AttributeModifier.Operation operation;

      AttributeTemplate(UUID var1, double var2, AttributeModifier.Operation var4) {
         super();
         this.id = var1;
         this.amount = var2;
         this.operation = var4;
      }

      public AttributeModifier create(String var1, int var2) {
         return new AttributeModifier(this.id, var1 + " " + var2, this.amount * (double)(var2 + 1), this.operation);
      }
   }
}
