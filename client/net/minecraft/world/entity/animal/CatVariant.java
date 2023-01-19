package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CatVariant(ResourceLocation l) {
   private final ResourceLocation texture;
   public static final ResourceKey<CatVariant> TABBY = createKey("tabby");
   public static final ResourceKey<CatVariant> BLACK = createKey("black");
   public static final ResourceKey<CatVariant> RED = createKey("red");
   public static final ResourceKey<CatVariant> SIAMESE = createKey("siamese");
   public static final ResourceKey<CatVariant> BRITISH_SHORTHAIR = createKey("british_shorthair");
   public static final ResourceKey<CatVariant> CALICO = createKey("calico");
   public static final ResourceKey<CatVariant> PERSIAN = createKey("persian");
   public static final ResourceKey<CatVariant> RAGDOLL = createKey("ragdoll");
   public static final ResourceKey<CatVariant> WHITE = createKey("white");
   public static final ResourceKey<CatVariant> JELLIE = createKey("jellie");
   public static final ResourceKey<CatVariant> ALL_BLACK = createKey("all_black");

   public CatVariant(ResourceLocation var1) {
      super();
      this.texture = var1;
   }

   private static ResourceKey<CatVariant> createKey(String var0) {
      return ResourceKey.create(Registries.CAT_VARIANT, new ResourceLocation(var0));
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
      return Registry.register(var0, var1, new CatVariant(new ResourceLocation(var2)));
   }
}
