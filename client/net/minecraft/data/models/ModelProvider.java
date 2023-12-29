package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
   private final PackOutput.PathProvider blockStatePathProvider;
   private final PackOutput.PathProvider modelPathProvider;

   public ModelProvider(PackOutput var1) {
      super();
      this.blockStatePathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
      this.modelPathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      HashMap var2 = Maps.newHashMap();
      Consumer var3 = var1x -> {
         Block var2xx = var1x.getBlock();
         BlockStateGenerator var3xx = var2.put(var2xx, var1x);
         if (var3xx != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + var2xx);
         }
      };
      HashMap var4 = Maps.newHashMap();
      HashSet var5 = Sets.newHashSet();
      BiConsumer var6 = (var1x, var2x) -> {
         Supplier var3xx = var4.put(var1x, var2x);
         if (var3xx != null) {
            throw new IllegalStateException("Duplicate model definition for " + var1x);
         }
      };
      Consumer var7 = var5::add;
      new BlockModelGenerators(var3, var6, var7).run();
      new ItemModelGenerators(var6).run();
      List var8 = BuiltInRegistries.BLOCK.entrySet().stream().filter(var0 -> true).map(Entry::getValue).filter(var1x -> !var2.containsKey(var1x)).toList();
      if (!var8.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + var8);
      } else {
         BuiltInRegistries.BLOCK.forEach(var2x -> {
            Item var3xx = Item.BY_BLOCK.get(var2x);
            if (var3xx != null) {
               if (var5.contains(var3xx)) {
                  return;
               }

               ResourceLocation var4xx = ModelLocationUtils.getModelLocation(var3xx);
               if (!var4.containsKey(var4xx)) {
                  var4.put(var4xx, new DelegatedModel(ModelLocationUtils.getModelLocation(var2x)));
               }
            }
         });
         return CompletableFuture.allOf(
            this.saveCollection(var1, var2, var1x -> this.blockStatePathProvider.json(var1x.builtInRegistryHolder().key().location())),
            this.saveCollection(var1, var4, this.modelPathProvider::json)
         );
      }
   }

   private <T> CompletableFuture<?> saveCollection(CachedOutput var1, Map<T, ? extends Supplier<JsonElement>> var2, Function<T, Path> var3) {
      return CompletableFuture.allOf(var2.entrySet().stream().map(var2x -> {
         Path var3xx = (Path)var3.apply(var2x.getKey());
         JsonElement var4 = (JsonElement)((Supplier)var2x.getValue()).get();
         return DataProvider.saveStable(var1, var4, var3xx);
      }).toArray(var0 -> new CompletableFuture[var0]));
   }

   @Override
   public final String getName() {
      return "Model Definitions";
   }
}
