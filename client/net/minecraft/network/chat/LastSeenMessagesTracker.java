package net.minecraft.network.chat;

import java.util.Arrays;

public class LastSeenMessagesTracker {
   private final LastSeenMessages.Entry[] status;
   private int size;
   private LastSeenMessages result = LastSeenMessages.EMPTY;

   public LastSeenMessagesTracker(int var1) {
      super();
      this.status = new LastSeenMessages.Entry[var1];
   }

   public void push(LastSeenMessages.Entry var1) {
      LastSeenMessages.Entry var2 = var1;

      for(int var3 = 0; var3 < this.size; ++var3) {
         LastSeenMessages.Entry var4 = this.status[var3];
         this.status[var3] = var2;
         var2 = var4;
         if (var4.profileId().equals(var1.profileId())) {
            var2 = null;
            break;
         }
      }

      if (var2 != null && this.size < this.status.length) {
         this.status[this.size++] = var2;
      }

      this.result = new LastSeenMessages(Arrays.asList(Arrays.copyOf(this.status, this.size)));
   }

   public LastSeenMessages get() {
      return this.result;
   }
}
