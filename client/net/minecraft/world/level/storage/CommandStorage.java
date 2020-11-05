package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;

public class CommandStorage {
   private final Map<String, CommandStorage.Container> namespaces = Maps.newHashMap();
   private final DimensionDataStorage storage;

   public CommandStorage(DimensionDataStorage var1) {
      super();
      this.storage = var1;
   }

   private CommandStorage.Container newStorage(String var1, String var2) {
      CommandStorage.Container var3 = new CommandStorage.Container(var2);
      this.namespaces.put(var1, var3);
      return var3;
   }

   public CompoundTag get(ResourceLocation var1) {
      String var2 = var1.getNamespace();
      String var3 = createId(var2);
      CommandStorage.Container var4 = (CommandStorage.Container)this.storage.get(() -> {
         return this.newStorage(var2, var3);
      }, var3);
      return var4 != null ? var4.get(var1.getPath()) : new CompoundTag();
   }

   public void set(ResourceLocation var1, CompoundTag var2) {
      String var3 = var1.getNamespace();
      String var4 = createId(var3);
      ((CommandStorage.Container)this.storage.computeIfAbsent(() -> {
         return this.newStorage(var3, var4);
      }, var4)).put(var1.getPath(), var2);
   }

   public Stream<ResourceLocation> keys() {
      return this.namespaces.entrySet().stream().flatMap((var0) -> {
         return ((CommandStorage.Container)var0.getValue()).getKeys((String)var0.getKey());
      });
   }

   private static String createId(String var0) {
      return "command_storage_" + var0;
   }

   static class Container extends SavedData {
      private final Map<String, CompoundTag> storage = Maps.newHashMap();

      public Container(String var1) {
         super(var1);
      }

      public void load(CompoundTag var1) {
         CompoundTag var2 = var1.getCompound("contents");
         Iterator var3 = var2.getAllKeys().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.storage.put(var4, var2.getCompound(var4));
         }

      }

      public CompoundTag save(CompoundTag var1) {
         CompoundTag var2 = new CompoundTag();
         this.storage.forEach((var1x, var2x) -> {
            var2.put(var1x, var2x.copy());
         });
         var1.put("contents", var2);
         return var1;
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
         return this.storage.keySet().stream().map((var1x) -> {
            return new ResourceLocation(var1, var1x);
         });
      }
   }
}
