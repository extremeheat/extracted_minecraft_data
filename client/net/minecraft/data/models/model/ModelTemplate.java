package net.minecraft.data.models.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModelTemplate {
   private final Optional<ResourceLocation> model;
   private final Set<TextureSlot> requiredSlots;
   private final Optional<String> suffix;

   public ModelTemplate(Optional<ResourceLocation> var1, Optional<String> var2, TextureSlot... var3) {
      super();
      this.model = var1;
      this.suffix = var2;
      this.requiredSlots = ImmutableSet.copyOf(var3);
   }

   public ResourceLocation getDefaultModelLocation(Block var1) {
      return ModelLocationUtils.getModelLocation(var1, this.suffix.orElse(""));
   }

   public ResourceLocation create(Block var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3) {
      return this.create(ModelLocationUtils.getModelLocation(var1, this.suffix.orElse("")), var2, var3);
   }

   public ResourceLocation createWithSuffix(Block var1, String var2, TextureMapping var3, BiConsumer<ResourceLocation, Supplier<JsonElement>> var4) {
      return this.create(ModelLocationUtils.getModelLocation(var1, var2 + this.suffix.orElse("")), var3, var4);
   }

   public ResourceLocation createWithOverride(Block var1, String var2, TextureMapping var3, BiConsumer<ResourceLocation, Supplier<JsonElement>> var4) {
      return this.create(ModelLocationUtils.getModelLocation(var1, var2), var3, var4);
   }

   public ResourceLocation create(ResourceLocation var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3) {
      return this.create(var1, var2, var3, this::createBaseTemplate);
   }

   public ResourceLocation create(
      ResourceLocation var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3, ModelTemplate.JsonFactory var4
   ) {
      Map var5 = this.createMap(var2);
      var3.accept(var1, () -> var4.create(var1, var5));
      return var1;
   }

   public JsonObject createBaseTemplate(ResourceLocation var1, Map<TextureSlot, ResourceLocation> var2) {
      JsonObject var3 = new JsonObject();
      this.model.ifPresent(var1x -> var3.addProperty("parent", var1x.toString()));
      if (!var2.isEmpty()) {
         JsonObject var4 = new JsonObject();
         var2.forEach((var1x, var2x) -> var4.addProperty(var1x.getId(), var2x.toString()));
         var3.add("textures", var4);
      }

      return var3;
   }

   private Map<TextureSlot, ResourceLocation> createMap(TextureMapping var1) {
      return Streams.concat(new Stream[]{this.requiredSlots.stream(), var1.getForced()}).collect(ImmutableMap.toImmutableMap(Function.identity(), var1::get));
   }

   public interface JsonFactory {
      JsonObject create(ResourceLocation var1, Map<TextureSlot, ResourceLocation> var2);
   }
}
