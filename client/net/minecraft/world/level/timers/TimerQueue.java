package net.minecraft.world.level.timers;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerQueue<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbacks<T> callbacksRegistry;
   private final Queue<TimerQueue.Event<T>> queue = new PriorityQueue(createComparator());
   private UnsignedLong sequentialId;
   private final Map<String, TimerQueue.Event<T>> events;

   private static <T> Comparator<TimerQueue.Event<T>> createComparator() {
      return (var0, var1) -> {
         int var2 = Long.compare(var0.triggerTime, var1.triggerTime);
         return var2 != 0 ? var2 : var0.sequentialId.compareTo(var1.sequentialId);
      };
   }

   public TimerQueue(TimerCallbacks<T> var1) {
      super();
      this.sequentialId = UnsignedLong.ZERO;
      this.events = Maps.newHashMap();
      this.callbacksRegistry = var1;
   }

   public void tick(T var1, long var2) {
      while(true) {
         TimerQueue.Event var4 = (TimerQueue.Event)this.queue.peek();
         if (var4 == null || var4.triggerTime > var2) {
            return;
         }

         this.queue.remove();
         this.events.remove(var4.id);
         var4.callback.handle(var1, this, var2);
      }
   }

   private void addEvent(String var1, long var2, TimerCallback<T> var4) {
      this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
      TimerQueue.Event var5 = new TimerQueue.Event(var2, this.sequentialId, var1, var4);
      this.events.put(var1, var5);
      this.queue.add(var5);
   }

   public boolean schedule(String var1, long var2, TimerCallback<T> var4) {
      if (this.events.containsKey(var1)) {
         return false;
      } else {
         this.addEvent(var1, var2, var4);
         return true;
      }
   }

   public void reschedule(String var1, long var2, TimerCallback<T> var4) {
      TimerQueue.Event var5 = (TimerQueue.Event)this.events.remove(var1);
      if (var5 != null) {
         this.queue.remove(var5);
      }

      this.addEvent(var1, var2, var4);
   }

   private void loadEvent(CompoundTag var1) {
      CompoundTag var2 = var1.getCompound("Callback");
      TimerCallback var3 = this.callbacksRegistry.deserialize(var2);
      if (var3 != null) {
         String var4 = var1.getString("Name");
         long var5 = var1.getLong("TriggerTime");
         this.schedule(var4, var5, var3);
      }

   }

   public void load(ListTag var1) {
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      if (!var1.isEmpty()) {
         if (var1.getElementType() != 10) {
            LOGGER.warn("Invalid format of events: " + var1);
         } else {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Tag var3 = (Tag)var2.next();
               this.loadEvent((CompoundTag)var3);
            }

         }
      }
   }

   private CompoundTag storeEvent(TimerQueue.Event<T> var1) {
      CompoundTag var2 = new CompoundTag();
      var2.putString("Name", var1.id);
      var2.putLong("TriggerTime", var1.triggerTime);
      var2.put("Callback", this.callbacksRegistry.serialize(var1.callback));
      return var2;
   }

   public ListTag store() {
      ListTag var1 = new ListTag();
      this.queue.stream().sorted(createComparator()).map(this::storeEvent).forEach(var1::add);
      return var1;
   }

   public static class Event<T> {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final TimerCallback<T> callback;

      private Event(long var1, UnsignedLong var3, String var4, TimerCallback<T> var5) {
         super();
         this.triggerTime = var1;
         this.sequentialId = var3;
         this.id = var4;
         this.callback = var5;
      }

      // $FF: synthetic method
      Event(long var1, UnsignedLong var3, String var4, TimerCallback var5, Object var6) {
         this(var1, var3, var4, var5);
      }
   }
}
