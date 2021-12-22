package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPartGenerator implements BlockStateGenerator {
   private final Block block;
   private final List<MultiPartGenerator.Entry> parts = Lists.newArrayList();

   private MultiPartGenerator(Block var1) {
      super();
      this.block = var1;
   }

   public Block getBlock() {
      return this.block;
   }

   public static MultiPartGenerator multiPart(Block var0) {
      return new MultiPartGenerator(var0);
   }

   public MultiPartGenerator with(List<Variant> var1) {
      this.parts.add(new MultiPartGenerator.Entry(var1));
      return this;
   }

   public MultiPartGenerator with(Variant var1) {
      return this.with((List)ImmutableList.of(var1));
   }

   public MultiPartGenerator with(Condition var1, List<Variant> var2) {
      this.parts.add(new MultiPartGenerator.ConditionalEntry(var1, var2));
      return this;
   }

   public MultiPartGenerator with(Condition var1, Variant... var2) {
      return this.with(var1, (List)ImmutableList.copyOf(var2));
   }

   public MultiPartGenerator with(Condition var1, Variant var2) {
      return this.with(var1, (List)ImmutableList.of(var2));
   }

   public JsonElement get() {
      StateDefinition var1 = this.block.getStateDefinition();
      this.parts.forEach((var1x) -> {
         var1x.validate(var1);
      });
      JsonArray var2 = new JsonArray();
      Stream var10000 = this.parts.stream().map(MultiPartGenerator.Entry::get);
      Objects.requireNonNull(var2);
      var10000.forEach(var2::add);
      JsonObject var3 = new JsonObject();
      var3.add("multipart", var2);
      return var3;
   }

   // $FF: synthetic method
   public Object get() {
      return this.get();
   }

   private static class Entry implements Supplier<JsonElement> {
      private final List<Variant> variants;

      Entry(List<Variant> var1) {
         super();
         this.variants = var1;
      }

      public void validate(StateDefinition<?, ?> var1) {
      }

      public void decorate(JsonObject var1) {
      }

      public JsonElement get() {
         JsonObject var1 = new JsonObject();
         this.decorate(var1);
         var1.add("apply", Variant.convertList(this.variants));
         return var1;
      }

      // $FF: synthetic method
      public Object get() {
         return this.get();
      }
   }

   static class ConditionalEntry extends MultiPartGenerator.Entry {
      private final Condition condition;

      ConditionalEntry(Condition var1, List<Variant> var2) {
         super(var2);
         this.condition = var1;
      }

      public void validate(StateDefinition<?, ?> var1) {
         this.condition.validate(var1);
      }

      public void decorate(JsonObject var1) {
         var1.add("when", (JsonElement)this.condition.get());
      }
   }
}
