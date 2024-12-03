package net.minecraft.world.effect;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class MobEffect implements FeatureElement {
   public static final Codec<Holder<MobEffect>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> STREAM_CODEC;
   private static final int AMBIENT_ALPHA;
   private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap();
   private final MobEffectCategory category;
   private final int color;
   private final Function<MobEffectInstance, ParticleOptions> particleFactory;
   @Nullable
   private String descriptionId;
   private int blendDurationTicks;
   private Optional<SoundEvent> soundOnAdded = Optional.empty();
   private FeatureFlagSet requiredFeatures;

   protected MobEffect(MobEffectCategory var1, int var2) {
      super();
      this.requiredFeatures = FeatureFlags.VANILLA_SET;
      this.category = var1;
      this.color = var2;
      this.particleFactory = (var1x) -> {
         int var2x = var1x.isAmbient() ? AMBIENT_ALPHA : 255;
         return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(var2x, var2));
      };
   }

   protected MobEffect(MobEffectCategory var1, int var2, ParticleOptions var3) {
      super();
      this.requiredFeatures = FeatureFlags.VANILLA_SET;
      this.category = var1;
      this.color = var2;
      this.particleFactory = (var1x) -> var3;
   }

   public int getBlendDurationTicks() {
      return this.blendDurationTicks;
   }

   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      return true;
   }

   public void applyInstantenousEffect(ServerLevel var1, @Nullable Entity var2, @Nullable Entity var3, LivingEntity var4, int var5, double var6) {
      this.applyEffectTick(var1, var4, var5);
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return false;
   }

   public void onEffectStarted(LivingEntity var1, int var2) {
   }

   public void onEffectAdded(LivingEntity var1, int var2) {
      this.soundOnAdded.ifPresent((var1x) -> var1.level().playSound((Player)null, var1.getX(), var1.getY(), var1.getZ(), var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public void onMobRemoved(ServerLevel var1, LivingEntity var2, int var3, Entity.RemovalReason var4) {
   }

   public void onMobHurt(ServerLevel var1, LivingEntity var2, int var3, DamageSource var4, float var5) {
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

   public MobEffect addAttributeModifier(Holder<Attribute> var1, ResourceLocation var2, double var3, AttributeModifier.Operation var5) {
      this.attributeModifiers.put(var1, new AttributeTemplate(var2, var3, var5));
      return this;
   }

   public MobEffect setBlendDuration(int var1) {
      this.blendDurationTicks = var1;
      return this;
   }

   public void createModifiers(int var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      this.attributeModifiers.forEach((var2x, var3) -> var2.accept(var2x, var3.create(var1)));
   }

   public void removeAttributeModifiers(AttributeMap var1) {
      for(Map.Entry var3 : this.attributeModifiers.entrySet()) {
         AttributeInstance var4 = var1.getInstance((Holder)var3.getKey());
         if (var4 != null) {
            var4.removeModifier(((AttributeTemplate)var3.getValue()).id());
         }
      }

   }

   public void addAttributeModifiers(AttributeMap var1, int var2) {
      for(Map.Entry var4 : this.attributeModifiers.entrySet()) {
         AttributeInstance var5 = var1.getInstance((Holder)var4.getKey());
         if (var5 != null) {
            var5.removeModifier(((AttributeTemplate)var4.getValue()).id());
            var5.addPermanentModifier(((AttributeTemplate)var4.getValue()).create(var2));
         }
      }

   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }

   public ParticleOptions createParticleOptions(MobEffectInstance var1) {
      return (ParticleOptions)this.particleFactory.apply(var1);
   }

   public MobEffect withSoundOnAdded(SoundEvent var1) {
      this.soundOnAdded = Optional.of(var1);
      return this;
   }

   public MobEffect requiredFeatures(FeatureFlag... var1) {
      this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
      return this;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   static {
      CODEC = BuiltInRegistries.MOB_EFFECT.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);
      AMBIENT_ALPHA = Mth.floor(38.25F);
   }

   static record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
      AttributeTemplate(ResourceLocation var1, double var2, AttributeModifier.Operation var4) {
         super();
         this.id = var1;
         this.amount = var2;
         this.operation = var4;
      }

      public AttributeModifier create(int var1) {
         return new AttributeModifier(this.id, this.amount * (double)(var1 + 1), this.operation);
      }
   }
}
