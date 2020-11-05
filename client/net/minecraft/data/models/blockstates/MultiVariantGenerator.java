package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator implements BlockStateGenerator {
   private final Block block;
   private final List<Variant> baseVariants;
   private final Set<Property<?>> seenProperties = Sets.newHashSet();
   private final List<PropertyDispatch> declaredPropertySets = Lists.newArrayList();

   private MultiVariantGenerator(Block var1, List<Variant> var2) {
      super();
      this.block = var1;
      this.baseVariants = var2;
   }

   public MultiVariantGenerator with(PropertyDispatch var1) {
      var1.getDefinedProperties().forEach((var1x) -> {
         if (this.block.getStateDefinition().getProperty(var1x.getName()) != var1x) {
            throw new IllegalStateException("Property " + var1x + " is not defined for block " + this.block);
         } else if (!this.seenProperties.add(var1x)) {
            throw new IllegalStateException("Values of property " + var1x + " already defined for block " + this.block);
         }
      });
      this.declaredPropertySets.add(var1);
      return this;
   }

   public JsonElement get() {
      Stream var1 = Stream.of(Pair.of(Selector.empty(), this.baseVariants));

      Map var4;
      for(Iterator var2 = this.declaredPropertySets.iterator(); var2.hasNext(); var1 = var1.flatMap((var1x) -> {
         return var4.entrySet().stream().map((var1) -> {
            Selector var2 = ((Selector)var1x.getFirst()).extend((Selector)var1.getKey());
            List var3 = mergeVariants((List)var1x.getSecond(), (List)var1.getValue());
            return Pair.of(var2, var3);
         });
      })) {
         PropertyDispatch var3 = (PropertyDispatch)var2.next();
         var4 = var3.getEntries();
      }

      TreeMap var5 = new TreeMap();
      var1.forEach((var1x) -> {
         JsonElement var10000 = (JsonElement)var5.put(((Selector)var1x.getFirst()).getKey(), Variant.convertList((List)var1x.getSecond()));
      });
      JsonObject var6 = new JsonObject();
      var6.add("variants", (JsonElement)Util.make(new JsonObject(), (var1x) -> {
         var5.forEach(var1x::add);
      }));
      return var6;
   }

   private static List<Variant> mergeVariants(List<Variant> var0, List<Variant> var1) {
      Builder var2 = ImmutableList.builder();
      var0.forEach((var2x) -> {
         var1.forEach((var2xx) -> {
            var2.add(Variant.merge(var2x, var2xx));
         });
      });
      return var2.build();
   }

   public Block getBlock() {
      return this.block;
   }

   public static MultiVariantGenerator multiVariant(Block var0) {
      return new MultiVariantGenerator(var0, ImmutableList.of(Variant.variant()));
   }

   public static MultiVariantGenerator multiVariant(Block var0, Variant var1) {
      return new MultiVariantGenerator(var0, ImmutableList.of(var1));
   }

   public static MultiVariantGenerator multiVariant(Block var0, Variant... var1) {
      return new MultiVariantGenerator(var0, ImmutableList.copyOf(var1));
   }

   // $FF: synthetic method
   public Object get() {
      return this.get();
   }
}
