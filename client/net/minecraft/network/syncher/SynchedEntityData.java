package net.minecraft.network.syncher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<? extends Entity>, Integer> ENTITY_ID_POOL = Maps.newHashMap();
   private final Entity entity;
   private final Map<Integer, SynchedEntityData.DataItem<?>> itemsById = Maps.newHashMap();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isEmpty = true;
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
               LOGGER.debug("defineId called for: {} from {}", var0, var2, new RuntimeException());
            }
         } catch (ClassNotFoundException var5) {
         }
      }

      int var6;
      if (ENTITY_ID_POOL.containsKey(var0)) {
         var6 = (Integer)ENTITY_ID_POOL.get(var0) + 1;
      } else {
         int var3 = 0;
         Class var4 = var0;

         while(var4 != Entity.class) {
            var4 = var4.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(var4)) {
               var3 = (Integer)ENTITY_ID_POOL.get(var4) + 1;
               break;
            }
         }

         var6 = var3;
      }

      if (var6 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var6 + "! (Max is " + 254 + ")");
      } else {
         ENTITY_ID_POOL.put(var0, var6);
         return var1.createAccessor(var6);
      }
   }

   public <T> void define(EntityDataAccessor<T> var1, T var2) {
      int var3 = var1.getId();
      if (var3 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is " + 254 + ")");
      } else if (this.itemsById.containsKey(var3)) {
         throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
      } else if (EntityDataSerializers.getSerializedId(var1.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + var1.getSerializer() + " for " + var3 + "!");
      } else {
         this.createDataItem(var1, var2);
      }
   }

   private <T> void createDataItem(EntityDataAccessor<T> var1, T var2) {
      SynchedEntityData.DataItem var3 = new SynchedEntityData.DataItem(var1, var2);
      this.lock.writeLock().lock();
      this.itemsById.put(var1.getId(), var3);
      this.isEmpty = false;
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
         var5.setDetail("Data ID", (Object)var1);
         throw new ReportedException(var4);
      } finally {
         this.lock.readLock().unlock();
      }

      return var2;
   }

   public <T> T get(EntityDataAccessor<T> var1) {
      return this.getItem(var1).getValue();
   }

   public <T> void set(EntityDataAccessor<T> var1, T var2) {
      SynchedEntityData.DataItem var3 = this.getItem(var1);
      if (ObjectUtils.notEqual(var2, var3.getValue())) {
         var3.setValue(var2);
         this.entity.onSyncedDataUpdated(var1);
         var3.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public static void pack(List<SynchedEntityData.DataItem<?>> var0, FriendlyByteBuf var1) {
      if (var0 != null) {
         int var2 = 0;

         for(int var3 = var0.size(); var2 < var3; ++var2) {
            writeDataItem(var1, (SynchedEntityData.DataItem)var0.get(var2));
         }
      }

      var1.writeByte(255);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> packDirty() {
      ArrayList var1 = null;
      if (this.isDirty) {
         this.lock.readLock().lock();
         Iterator var2 = this.itemsById.values().iterator();

         while(var2.hasNext()) {
            SynchedEntityData.DataItem var3 = (SynchedEntityData.DataItem)var2.next();
            if (var3.isDirty()) {
               var3.setDirty(false);
               if (var1 == null) {
                  var1 = Lists.newArrayList();
               }

               var1.add(var3.copy());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return var1;
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getAll() {
      ArrayList var1 = null;
      this.lock.readLock().lock();

      SynchedEntityData.DataItem var3;
      for(Iterator var2 = this.itemsById.values().iterator(); var2.hasNext(); var1.add(var3.copy())) {
         var3 = (SynchedEntityData.DataItem)var2.next();
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }
      }

      this.lock.readLock().unlock();
      return var1;
   }

   private static <T> void writeDataItem(FriendlyByteBuf var0, SynchedEntityData.DataItem<T> var1) {
      EntityDataAccessor var2 = var1.getAccessor();
      int var3 = EntityDataSerializers.getSerializedId(var2.getSerializer());
      if (var3 < 0) {
         throw new EncoderException("Unknown serializer type " + var2.getSerializer());
      } else {
         var0.writeByte(var2.getId());
         var0.writeVarInt(var3);
         var2.getSerializer().write(var0, var1.getValue());
      }
   }

   @Nullable
   public static List<SynchedEntityData.DataItem<?>> unpack(FriendlyByteBuf var0) {
      ArrayList var1 = null;

      short var2;
      while((var2 = var0.readUnsignedByte()) != 255) {
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }

         int var3 = var0.readVarInt();
         EntityDataSerializer var4 = EntityDataSerializers.getSerializer(var3);
         if (var4 == null) {
            throw new DecoderException("Unknown serializer type " + var3);
         }

         var1.add(genericHelper(var0, var2, var4));
      }

      return var1;
   }

   private static <T> SynchedEntityData.DataItem<T> genericHelper(FriendlyByteBuf var0, int var1, EntityDataSerializer<T> var2) {
      return new SynchedEntityData.DataItem(var2.createAccessor(var1), var2.read(var0));
   }

   public void assignValues(List<SynchedEntityData.DataItem<?>> var1) {
      this.lock.writeLock().lock();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         SynchedEntityData.DataItem var3 = (SynchedEntityData.DataItem)var2.next();
         SynchedEntityData.DataItem var4 = (SynchedEntityData.DataItem)this.itemsById.get(var3.getAccessor().getId());
         if (var4 != null) {
            this.assignValue(var4, var3);
            this.entity.onSyncedDataUpdated(var3.getAccessor());
         }
      }

      this.lock.writeLock().unlock();
      this.isDirty = true;
   }

   private <T> void assignValue(SynchedEntityData.DataItem<T> var1, SynchedEntityData.DataItem<?> var2) {
      if (!Objects.equals(var2.accessor.getSerializer(), var1.accessor.getSerializer())) {
         throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", var1.accessor.getId(), this.entity, var1.value, var1.value.getClass(), var2.value, var2.value.getClass()));
      } else {
         var1.setValue(var2.getValue());
      }
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public void clearDirty() {
      this.isDirty = false;
      this.lock.readLock().lock();
      Iterator var1 = this.itemsById.values().iterator();

      while(var1.hasNext()) {
         SynchedEntityData.DataItem var2 = (SynchedEntityData.DataItem)var1.next();
         var2.setDirty(false);
      }

      this.lock.readLock().unlock();
   }

   public static class DataItem<T> {
      private final EntityDataAccessor<T> accessor;
      private T value;
      private boolean dirty;

      public DataItem(EntityDataAccessor<T> var1, T var2) {
         super();
         this.accessor = var1;
         this.value = var2;
         this.dirty = true;
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

      public SynchedEntityData.DataItem<T> copy() {
         return new SynchedEntityData.DataItem(this.accessor, this.accessor.getSerializer().copy(this.value));
      }
   }
}
