package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record ItemOverride(ResourceLocation model, List<ItemOverride.Predicate> predicates) {
   public ItemOverride(ResourceLocation model, List<ItemOverride.Predicate> predicates) {
      super();
      predicates = List.copyOf(predicates);
      this.model = model;
      this.predicates = predicates;
   }

   protected static class Deserializer implements JsonDeserializer<ItemOverride> {
      protected Deserializer() {
         super();
      }

      public ItemOverride deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = ResourceLocation.parse(GsonHelper.getAsString(var4, "model"));
         List var6 = this.getPredicates(var4);
         return new ItemOverride(var5, var6);
      }

      protected List<ItemOverride.Predicate> getPredicates(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(var1, "predicate");

         for (Entry var5 : var3.entrySet()) {
            var2.put(ResourceLocation.parse((String)var5.getKey()), GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return var2.entrySet()
            .stream()
            .map(var0 -> new ItemOverride.Predicate((ResourceLocation)var0.getKey(), (Float)var0.getValue()))
            .collect(ImmutableList.toImmutableList());
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
