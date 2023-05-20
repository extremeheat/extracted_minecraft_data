package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap();
   private static final int MAX_ID_VALUE = 254;
   private final Entity entity;
   private final Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = new Int2ObjectOpenHashMap();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isDirty;

   public SynchedEntityData(Entity var1) {
      super();
      this.entity = var1;
   }

   public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> var0, EntityDataSerializer<T> var1) {
      if (LOGGER.isDebugEnabled()) {
         try {
            Class var2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!var2.equals(var0)) {
               LOGGER.debug("defineId called for: {} from {}", new Object[]{var0, var2, new RuntimeException()});
            }
         } catch (ClassNotFoundException var5) {
         }
      }

      int var6;
      if (ENTITY_ID_POOL.containsKey(var0)) {
         var6 = ENTITY_ID_POOL.getInt(var0) + 1;
      } else {
         int var3 = 0;
         Class var4 = var0;

         while(var4 != Entity.class) {
            var4 = var4.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(var4)) {
               var3 = ENTITY_ID_POOL.getInt(var4) + 1;
               break;
            }
         }

         var6 = var3;
      }

      if (var6 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var6 + "! (Max is 254)");
      } else {
         ENTITY_ID_POOL.put(var0, var6);
         return var1.createAccessor(var6);
      }
   }

   public <T> void define(EntityDataAccessor<T> var1, T var2) {
      int var3 = var1.getId();
      if (var3 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is 254)");
      } else if (this.itemsById.containsKey(var3)) {
         throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
      } else if (EntityDataSerializers.getSerializedId(var1.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + var1.getSerializer() + " for " + var3 + "!");
      } else {
         this.createDataItem(var1, var2);
      }
   }

   private <T> void createDataItem(EntityDataAccessor<T> var1, T var2) {
      SynchedEntityData.DataItem var3 = new SynchedEntityData.DataItem<>(var1, var2);
      this.lock.writeLock().lock();
      this.itemsById.put(var1.getId(), var3);
      this.lock.writeLock().unlock();
   }

   private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> var1) {
      this.lock.readLock().lock();

      SynchedEntityData.DataItem var2;
      try {
         var2 = (SynchedEntityData.DataItem)this.itemsById.get(var1.getId());
      } catch (Throwable var9) {
         CrashReport var4 = CrashReport.forThrowable(var9, "Getting synched entity data");
         CrashReportCategory var5 = var4.addCategory("Synched entity data");
         var5.setDetail("Data ID", var1);
         throw new ReportedException(var4);
      } finally {
         this.lock.readLock().unlock();
      }

      return var2;
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
         var4.setValue((T)var2);
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
      ArrayList var1 = null;
      if (this.isDirty) {
         this.lock.readLock().lock();
         ObjectIterator var2 = this.itemsById.values().iterator();

         while(var2.hasNext()) {
            SynchedEntityData.DataItem var3 = (SynchedEntityData.DataItem)var2.next();
            if (var3.isDirty()) {
               var3.setDirty(false);
               if (var1 == null) {
                  var1 = new ArrayList();
               }

               var1.add(var3.value());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return var1;
   }

   @Nullable
   public List<SynchedEntityData.DataValue<?>> getNonDefaultValues() {
      ArrayList var1 = null;
      this.lock.readLock().lock();
      ObjectIterator var2 = this.itemsById.values().iterator();

      while(var2.hasNext()) {
         SynchedEntityData.DataItem var3 = (SynchedEntityData.DataItem)var2.next();
         if (!var3.isSetToDefault()) {
            if (var1 == null) {
               var1 = new ArrayList();
            }

            var1.add(var3.value());
         }
      }

      this.lock.readLock().unlock();
      return var1;
   }

   public void assignValues(List<SynchedEntityData.DataValue<?>> var1) {
      this.lock.writeLock().lock();

      try {
         for(SynchedEntityData.DataValue var3 : var1) {
            SynchedEntityData.DataItem var4 = (SynchedEntityData.DataItem)this.itemsById.get(var3.id);
            if (var4 != null) {
               this.assignValue(var4, var3);
               this.entity.onSyncedDataUpdated(var4.getAccessor());
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      this.entity.onSyncedDataUpdated(var1);
   }

   private <T> void assignValue(SynchedEntityData.DataItem<T> var1, SynchedEntityData.DataValue<?> var2) {
      if (!Objects.equals(var2.serializer(), var1.accessor.getSerializer())) {
         throw new IllegalStateException(
            String.format(
               Locale.ROOT,
               "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)",
               var1.accessor.getId(),
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

   public boolean isEmpty() {
      return this.itemsById.isEmpty();
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

   public static record DataValue<T>(int a, EntityDataSerializer<T> b, T c) {
      final int id;
      private final EntityDataSerializer<T> serializer;
      final T value;

      public DataValue(int var1, EntityDataSerializer<T> var2, T var3) {
         super();
         this.id = var1;
         this.serializer = var2;
         this.value = (T)var3;
      }

      public static <T> SynchedEntityData.DataValue<T> create(EntityDataAccessor<T> var0, T var1) {
         EntityDataSerializer var2 = var0.getSerializer();
         return new SynchedEntityData.DataValue<>(var0.getId(), var2, (T)var2.copy(var1));
      }

      public void write(FriendlyByteBuf var1) {
         int var2 = EntityDataSerializers.getSerializedId(this.serializer);
         if (var2 < 0) {
            throw new EncoderException("Unknown serializer type " + this.serializer);
         } else {
            var1.writeByte(this.id);
            var1.writeVarInt(var2);
            this.serializer.write(var1, this.value);
         }
      }

      public static SynchedEntityData.DataValue<?> read(FriendlyByteBuf var0, int var1) {
         int var2 = var0.readVarInt();
         EntityDataSerializer var3 = EntityDataSerializers.getSerializer(var2);
         if (var3 == null) {
            throw new DecoderException("Unknown serializer type " + var2);
         } else {
            return read(var0, var1, var3);
         }
      }

      private static <T> SynchedEntityData.DataValue<T> read(FriendlyByteBuf var0, int var1, EntityDataSerializer<T> var2) {
         return new SynchedEntityData.DataValue<>(var1, var2, (T)var2.read(var0));
      }
   }
}
