package net.minecraft.client.data.models.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelTemplate {
   private final Optional<Identifier> model;
   private final Set<TextureSlot> requiredSlots;
   private final Optional<String> suffix;

   public ModelTemplate(Optional<Identifier> var1, Optional<String> var2, TextureSlot... var3) {
      super();
      this.model = var1;
      this.suffix = var2;
      this.requiredSlots = ImmutableSet.copyOf(var3);
   }

   public Identifier getDefaultModelLocation(Block var1) {
      return ModelLocationUtils.getModelLocation(var1, (String)this.suffix.orElse(""));
   }

   public Identifier create(Block var1, TextureMapping var2, BiConsumer<Identifier, ModelInstance> var3) {
      return this.create(ModelLocationUtils.getModelLocation(var1, (String)this.suffix.orElse("")), var2, var3);
   }

   public Identifier createWithSuffix(Block var1, String var2, TextureMapping var3, BiConsumer<Identifier, ModelInstance> var4) {
      return this.create(ModelLocationUtils.getModelLocation(var1, var2 + (String)this.suffix.orElse("")), var3, var4);
   }

   public Identifier createWithOverride(Block var1, String var2, TextureMapping var3, BiConsumer<Identifier, ModelInstance> var4) {
      return this.create(ModelLocationUtils.getModelLocation(var1, var2), var3, var4);
   }

   public Identifier create(Item var1, TextureMapping var2, BiConsumer<Identifier, ModelInstance> var3) {
      return this.create(ModelLocationUtils.getModelLocation(var1, (String)this.suffix.orElse("")), var2, var3);
   }

   public Identifier create(Identifier var1, TextureMapping var2, BiConsumer<Identifier, ModelInstance> var3) {
      Map var4 = this.createMap(var2);
      var3.accept(var1, (ModelInstance)() -> {
         JsonObject var2 = new JsonObject();
         this.model.ifPresent((var1) -> var2.addProperty("parent", var1.toString()));
         if (!var4.isEmpty()) {
            JsonObject var3 = new JsonObject();
            var4.forEach((var1, var2x) -> var3.addProperty(var1.getId(), var2x.toString()));
            var2.add("textures", var3);
         }

         return var2;
      });
      return var1;
   }

   private Map<TextureSlot, Identifier> createMap(TextureMapping var1) {
      Stream var10000 = Streams.concat(new Stream[]{this.requiredSlots.stream(), var1.getForced()});
      Function var10001 = Function.identity();
      Objects.requireNonNull(var1);
      return (Map)var10000.collect(ImmutableMap.toImmutableMap(var10001, var1::get));
   }
}
