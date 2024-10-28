package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Optional;
import javax.annotation.Nullable;

public class LastSeenMessagesValidator {
   private final int lastSeenCount;
   private final ObjectList<LastSeenTrackedEntry> trackedMessages = new ObjectArrayList();
   @Nullable
   private MessageSignature lastPendingMessage;

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

   public boolean applyOffset(int var1) {
      int var2 = this.trackedMessages.size() - this.lastSeenCount;
      if (var1 >= 0 && var1 <= var2) {
         this.trackedMessages.removeElements(0, var1);
         return true;
      } else {
         return false;
      }
   }

   public Optional<LastSeenMessages> applyUpdate(LastSeenMessages.Update var1) {
      if (!this.applyOffset(var1.offset())) {
         return Optional.empty();
      } else {
         ObjectArrayList var2 = new ObjectArrayList(var1.acknowledged().cardinality());
         if (var1.acknowledged().length() > this.lastSeenCount) {
            return Optional.empty();
         } else {
            for(int var3 = 0; var3 < this.lastSeenCount; ++var3) {
               boolean var4 = var1.acknowledged().get(var3);
               LastSeenTrackedEntry var5 = (LastSeenTrackedEntry)this.trackedMessages.get(var3);
               if (var4) {
                  if (var5 == null) {
                     return Optional.empty();
                  }

                  this.trackedMessages.set(var3, var5.acknowledge());
                  var2.add(var5.signature());
               } else {
                  if (var5 != null && !var5.pending()) {
                     return Optional.empty();
                  }

                  this.trackedMessages.set(var3, (Object)null);
               }
            }

            return Optional.of(new LastSeenMessages(var2));
         }
      }
   }
}
