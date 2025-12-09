package net.minecraft.world.entity.animal.wolf;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
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
      return ResourceKey.create(Registries.WOLF_VARIANT, Identifier.withDefaultNamespace(var0));
   }

   private static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, ResourceKey<Biome> var3) {
      register(var0, var1, var2, highPrioBiome(HolderSet.direct(var0.lookup(Registries.BIOME).getOrThrow(var3))));
   }

   private static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, TagKey<Biome> var3) {
      register(var0, var1, var2, highPrioBiome(var0.lookup(Registries.BIOME).getOrThrow(var3)));
   }

   private static SpawnPrioritySelectors highPrioBiome(HolderSet<Biome> var0) {
      return SpawnPrioritySelectors.single(new BiomeCheck(var0), 1);
   }

   private static void register(BootstrapContext<WolfVariant> var0, ResourceKey<WolfVariant> var1, String var2, SpawnPrioritySelectors var3) {
      Identifier var4 = Identifier.withDefaultNamespace("entity/wolf/" + var2);
      Identifier var5 = Identifier.withDefaultNamespace("entity/wolf/" + var2 + "_tame");
      Identifier var6 = Identifier.withDefaultNamespace("entity/wolf/" + var2 + "_angry");
      var0.register(var1, new WolfVariant(new WolfVariant.AssetInfo(new ClientAsset.ResourceTexture(var4), new ClientAsset.ResourceTexture(var5), new ClientAsset.ResourceTexture(var6)), var3));
   }

   public static void bootstrap(BootstrapContext<WolfVariant> var0) {
      register(var0, PALE, "wolf", SpawnPrioritySelectors.fallback(0));
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
