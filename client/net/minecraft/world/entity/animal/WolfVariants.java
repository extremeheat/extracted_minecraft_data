package net.minecraft.world.entity.animal;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class WolfVariants {
   public static final ResourceKey<WolfVariant> PALE = createKey("pale");
   public static final ResourceKey<WolfVariant> SPOTTED = createKey("spotted");
   public static final ResourceKey<WolfVariant> SNOWY = createKey("snowy");
   public static final ResourceKey<WolfVariant> BLACK = createKey("black");
   public static final ResourceKey<WolfVariant> ASHEN = createKey("ashen");
   public static final ResourceKey<WolfVariant> RUSTY = createKey("rusty");
   public static final ResourceKey<WolfVariant> WOODS = createKey("woods");
   public static final ResourceKey<WolfVariant> CHESTNUT = createKey("chestnut");
   public static final ResourceKey<WolfVariant> STRIPED = createKey("striped");
   public static final ResourceKey<WolfVariant> DEFAULT;

   public WolfVariants() {
      super();
   }

   private static ResourceKey<WolfVariant> createKey(String var0) {
      return ResourceKey.create(Registries.WOLF_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }

   static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, ResourceKey<Biome> var3) {
      register(var0, var1, var2, (HolderSet)HolderSet.direct(var0.lookup(Registries.BIOME).getOrThrow(var3)));
   }

   static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, TagKey<Biome> var3) {
      register(var0, var1, var2, (HolderSet)var0.lookup(Registries.BIOME).getOrThrow(var3));
   }

   static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, HolderSet<Biome> var3) {
      ResourceLocation var4 = ResourceLocation.withDefaultNamespace("entity/wolf/" + var2);
      ResourceLocation var5 = ResourceLocation.withDefaultNamespace("entity/wolf/" + var2 + "_tame");
      ResourceLocation var6 = ResourceLocation.withDefaultNamespace("entity/wolf/" + var2 + "_angry");
      var0.register(var1, new WolfVariant(var4, var5, var6, var3));
   }

   public static Holder<WolfVariant> getSpawnVariant(RegistryAccess var0, Holder<Biome> var1) {
      Registry var2 = var0.registryOrThrow(Registries.WOLF_VARIANT);
      Optional var10000 = var2.holders().filter((var1x) -> {
         return ((WolfVariant)var1x.value()).biomes().contains(var1);
      }).findFirst().or(() -> {
         return var2.getHolder(DEFAULT);
      });
      Objects.requireNonNull(var2);
      return (Holder)var10000.or(var2::getAny).orElseThrow();
   }

   public static void bootstrap(BootstrapContext<WolfVariant> var0) {
      register(var0, PALE, "wolf", Biomes.TAIGA);
      register(var0, SPOTTED, "wolf_spotted", BiomeTags.IS_SAVANNA);
      register(var0, SNOWY, "wolf_snowy", Biomes.GROVE);
      register(var0, BLACK, "wolf_black", Biomes.OLD_GROWTH_PINE_TAIGA);
      register(var0, ASHEN, "wolf_ashen", Biomes.SNOWY_TAIGA);
      register(var0, RUSTY, "wolf_rusty", BiomeTags.IS_JUNGLE);
      register(var0, WOODS, "wolf_woods", Biomes.FOREST);
      register(var0, CHESTNUT, "wolf_chestnut", Biomes.OLD_GROWTH_SPRUCE_TAIGA);
      register(var0, STRIPED, "wolf_striped", BiomeTags.IS_BADLANDS);
   }

   static {
      DEFAULT = PALE;
   }
}
