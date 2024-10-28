package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ClassTreeIdRegistry;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ID_VALUE = 254;
   static final ClassTreeIdRegistry ID_REGISTRY = new ClassTreeIdRegistry();
   private final SyncedDataHolder entity;
   private final DataItem<?>[] itemsById;
   private boolean isDirty;

   SynchedEntityData(SyncedDataHolder var1, DataItem<?>[] var2) {
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

   private <T> DataItem<T> getItem(EntityDataAccessor<T> var1) {
      return this.itemsById[var1.id()];
   }

   public <T> T get(EntityDataAccessor<T> var1) {
      return this.getItem(var1).getValue();
   }

   public <T> void set(EntityDataAccessor<T> var1, T var2) {
      this.set(var1, var2, false);
   }

   public <T> void set(EntityDataAccessor<T> var1, T var2, boolean var3) {
      DataItem var4 = this.getItem(var1);
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
   public List<DataValue<?>> packDirty() {
      if (!this.isDirty) {
         return null;
      } else {
         this.isDirty = false;
         ArrayList var1 = new ArrayList();
         DataItem[] var2 = this.itemsById;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            DataItem var5 = var2[var4];
            if (var5.isDirty()) {
               var5.setDirty(false);
               var1.add(var5.value());
            }
         }

         return var1;
      }
   }

   @Nullable
   public List<DataValue<?>> getNonDefaultValues() {
      ArrayList var1 = null;
      DataItem[] var2 = this.itemsById;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DataItem var5 = var2[var4];
         if (!var5.isSetToDefault()) {
            if (var1 == null) {
               var1 = new ArrayList();
            }

            var1.add(var5.value());
         }
      }

      return var1;
   }

   public void assignValues(List<DataValue<?>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         DataValue var3 = (DataValue)var2.next();
         DataItem var4 = this.itemsById[var3.id];
         this.assignValue(var4, var3);
         this.entity.onSyncedDataUpdated(var4.getAccessor());
      }

      this.entity.onSyncedDataUpdated(var1);
   }

   private <T> void assignValue(DataItem<T> var1, DataValue<?> var2) {
      if (!Objects.equals(var2.serializer(), var1.accessor.serializer())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", var1.accessor.id(), this.entity, var1.value, var1.value.getClass(), var2.value, var2.value.getClass()));
      } else {
         var1.setValue(var2.value);
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
         this.initialValue = var2;
         this.value = var2;
      }

      public EntityDataAccessor<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(T var1) {
         this.value = var1;
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

      public DataValue<T> value() {
         return SynchedEntityData.DataValue.create(this.accessor, this.value);
      }
   }

   public static record DataValue<T>(int id, EntityDataSerializer<T> serializer, T value) {
      final int id;
      final T value;

      public DataValue(int var1, EntityDataSerializer<T> var2, T var3) {
         super();
         this.id = var1;
         this.serializer = var2;
         this.value = var3;
      }

      public static <T> DataValue<T> create(EntityDataAccessor<T> var0, T var1) {
         EntityDataSerializer var2 = var0.serializer();
         return new DataValue(var0.id(), var2, var2.copy(var1));
      }

      public void write(RegistryFriendlyByteBuf var1) {
         int var2 = EntityDataSerializers.getSerializedId(this.serializer);
         if (var2 < 0) {
            throw new EncoderException("Unknown serializer type " + String.valueOf(this.serializer));
         } else {
            var1.writeByte(this.id);
            var1.writeVarInt(var2);
            this.serializer.codec().encode(var1, this.value);
         }
      }

      public static DataValue<?> read(RegistryFriendlyByteBuf var0, int var1) {
         int var2 = var0.readVarInt();
         EntityDataSerializer var3 = EntityDataSerializers.getSerializer(var2);
         if (var3 == null) {
            throw new DecoderException("Unknown serializer type " + var2);
         } else {
            return read(var0, var1, var3);
         }
      }

      private static <T> DataValue<T> read(RegistryFriendlyByteBuf var0, int var1, EntityDataSerializer<T> var2) {
         return new DataValue(var1, var2, var2.codec().decode(var0));
      }

      public int id() {
         return this.id;
      }

      public EntityDataSerializer<T> serializer() {
         return this.serializer;
      }

      public T value() {
         return this.value;
      }
   }

   public static class Builder {
      private final SyncedDataHolder entity;
      private final DataItem<?>[] itemsById;

      public Builder(SyncedDataHolder var1) {
         super();
         this.entity = var1;
         this.itemsById = new DataItem[SynchedEntityData.ID_REGISTRY.getCount(var1.getClass())];
      }

      public <T> Builder define(EntityDataAccessor<T> var1, T var2) {
         int var3 = var1.id();
         if (var3 > this.itemsById.length) {
            throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is " + this.itemsById.length + ")");
         } else if (this.itemsById[var3] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
         } else if (EntityDataSerializers.getSerializedId(var1.serializer()) < 0) {
            String var10002 = String.valueOf(var1.serializer());
            throw new IllegalArgumentException("Unregistered serializer " + var10002 + " for " + var3 + "!");
         } else {
            this.itemsById[var1.id()] = new DataItem(var1, var2);
            return this;
         }
      }

      public SynchedEntityData build() {
         for(int var1 = 0; var1 < this.itemsById.length; ++var1) {
            if (this.itemsById[var1] == null) {
               String var10002 = String.valueOf(this.entity.getClass());
               throw new IllegalStateException("Entity " + var10002 + " has not defined synched data value " + var1);
            }
         }

         return new SynchedEntityData(this.entity, this.itemsById);
      }
   }
}
