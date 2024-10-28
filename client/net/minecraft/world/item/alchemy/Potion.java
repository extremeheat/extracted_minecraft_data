package net.minecraft.world.item.alchemy;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
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
   @Nullable
   private final String name;
   private final List<MobEffectInstance> effects;
   private FeatureFlagSet requiredFeatures;

   public Potion(MobEffectInstance... var1) {
      this((String)null, var1);
   }

   public Potion(@Nullable String var1, MobEffectInstance... var2) {
      super();
      this.requiredFeatures = FeatureFlags.VANILLA_SET;
      this.name = var1;
      this.effects = List.of(var2);
   }

   public Potion requiredFeatures(FeatureFlag... var1) {
      this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
      return this;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public static String getName(Optional<Holder<Potion>> var0, String var1) {
      String var2;
      if (var0.isPresent()) {
         var2 = ((Potion)((Holder)var0.get()).value()).name;
         if (var2 != null) {
            return var1 + var2;
         }
      }

      var2 = (String)var0.flatMap(Holder::unwrapKey).map((var0x) -> {
         return var0x.location().getPath();
      }).orElse("empty");
      return var1 + var2;
   }

   public List<MobEffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if (!this.effects.isEmpty()) {
         Iterator var1 = this.effects.iterator();

         while(var1.hasNext()) {
            MobEffectInstance var2 = (MobEffectInstance)var1.next();
            if (((MobEffect)var2.getEffect().value()).isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }

   static {
      CODEC = BuiltInRegistries.POTION.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.POTION);
   }
}
