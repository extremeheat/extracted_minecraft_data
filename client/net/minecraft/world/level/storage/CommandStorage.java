package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class CommandStorage {
   private static final String ID_PREFIX = "command_storage_";
   private final Map<String, Container> namespaces = new HashMap();
   private final DimensionDataStorage storage;

   public CommandStorage(DimensionDataStorage var1) {
      super();
      this.storage = var1;
   }

   public CompoundTag get(ResourceLocation var1) {
      Container var2 = this.getContainer(var1.getNamespace());
      return var2 != null ? var2.get(var1.getPath()) : new CompoundTag();
   }

   private @Nullable Container getContainer(String var1) {
      Container var2 = (Container)this.namespaces.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Container var3 = (Container)this.storage.get(CommandStorage.Container.type(var1));
         if (var3 != null) {
            this.namespaces.put(var1, var3);
         }

         return var3;
      }
   }

   private Container getOrCreateContainer(String var1) {
      Container var2 = (Container)this.namespaces.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Container var3 = (Container)this.storage.computeIfAbsent(CommandStorage.Container.type(var1));
         this.namespaces.put(var1, var3);
         return var3;
      }
   }

   public void set(ResourceLocation var1, CompoundTag var2) {
      this.getOrCreateContainer(var1.getNamespace()).put(var1.getPath(), var2);
   }

   public Stream<ResourceLocation> keys() {
      return this.namespaces.entrySet().stream().flatMap((var0) -> ((Container)var0.getValue()).getKeys((String)var0.getKey()));
   }

   static String createId(String var0) {
      return "command_storage_" + var0;
   }

   static class Container extends SavedData {
      public static final Codec<Container> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(ExtraCodecs.RESOURCE_PATH_CODEC, CompoundTag.CODEC).fieldOf("contents").forGetter((var0x) -> var0x.storage)).apply(var0, Container::new));
      private final Map<String, CompoundTag> storage;

      private Container(Map<String, CompoundTag> var1) {
         super();
         this.storage = new HashMap(var1);
      }

      private Container() {
         this(new HashMap());
      }

      public static SavedDataType<Container> type(String var0) {
         return new SavedDataType<Container>(CommandStorage.createId(var0), Container::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
      }

      public CompoundTag get(String var1) {
         CompoundTag var2 = (CompoundTag)this.storage.get(var1);
         return var2 != null ? var2 : new CompoundTag();
      }

      public void put(String var1, CompoundTag var2) {
         if (var2.isEmpty()) {
            this.storage.remove(var1);
         } else {
            this.storage.put(var1, var2);
         }

         this.setDirty();
      }

      public Stream<ResourceLocation> getKeys(String var1) {
         return this.storage.keySet().stream().map((var1x) -> ResourceLocation.fromNamespaceAndPath(var1, var1x));
      }
   }
}
