package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

public class ModelProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator.PathProvider blockStatePathProvider;
   private final DataGenerator.PathProvider modelPathProvider;

   public ModelProvider(DataGenerator var1) {
      super();
      this.blockStatePathProvider = var1.createPathProvider(DataGenerator.Target.RESOURCE_PACK, "blockstates");
      this.modelPathProvider = var1.createPathProvider(DataGenerator.Target.RESOURCE_PACK, "models");
   }

   public void run(CachedOutput var1) {
      HashMap var2 = Maps.newHashMap();
      Consumer var3 = (var1x) -> {
         Block var2x = var1x.getBlock();
         BlockStateGenerator var3 = (BlockStateGenerator)var2.put(var2x, var1x);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + var2x);
         }
      };
      HashMap var4 = Maps.newHashMap();
      HashSet var5 = Sets.newHashSet();
      BiConsumer var6 = (var1x, var2x) -> {
         Supplier var3 = (Supplier)var4.put(var1x, var2x);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate model definition for " + var1x);
         }
      };
      Objects.requireNonNull(var5);
      Consumer var7 = var5::add;
      (new BlockModelGenerators(var3, var6, var7)).run();
      (new ItemModelGenerators(var6)).run();
      List var8 = Registry.BLOCK.stream().filter((var1x) -> {
         return !var2.containsKey(var1x);
      }).toList();
      if (!var8.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + var8);
      } else {
         Registry.BLOCK.forEach((var2x) -> {
            Item var3 = (Item)Item.BY_BLOCK.get(var2x);
            if (var3 != null) {
               if (var5.contains(var3)) {
                  return;
               }

               ResourceLocation var4x = ModelLocationUtils.getModelLocation(var3);
               if (!var4.containsKey(var4x)) {
                  var4.put(var4x, new DelegatedModel(ModelLocationUtils.getModelLocation(var2x)));
               }
            }

         });
         this.saveCollection(var1, var2, (var1x) -> {
            return this.blockStatePathProvider.json(var1x.builtInRegistryHolder().key().location());
         });
         DataGenerator.PathProvider var10003 = this.modelPathProvider;
         Objects.requireNonNull(var10003);
         this.saveCollection(var1, var4, var10003::json);
      }
   }

   private <T> void saveCollection(CachedOutput var1, Map<T, ? extends Supplier<JsonElement>> var2, Function<T, Path> var3) {
      var2.forEach((var2x, var3x) -> {
         Path var4 = (Path)var3.apply(var2x);

         try {
            DataProvider.saveStable(var1, (JsonElement)var3x.get(), var4);
         } catch (Exception var6) {
            LOGGER.error("Couldn't save {}", var4, var6);
         }

      });
   }

   public String getName() {
      return "Block State Definitions";
   }
}
