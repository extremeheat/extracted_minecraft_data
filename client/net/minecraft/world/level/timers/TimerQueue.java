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
      return Comparator.comparingLong((l) -> l.triggerTime).thenComparing((l) -> l.sequentialId);
   }

   public TimerQueue(final TimerCallbacks<T> callbacksRegistry, final Stream<? extends Dynamic<?>> eventData) {
      this(callbacksRegistry);
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      eventData.forEach((input) -> {
         Tag tag = (Tag)input.convert(NbtOps.INSTANCE).getValue();
         if (tag instanceof CompoundTag compoundTag) {
            this.loadEvent(compoundTag);
         } else {
            LOGGER.warn("Invalid format of events: {}", tag);
         }

      });
   }

   public TimerQueue(final TimerCallbacks<T> callbacksRegistry) {
      super();
      this.queue = new PriorityQueue(createComparator());
      this.sequentialId = UnsignedLong.ZERO;
      this.events = HashBasedTable.create();
      this.callbacksRegistry = callbacksRegistry;
   }

   public void tick(final T context, final long currentTick) {
      while(true) {
         Event<T> event = (Event)this.queue.peek();
         if (event == null || event.triggerTime > currentTick) {
            return;
         }

         this.queue.remove();
         this.events.remove(event.id, currentTick);
         event.callback.handle(context, this, currentTick);
      }
   }

   public void schedule(final String id, final long time, final TimerCallback<T> callback) {
      if (!this.events.contains(id, time)) {
         this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
         Event<T> newEvent = new Event<T>(time, this.sequentialId, id, callback);
         this.events.put(id, time, newEvent);
         this.queue.add(newEvent);
      }
   }

   public int remove(final String id) {
      Collection<Event<T>> eventsToRemove = this.events.row(id).values();
      Queue var10001 = this.queue;
      Objects.requireNonNull(var10001);
      eventsToRemove.forEach(var10001::remove);
      int size = eventsToRemove.size();
      eventsToRemove.clear();
      return size;
   }

   public Set<String> getEventsIds() {
      return Collections.unmodifiableSet(this.events.rowKeySet());
   }

   private void loadEvent(final CompoundTag tag) {
      TimerCallback<T> callback = (TimerCallback)tag.read("Callback", this.callbacksRegistry.codec()).orElse((Object)null);
      if (callback != null) {
         String id = tag.getStringOr("Name", "");
         long time = tag.getLongOr("TriggerTime", 0L);
         this.schedule(id, time, callback);
      }

   }

   private CompoundTag storeEvent(final Event<T> event) {
      CompoundTag result = new CompoundTag();
      result.putString("Name", event.id);
      result.putLong("TriggerTime", event.triggerTime);
      result.store("Callback", this.callbacksRegistry.codec(), event.callback);
      return result;
   }

   public ListTag store() {
      ListTag result = new ListTag();
      Stream var10000 = this.queue.stream().sorted(createComparator()).map(this::storeEvent);
      Objects.requireNonNull(result);
      var10000.forEach(result::add);
      return result;
   }

   public static class Event<T> {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final TimerCallback<T> callback;

      private Event(final long triggerTime, final UnsignedLong sequentialId, final String id, final TimerCallback<T> callback) {
         super();
         this.triggerTime = triggerTime;
         this.sequentialId = sequentialId;
         this.id = id;
         this.callback = callback;
      }
   }
}
