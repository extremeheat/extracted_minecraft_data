package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public ModelProvider(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) {
      Path var2 = this.generator.getOutputFolder();
      HashMap var3 = Maps.newHashMap();
      Consumer var4 = (var1x) -> {
         Block var2 = var1x.getBlock();
         BlockStateGenerator var3x = (BlockStateGenerator)var3.put(var2, var1x);
         if (var3x != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + var2);
         }
      };
      HashMap var5 = Maps.newHashMap();
      HashSet var6 = Sets.newHashSet();
      BiConsumer var7 = (var1x, var2x) -> {
         Supplier var3 = (Supplier)var5.put(var1x, var2x);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate model definition for " + var1x);
         }
      };
      Consumer var8 = var6::add;
      (new BlockModelGenerators(var4, var7, var8)).run();
      (new ItemModelGenerators(var7)).run();
      List var9 = (List)Registry.BLOCK.stream().filter((var1x) -> {
         return !var3.containsKey(var1x);
      }).collect(Collectors.toList());
      if (!var9.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + var9);
      } else {
         Registry.BLOCK.forEach((var2x) -> {
            Item var3 = (Item)Item.BY_BLOCK.get(var2x);
            if (var3 != null) {
               if (var6.contains(var3)) {
                  return;
               }

               ResourceLocation var4 = ModelLocationUtils.getModelLocation(var3);
               if (!var5.containsKey(var4)) {
                  var5.put(var4, new DelegatedModel(ModelLocationUtils.getModelLocation(var2x)));
               }
            }

         });
         this.saveCollection(var1, var2, var3, ModelProvider::createBlockStatePath);
         this.saveCollection(var1, var2, var5, ModelProvider::createModelPath);
      }
   }

   private <T> void saveCollection(HashCache var1, Path var2, Map<T, ? extends Supplier<JsonElement>> var3, BiFunction<Path, T, Path> var4) {
      var3.forEach((var3x, var4x) -> {
         Path var5 = (Path)var4.apply(var2, var3x);

         try {
            DataProvider.save(GSON, var1, (JsonElement)var4x.get(), var5);
         } catch (Exception var7) {
            LOGGER.error("Couldn't save {}", var5, var7);
         }

      });
   }

   private static Path createBlockStatePath(Path var0, Block var1) {
      ResourceLocation var2 = Registry.BLOCK.getKey(var1);
      return var0.resolve("assets/" + var2.getNamespace() + "/blockstates/" + var2.getPath() + ".json");
   }

   private static Path createModelPath(Path var0, ResourceLocation var1) {
      return var0.resolve("assets/" + var1.getNamespace() + "/models/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "Block State Definitions";
   }
}
