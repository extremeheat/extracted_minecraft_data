package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.DatapackStructureReport;
import net.minecraft.data.info.ItemListReport;
import net.minecraft.data.info.PacketReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.TradeRebalanceRegistries;
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
import net.minecraft.data.tags.TradeRebalanceEnchantmentTagsProvider;
import net.minecraft.data.tags.TradeRebalanceStructureTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaEnchantmentTagsProvider;
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

   @SuppressForbidden(
      a = "System.out needed before bootstrap"
   )
   @DontObfuscate
   public static void main(String[] var0) throws IOException {
      SharedConstants.tryDetectVersion();
      OptionParser var1 = new OptionParser();
      AbstractOptionSpec var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder var3 = var1.accepts("server", "Include server generators");
      OptionSpecBuilder var4 = var1.accepts("dev", "Include development tools");
      OptionSpecBuilder var5 = var1.accepts("reports", "Include data reports");
      var1.accepts("validate", "Validate inputs");
      OptionSpecBuilder var6 = var1.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec var7 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      ArgumentAcceptingOptionSpec var8 = var1.accepts("input", "Input folder").withRequiredArg();
      OptionSet var9 = var1.parse(var0);
      if (!var9.has(var2) && var9.hasOptions()) {
         Path var10 = Paths.get((String)var7.value(var9));
         boolean var11 = var9.has(var6);
         boolean var12 = var11 || var9.has(var3);
         boolean var13 = var11 || var9.has(var4);
         boolean var14 = var11 || var9.has(var5);
         List var15 = var9.valuesOf(var8).stream().map((var0x) -> Paths.get(var0x)).toList();
         DataGenerator var16 = new DataGenerator(var10, SharedConstants.getCurrentVersion(), true);
         addServerProviders(var16, var15, var12, var13, var14);
         var16.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> var0, CompletableFuture<HolderLookup.Provider> var1) {
      return (var2) -> (DataProvider)var0.apply(var2, var1);
   }

   public static void addServerProviders(DataGenerator var0, Collection<Path> var1, boolean var2, boolean var3, boolean var4) {
      DataGenerator.PackGenerator var5 = var0.getVanillaPack(var2);
      var5.addProvider((var1x) -> (new SnbtToNbt(var1x, var1)).addFilter(new StructureUpdater()));
      CompletableFuture var14 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
      DataGenerator.PackGenerator var11 = var0.getVanillaPack(var2);
      var11.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var14));
      var11.addProvider(bindRegistries(VanillaAdvancementProvider::create, var14));
      var11.addProvider(bindRegistries(VanillaLootTableProvider::create, var14));
      var11.addProvider(bindRegistries(VanillaRecipeProvider.Runner::new, var14));
      TagsProvider var6 = (TagsProvider)var11.addProvider(bindRegistries(VanillaBlockTagsProvider::new, var14));
      TagsProvider var7 = (TagsProvider)var11.addProvider((var2x) -> new VanillaItemTagsProvider(var2x, var14, var6.contentsGetter()));
      TagsProvider var8 = (TagsProvider)var11.addProvider(bindRegistries(BiomeTagsProvider::new, var14));
      TagsProvider var9 = (TagsProvider)var11.addProvider(bindRegistries(BannerPatternTagsProvider::new, var14));
      TagsProvider var10 = (TagsProvider)var11.addProvider(bindRegistries(StructureTagsProvider::new, var14));
      var11.addProvider(bindRegistries(CatVariantTagsProvider::new, var14));
      var11.addProvider(bindRegistries(DamageTypeTagsProvider::new, var14));
      var11.addProvider(bindRegistries(EntityTypeTagsProvider::new, var14));
      var11.addProvider(bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, var14));
      var11.addProvider(bindRegistries(FluidTagsProvider::new, var14));
      var11.addProvider(bindRegistries(GameEventTagsProvider::new, var14));
      var11.addProvider(bindRegistries(InstrumentTagsProvider::new, var14));
      var11.addProvider(bindRegistries(PaintingVariantTagsProvider::new, var14));
      var11.addProvider(bindRegistries(PoiTypeTagsProvider::new, var14));
      var11.addProvider(bindRegistries(WorldPresetTagsProvider::new, var14));
      var11.addProvider(bindRegistries(VanillaEnchantmentTagsProvider::new, var14));
      var11 = var0.getVanillaPack(var3);
      var11.addProvider((var1x) -> new NbtToSnbt(var1x, var1));
      var11 = var0.getVanillaPack(var4);
      var11.addProvider(bindRegistries(BiomeParametersDumpReport::new, var14));
      var11.addProvider(bindRegistries(ItemListReport::new, var14));
      var11.addProvider(bindRegistries(BlockListReport::new, var14));
      var11.addProvider(bindRegistries(CommandsReport::new, var14));
      var11.addProvider(RegistryDumpReport::new);
      var11.addProvider(PacketReport::new);
      var11.addProvider(DatapackStructureReport::new);
      CompletableFuture var17 = TradeRebalanceRegistries.createLookup(var14);
      CompletableFuture var12 = var17.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
      DataGenerator.PackGenerator var13 = var0.getBuiltinDatapack(var2, "trade_rebalance");
      var13.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var12));
      var13.addProvider((var0x) -> PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.trade_rebalance.description"), FeatureFlagSet.of(FeatureFlags.TRADE_REBALANCE)));
      var13.addProvider(bindRegistries(TradeRebalanceLootTableProvider::create, var14));
      var13.addProvider(bindRegistries(TradeRebalanceStructureTagsProvider::new, var14));
      var13.addProvider(bindRegistries(TradeRebalanceEnchantmentTagsProvider::new, var14));
      DataGenerator.PackGenerator var18 = var0.getBuiltinDatapack(var2, "redstone_experiments");
      var18.addProvider((var0x) -> PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.redstone_experiments.description"), FeatureFlagSet.of(FeatureFlags.REDSTONE_EXPERIMENTS)));
      var18 = var0.getBuiltinDatapack(var2, "minecart_improvements");
      var18.addProvider((var0x) -> PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.minecart_improvements.description"), FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS)));
   }
}
