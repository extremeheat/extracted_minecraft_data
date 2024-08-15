package net.minecraft.world.effect;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class MobEffect implements FeatureElement {
   public static final Codec<Holder<MobEffect>> CODEC = BuiltInRegistries.MOB_EFFECT.holderByNameCodec();
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);
   private static final int AMBIENT_ALPHA = Mth.floor(38.25F);
   private final Map<Holder<Attribute>, MobEffect.AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap();
   private final MobEffectCategory category;
   private final int color;
   private final Function<MobEffectInstance, ParticleOptions> particleFactory;
   @Nullable
   private String descriptionId;
   private int blendDurationTicks;
   private Optional<SoundEvent> soundOnAdded = Optional.empty();
   private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

   protected MobEffect(MobEffectCategory var1, int var2) {
      super();
      this.category = var1;
      this.color = var2;
      this.particleFactory = var1x -> {
         int var2x = var1x.isAmbient() ? AMBIENT_ALPHA : 255;
         return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(var2x, var2));
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

   public void onEffectAdded(LivingEntity var1, int var2) {
      this.soundOnAdded.ifPresent(var1x -> var1.level().playSound(null, var1.getX(), var1.getY(), var1.getZ(), var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public void onMobRemoved(LivingEntity var1, int var2, Entity.RemovalReason var3) {
   }

   public void onMobHurt(LivingEntity var1, int var2, DamageSource var3, float var4) {
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
      this.attributeModifiers.put(var1, new MobEffect.AttributeTemplate(var2, var3, var5));
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
      for (Entry var3 : this.attributeModifiers.entrySet()) {
         AttributeInstance var4 = var1.getInstance((Holder<Attribute>)var3.getKey());
         if (var4 != null) {
            var4.removeModifier(((MobEffect.AttributeTemplate)var3.getValue()).id());
         }
      }
   }

   public void addAttributeModifiers(AttributeMap var1, int var2) {
      for (Entry var4 : this.attributeModifiers.entrySet()) {
         AttributeInstance var5 = var1.getInstance((Holder<Attribute>)var4.getKey());
         if (var5 != null) {
            var5.removeModifier(((MobEffect.AttributeTemplate)var4.getValue()).id());
            var5.addPermanentModifier(((MobEffect.AttributeTemplate)var4.getValue()).create(var2));
         }
      }
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }

   public ParticleOptions createParticleOptions(MobEffectInstance var1) {
      return this.particleFactory.apply(var1);
   }

   public MobEffect withSoundOnAdded(SoundEvent var1) {
      this.soundOnAdded = Optional.of(var1);
      return this;
   }

   public MobEffect requiredFeatures(FeatureFlag... var1) {
      this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
      return this;
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
