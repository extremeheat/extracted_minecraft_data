package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.jspecify.annotations.Nullable;

public class LastSeenMessagesValidator {
   private final int lastSeenCount;
   private final ObjectList<LastSeenTrackedEntry> trackedMessages = new ObjectArrayList();
   private @Nullable MessageSignature lastPendingMessage;

   public LastSeenMessagesValidator(int var1) {
      super();
      this.lastSeenCount = var1;

      for(int var2 = 0; var2 < var1; ++var2) {
         this.trackedMessages.add((Object)null);
      }

   }

   public void addPending(MessageSignature var1) {
      if (!var1.equals(this.lastPendingMessage)) {
         this.trackedMessages.add(new LastSeenTrackedEntry(var1, true));
         this.lastPendingMessage = var1;
      }

   }

   public int trackedMessagesCount() {
      return this.trackedMessages.size();
   }

   public void applyOffset(int var1) throws ValidationException {
      int var2 = this.trackedMessages.size() - this.lastSeenCount;
      if (var1 >= 0 && var1 <= var2) {
         this.trackedMessages.removeElements(0, var1);
      } else {
         throw new ValidationException("Advanced last seen window by " + var1 + " messages, but expected at most " + var2);
      }
   }

   public LastSeenMessages applyUpdate(LastSeenMessages.Update var1) throws ValidationException {
      this.applyOffset(var1.offset());
      ObjectArrayList var2 = new ObjectArrayList(var1.acknowledged().cardinality());
      if (var1.acknowledged().length() > this.lastSeenCount) {
         int var10002 = var1.acknowledged().length();
         throw new ValidationException("Last seen update contained " + var10002 + " messages, but maximum window size is " + this.lastSeenCount);
      } else {
         for(int var3 = 0; var3 < this.lastSeenCount; ++var3) {
            boolean var4 = var1.acknowledged().get(var3);
            LastSeenTrackedEntry var5 = (LastSeenTrackedEntry)this.trackedMessages.get(var3);
            if (var4) {
               if (var5 == null) {
                  throw new ValidationException("Last seen update acknowledged unknown or previously ignored message at index " + var3);
               }

               this.trackedMessages.set(var3, var5.acknowledge());
               var2.add(var5.signature());
            } else {
               if (var5 != null && !var5.pending()) {
                  throw new ValidationException("Last seen update ignored previously acknowledged message at index " + var3 + " and signature " + String.valueOf(var5.signature()));
               }

               this.trackedMessages.set(var3, (Object)null);
            }
         }

         LastSeenMessages var6 = new LastSeenMessages(var2);
         if (!var1.verifyChecksum(var6)) {
            throw new ValidationException("Checksum mismatch on last seen update: the client and server must have desynced");
         } else {
            return var6;
         }
      }
   }

   public static class ValidationException extends Exception {
      public ValidationException(String var1) {
         super(var1);
      }
   }
}
