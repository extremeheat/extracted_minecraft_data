package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class CommandStorage {
   private static final String ID_PREFIX = "command_storage_";
   private final Map<String, Container> namespaces = new HashMap();
   private final DimensionDataStorage storage;

   public CommandStorage(final DimensionDataStorage storage) {
      super();
      this.storage = storage;
   }

   public CompoundTag get(final Identifier id) {
      Container container = this.getContainer(id.getNamespace());
      return container != null ? container.get(id.getPath()) : new CompoundTag();
   }

   private @Nullable Container getContainer(final String namespace) {
      Container container = (Container)this.namespaces.get(namespace);
      if (container != null) {
         return container;
      } else {
         Container newContainer = (Container)this.storage.get(CommandStorage.Container.type(namespace));
         if (newContainer != null) {
            this.namespaces.put(namespace, newContainer);
         }

         return newContainer;
      }
   }

   private Container getOrCreateContainer(final String namespace) {
      Container container = (Container)this.namespaces.get(namespace);
      if (container != null) {
         return container;
      } else {
         Container newContainer = (Container)this.storage.computeIfAbsent(CommandStorage.Container.type(namespace));
         this.namespaces.put(namespace, newContainer);
         return newContainer;
      }
   }

   public void set(final Identifier id, final CompoundTag contents) {
      this.getOrCreateContainer(id.getNamespace()).put(id.getPath(), contents);
   }

   public Stream<Identifier> keys() {
      return this.namespaces.entrySet().stream().flatMap((e) -> ((Container)e.getValue()).getKeys((String)e.getKey()));
   }

   private static String createId(final String namespace) {
      return "command_storage_" + namespace;
   }

   private static class Container extends SavedData {
      public static final Codec<Container> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(ExtraCodecs.RESOURCE_PATH_CODEC, CompoundTag.CODEC).fieldOf("contents").forGetter((container) -> container.storage)).apply(i, Container::new));
      private final Map<String, CompoundTag> storage;

      private Container(final Map<String, CompoundTag> storage) {
         super();
         this.storage = new HashMap(storage);
      }

      private Container() {
         this(new HashMap());
      }

      public static SavedDataType<Container> type(final String namespace) {
         return new SavedDataType<Container>(CommandStorage.createId(namespace), Container::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
      }

      public CompoundTag get(final String id) {
         CompoundTag result = (CompoundTag)this.storage.get(id);
         return result != null ? result : new CompoundTag();
      }

      public void put(final String id, final CompoundTag contents) {
         if (contents.isEmpty()) {
            this.storage.remove(id);
         } else {
            this.storage.put(id, contents);
         }

         this.setDirty();
      }

      public Stream<Identifier> getKeys(final String namespace) {
         return this.storage.keySet().stream().map((p) -> Identifier.fromNamespaceAndPath(namespace, p));
      }
   }
}
