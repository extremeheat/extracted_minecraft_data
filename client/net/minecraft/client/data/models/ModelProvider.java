package net.minecraft.client.data.models;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
   private final PackOutput.PathProvider blockStatePathProvider;
   private final PackOutput.PathProvider itemInfoPathProvider;
   private final PackOutput.PathProvider modelPathProvider;

   public ModelProvider(PackOutput var1) {
      super();
      this.blockStatePathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
      this.itemInfoPathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
      this.modelPathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      ItemInfoCollector var2 = new ItemInfoCollector();
      BlockStateGeneratorCollector var3 = new BlockStateGeneratorCollector();
      SimpleModelCollector var4 = new SimpleModelCollector();
      (new BlockModelGenerators(var3, var2, var4)).run();
      (new ItemModelGenerators(var2, var4)).run();
      var3.validate();
      var2.finalizeAndValidate();
      return CompletableFuture.allOf(var3.save(var1, this.blockStatePathProvider), var4.save(var1, this.modelPathProvider), var2.save(var1, this.itemInfoPathProvider));
   }

   static <T> CompletableFuture<?> saveAll(CachedOutput var0, Function<T, Path> var1, Map<T, ? extends Supplier<JsonElement>> var2) {
      return DataProvider.saveAll(var0, Supplier::get, var1, var2);
   }

   public final String getName() {
      return "Model Definitions";
   }

   static class SimpleModelCollector implements BiConsumer<ResourceLocation, ModelInstance> {
      private final Map<ResourceLocation, ModelInstance> models = new HashMap();

      SimpleModelCollector() {
         super();
      }

      public void accept(ResourceLocation var1, ModelInstance var2) {
         Supplier var3 = (Supplier)this.models.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate model definition for " + String.valueOf(var1));
         }
      }

      public CompletableFuture<?> save(CachedOutput var1, PackOutput.PathProvider var2) {
         Objects.requireNonNull(var2);
         return ModelProvider.saveAll(var1, var2::json, this.models);
      }

      // $FF: synthetic method
      public void accept(final Object var1, final Object var2) {
         this.accept((ResourceLocation)var1, (ModelInstance)var2);
      }
   }

   static class BlockStateGeneratorCollector implements Consumer<BlockStateGenerator> {
      private final Map<Block, BlockStateGenerator> generators = new HashMap();

      BlockStateGeneratorCollector() {
         super();
      }

      public void accept(BlockStateGenerator var1) {
         Block var2 = var1.getBlock();
         BlockStateGenerator var3 = (BlockStateGenerator)this.generators.put(var2, var1);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(var2));
         }
      }

      public void validate() {
         Stream var1 = BuiltInRegistries.BLOCK.listElements().filter((var0) -> true);
         List var2 = var1.filter((var1x) -> !this.generators.containsKey(var1x.value())).map((var0) -> var0.key().location()).toList();
         if (!var2.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(var2));
         }
      }

      public CompletableFuture<?> save(CachedOutput var1, PackOutput.PathProvider var2) {
         return ModelProvider.saveAll(var1, (var1x) -> var2.json(var1x.builtInRegistryHolder().key().location()), this.generators);
      }

      // $FF: synthetic method
      public void accept(final Object var1) {
         this.accept((BlockStateGenerator)var1);
      }
   }

   static class ItemInfoCollector implements ItemModelOutput {
      private final Map<Item, ClientItem> itemInfos = new HashMap();
      private final Map<Item, Item> copies = new HashMap();

      ItemInfoCollector() {
         super();
      }

      public void accept(Item var1, ItemModel.Unbaked var2) {
         this.register(var1, new ClientItem(var2));
      }

      private void register(Item var1, ClientItem var2) {
         ClientItem var3 = (ClientItem)this.itemInfos.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(var1));
         }
      }

      public void copy(Item var1, Item var2) {
         this.copies.put(var2, var1);
      }

      public void finalizeAndValidate() {
         BuiltInRegistries.ITEM.forEach((var1x) -> {
            if (!this.copies.containsKey(var1x)) {
               if (var1x instanceof BlockItem) {
                  BlockItem var2 = (BlockItem)var1x;
                  if (!this.itemInfos.containsKey(var2)) {
                     ResourceLocation var3 = ModelLocationUtils.getModelLocation(var2.getBlock());
                     this.accept(var2, ItemModelUtils.plainModel(var3));
                  }
               }

            }
         });
         this.copies.forEach((var1x, var2) -> {
            ClientItem var3 = (ClientItem)this.itemInfos.get(var2);
            if (var3 == null) {
               String var10002 = String.valueOf(var2);
               throw new IllegalStateException("Missing donor: " + var10002 + " -> " + String.valueOf(var1x));
            } else {
               this.register(var1x, var3);
            }
         });
         List var1 = BuiltInRegistries.ITEM.listElements().filter((var1x) -> !this.itemInfos.containsKey(var1x.value())).map((var0) -> var0.key().location()).toList();
         if (!var1.isEmpty()) {
            throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(var1));
         }
      }

      public CompletableFuture<?> save(CachedOutput var1, PackOutput.PathProvider var2) {
         return DataProvider.saveAll(var1, ClientItem.CODEC, (Function)((var1x) -> var2.json(var1x.builtInRegistryHolder().key().location())), this.itemInfos);
      }
   }
}
