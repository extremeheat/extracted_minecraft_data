package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.Objects;
import java.util.Spliterators;
import java.util.PrimitiveIterator.OfInt;
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
   default ChatLog.Entry<LoggedChatEvent> lookupEntry(int var1) {
      LoggedChatEvent var2 = this.lookup(var1);
      return var2 != null ? new ChatLog.Entry<>(var1, var2) : null;
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

   default ChatLog.Selection selectAll() {
      return this.selectAfter(this.oldest());
   }

   default ChatLog.Selection selectAllDescending() {
      return this.selectBefore(this.newest());
   }

   default ChatLog.Selection selectAfter(int var1) {
      return this.selectSequence(var1, this::after);
   }

   default ChatLog.Selection selectBefore(int var1) {
      return this.selectSequence(var1, this::before);
   }

   default ChatLog.Selection selectBetween(int var1, int var2) {
      return this.contains(var1) && this.contains(var2) ? this.selectSequence(var1, var2x -> var2x == var2 ? -1 : this.after(var2x)) : this.selectNone();
   }

   default ChatLog.Selection selectSequence(final int var1, final IntUnaryOperator var2) {
      return !this.contains(var1) ? this.selectNone() : new ChatLog.Selection(this, new OfInt() {
         private int nextId = var1;

         @Override
         public int nextInt() {
            int var1x = this.nextId;
            this.nextId = var2.applyAsInt(var1x);
            return var1x;
         }

         @Override
         public boolean hasNext() {
            return this.nextId != -1;
         }
      });
   }

   private ChatLog.Selection selectNone() {
      return new ChatLog.Selection(this, IntList.of().iterator());
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
      public <U extends LoggedChatEvent> ChatLog.Entry<U> tryCast(Class<U> var1) {
         return var1.isInstance(this.event) ? new ChatLog.Entry<>(this.id, (U)var1.cast(this.event)) : null;
      }
   }

   public static class Selection {
      private static final int CHARACTERISTICS = 1041;
      private final ChatLog log;
      private final OfInt ids;

      Selection(ChatLog var1, OfInt var2) {
         super();
         this.log = var1;
         this.ids = var2;
      }

      public IntStream ids() {
         return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(this.ids, 1041), false);
      }

      public Stream<LoggedChatEvent> events() {
         return this.ids().mapToObj(this.log::lookup).filter(Objects::nonNull);
      }

      public Collection<GameProfile> reportableGameProfiles() {
         return this.events().map(var0 -> {
            if (var0 instanceof LoggedChatMessage.Player var1 && var1.canReport(var1.profile().getId())) {
               return var1.profile();
            }

            return null;
         }).filter(Objects::nonNull).distinct().toList();
      }

      public Stream<ChatLog.Entry<LoggedChatEvent>> entries() {
         return this.ids().mapToObj(this.log::lookupEntry).filter(Objects::nonNull);
      }
   }
}
