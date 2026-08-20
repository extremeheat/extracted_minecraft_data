package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.jspecify.annotations.Nullable;

public class LastSeenMessagesValidator {
   private final int lastSeenCount;
   private final ObjectList<@Nullable LastSeenTrackedEntry> trackedMessages = new ObjectArrayList();
   private @Nullable MessageSignature lastPendingMessage;

   public LastSeenMessagesValidator(final int lastSeenCount) {
      super();
      this.lastSeenCount = lastSeenCount;

      for(int i = 0; i < lastSeenCount; ++i) {
         this.trackedMessages.add((Object)null);
      }

   }

   public void addPending(final MessageSignature message) {
      if (!message.equals(this.lastPendingMessage)) {
         this.trackedMessages.add(new LastSeenTrackedEntry(message, true));
         this.lastPendingMessage = message;
      }

   }

   public int trackedMessagesCount() {
      return this.trackedMessages.size();
   }

   public void applyOffset(final int offset) throws ValidationException {
      int maxOffset = this.trackedMessages.size() - this.lastSeenCount;
      if (offset >= 0 && offset <= maxOffset) {
         this.trackedMessages.removeElements(0, offset);
      } else {
         throw new ValidationException("Advanced last seen window by " + offset + " messages, but expected at most " + maxOffset);
      }
   }

   public LastSeenMessages applyUpdate(final LastSeenMessages.Update update) throws ValidationException {
      this.applyOffset(update.offset());
      ObjectList<MessageSignature> lastSeenEntries = new ObjectArrayList(update.acknowledged().cardinality());
      if (update.acknowledged().length() > this.lastSeenCount) {
         int var10002 = update.acknowledged().length();
         throw new ValidationException("Last seen update contained " + var10002 + " messages, but maximum window size is " + this.lastSeenCount);
      } else {
         for(int i = 0; i < this.lastSeenCount; ++i) {
            boolean acknowledged = update.acknowledged().get(i);
            LastSeenTrackedEntry message = (LastSeenTrackedEntry)this.trackedMessages.get(i);
            if (acknowledged) {
               if (message == null) {
                  throw new ValidationException("Last seen update acknowledged unknown or previously ignored message at index " + i);
               }

               this.trackedMessages.set(i, message.acknowledge());
               lastSeenEntries.add(message.signature());
            } else {
               if (message != null && !message.pending()) {
                  throw new ValidationException("Last seen update ignored previously acknowledged message at index " + i + " and signature " + String.valueOf(message.signature()));
               }

               this.trackedMessages.set(i, (Object)null);
            }
         }

         LastSeenMessages lastSeen = new LastSeenMessages(lastSeenEntries);
         if (!update.verifyChecksum(lastSeen)) {
            throw new ValidationException("Checksum mismatch on last seen update: the client and server must have desynced");
         } else {
            return lastSeen;
         }
      }
   }

   public static class ValidationException extends Exception {
      public ValidationException(final String message) {
         super(message);
      }
   }
}
