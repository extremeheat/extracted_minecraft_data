package net.minecraft.client.data.models;

import com.google.common.collect.Maps;
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
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
   private final PackOutput.PathProvider blockStatePathProvider;
   private final PackOutput.PathProvider itemInfoPathProvider;
   private final PackOutput.PathProvider modelPathProvider;

   public ModelProvider(final PackOutput output) {
      super();
      this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
      this.itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
      this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      ItemInfoCollector itemModels = new ItemInfoCollector();
      BlockStateGeneratorCollector blockStateGenerators = new BlockStateGeneratorCollector();
      SimpleModelCollector simpleModels = new SimpleModelCollector();
      (new BlockModelGenerators(blockStateGenerators, itemModels, simpleModels)).run();
      (new ItemModelGenerators(itemModels, simpleModels)).run();
      blockStateGenerators.validate();
      itemModels.finalizeAndValidate();
      return CompletableFuture.allOf(blockStateGenerators.save(cache, this.blockStatePathProvider), simpleModels.save(cache, this.modelPathProvider), itemModels.save(cache, this.itemInfoPathProvider));
   }

   public final String getName() {
      return "Model Definitions";
   }

   private static class SimpleModelCollector implements BiConsumer<Identifier, ModelInstance> {
      private final Map<Identifier, ModelInstance> models = new HashMap();

      private SimpleModelCollector() {
         super();
      }

      public void accept(final Identifier id, final ModelInstance contents) {
         Supplier<JsonElement> prev = (Supplier)this.models.put(id, contents);
         if (prev != null) {
            throw new IllegalStateException("Duplicate model definition for " + String.valueOf(id));
         }
      }

      public CompletableFuture<?> save(final CachedOutput cache, final PackOutput.PathProvider pathProvider) {
         Function var10001 = Supplier::get;
         Objects.requireNonNull(pathProvider);
         return DataProvider.saveAll(cache, var10001, pathProvider::json, this.models);
      }
   }

   private static class BlockStateGeneratorCollector implements Consumer<BlockModelDefinitionGenerator> {
      private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap();

      private BlockStateGeneratorCollector() {
         super();
      }

      public void accept(final BlockModelDefinitionGenerator generator) {
         Block block = generator.block();
         BlockModelDefinitionGenerator prev = (BlockModelDefinitionGenerator)this.generators.put(block, generator);
         if (prev != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(block));
         }
      }

      public void validate() {
         List<Identifier> missingDefinitions = BuiltInRegistries.BLOCK.listElements().filter((e) -> !this.generators.containsKey(e.value())).map((e) -> e.key().identifier()).toList();
         if (!missingDefinitions.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(missingDefinitions));
         }
      }

      public CompletableFuture<?> save(final CachedOutput cache, final PackOutput.PathProvider pathProvider) {
         Map<Block, BlockModelDefinition> definitions = Maps.transformValues(this.generators, BlockModelDefinitionGenerator::create);
         Function<Block, Path> pathGetter = (block) -> pathProvider.json(block.builtInRegistryHolder().key().identifier());
         return DataProvider.saveAll(cache, BlockModelDefinition.CODEC, pathGetter, definitions);
      }
   }

   private static class ItemInfoCollector implements ItemModelOutput {
      private final Map<Item, ClientItem> itemInfos = new HashMap();
      private final Map<Item, Item> copies = new HashMap();

      private ItemInfoCollector() {
         super();
      }

      public void accept(final Item item, final ItemModel.Unbaked model, final ClientItem.Properties properties) {
         this.register(item, new ClientItem(model, properties));
      }

      private void register(final Item item, final ClientItem itemInfo) {
         ClientItem prev = (ClientItem)this.itemInfos.put(item, itemInfo);
         if (prev != null) {
            throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(item));
         }
      }

      public void copy(final Item donor, final Item acceptor) {
         this.copies.put(acceptor, donor);
      }

      public void finalizeAndValidate() {
         BuiltInRegistries.ITEM.forEach((item) -> {
            if (!this.copies.containsKey(item)) {
               if (item instanceof BlockItem) {
                  BlockItem blockItem = (BlockItem)item;
                  if (!this.itemInfos.containsKey(blockItem)) {
                     Identifier targetModel = ModelLocationUtils.getModelLocation(blockItem.getBlock());
                     this.accept(blockItem, ItemModelUtils.plainModel(targetModel));
                  }
               }

            }
         });
         this.copies.forEach((acceptor, donor) -> {
            ClientItem donorInfo = (ClientItem)this.itemInfos.get(donor);
            if (donorInfo == null) {
               String var10002 = String.valueOf(donor);
               throw new IllegalStateException("Missing donor: " + var10002 + " -> " + String.valueOf(acceptor));
            } else {
               this.register(acceptor, donorInfo);
            }
         });
         List<Identifier> missingDefinitions = BuiltInRegistries.ITEM.listElements().filter((e) -> !this.itemInfos.containsKey(e.value())).map((e) -> e.key().identifier()).toList();
         if (!missingDefinitions.isEmpty()) {
            throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(missingDefinitions));
         }
      }

      public CompletableFuture<?> save(final CachedOutput cache, final PackOutput.PathProvider pathProvider) {
         return DataProvider.saveAll(cache, ClientItem.CODEC, (Function)((item) -> pathProvider.json(item.builtInRegistryHolder().key().identifier())), this.itemInfos);
      }
   }
}
