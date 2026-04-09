package net.minecraft.world.item.alchemy;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class Potion implements FeatureElement {
   public static final Codec<Holder<Potion>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Potion>> STREAM_CODEC;
   private final String name;
   private final List<MobEffectInstance> effects;
   private FeatureFlagSet requiredFeatures;

   public Potion(final String name, final MobEffectInstance... effects) {
      super();
      this.requiredFeatures = FeatureFlags.VANILLA_SET;
      this.name = name;
      this.effects = List.of(effects);
   }

   public Potion requiredFeatures(final FeatureFlag... flags) {
      this.requiredFeatures = FeatureFlags.REGISTRY.subset(flags);
      return this;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public List<MobEffectInstance> getEffects() {
      return this.effects;
   }

   public String name() {
      return this.name;
   }

   public boolean hasInstantEffects() {
      for(MobEffectInstance effect : this.effects) {
         if (((MobEffect)effect.getEffect().value()).isInstantaneous()) {
            return true;
         }
      }

      return false;
   }

   static {
      CODEC = BuiltInRegistries.POTION.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.POTION);
   }
}
