package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.BitSet;
import java.util.Objects;
import javax.annotation.Nullable;

public class LastSeenMessagesTracker {
   private final LastSeenTrackedEntry[] trackedMessages;
   private int tail;
   private int offset;
   @Nullable
   private MessageSignature lastTrackedMessage;

   public LastSeenMessagesTracker(int var1) {
      super();
      this.trackedMessages = new LastSeenTrackedEntry[var1];
   }

   public boolean addPending(MessageSignature var1, boolean var2) {
      if (Objects.equals(var1, this.lastTrackedMessage)) {
         return false;
      } else {
         this.lastTrackedMessage = var1;
         this.addEntry(var2 ? new LastSeenTrackedEntry(var1, true) : null);
         return true;
      }
   }

   private void addEntry(@Nullable LastSeenTrackedEntry var1) {
      int var2 = this.tail;
      this.tail = (var2 + 1) % this.trackedMessages.length;
      ++this.offset;
      this.trackedMessages[var2] = var1;
   }

   public void ignorePending(MessageSignature var1) {
      for(int var2 = 0; var2 < this.trackedMessages.length; ++var2) {
         LastSeenTrackedEntry var3 = this.trackedMessages[var2];
         if (var3 != null && var3.pending() && var1.equals(var3.signature())) {
            this.trackedMessages[var2] = null;
            break;
         }
      }

   }

   public int getAndClearOffset() {
      int var1 = this.offset;
      this.offset = 0;
      return var1;
   }

   public Update generateAndApplyUpdate() {
      int var1 = this.getAndClearOffset();
      BitSet var2 = new BitSet(this.trackedMessages.length);
      ObjectArrayList var3 = new ObjectArrayList(this.trackedMessages.length);

      for(int var4 = 0; var4 < this.trackedMessages.length; ++var4) {
         int var5 = (this.tail + var4) % this.trackedMessages.length;
         LastSeenTrackedEntry var6 = this.trackedMessages[var5];
         if (var6 != null) {
            var2.set(var4, true);
            var3.add(var6.signature());
            this.trackedMessages[var5] = var6.acknowledge();
         }
      }

      LastSeenMessages var7 = new LastSeenMessages(var3);
      LastSeenMessages.Update var8 = new LastSeenMessages.Update(var1, var2);
      return new Update(var7, var8);
   }

   public int offset() {
      return this.offset;
   }

   public static record Update(LastSeenMessages lastSeen, LastSeenMessages.Update update) {
      public Update(LastSeenMessages var1, LastSeenMessages.Update var2) {
         super();
         this.lastSeen = var1;
         this.update = var2;
      }
   }
}
