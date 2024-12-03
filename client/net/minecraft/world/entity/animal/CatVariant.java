package net.minecraft.world.entity.animal;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CatVariant(ResourceLocation texture) {
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CatVariant>> STREAM_CODEC;
   public static final ResourceKey<CatVariant> TABBY;
   public static final ResourceKey<CatVariant> BLACK;
   public static final ResourceKey<CatVariant> RED;
   public static final ResourceKey<CatVariant> SIAMESE;
   public static final ResourceKey<CatVariant> BRITISH_SHORTHAIR;
   public static final ResourceKey<CatVariant> CALICO;
   public static final ResourceKey<CatVariant> PERSIAN;
   public static final ResourceKey<CatVariant> RAGDOLL;
   public static final ResourceKey<CatVariant> WHITE;
   public static final ResourceKey<CatVariant> JELLIE;
   public static final ResourceKey<CatVariant> ALL_BLACK;

   public CatVariant(ResourceLocation var1) {
      super();
      this.texture = var1;
   }

   private static ResourceKey<CatVariant> createKey(String var0) {
      return ResourceKey.create(Registries.CAT_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }

   public static CatVariant bootstrap(Registry<CatVariant> var0) {
      register(var0, TABBY, "textures/entity/cat/tabby.png");
      register(var0, BLACK, "textures/entity/cat/black.png");
      register(var0, RED, "textures/entity/cat/red.png");
      register(var0, SIAMESE, "textures/entity/cat/siamese.png");
      register(var0, BRITISH_SHORTHAIR, "textures/entity/cat/british_shorthair.png");
      register(var0, CALICO, "textures/entity/cat/calico.png");
      register(var0, PERSIAN, "textures/entity/cat/persian.png");
      register(var0, RAGDOLL, "textures/entity/cat/ragdoll.png");
      register(var0, WHITE, "textures/entity/cat/white.png");
      register(var0, JELLIE, "textures/entity/cat/jellie.png");
      return register(var0, ALL_BLACK, "textures/entity/cat/all_black.png");
   }

   private static CatVariant register(Registry<CatVariant> var0, ResourceKey<CatVariant> var1, String var2) {
      return (CatVariant)Registry.register(var0, (ResourceKey)var1, new CatVariant(ResourceLocation.withDefaultNamespace(var2)));
   }

   static {
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CAT_VARIANT);
      TABBY = createKey("tabby");
      BLACK = createKey("black");
      RED = createKey("red");
      SIAMESE = createKey("siamese");
      BRITISH_SHORTHAIR = createKey("british_shorthair");
      CALICO = createKey("calico");
      PERSIAN = createKey("persian");
      RAGDOLL = createKey("ragdoll");
      WHITE = createKey("white");
      JELLIE = createKey("jellie");
      ALL_BLACK = createKey("all_black");
   }
}
