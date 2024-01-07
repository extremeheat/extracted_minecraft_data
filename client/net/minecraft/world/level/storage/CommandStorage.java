package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class CommandStorage {
   private static final String ID_PREFIX = "command_storage_";
   private final Map<String, CommandStorage.Container> namespaces = Maps.newHashMap();
   private final DimensionDataStorage storage;

   public CommandStorage(DimensionDataStorage var1) {
      super();
      this.storage = var1;
   }

   private CommandStorage.Container newStorage(String var1) {
      CommandStorage.Container var2 = new CommandStorage.Container();
      this.namespaces.put(var1, var2);
      return var2;
   }

   private SavedData.Factory<CommandStorage.Container> factory(String var1) {
      return new SavedData.Factory<>(() -> this.newStorage(var1), var2 -> this.newStorage(var1).load(var2), DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
   }

   public CompoundTag get(ResourceLocation var1) {
      String var2 = var1.getNamespace();
      CommandStorage.Container var3 = this.storage.get(this.factory(var2), createId(var2));
      return var3 != null ? var3.get(var1.getPath()) : new CompoundTag();
   }

   public void set(ResourceLocation var1, CompoundTag var2) {
      String var3 = var1.getNamespace();
      this.storage.computeIfAbsent(this.factory(var3), createId(var3)).put(var1.getPath(), var2);
   }

   public Stream<ResourceLocation> keys() {
      return this.namespaces.entrySet().stream().flatMap(var0 -> var0.getValue().getKeys(var0.getKey()));
   }

   private static String createId(String var0) {
      return "command_storage_" + var0;
   }

   static class Container extends SavedData {
      private static final String TAG_CONTENTS = "contents";
      private final Map<String, CompoundTag> storage = Maps.newHashMap();

      Container() {
         super();
      }

      CommandStorage.Container load(CompoundTag var1) {
         CompoundTag var2 = var1.getCompound("contents");

         for(String var4 : var2.getAllKeys()) {
            this.storage.put(var4, var2.getCompound(var4));
         }

         return this;
      }

      @Override
      public CompoundTag save(CompoundTag var1) {
         CompoundTag var2 = new CompoundTag();
         this.storage.forEach((var1x, var2x) -> var2.put(var1x, var2x.copy()));
         var1.put("contents", var2);
         return var1;
      }

      public CompoundTag get(String var1) {
         CompoundTag var2 = this.storage.get(var1);
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
         return this.storage.keySet().stream().map(var1x -> new ResourceLocation(var1, var1x));
      }
   }
}
