package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class TimerQueue<T> extends SavedData {
   public static final Codec<TimerQueue<MinecraftServer>> CODEC;
   public static final SavedDataType<TimerQueue<MinecraftServer>> TYPE;
   private final Queue<Event<T>> queue;
   private UnsignedLong sequentialId;
   private final Table<String, Long, Event<T>> events;

   @VisibleForTesting
   protected static <T> Codec<TimerQueue<T>> codec(final TimerCallbacks<T> callbacks) {
      return TimerQueue.Packed.codec(callbacks.codec()).xmap(TimerQueue::new, TimerQueue::pack);
   }

   private static <T> Comparator<Event<T>> createComparator() {
      return Comparator.comparingLong((l) -> l.triggerTime).thenComparing((l) -> l.sequentialId);
   }

   public TimerQueue(final Packed<T> packedEvents) {
      this();
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      packedEvents.events.forEach((event) -> this.schedule(event.id, event.triggerTime, event.callback));
   }

   public TimerQueue() {
      super();
      this.queue = new PriorityQueue(createComparator());
      this.sequentialId = UnsignedLong.ZERO;
      this.events = HashBasedTable.create();
   }

   public void tick(final T context, final long currentTick) {
      while(true) {
         Event<T> event = (Event)this.queue.peek();
         if (event == null || event.triggerTime > currentTick) {
            return;
         }

         this.queue.remove();
         this.events.remove(event.id, currentTick);
         this.setDirty();
         event.callback.handle(context, this, currentTick);
      }
   }

   public void schedule(final String id, final long time, final TimerCallback<T> callback) {
      if (!this.events.contains(id, time)) {
         this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
         Event<T> newEvent = new Event<T>(time, this.sequentialId, id, callback);
         this.events.put(id, time, newEvent);
         this.queue.add(newEvent);
         this.setDirty();
      }
   }

   public int remove(final String id) {
      Collection<Event<T>> eventsToRemove = this.events.row(id).values();
      Queue var10001 = this.queue;
      Objects.requireNonNull(var10001);
      eventsToRemove.forEach(var10001::remove);
      int size = eventsToRemove.size();
      eventsToRemove.clear();
      this.setDirty();
      return size;
   }

   public Set<String> getEventsIds() {
      return Collections.unmodifiableSet(this.events.rowKeySet());
   }

   @VisibleForTesting
   protected Packed<T> pack() {
      return new Packed<T>(this.queue.stream().sorted(createComparator()).map((event) -> new Event.Packed(event.triggerTime, event.id, event.callback)).toList());
   }

   static {
      CODEC = codec(TimerCallbacks.SERVER_CALLBACKS);
      TYPE = new SavedDataType<TimerQueue<MinecraftServer>>(Identifier.withDefaultNamespace("scheduled_events"), TimerQueue::new, CODEC, DataFixTypes.SAVED_DATA_SCHEDULED_EVENTS);
   }

   public static record Event<T>(long triggerTime, UnsignedLong sequentialId, String id, TimerCallback<T> callback) {
      public Event {
         super();
      }

      public static record Packed<T>(long triggerTime, String id, TimerCallback<T> callback) {
         public Packed {
            super();
         }

         public static <T> Codec<Packed<T>> codec(final Codec<TimerCallback<T>> callbackCodec) {
            return RecordCodecBuilder.create((i) -> i.group(Codec.LONG.fieldOf("trigger_time").forGetter(Packed::triggerTime), Codec.STRING.fieldOf("id").forGetter(Packed::id), callbackCodec.fieldOf("callback").forGetter(Packed::callback)).apply(i, Packed::new));
         }
      }
   }

   public static record Packed<T>(List<Event.Packed<T>> events) {
      public Packed {
         super();
      }

      public static <T> Codec<Packed<T>> codec(final Codec<TimerCallback<T>> callbackCodec) {
         return RecordCodecBuilder.create((i) -> i.group(TimerQueue.Event.Packed.codec(callbackCodec).listOf().fieldOf("events").forGetter(Packed::events)).apply(i, Packed::new));
      }
   }
}
