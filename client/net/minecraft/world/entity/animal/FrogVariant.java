package net.minecraft.world.entity.animal;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record FrogVariant(ResourceLocation texture) {
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FrogVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.FROG_VARIANT);
   public static final ResourceKey<FrogVariant> TEMPERATE = createKey("temperate");
   public static final ResourceKey<FrogVariant> WARM = createKey("warm");
   public static final ResourceKey<FrogVariant> COLD = createKey("cold");

   public FrogVariant(ResourceLocation texture) {
      super();
      this.texture = texture;
   }

   private static ResourceKey<FrogVariant> createKey(String var0) {
      return ResourceKey.create(Registries.FROG_VARIANT, new ResourceLocation(var0));
   }

   public static FrogVariant bootstrap(Registry<FrogVariant> var0) {
      register(var0, TEMPERATE, "textures/entity/frog/temperate_frog.png");
      register(var0, WARM, "textures/entity/frog/warm_frog.png");
      return register(var0, COLD, "textures/entity/frog/cold_frog.png");
   }

   private static FrogVariant register(Registry<FrogVariant> var0, ResourceKey<FrogVariant> var1, String var2) {
      return Registry.register(var0, var1, new FrogVariant(new ResourceLocation(var2)));
   }
}
