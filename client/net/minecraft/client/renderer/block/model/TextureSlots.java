package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextureSlots {
   public static final TextureSlots EMPTY = new TextureSlots(Map.of());
   private static final char REFERENCE_CHAR = '#';
   private final Map<String, Material> resolvedValues;

   TextureSlots(Map<String, Material> var1) {
      super();
      this.resolvedValues = var1;
   }

   @Nullable
   public Material getMaterial(String var1) {
      if (isTextureReference(var1)) {
         var1 = var1.substring(1);
      }

      return (Material)this.resolvedValues.get(var1);
   }

   private static boolean isTextureReference(String var0) {
      return var0.charAt(0) == '#';
   }

   public static Data parseTextureMap(JsonObject var0, ResourceLocation var1) {
      Data.Builder var2 = new Data.Builder();

      for(Map.Entry var4 : var0.entrySet()) {
         parseEntry(var1, (String)var4.getKey(), ((JsonElement)var4.getValue()).getAsString(), var2);
      }

      return var2.build();
   }

   private static void parseEntry(ResourceLocation var0, String var1, String var2, Data.Builder var3) {
      if (isTextureReference(var2)) {
         var3.addReference(var1, var2.substring(1));
      } else {
         ResourceLocation var4 = ResourceLocation.tryParse(var2);
         if (var4 == null) {
            throw new JsonParseException(var2 + " is not valid resource location");
         }

         var3.addTexture(var1, new Material(var0, var4));
      }

   }

   static record Value(Material material) implements SlotContents {
      Value(Material var1) {
         super();
         this.material = var1;
      }
   }

   static record Reference(String target) implements SlotContents {
      final String target;

      Reference(String var1) {
         super();
         this.target = var1;
      }
   }

   public static record Data(Map<String, SlotContents> values) {
      final Map<String, SlotContents> values;
      public static final Data EMPTY = new Data(Map.of());

      public Data(Map<String, SlotContents> var1) {
         super();
         this.values = var1;
      }

      public static class Builder {
         private final Map<String, SlotContents> textureMap = new HashMap();

         public Builder() {
            super();
         }

         public Builder addReference(String var1, String var2) {
            this.textureMap.put(var1, new Reference(var2));
            return this;
         }

         public Builder addTexture(String var1, Material var2) {
            this.textureMap.put(var1, new Value(var2));
            return this;
         }

         public Data build() {
            return this.textureMap.isEmpty() ? TextureSlots.Data.EMPTY : new Data(Map.copyOf(this.textureMap));
         }
      }
   }

   public static class Resolver {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final List<Data> entries = new ArrayList();

      public Resolver() {
         super();
      }

      public Resolver addLast(Data var1) {
         this.entries.addLast(var1);
         return this;
      }

      public Resolver addFirst(Data var1) {
         this.entries.addFirst(var1);
         return this;
      }

      public TextureSlots resolve(ModelDebugName var1) {
         if (this.entries.isEmpty()) {
            return TextureSlots.EMPTY;
         } else {
            Object2ObjectArrayMap var2 = new Object2ObjectArrayMap();
            Object2ObjectArrayMap var3 = new Object2ObjectArrayMap();

            for(Data var5 : Lists.reverse(this.entries)) {
               var5.values.forEach((var2x, var3x) -> {
                  Objects.requireNonNull(var3x);
                  byte var5 = 0;
                  //$FF: var5->value
                  //0->net/minecraft/client/renderer/block/model/TextureSlots$Value
                  //1->net/minecraft/client/renderer/block/model/TextureSlots$Reference
                  switch (var3x.typeSwitch<invokedynamic>(var3x, var5)) {
                     case 0:
                        Value var6 = (Value)var3x;
                        var3.remove(var2x);
                        var2.put(var2x, var6.material());
                        break;
                     case 1:
                        Reference var7 = (Reference)var3x;
                        var2.remove(var2x);
                        var3.put(var2x, var7);
                        break;
                     default:
                        throw new MatchException((String)null, (Throwable)null);
                  }

               });
            }

            if (var3.isEmpty()) {
               return new TextureSlots(var2);
            } else {
               boolean var8 = true;

               while(var8) {
                  var8 = false;
                  ObjectIterator var9 = Object2ObjectMaps.fastIterator(var3);

                  while(var9.hasNext()) {
                     Object2ObjectMap.Entry var6 = (Object2ObjectMap.Entry)var9.next();
                     Material var7 = (Material)var2.get(((Reference)var6.getValue()).target);
                     if (var7 != null) {
                        var2.put((String)var6.getKey(), var7);
                        var9.remove();
                        var8 = true;
                     }
                  }
               }

               if (!var3.isEmpty()) {
                  LOGGER.warn("Unresolved texture references in {}:\n{}", var1.get(), var3.entrySet().stream().map((var0) -> {
                     String var10000 = (String)var0.getKey();
                     return "\t#" + var10000 + "-> #" + ((Reference)var0.getValue()).target + "\n";
                  }).collect(Collectors.joining()));
               }

               return new TextureSlots(var2);
            }
         }
      }
   }

   public sealed interface SlotContents permits TextureSlots.Value, TextureSlots.Reference {
   }
}
