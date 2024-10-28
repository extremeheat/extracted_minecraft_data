package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

public class TimerQueue<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String CALLBACK_DATA_TAG = "Callback";
   private static final String TIMER_NAME_TAG = "Name";
   private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
   private final TimerCallbacks<T> callbacksRegistry;
   private final Queue<Event<T>> queue;
   private UnsignedLong sequentialId;
   private final Table<String, Long, Event<T>> events;

   private static <T> Comparator<Event<T>> createComparator() {
      return Comparator.comparingLong((var0) -> {
         return var0.triggerTime;
      }).thenComparing((var0) -> {
         return var0.sequentialId;
      });
   }

   public TimerQueue(TimerCallbacks<T> var1, Stream<? extends Dynamic<?>> var2) {
      this(var1);
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      var2.forEach((var1x) -> {
         Tag var2 = (Tag)var1x.convert(NbtOps.INSTANCE).getValue();
         if (var2 instanceof CompoundTag var3) {
            this.loadEvent(var3);
         } else {
            LOGGER.warn("Invalid format of events: {}", var2);
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
         Event var4 = (Event)this.queue.peek();
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
         Event var5 = new Event(var2, this.sequentialId, var1, var4);
         this.events.put(var1, var2, var5);
         this.queue.add(var5);
      }
   }

   public int remove(String var1) {
      Collection var2 = this.events.row(var1).values();
      Queue var10001 = this.queue;
      Objects.requireNonNull(var10001);
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

   private CompoundTag storeEvent(Event<T> var1) {
      CompoundTag var2 = new CompoundTag();
      var2.putString("Name", var1.id);
      var2.putLong("TriggerTime", var1.triggerTime);
      var2.put("Callback", this.callbacksRegistry.serialize(var1.callback));
      return var2;
   }

   public ListTag store() {
      ListTag var1 = new ListTag();
      Stream var10000 = this.queue.stream().sorted(createComparator()).map(this::storeEvent);
      Objects.requireNonNull(var1);
      var10000.forEach(var1::add);
      return var1;
   }

   public static class Event<T> {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final TimerCallback<T> callback;

      Event(long var1, UnsignedLong var3, String var4, TimerCallback<T> var5) {
         super();
         this.triggerTime = var1;
         this.sequentialId = var3;
         this.id = var4;
         this.callback = var5;
      }
   }
}
