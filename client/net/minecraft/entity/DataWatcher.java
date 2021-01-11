package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
   private final Entity field_151511_a;
   private boolean field_92086_a = true;
   private static final Map<Class<?>, Integer> field_75697_a = Maps.newHashMap();
   private final Map<Integer, DataWatcher.WatchableObject> field_75695_b = Maps.newHashMap();
   private boolean field_75696_c;
   private ReadWriteLock field_75694_d = new ReentrantReadWriteLock();

   public DataWatcher(Entity var1) {
      super();
      this.field_151511_a = var1;
   }

   public <T> void func_75682_a(int var1, T var2) {
      Integer var3 = (Integer)field_75697_a.get(var2.getClass());
      if (var3 == null) {
         throw new IllegalArgumentException("Unknown data type: " + var2.getClass());
      } else if (var1 > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + var1 + "! (Max is " + 31 + ")");
      } else if (this.field_75695_b.containsKey(var1)) {
         throw new IllegalArgumentException("Duplicate id value for " + var1 + "!");
      } else {
         DataWatcher.WatchableObject var4 = new DataWatcher.WatchableObject(var3, var1, var2);
         this.field_75694_d.writeLock().lock();
         this.field_75695_b.put(var1, var4);
         this.field_75694_d.writeLock().unlock();
         this.field_92086_a = false;
      }
   }

   public void func_82709_a(int var1, int var2) {
      DataWatcher.WatchableObject var3 = new DataWatcher.WatchableObject(var2, var1, (Object)null);
      this.field_75694_d.writeLock().lock();
      this.field_75695_b.put(var1, var3);
      this.field_75694_d.writeLock().unlock();
      this.field_92086_a = false;
   }

   public byte func_75683_a(int var1) {
      return (Byte)this.func_75691_i(var1).func_75669_b();
   }

   public short func_75693_b(int var1) {
      return (Short)this.func_75691_i(var1).func_75669_b();
   }

   public int func_75679_c(int var1) {
      return (Integer)this.func_75691_i(var1).func_75669_b();
   }

   public float func_111145_d(int var1) {
      return (Float)this.func_75691_i(var1).func_75669_b();
   }

   public String func_75681_e(int var1) {
      return (String)this.func_75691_i(var1).func_75669_b();
   }

   public ItemStack func_82710_f(int var1) {
      return (ItemStack)this.func_75691_i(var1).func_75669_b();
   }

   private DataWatcher.WatchableObject func_75691_i(int var1) {
      this.field_75694_d.readLock().lock();

      DataWatcher.WatchableObject var2;
      try {
         var2 = (DataWatcher.WatchableObject)this.field_75695_b.get(var1);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.func_85055_a(var6, "Getting synched entity data");
         CrashReportCategory var5 = var4.func_85058_a("Synched entity data");
         var5.func_71507_a("Data ID", var1);
         throw new ReportedException(var4);
      }

      this.field_75694_d.readLock().unlock();
      return var2;
   }

   public Rotations func_180115_h(int var1) {
      return (Rotations)this.func_75691_i(var1).func_75669_b();
   }

   public <T> void func_75692_b(int var1, T var2) {
      DataWatcher.WatchableObject var3 = this.func_75691_i(var1);
      if (ObjectUtils.notEqual(var2, var3.func_75669_b())) {
         var3.func_75673_a(var2);
         this.field_151511_a.func_145781_i(var1);
         var3.func_75671_a(true);
         this.field_75696_c = true;
      }

   }

   public void func_82708_h(int var1) {
      this.func_75691_i(var1).field_75675_d = true;
      this.field_75696_c = true;
   }

   public boolean func_75684_a() {
      return this.field_75696_c;
   }

   public static void func_151507_a(List<DataWatcher.WatchableObject> var0, PacketBuffer var1) throws IOException {
      if (var0 != null) {
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            DataWatcher.WatchableObject var3 = (DataWatcher.WatchableObject)var2.next();
            func_151510_a(var1, var3);
         }
      }

      var1.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> func_75688_b() {
      ArrayList var1 = null;
      if (this.field_75696_c) {
         this.field_75694_d.readLock().lock();
         Iterator var2 = this.field_75695_b.values().iterator();

         while(var2.hasNext()) {
            DataWatcher.WatchableObject var3 = (DataWatcher.WatchableObject)var2.next();
            if (var3.func_75670_d()) {
               var3.func_75671_a(false);
               if (var1 == null) {
                  var1 = Lists.newArrayList();
               }

               var1.add(var3);
            }
         }

         this.field_75694_d.readLock().unlock();
      }

      this.field_75696_c = false;
      return var1;
   }

   public void func_151509_a(PacketBuffer var1) throws IOException {
      this.field_75694_d.readLock().lock();
      Iterator var2 = this.field_75695_b.values().iterator();

      while(var2.hasNext()) {
         DataWatcher.WatchableObject var3 = (DataWatcher.WatchableObject)var2.next();
         func_151510_a(var1, var3);
      }

      this.field_75694_d.readLock().unlock();
      var1.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> func_75685_c() {
      ArrayList var1 = null;
      this.field_75694_d.readLock().lock();

      DataWatcher.WatchableObject var3;
      for(Iterator var2 = this.field_75695_b.values().iterator(); var2.hasNext(); var1.add(var3)) {
         var3 = (DataWatcher.WatchableObject)var2.next();
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }
      }

      this.field_75694_d.readLock().unlock();
      return var1;
   }

   private static void func_151510_a(PacketBuffer var0, DataWatcher.WatchableObject var1) throws IOException {
      int var2 = (var1.func_75674_c() << 5 | var1.func_75672_a() & 31) & 255;
      var0.writeByte(var2);
      switch(var1.func_75674_c()) {
      case 0:
         var0.writeByte((Byte)var1.func_75669_b());
         break;
      case 1:
         var0.writeShort((Short)var1.func_75669_b());
         break;
      case 2:
         var0.writeInt((Integer)var1.func_75669_b());
         break;
      case 3:
         var0.writeFloat((Float)var1.func_75669_b());
         break;
      case 4:
         var0.func_180714_a((String)var1.func_75669_b());
         break;
      case 5:
         ItemStack var3 = (ItemStack)var1.func_75669_b();
         var0.func_150788_a(var3);
         break;
      case 6:
         BlockPos var4 = (BlockPos)var1.func_75669_b();
         var0.writeInt(var4.func_177958_n());
         var0.writeInt(var4.func_177956_o());
         var0.writeInt(var4.func_177952_p());
         break;
      case 7:
         Rotations var5 = (Rotations)var1.func_75669_b();
         var0.writeFloat(var5.func_179415_b());
         var0.writeFloat(var5.func_179416_c());
         var0.writeFloat(var5.func_179413_d());
      }

   }

   public static List<DataWatcher.WatchableObject> func_151508_b(PacketBuffer var0) throws IOException {
      ArrayList var1 = null;

      for(byte var2 = var0.readByte(); var2 != 127; var2 = var0.readByte()) {
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }

         int var3 = (var2 & 224) >> 5;
         int var4 = var2 & 31;
         DataWatcher.WatchableObject var5 = null;
         switch(var3) {
         case 0:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.readByte());
            break;
         case 1:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.readShort());
            break;
         case 2:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.readInt());
            break;
         case 3:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.readFloat());
            break;
         case 4:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.func_150789_c(32767));
            break;
         case 5:
            var5 = new DataWatcher.WatchableObject(var3, var4, var0.func_150791_c());
            break;
         case 6:
            int var6 = var0.readInt();
            int var7 = var0.readInt();
            int var8 = var0.readInt();
            var5 = new DataWatcher.WatchableObject(var3, var4, new BlockPos(var6, var7, var8));
            break;
         case 7:
            float var9 = var0.readFloat();
            float var10 = var0.readFloat();
            float var11 = var0.readFloat();
            var5 = new DataWatcher.WatchableObject(var3, var4, new Rotations(var9, var10, var11));
         }

         var1.add(var5);
      }

      return var1;
   }

   public void func_75687_a(List<DataWatcher.WatchableObject> var1) {
      this.field_75694_d.writeLock().lock();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         DataWatcher.WatchableObject var3 = (DataWatcher.WatchableObject)var2.next();
         DataWatcher.WatchableObject var4 = (DataWatcher.WatchableObject)this.field_75695_b.get(var3.func_75672_a());
         if (var4 != null) {
            var4.func_75673_a(var3.func_75669_b());
            this.field_151511_a.func_145781_i(var3.func_75672_a());
         }
      }

      this.field_75694_d.writeLock().unlock();
      this.field_75696_c = true;
   }

   public boolean func_92085_d() {
      return this.field_92086_a;
   }

   public void func_111144_e() {
      this.field_75696_c = false;
   }

   static {
      field_75697_a.put(Byte.class, 0);
      field_75697_a.put(Short.class, 1);
      field_75697_a.put(Integer.class, 2);
      field_75697_a.put(Float.class, 3);
      field_75697_a.put(String.class, 4);
      field_75697_a.put(ItemStack.class, 5);
      field_75697_a.put(BlockPos.class, 6);
      field_75697_a.put(Rotations.class, 7);
   }

   public static class WatchableObject {
      private final int field_75678_a;
      private final int field_75676_b;
      private Object field_75677_c;
      private boolean field_75675_d;

      public WatchableObject(int var1, int var2, Object var3) {
         super();
         this.field_75676_b = var2;
         this.field_75677_c = var3;
         this.field_75678_a = var1;
         this.field_75675_d = true;
      }

      public int func_75672_a() {
         return this.field_75676_b;
      }

      public void func_75673_a(Object var1) {
         this.field_75677_c = var1;
      }

      public Object func_75669_b() {
         return this.field_75677_c;
      }

      public int func_75674_c() {
         return this.field_75678_a;
      }

      public boolean func_75670_d() {
         return this.field_75675_d;
      }

      public void func_75671_a(boolean var1) {
         this.field_75675_d = var1;
      }
   }
}
