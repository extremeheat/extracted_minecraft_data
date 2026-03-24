package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ClassTreeIdRegistry;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ID_VALUE = 254;
   private static final ClassTreeIdRegistry ID_REGISTRY = new ClassTreeIdRegistry();
   private final SyncedDataHolder entity;
   private final DataItem<?>[] itemsById;
   private boolean isDirty;

   private SynchedEntityData(final SyncedDataHolder entity, final DataItem<?>[] itemsById) {
      super();
      this.entity = entity;
      this.itemsById = itemsById;
   }

   public static <T> EntityDataAccessor<T> defineId(final Class<? extends SyncedDataHolder> clazz, final EntityDataSerializer<T> type) {
      if (LOGGER.isDebugEnabled()) {
         try {
            Class<?> aClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!aClass.equals(clazz)) {
               LOGGER.debug("defineId called for: {} from {}", new Object[]{clazz, aClass, new RuntimeException()});
            }
         } catch (ClassNotFoundException var3) {
         }
      }

      int id = ID_REGISTRY.define(clazz);
      if (id > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 254)");
      } else {
         return type.createAccessor(id);
      }
   }

   private <T> DataItem<T> getItem(final EntityDataAccessor<T> accessor) {
      return this.itemsById[accessor.id()];
   }

   public <T> T get(final EntityDataAccessor<T> accessor) {
      return (T)this.getItem(accessor).getValue();
   }

   public <T> void set(final EntityDataAccessor<T> accessor, final T value) {
      this.set(accessor, value, false);
   }

   public <T> void set(final EntityDataAccessor<T> accessor, final T value, final boolean forceDirty) {
      DataItem<T> dataItem = this.<T>getItem(accessor);
      if (forceDirty || ObjectUtils.notEqual(value, dataItem.getValue())) {
         dataItem.setValue(value);
         this.entity.onSyncedDataUpdated(accessor);
         dataItem.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public @Nullable List<DataValue<?>> packDirty() {
      if (!this.isDirty) {
         return null;
      } else {
         this.isDirty = false;
         List<DataValue<?>> result = new ArrayList();

         for(DataItem<?> dataItem : this.itemsById) {
            if (dataItem.isDirty()) {
               dataItem.setDirty(false);
               result.add(dataItem.value());
            }
         }

         return result;
      }
   }

   public @Nullable List<DataValue<?>> getNonDefaultValues() {
      List<DataValue<?>> result = null;

      for(DataItem<?> dataItem : this.itemsById) {
         if (!dataItem.isSetToDefault()) {
            if (result == null) {
               result = new ArrayList();
            }

            result.add(dataItem.value());
         }
      }

      return result;
   }

   public void assignValues(final List<DataValue<?>> items) {
      for(DataValue<?> item : items) {
         DataItem<?> dataItem = this.itemsById[item.id];
         this.assignValue(dataItem, item);
         this.entity.onSyncedDataUpdated(dataItem.getAccessor());
      }

      this.entity.onSyncedDataUpdated(items);
   }

   private <T> void assignValue(final DataItem<T> dataItem, final DataValue<?> item) {
      if (!Objects.equals(item.serializer(), dataItem.accessor.serializer())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", dataItem.accessor.id(), this.entity, dataItem.value, dataItem.value.getClass(), item.value, item.value.getClass()));
      } else {
         dataItem.setValue(item.value);
      }
   }

   public static record DataValue<T>(int id, EntityDataSerializer<T> serializer, T value) {
      public DataValue {
         super();
      }

      public static <T> DataValue<T> create(final EntityDataAccessor<T> accessor, final T value) {
         EntityDataSerializer<T> serializer = accessor.serializer();
         return new DataValue<T>(accessor.id(), serializer, serializer.copy(value));
      }

      public void write(final RegistryFriendlyByteBuf output) {
         int serializerId = EntityDataSerializers.getSerializedId(this.serializer);
         if (serializerId < 0) {
            throw new EncoderException("Unknown serializer type " + String.valueOf(this.serializer));
         } else {
            output.writeByte(this.id);
            output.writeVarInt(serializerId);
            this.serializer.codec().encode(output, this.value);
         }
      }

      public static DataValue<?> read(final RegistryFriendlyByteBuf input, final int id) {
         int type = input.readVarInt();
         EntityDataSerializer<?> serializer = EntityDataSerializers.getSerializer(type);
         if (serializer == null) {
            throw new DecoderException("Unknown serializer type " + type);
         } else {
            return read(input, id, serializer);
         }
      }

      private static <T> DataValue<T> read(final RegistryFriendlyByteBuf input, final int id, final EntityDataSerializer<T> serializer) {
         return new DataValue<T>(id, serializer, serializer.codec().decode(input));
      }
   }

   public static class DataItem<T> {
      private final EntityDataAccessor<T> accessor;
      private T value;
      private final T initialValue;
      private boolean dirty;

      public DataItem(final EntityDataAccessor<T> accessor, final T initialValue) {
         super();
         this.accessor = accessor;
         this.initialValue = initialValue;
         this.value = initialValue;
      }

      public EntityDataAccessor<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(final T value) {
         this.value = value;
      }

      public T getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(final boolean dirty) {
         this.dirty = dirty;
      }

      public boolean isSetToDefault() {
         return this.initialValue.equals(this.value);
      }

      public DataValue<T> value() {
         return SynchedEntityData.DataValue.<T>create(this.accessor, this.value);
      }
   }

   public static class Builder {
      private final SyncedDataHolder entity;
      private final @Nullable SynchedEntityData.DataItem<?>[] itemsById;

      public Builder(final SyncedDataHolder entity) {
         super();
         this.entity = entity;
         this.itemsById = new DataItem[SynchedEntityData.ID_REGISTRY.getCount(entity.getClass())];
      }

      public <T> Builder define(final EntityDataAccessor<T> accessor, final T value) {
         int id = accessor.id();
         if (id > this.itemsById.length) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + this.itemsById.length + ")");
         } else if (this.itemsById[id] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
         } else if (EntityDataSerializers.getSerializedId(accessor.serializer()) < 0) {
            String var10002 = String.valueOf(accessor.serializer());
            throw new IllegalArgumentException("Unregistered serializer " + var10002 + " for " + id + "!");
         } else {
            this.itemsById[accessor.id()] = new DataItem(accessor, value);
            return this;
         }
      }

      public SynchedEntityData build() {
         for(int i = 0; i < this.itemsById.length; ++i) {
            if (this.itemsById[i] == null) {
               String var10002 = String.valueOf(this.entity.getClass());
               throw new IllegalStateException("Entity " + var10002 + " has not defined synched data value " + i);
            }
         }

         return new SynchedEntityData(this.entity, this.itemsById);
      }
   }
}
