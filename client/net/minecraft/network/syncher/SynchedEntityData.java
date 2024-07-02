package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.ClassTreeIdRegistry;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ID_VALUE = 254;
   static final ClassTreeIdRegistry ID_REGISTRY = new ClassTreeIdRegistry();
   private final SyncedDataHolder entity;
   private final SynchedEntityData.DataItem<?>[] itemsById;
   private boolean isDirty;

   SynchedEntityData(SyncedDataHolder var1, SynchedEntityData.DataItem<?>[] var2) {
      super();
      this.entity = var1;
      this.itemsById = var2;
   }

   public static <T> EntityDataAccessor<T> defineId(Class<? extends SyncedDataHolder> var0, EntityDataSerializer<T> var1) {
      if (LOGGER.isDebugEnabled()) {
         try {
            Class var2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!var2.equals(var0)) {
               LOGGER.debug("defineId called for: {} from {}", new Object[]{var0, var2, new RuntimeException()});
            }
         } catch (ClassNotFoundException var3) {
         }
      }

      int var4 = ID_REGISTRY.define(var0);
      if (var4 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var4 + "! (Max is 254)");
      } else {
         return var1.createAccessor(var4);
      }
   }

   private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> var1) {
      return (SynchedEntityData.DataItem<T>)this.itemsById[var1.id()];
   }

   public <T> T get(EntityDataAccessor<T> var1) {
      return this.<T>getItem(var1).getValue();
   }

   public <T> void set(EntityDataAccessor<T> var1, T var2) {
      this.set(var1, var2, false);
   }

   public <T> void set(EntityDataAccessor<T> var1, T var2, boolean var3) {
      SynchedEntityData.DataItem var4 = this.getItem(var1);
      if (var3 || ObjectUtils.notEqual(var2, var4.getValue())) {
         var4.setValue(var2);
         this.entity.onSyncedDataUpdated(var1);
         var4.setDirty(true);
         this.isDirty = true;
      }
   }

   public boolean isDirty() {
      return this.isDirty;
   }

   @Nullable
   public List<SynchedEntityData.DataValue<?>> packDirty() {
      if (!this.isDirty) {
         return null;
      } else {
         this.isDirty = false;
         ArrayList var1 = new ArrayList();

         for (SynchedEntityData.DataItem var5 : this.itemsById) {
            if (var5.isDirty()) {
               var5.setDirty(false);
               var1.add(var5.value());
            }
         }

         return var1;
      }
   }

   @Nullable
   public List<SynchedEntityData.DataValue<?>> getNonDefaultValues() {
      ArrayList var1 = null;

      for (SynchedEntityData.DataItem var5 : this.itemsById) {
         if (!var5.isSetToDefault()) {
            if (var1 == null) {
               var1 = new ArrayList();
            }

            var1.add(var5.value());
         }
      }

      return var1;
   }

   public void assignValues(List<SynchedEntityData.DataValue<?>> var1) {
      for (SynchedEntityData.DataValue var3 : var1) {
         SynchedEntityData.DataItem var4 = this.itemsById[var3.id];
         this.assignValue(var4, var3);
         this.entity.onSyncedDataUpdated(var4.getAccessor());
      }

      this.entity.onSyncedDataUpdated(var1);
   }

   private <T> void assignValue(SynchedEntityData.DataItem<T> var1, SynchedEntityData.DataValue<?> var2) {
      if (!Objects.equals(var2.serializer(), var1.accessor.serializer())) {
         throw new IllegalStateException(
            String.format(
               Locale.ROOT,
               "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)",
               var1.accessor.id(),
               this.entity,
               var1.value,
               var1.value.getClass(),
               var2.value,
               var2.value.getClass()
            )
         );
      } else {
         var1.setValue(var2.value);
      }
   }

   public static class Builder {
      private final SyncedDataHolder entity;
      private final SynchedEntityData.DataItem<?>[] itemsById;

      public Builder(SyncedDataHolder var1) {
         super();
         this.entity = var1;
         this.itemsById = new SynchedEntityData.DataItem[SynchedEntityData.ID_REGISTRY.getCount(var1.getClass())];
      }

      public <T> SynchedEntityData.Builder define(EntityDataAccessor<T> var1, T var2) {
         int var3 = var1.id();
         if (var3 > this.itemsById.length) {
            throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is " + this.itemsById.length + ")");
         } else if (this.itemsById[var3] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
         } else if (EntityDataSerializers.getSerializedId(var1.serializer()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + var1.serializer() + " for " + var3 + "!");
         } else {
            this.itemsById[var1.id()] = new SynchedEntityData.DataItem<>(var1, var2);
            return this;
         }
      }

      public SynchedEntityData build() {
         for (int var1 = 0; var1 < this.itemsById.length; var1++) {
            if (this.itemsById[var1] == null) {
               throw new IllegalStateException("Entity " + this.entity.getClass() + " has not defined synched data value " + var1);
            }
         }

         return new SynchedEntityData(this.entity, this.itemsById);
      }
   }

   public static class DataItem<T> {
      final EntityDataAccessor<T> accessor;
      T value;
      private final T initialValue;
      private boolean dirty;

      public DataItem(EntityDataAccessor<T> var1, T var2) {
         super();
         this.accessor = var1;
         this.initialValue = (T)var2;
         this.value = (T)var2;
      }

      public EntityDataAccessor<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(T var1) {
         this.value = (T)var1;
      }

      public T getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean var1) {
         this.dirty = var1;
      }

      public boolean isSetToDefault() {
         return this.initialValue.equals(this.value);
      }

      public SynchedEntityData.DataValue<T> value() {
         return SynchedEntityData.DataValue.create(this.accessor, this.value);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
