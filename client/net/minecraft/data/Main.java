package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.WorldVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.advancements.packs.UpdateOneTwentyOneAdvancementProvider;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.data.loot.packs.UpdateOneTwentyOneLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.packs.BundleRecipeProvider;
import net.minecraft.data.recipes.packs.UpdateOneTwentyOneRecipeProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.UpdateOneTwentyOneRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.CatVariantTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FlatLevelGeneratorPresetTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.TradeRebalanceStructureTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneBiomeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneBlockTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneDamageTypeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneEntityTypeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneItemTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class Main {
   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) throws IOException {
      SharedConstants.tryDetectVersion();
      OptionParser var1 = new OptionParser();
      AbstractOptionSpec var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder var3 = var1.accepts("server", "Include server generators");
      OptionSpecBuilder var4 = var1.accepts("client", "Include client generators");
      OptionSpecBuilder var5 = var1.accepts("dev", "Include development tools");
      OptionSpecBuilder var6 = var1.accepts("reports", "Include data reports");
      OptionSpecBuilder var7 = var1.accepts("validate", "Validate inputs");
      OptionSpecBuilder var8 = var1.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec var9 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      ArgumentAcceptingOptionSpec var10 = var1.accepts("input", "Input folder").withRequiredArg();
      OptionSet var11 = var1.parse(var0);
      if (!var11.has(var2) && var11.hasOptions()) {
         Path var12 = Paths.get((String)var9.value(var11));
         boolean var13 = var11.has(var8);
         boolean var14 = var13 || var11.has(var4);
         boolean var15 = var13 || var11.has(var3);
         boolean var16 = var13 || var11.has(var5);
         boolean var17 = var13 || var11.has(var6);
         boolean var18 = var13 || var11.has(var7);
         DataGenerator var19 = createStandardGenerator(
            var12,
            var11.valuesOf(var10).stream().map(var0x -> Paths.get(var0x)).collect(Collectors.toList()),
            var14,
            var15,
            var16,
            var17,
            var18,
            SharedConstants.getCurrentVersion(),
            true
         );
         var19.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
      BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> var0, CompletableFuture<HolderLookup.Provider> var1
   ) {
      return var2 -> (T)var0.apply(var2, var1);
   }

   public static DataGenerator createStandardGenerator(
      Path var0, Collection<Path> var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, WorldVersion var7, boolean var8
   ) {
      DataGenerator var9 = new DataGenerator(var0, var7, var8);
      DataGenerator.PackGenerator var10 = var9.getVanillaPack(var2 || var3);
      var10.addProvider(var1x -> new SnbtToNbt(var1x, var1).addFilter(new StructureUpdater()));
      CompletableFuture var19 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
      DataGenerator.PackGenerator var11 = var9.getVanillaPack(var2);
      var11.addProvider(ModelProvider::new);
      DataGenerator.PackGenerator var14 = var9.getVanillaPack(var3);
      var14.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var19));
      var14.addProvider(bindRegistries(VanillaAdvancementProvider::create, var19));
      var14.addProvider(VanillaLootTableProvider::create);
      var14.addProvider(VanillaRecipeProvider::new);
      TagsProvider var20 = var14.addProvider(bindRegistries(VanillaBlockTagsProvider::new, var19));
      TagsProvider var12 = var14.addProvider(var2x -> new VanillaItemTagsProvider(var2x, var19, var20.contentsGetter()));
      TagsProvider var13 = var14.addProvider(bindRegistries(BiomeTagsProvider::new, var19));
      var14.addProvider(bindRegistries(BannerPatternTagsProvider::new, var19));
      var14.addProvider(bindRegistries(CatVariantTagsProvider::new, var19));
      var14.addProvider(bindRegistries(DamageTypeTagsProvider::new, var19));
      var14.addProvider(bindRegistries(EntityTypeTagsProvider::new, var19));
      var14.addProvider(bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, var19));
      var14.addProvider(bindRegistries(FluidTagsProvider::new, var19));
      var14.addProvider(bindRegistries(GameEventTagsProvider::new, var19));
      var14.addProvider(bindRegistries(InstrumentTagsProvider::new, var19));
      var14.addProvider(bindRegistries(PaintingVariantTagsProvider::new, var19));
      var14.addProvider(bindRegistries(PoiTypeTagsProvider::new, var19));
      var14.addProvider(bindRegistries(StructureTagsProvider::new, var19));
      var14.addProvider(bindRegistries(WorldPresetTagsProvider::new, var19));
      var14 = var9.getVanillaPack(var4);
      var14.addProvider(var1x -> new NbtToSnbt(var1x, var1));
      var14 = var9.getVanillaPack(var5);
      var14.addProvider(bindRegistries(BiomeParametersDumpReport::new, var19));
      var14.addProvider(BlockListReport::new);
      var14.addProvider(bindRegistries(CommandsReport::new, var19));
      var14.addProvider(RegistryDumpReport::new);
      var14 = var9.getBuiltinDatapack(var3, "bundle");
      var14.addProvider(BundleRecipeProvider::new);
      var14.addProvider(
         var0x -> PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.bundle.description"), FeatureFlagSet.of(FeatureFlags.BUNDLE))
      );
      var14 = var9.getBuiltinDatapack(var3, "trade_rebalance");
      var14.addProvider(
         var0x -> PackMetadataGenerator.forFeaturePack(
               var0x, Component.translatable("dataPack.trade_rebalance.description"), FeatureFlagSet.of(FeatureFlags.TRADE_REBALANCE)
            )
      );
      var14.addProvider(TradeRebalanceLootTableProvider::create);
      var14.addProvider(bindRegistries(TradeRebalanceStructureTagsProvider::new, var19));
      CompletableFuture var25 = UpdateOneTwentyOneRegistries.createLookup(var19);
      CompletableFuture var15 = var25.thenApply(RegistrySetBuilder.PatchedRegistries::full);
      CompletableFuture var16 = var25.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
      DataGenerator.PackGenerator var17 = var9.getBuiltinDatapack(var3, "update_1_21");
      var17.addProvider(UpdateOneTwentyOneRecipeProvider::new);
      TagsProvider var18 = var17.addProvider(var2x -> new UpdateOneTwentyOneBlockTagsProvider(var2x, var16, var20.contentsGetter()));
      var17.addProvider(var3x -> new UpdateOneTwentyOneItemTagsProvider(var3x, var16, var12.contentsGetter(), var18.contentsGetter()));
      var17.addProvider(var2x -> new UpdateOneTwentyOneBiomeTagsProvider(var2x, var16, var13.contentsGetter()));
      var17.addProvider(UpdateOneTwentyOneLootTableProvider::create);
      var17.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var16));
      var17.addProvider(
         var0x -> PackMetadataGenerator.forFeaturePack(
               var0x, Component.translatable("dataPack.update_1_21.description"), FeatureFlagSet.of(FeatureFlags.UPDATE_1_21)
            )
      );
      var17.addProvider(bindRegistries(UpdateOneTwentyOneEntityTypeTagsProvider::new, var15));
      var17.addProvider(bindRegistries(UpdateOneTwentyOneDamageTypeTagsProvider::new, var15));
      var17.addProvider(bindRegistries(UpdateOneTwentyOneAdvancementProvider::create, var15));
      return var9;
   }
}
