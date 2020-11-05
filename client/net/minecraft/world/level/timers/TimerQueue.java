package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerQueue<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbacks<T> callbacksRegistry;
   private final Queue<TimerQueue.Event<T>> queue;
   private UnsignedLong sequentialId;
   private final Table<String, Long, TimerQueue.Event<T>> events;

   private static <T> Comparator<TimerQueue.Event<T>> createComparator() {
      return Comparator.comparingLong((var0) -> {
         return var0.triggerTime;
      }).thenComparing((var0) -> {
         return var0.sequentialId;
      });
   }

   public TimerQueue(TimerCallbacks<T> var1, Stream<Dynamic<Tag>> var2) {
      this(var1);
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      var2.forEach((var1x) -> {
         if (!(var1x.getValue() instanceof CompoundTag)) {
            LOGGER.warn("Invalid format of events: {}", var1x);
         } else {
            this.loadEvent((CompoundTag)var1x.getValue());
         }
      });
   }

   public TimerQueue(TimerCallbacks<T> var1) {
      super();
      this.queue = new PriorityQueue(createComparator());
      this.sequentialId = UnsignedLong.ZERO;
      this.events = HashBasedTable.create();
      this.callbacksRegistry = var1;
   }

   public void tick(T var1, long var2) {
      while(true) {
         TimerQueue.Event var4 = (TimerQueue.Event)this.queue.peek();
         if (var4 == null || var4.triggerTime > var2) {
            return;
         }

         this.queue.remove();
         this.events.remove(var4.id, var2);
         var4.callback.handle(var1, this, var2);
      }
   }

   public void schedule(String var1, long var2, TimerCallback<T> var4) {
      if (!this.events.contains(var1, var2)) {
         this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
         TimerQueue.Event var5 = new TimerQueue.Event(var2, this.sequentialId, var1, var4);
         this.events.put(var1, var2, var5);
         this.queue.add(var5);
      }
   }

   public int remove(String var1) {
      Collection var2 = this.events.row(var1).values();
      Queue var10001 = this.queue;
      var2.forEach(var10001::remove);
      int var3 = var2.size();
      var2.clear();
      return var3;
   }

   public Set<String> getEventsIds() {
      return Collections.unmodifiableSet(this.events.rowKeySet());
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
