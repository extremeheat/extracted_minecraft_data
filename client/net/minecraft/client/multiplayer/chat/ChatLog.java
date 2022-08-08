package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface ChatLog {
   int NO_MESSAGE = -1;

   void push(LoggedChatEvent var1);

   @Nullable
   LoggedChatEvent lookup(int var1);

   @Nullable
   default Entry<LoggedChatEvent> lookupEntry(int var1) {
      LoggedChatEvent var2 = this.lookup(var1);
      return var2 != null ? new Entry(var1, var2) : null;
   }

   default boolean contains(int var1) {
      return this.lookup(var1) != null;
   }

   int offset(int var1, int var2);

   default int before(int var1) {
      return this.offset(var1, -1);
   }

   default int after(int var1) {
      return this.offset(var1, 1);
   }

   int newest();

   int oldest();

   default Selection selectAll() {
      return this.selectAfter(this.oldest());
   }

   default Selection selectAllDescending() {
      return this.selectBefore(this.newest());
   }

   default Selection selectAfter(int var1) {
      return this.selectSequence(var1, this::after);
   }

   default Selection selectBefore(int var1) {
      return this.selectSequence(var1, this::before);
   }

   default Selection selectBetween(int var1, int var2) {
      return this.contains(var1) && this.contains(var2) ? this.selectSequence(var1, (var2x) -> {
         return var2x == var2 ? -1 : this.after(var2x);
      }) : this.selectNone();
   }

   default Selection selectSequence(final int var1, final IntUnaryOperator var2) {
      return !this.contains(var1) ? this.selectNone() : new Selection(this, new PrimitiveIterator.OfInt() {
         private int nextId = var1;

         public int nextInt() {
            int var1x = this.nextId;
            this.nextId = var2.applyAsInt(var1x);
            return var1x;
         }

         public boolean hasNext() {
            return this.nextId != -1;
         }
      });
   }

   private Selection selectNone() {
      return new Selection(this, IntList.of().iterator());
   }

   public static record Entry<T extends LoggedChatEvent>(int a, T b) {
      private final int id;
      private final T event;

      public Entry(int var1, T var2) {
         super();
         this.id = var1;
         this.event = var2;
      }

      @Nullable
      public <U extends LoggedChatEvent> Entry<U> tryCast(Class<U> var1) {
         return var1.isInstance(this.event) ? new Entry(this.id, (LoggedChatEvent)var1.cast(this.event)) : null;
      }

      public int id() {
         return this.id;
      }

      public T event() {
         return this.event;
      }
   }

   public static class Selection {
      private static final int CHARACTERISTICS = 1041;
      private final ChatLog log;
      private final PrimitiveIterator.OfInt ids;

      Selection(ChatLog var1, PrimitiveIterator.OfInt var2) {
         super();
         this.log = var1;
         this.ids = var2;
      }

      public IntStream ids() {
         return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(this.ids, 1041), false);
      }

      public Stream<LoggedChatEvent> events() {
         IntStream var10000 = this.ids();
         ChatLog var10001 = this.log;
         Objects.requireNonNull(var10001);
         return var10000.mapToObj(var10001::lookup).filter(Objects::nonNull);
      }

      public Collection<GameProfile> reportableGameProfiles() {
         return this.events().map((var0) -> {
            if (var0 instanceof LoggedChatMessage.Player var1) {
               if (var1.canReport(var1.profile().getId())) {
                  return var1.profile();
               }
            }

            return null;
         }).filter(Objects::nonNull).distinct().toList();
      }

      public Stream<Entry<LoggedChatEvent>> entries() {
         IntStream var10000 = this.ids();
         ChatLog var10001 = this.log;
         Objects.requireNonNull(var10001);
         return var10000.mapToObj(var10001::lookupEntry).filter(Objects::nonNull);
      }
   }
}
