package net.minecraft.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;
   private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders;

   public LootTableProvider(DataGenerator var1) {
      super();
      this.subProviders = ImmutableList.of(Pair.of(FishingLoot::new, LootContextParamSets.FISHING), Pair.of(ChestLoot::new, LootContextParamSets.CHEST), Pair.of(EntityLoot::new, LootContextParamSets.ENTITY), Pair.of(BlockLoot::new, LootContextParamSets.BLOCK), Pair.of(PiglinBarterLoot::new, LootContextParamSets.PIGLIN_BARTER), Pair.of(GiftLoot::new, LootContextParamSets.GIFT));
      this.generator = var1;
   }

   public void run(HashCache var1) {
      Path var2 = this.generator.getOutputFolder();
      HashMap var3 = Maps.newHashMap();
      this.subProviders.forEach((var1x) -> {
         ((Consumer)((Supplier)var1x.getFirst()).get()).accept((var2, var3x) -> {
            if (var3.put(var2, var3x.setParamSet((LootContextParamSet)var1x.getSecond()).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + var2);
            }
         });
      });
      LootContextParamSet var10002 = LootContextParamSets.ALL_PARAMS;
      Function var10003 = (var0) -> {
         return null;
      };
      var3.getClass();
      ValidationContext var4 = new ValidationContext(var10002, var10003, var3::get);
      SetView var5 = Sets.difference(BuiltInLootTables.all(), var3.keySet());
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         ResourceLocation var7 = (ResourceLocation)var6.next();
         var4.reportProblem("Missing built-in table: " + var7);
      }

      var3.forEach((var1x, var2x) -> {
         LootTables.validate(var4, var1x, var2x);
      });
      Multimap var8 = var4.getProblems();
      if (!var8.isEmpty()) {
         var8.forEach((var0, var1x) -> {
            LOGGER.warn("Found validation problem in " + var0 + ": " + var1x);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         var3.forEach((var2x, var3x) -> {
            Path var4 = createPath(var2, var2x);

            try {
               DataProvider.save(GSON, var1, LootTables.serialize(var3x), var4);
            } catch (IOException var6) {
               LOGGER.error("Couldn't save loot table {}", var4, var6);
            }

         });
      }
   }

   private static Path createPath(Path var0, ResourceLocation var1) {
      return var0.resolve("data/" + var1.getNamespace() + "/loot_tables/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "LootTables";
   }
}
