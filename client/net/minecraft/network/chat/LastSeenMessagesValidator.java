package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class LastSeenMessagesValidator {
   private static final int NOT_FOUND = -2147483648;
   private LastSeenMessages lastSeenMessages = LastSeenMessages.EMPTY;
   private final ObjectList<LastSeenMessages.Entry> pendingEntries = new ObjectArrayList();

   public LastSeenMessagesValidator() {
      super();
   }

   public void addPending(LastSeenMessages.Entry var1) {
      this.pendingEntries.add(var1);
   }

   public int pendingMessagesCount() {
      return this.pendingEntries.size();
   }

   private boolean hasDuplicateProfiles(LastSeenMessages var1) {
      HashSet var2 = new HashSet(var1.entries().size());

      for(LastSeenMessages.Entry var4 : var1.entries()) {
         if (!var2.add(var4.profileId())) {
            return true;
         }
      }

      return false;
   }

   private int calculateIndices(List<LastSeenMessages.Entry> var1, int[] var2, @Nullable LastSeenMessages.Entry var3) {
      Arrays.fill(var2, -2147483648);
      List var4 = this.lastSeenMessages.entries();
      int var5 = var4.size();

      for(int var6 = var5 - 1; var6 >= 0; --var6) {
         int var7 = var1.indexOf(var4.get(var6));
         if (var7 != -1) {
            var2[var7] = -var6 - 1;
         }
      }

      int var11 = -2147483648;
      int var12 = this.pendingEntries.size();

      for(int var8 = 0; var8 < var12; ++var8) {
         LastSeenMessages.Entry var9 = (LastSeenMessages.Entry)this.pendingEntries.get(var8);
         int var10 = var1.indexOf(var9);
         if (var10 != -1) {
            var2[var10] = var8;
         }

         if (var9.equals(var3)) {
            var11 = var8;
         }
      }

      return var11;
   }

   public Set<LastSeenMessagesValidator.ErrorCondition> validateAndUpdate(LastSeenMessages.Update var1) {
      EnumSet var2 = EnumSet.noneOf(LastSeenMessagesValidator.ErrorCondition.class);
      LastSeenMessages var3 = var1.lastSeen();
      LastSeenMessages.Entry var4 = var1.lastReceived().orElse(null);
      List var5 = var3.entries();
      int var6 = this.lastSeenMessages.entries().size();
      int var7 = -2147483648;
      int var8 = var5.size();
      if (var8 < var6) {
         var2.add(LastSeenMessagesValidator.ErrorCondition.REMOVED_MESSAGES);
      }

      int[] var9 = new int[var8];
      int var10 = this.calculateIndices(var5, var9, var4);

      for(int var11 = var8 - 1; var11 >= 0; --var11) {
         int var12 = var9[var11];
         if (var12 != -2147483648) {
            if (var12 < var7) {
               var2.add(LastSeenMessagesValidator.ErrorCondition.OUT_OF_ORDER);
            } else {
               var7 = var12;
            }
         } else {
            var2.add(LastSeenMessagesValidator.ErrorCondition.UNKNOWN_MESSAGES);
         }
      }

      if (var4 != null) {
         if (var10 != -2147483648 && var10 >= var7) {
            var7 = var10;
         } else {
            var2.add(LastSeenMessagesValidator.ErrorCondition.UNKNOWN_MESSAGES);
         }
      }

      if (var7 >= 0) {
         this.pendingEntries.removeElements(0, var7 + 1);
      }

      if (this.hasDuplicateProfiles(var3)) {
         var2.add(LastSeenMessagesValidator.ErrorCondition.DUPLICATED_PROFILES);
      }

      this.lastSeenMessages = var3;
      return var2;
   }

   public static enum ErrorCondition {
      OUT_OF_ORDER("messages received out of order"),
      DUPLICATED_PROFILES("multiple entries for single profile"),
      UNKNOWN_MESSAGES("unknown message"),
      REMOVED_MESSAGES("previously present messages removed from context");

      private final String message;

      private ErrorCondition(String var3) {
         this.message = var3;
      }

      public String message() {
         return this.message;
      }
   }
}
