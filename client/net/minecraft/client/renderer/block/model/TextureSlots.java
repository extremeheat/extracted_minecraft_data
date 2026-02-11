package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
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
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class TextureSlots {
   public static final TextureSlots EMPTY = new TextureSlots(Map.of());
   private static final char REFERENCE_CHAR = '#';
   private final Map<String, Material> resolvedValues;

   private TextureSlots(final Map<String, Material> resolvedValues) {
      super();
      this.resolvedValues = resolvedValues;
   }

   public @Nullable Material getMaterial(String reference) {
      if (isTextureReference(reference)) {
         reference = reference.substring(1);
      }

      return (Material)this.resolvedValues.get(reference);
   }

   private static boolean isTextureReference(final String texture) {
      return texture.charAt(0) == '#';
   }

   public static Data parseTextureMap(final JsonObject texturesObject) {
      Data.Builder builder = new Data.Builder();

      for(Map.Entry<String, JsonElement> entry : texturesObject.entrySet()) {
         parseEntry((String)entry.getKey(), (JsonElement)entry.getValue(), builder);
      }

      return builder.build();
   }

   private static void parseEntry(final String slot, final JsonElement value, final Data.Builder output) {
      if (GsonHelper.isStringValue(value) && isTextureReference(value.getAsString())) {
         output.addReference(slot, value.getAsString().substring(1));
      } else {
         output.addTexture(slot, (Material)Material.CODEC.parse(JsonOps.INSTANCE, value).getOrThrow(JsonParseException::new));
      }

   }

   private static record Value(Material material) implements SlotContents {
      private Value {
         super();
      }
   }

   private static record Reference(String target) implements SlotContents {
      private Reference {
         super();
      }
   }

   public static record Data(Map<String, SlotContents> values) {
      public static final Data EMPTY = new Data(Map.of());

      public Data {
         super();
      }

      public static class Builder {
         private final Map<String, SlotContents> textureMap = new HashMap();

         public Builder() {
            super();
         }

         public Builder addReference(final String slot, final String reference) {
            this.textureMap.put(slot, new Reference(reference));
            return this;
         }

         public Builder addTexture(final String slot, final Material material) {
            this.textureMap.put(slot, new Value(material));
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

      public Resolver addLast(final Data data) {
         this.entries.addLast(data);
         return this;
      }

      public Resolver addFirst(final Data data) {
         this.entries.addFirst(data);
         return this;
      }

      public TextureSlots resolve(final ModelDebugName debugNameProvider) {
         if (this.entries.isEmpty()) {
            return TextureSlots.EMPTY;
         } else {
            Object2ObjectMap<String, Material> resolved = new Object2ObjectArrayMap();
            Object2ObjectMap<String, Reference> unresolved = new Object2ObjectArrayMap();

            for(Data data : Lists.reverse(this.entries)) {
               data.values.forEach((slot, contents) -> {
                  Objects.requireNonNull(contents);
                  int index$1 = 0;
                  //$FF: index$1->value
                  //0->net/minecraft/client/renderer/block/model/TextureSlots$Value
                  //1->net/minecraft/client/renderer/block/model/TextureSlots$Reference
                  switch (contents.typeSwitch<invokedynamic>(contents, index$1)) {
                     case 0:
                        Value value = (Value)contents;
                        unresolved.remove(slot);
                        resolved.put(slot, value.material());
                        break;
                     case 1:
                        Reference reference = (Reference)contents;
                        resolved.remove(slot);
                        unresolved.put(slot, reference);
                        break;
                     default:
                        throw new MatchException((String)null, (Throwable)null);
                  }

               });
            }

            if (unresolved.isEmpty()) {
               return new TextureSlots(resolved);
            } else {
               boolean hasChanges = true;

               while(hasChanges) {
                  hasChanges = false;
                  ObjectIterator<Object2ObjectMap.Entry<String, Reference>> iterator = Object2ObjectMaps.fastIterator(unresolved);

                  while(iterator.hasNext()) {
                     Object2ObjectMap.Entry<String, Reference> entry = (Object2ObjectMap.Entry)iterator.next();
                     Material maybeResolved = (Material)resolved.get(((Reference)entry.getValue()).target);
                     if (maybeResolved != null) {
                        resolved.put((String)entry.getKey(), maybeResolved);
                        iterator.remove();
                        hasChanges = true;
                     }
                  }
               }

               if (!unresolved.isEmpty()) {
                  LOGGER.warn("Unresolved texture references in {}:\n{}", debugNameProvider.debugName(), unresolved.entrySet().stream().map((e) -> {
                     String var10000 = (String)e.getKey();
                     return "\t#" + var10000 + "-> #" + ((Reference)e.getValue()).target + "\n";
                  }).collect(Collectors.joining()));
               }

               return new TextureSlots(resolved);
            }
         }
      }
   }

   public sealed interface SlotContents permits TextureSlots.Value, TextureSlots.Reference {
   }
}
