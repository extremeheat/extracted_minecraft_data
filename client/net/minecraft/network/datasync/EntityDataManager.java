package net.minecraft.network.datasync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDataManager {
   private static final Logger field_190303_a = LogManager.getLogger();
   private static final Map<Class<? extends Entity>, Integer> field_187232_a = Maps.newHashMap();
   private final Entity field_187233_b;
   private final Map<Integer, EntityDataManager.DataEntry<?>> field_187234_c = Maps.newHashMap();
   private final ReadWriteLock field_187235_d = new ReentrantReadWriteLock();
   private boolean field_187236_e = true;
   private boolean field_187237_f;

   public EntityDataManager(Entity var1) {
      super();
      this.field_187233_b = var1;
   }

   public static <T> DataParameter<T> func_187226_a(Class<? extends Entity> var0, DataSerializer<T> var1) {
      if (field_190303_a.isDebugEnabled()) {
         try {
            Class var2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!var2.equals(var0)) {
               field_190303_a.debug("defineId called for: {} from {}", var0, var2, new RuntimeException());
            }
         } catch (ClassNotFoundException var5) {
         }
      }

      int var6;
      if (field_187232_a.containsKey(var0)) {
         var6 = (Integer)field_187232_a.get(var0) + 1;
      } else {
         int var3 = 0;
         Class var4 = var0;

         while(var4 != Entity.class) {
            var4 = var4.getSuperclass();
            if (field_187232_a.containsKey(var4)) {
               var3 = (Integer)field_187232_a.get(var4) + 1;
               break;
            }
         }

         var6 = var3;
      }

      if (var6 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var6 + "! (Max is " + 254 + ")");
      } else {
         field_187232_a.put(var0, var6);
         return var1.func_187161_a(var6);
      }
   }

   public <T> void func_187214_a(DataParameter<T> var1, T var2) {
      int var3 = var1.func_187155_a();
      if (var3 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is " + 254 + ")");
      } else if (this.field_187234_c.containsKey(var3)) {
         throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
      } else if (DataSerializers.func_187188_b(var1.func_187156_b()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + var1.func_187156_b() + " for " + var3 + "!");
      } else {
         this.func_187222_c(var1, var2);
      }
   }

   private <T> void func_187222_c(DataParameter<T> var1, T var2) {
      EntityDataManager.DataEntry var3 = new EntityDataManager.DataEntry(var1, var2);
      this.field_187235_d.writeLock().lock();
      this.field_187234_c.put(var1.func_187155_a(), var3);
      this.field_187236_e = false;
      this.field_187235_d.writeLock().unlock();
   }

   private <T> EntityDataManager.DataEntry<T> func_187219_c(DataParameter<T> var1) {
      this.field_187235_d.readLock().lock();

      EntityDataManager.DataEntry var2;
      try {
         var2 = (EntityDataManager.DataEntry)this.field_187234_c.get(var1.func_187155_a());
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.func_85055_a(var6, "Getting synched entity data");
         CrashReportCategory var5 = var4.func_85058_a("Synched entity data");
         var5.func_71507_a("Data ID", var1);
         throw new ReportedException(var4);
      }

      this.field_187235_d.readLock().unlock();
      return var2;
   }

   public <T> T func_187225_a(DataParameter<T> var1) {
      return this.func_187219_c(var1).func_187206_b();
   }

   public <T> void func_187227_b(DataParameter<T> var1, T var2) {
      EntityDataManager.DataEntry var3 = this.func_187219_c(var1);
      if (ObjectUtils.notEqual(var2, var3.func_187206_b())) {
         var3.func_187210_a(var2);
         this.field_187233_b.func_184206_a(var1);
         var3.func_187208_a(true);
         this.field_187237_f = true;
      }

   }

   public boolean func_187223_a() {
      return this.field_187237_f;
   }

   public static void func_187229_a(List<EntityDataManager.DataEntry<?>> var0, PacketBuffer var1) throws IOException {
      if (var0 != null) {
         int var2 = 0;

         for(int var3 = var0.size(); var2 < var3; ++var2) {
            func_187220_a(var1, (EntityDataManager.DataEntry)var0.get(var2));
         }
      }

      var1.writeByte(255);
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> func_187221_b() {
      ArrayList var1 = null;
      if (this.field_187237_f) {
         this.field_187235_d.readLock().lock();
         Iterator var2 = this.field_187234_c.values().iterator();

         while(var2.hasNext()) {
            EntityDataManager.DataEntry var3 = (EntityDataManager.DataEntry)var2.next();
            if (var3.func_187209_c()) {
               var3.func_187208_a(false);
               if (var1 == null) {
                  var1 = Lists.newArrayList();
               }

               var1.add(var3.func_192735_d());
            }
         }

         this.field_187235_d.readLock().unlock();
      }

      this.field_187237_f = false;
      return var1;
   }

   public void func_187216_a(PacketBuffer var1) throws IOException {
      this.field_187235_d.readLock().lock();
      Iterator var2 = this.field_187234_c.values().iterator();

      while(var2.hasNext()) {
         EntityDataManager.DataEntry var3 = (EntityDataManager.DataEntry)var2.next();
         func_187220_a(var1, var3);
      }

      this.field_187235_d.readLock().unlock();
      var1.writeByte(255);
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> func_187231_c() {
      ArrayList var1 = null;
      this.field_187235_d.readLock().lock();

      EntityDataManager.DataEntry var3;
      for(Iterator var2 = this.field_187234_c.values().iterator(); var2.hasNext(); var1.add(var3.func_192735_d())) {
         var3 = (EntityDataManager.DataEntry)var2.next();
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }
      }

      this.field_187235_d.readLock().unlock();
      return var1;
   }

   private static <T> void func_187220_a(PacketBuffer var0, EntityDataManager.DataEntry<T> var1) throws IOException {
      DataParameter var2 = var1.func_187205_a();
      int var3 = DataSerializers.func_187188_b(var2.func_187156_b());
      if (var3 < 0) {
         throw new EncoderException("Unknown serializer type " + var2.func_187156_b());
      } else {
         var0.writeByte(var2.func_187155_a());
         var0.func_150787_b(var3);
         var2.func_187156_b().func_187160_a(var0, var1.func_187206_b());
      }
   }

   @Nullable
   public static List<EntityDataManager.DataEntry<?>> func_187215_b(PacketBuffer var0) throws IOException {
      ArrayList var1 = null;

      short var2;
      while((var2 = var0.readUnsignedByte()) != 255) {
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }

         int var3 = var0.func_150792_a();
         DataSerializer var4 = DataSerializers.func_187190_a(var3);
         if (var4 == null) {
            throw new DecoderException("Unknown serializer type " + var3);
         }

         var1.add(func_198167_a(var0, var2, var4));
      }

      return var1;
   }

   private static <T> EntityDataManager.DataEntry<T> func_198167_a(PacketBuffer var0, int var1, DataSerializer<T> var2) {
      return new EntityDataManager.DataEntry(var2.func_187161_a(var1), var2.func_187159_a(var0));
   }

   public void func_187218_a(List<EntityDataManager.DataEntry<?>> var1) {
      this.field_187235_d.writeLock().lock();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         EntityDataManager.DataEntry var3 = (EntityDataManager.DataEntry)var2.next();
         EntityDataManager.DataEntry var4 = (EntityDataManager.DataEntry)this.field_187234_c.get(var3.func_187205_a().func_187155_a());
         if (var4 != null) {
            this.func_187224_a(var4, var3);
            this.field_187233_b.func_184206_a(var3.func_187205_a());
         }
      }

      this.field_187235_d.writeLock().unlock();
      this.field_187237_f = true;
   }

   protected <T> void func_187224_a(EntityDataManager.DataEntry<T> var1, EntityDataManager.DataEntry<?> var2) {
      var1.func_187210_a(var2.func_187206_b());
   }

   public boolean func_187228_d() {
      return this.field_187236_e;
   }

   public void func_187230_e() {
      this.field_187237_f = false;
      this.field_187235_d.readLock().lock();
      Iterator var1 = this.field_187234_c.values().iterator();

      while(var1.hasNext()) {
         EntityDataManager.DataEntry var2 = (EntityDataManager.DataEntry)var1.next();
         var2.func_187208_a(false);
      }

      this.field_187235_d.readLock().unlock();
   }

   public static class DataEntry<T> {
      private final DataParameter<T> field_187211_a;
      private T field_187212_b;
      private boolean field_187213_c;

      public DataEntry(DataParameter<T> var1, T var2) {
         super();
         this.field_187211_a = var1;
         this.field_187212_b = var2;
         this.field_187213_c = true;
      }

      public DataParameter<T> func_187205_a() {
         return this.field_187211_a;
      }

      public void func_187210_a(T var1) {
         this.field_187212_b = var1;
      }

      public T func_187206_b() {
         return this.field_187212_b;
      }

      public boolean func_187209_c() {
         return this.field_187213_c;
      }

      public void func_187208_a(boolean var1) {
         this.field_187213_c = var1;
      }

      public EntityDataManager.DataEntry<T> func_192735_d() {
         return new EntityDataManager.DataEntry(this.field_187211_a, this.field_187211_a.func_187156_b().func_192717_a(this.field_187212_b));
      }
   }
}
