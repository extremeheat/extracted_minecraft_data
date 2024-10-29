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
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.advancements.packs.WinterDropAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.DatapackStructureReport;
import net.minecraft.data.info.ItemListReport;
import net.minecraft.data.info.PacketReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.loot.packs.WinterDropLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.models.EquipmentModelProvider;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.recipes.packs.WinterDropRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.TradeRebalanceRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.registries.WinterDropRegistries;
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
import net.minecraft.data.tags.WinterDropBiomeTagsProvider;
import net.minecraft.data.tags.WinterDropBlockTagsProvider;
import net.minecraft.data.tags.WinterDropEntityTypeTagsProvider;
import net.minecraft.data.tags.WinterDropItemTagsProvider;
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
         DataGenerator var19 = createStandardGenerator(var12, (Collection)var11.valuesOf(var10).stream().map((var0x) -> {
            return Paths.get(var0x);
         }).collect(Collectors.toList()), var14, var15, var16, var17, var18, SharedConstants.getCurrentVersion(), true);
         var19.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> var0, CompletableFuture<HolderLookup.Provider> var1) {
      return (var2) -> {
         return (DataProvider)var0.apply(var2, var1);
      };
   }

   public static DataGenerator createStandardGenerator(Path var0, Collection<Path> var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, WorldVersion var7, boolean var8) {
      DataGenerator var9 = new DataGenerator(var0, var7, var8);
      DataGenerator.PackGenerator var10 = var9.getVanillaPack(var2 || var3);
      var10.addProvider((var1x) -> {
         return (new SnbtToNbt(var1x, var1)).addFilter(new StructureUpdater());
      });
      CompletableFuture var20 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
      DataGenerator.PackGenerator var11 = var9.getVanillaPack(var2);
      var11.addProvider(ModelProvider::new);
      var11.addProvider(EquipmentModelProvider::new);
      DataGenerator.PackGenerator var16 = var9.getVanillaPack(var3);
      var16.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var20));
      var16.addProvider(bindRegistries(VanillaAdvancementProvider::create, var20));
      var16.addProvider(bindRegistries(VanillaLootTableProvider::create, var20));
      var16.addProvider(bindRegistries(VanillaRecipeProvider.Runner::new, var20));
      TagsProvider var21 = (TagsProvider)var16.addProvider(bindRegistries(VanillaBlockTagsProvider::new, var20));
      TagsProvider var12 = (TagsProvider)var16.addProvider((var2x) -> {
         return new VanillaItemTagsProvider(var2x, var20, var21.contentsGetter());
      });
      TagsProvider var13 = (TagsProvider)var16.addProvider(bindRegistries(BiomeTagsProvider::new, var20));
      TagsProvider var14 = (TagsProvider)var16.addProvider(bindRegistries(BannerPatternTagsProvider::new, var20));
      TagsProvider var15 = (TagsProvider)var16.addProvider(bindRegistries(StructureTagsProvider::new, var20));
      var16.addProvider(bindRegistries(CatVariantTagsProvider::new, var20));
      var16.addProvider(bindRegistries(DamageTypeTagsProvider::new, var20));
      var16.addProvider(bindRegistries(EntityTypeTagsProvider::new, var20));
      var16.addProvider(bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, var20));
      var16.addProvider(bindRegistries(FluidTagsProvider::new, var20));
      var16.addProvider(bindRegistries(GameEventTagsProvider::new, var20));
      var16.addProvider(bindRegistries(InstrumentTagsProvider::new, var20));
      var16.addProvider(bindRegistries(PaintingVariantTagsProvider::new, var20));
      var16.addProvider(bindRegistries(PoiTypeTagsProvider::new, var20));
      var16.addProvider(bindRegistries(WorldPresetTagsProvider::new, var20));
      var16.addProvider(bindRegistries(VanillaEnchantmentTagsProvider::new, var20));
      var16 = var9.getVanillaPack(var4);
      var16.addProvider((var1x) -> {
         return new NbtToSnbt(var1x, var1);
      });
      var16 = var9.getVanillaPack(var5);
      var16.addProvider(bindRegistries(BiomeParametersDumpReport::new, var20));
      var16.addProvider(bindRegistries(ItemListReport::new, var20));
      var16.addProvider(bindRegistries(BlockListReport::new, var20));
      var16.addProvider(bindRegistries(CommandsReport::new, var20));
      var16.addProvider(RegistryDumpReport::new);
      var16.addProvider(PacketReport::new);
      var16.addProvider(DatapackStructureReport::new);
      CompletableFuture var22 = TradeRebalanceRegistries.createLookup(var20);
      CompletableFuture var17 = var22.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
      DataGenerator.PackGenerator var18 = var9.getBuiltinDatapack(var3, "trade_rebalance");
      var18.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var17));
      var18.addProvider((var0x) -> {
         return PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.trade_rebalance.description"), FeatureFlagSet.of(FeatureFlags.TRADE_REBALANCE));
      });
      var18.addProvider(bindRegistries(TradeRebalanceLootTableProvider::create, var20));
      var18.addProvider(bindRegistries(TradeRebalanceStructureTagsProvider::new, var20));
      var18.addProvider(bindRegistries(TradeRebalanceEnchantmentTagsProvider::new, var20));
      var16 = var9.getBuiltinDatapack(var3, "redstone_experiments");
      var16.addProvider((var0x) -> {
         return PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.redstone_experiments.description"), FeatureFlagSet.of(FeatureFlags.REDSTONE_EXPERIMENTS));
      });
      var16 = var9.getBuiltinDatapack(var3, "minecart_improvements");
      var16.addProvider((var0x) -> {
         return PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.minecart_improvements.description"), FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS));
      });
      var22 = WinterDropRegistries.createLookup(var20);
      var17 = var22.thenApply(RegistrySetBuilder.PatchedRegistries::full);
      var18 = var9.getBuiltinDatapack(var3, "winter_drop");
      var18.addProvider(bindRegistries(RegistriesDatapackGenerator::new, var22.thenApply(RegistrySetBuilder.PatchedRegistries::patches)));
      var18.addProvider(bindRegistries(WinterDropRecipeProvider.Runner::new, var17));
      TagsProvider var19 = (TagsProvider)var18.addProvider((var2x) -> {
         return new WinterDropBlockTagsProvider(var2x, var17, var21.contentsGetter());
      });
      var18.addProvider((var3x) -> {
         return new WinterDropItemTagsProvider(var3x, var17, var12.contentsGetter(), var19.contentsGetter());
      });
      var18.addProvider((var2x) -> {
         return new WinterDropBiomeTagsProvider(var2x, var17, var13.contentsGetter());
      });
      var18.addProvider(bindRegistries(WinterDropLootTableProvider::create, var17));
      var18.addProvider((var0x) -> {
         return PackMetadataGenerator.forFeaturePack(var0x, Component.translatable("dataPack.winter_drop.description"), FeatureFlagSet.of(FeatureFlags.WINTER_DROP));
      });
      var18.addProvider(bindRegistries(WinterDropEntityTypeTagsProvider::new, var17));
      var18.addProvider(bindRegistries(WinterDropAdvancementProvider::create, var17));
      return var9;
   }
}
