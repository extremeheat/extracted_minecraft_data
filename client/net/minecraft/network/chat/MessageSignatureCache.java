package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureCache {
   public static final int NOT_FOUND = -1;
   private static final int DEFAULT_CAPACITY = 128;
   private final MessageSignature[] entries;

   public MessageSignatureCache(int var1) {
      super();
      this.entries = new MessageSignature[var1];
   }

   public static MessageSignatureCache createDefault() {
      return new MessageSignatureCache(128);
   }

   public int pack(MessageSignature var1) {
      for(int var2 = 0; var2 < this.entries.length; ++var2) {
         if (var1.equals(this.entries[var2])) {
            return var2;
         }
      }

      return -1;
   }

   @Nullable
   public MessageSignature unpack(int var1) {
      return this.entries[var1];
   }

   public void push(PlayerChatMessage var1) {
      List var2 = var1.signedBody().lastSeen().entries();
      ArrayDeque var3 = new ArrayDeque(var2.size() + 1);
      var3.addAll(var2);
      MessageSignature var4 = var1.signature();
      if (var4 != null) {
         var3.add(var4);
      }

      this.push(var3);
   }

   @VisibleForTesting
   void push(List<MessageSignature> var1) {
      this.push(new ArrayDeque<>(var1));
   }

   private void push(ArrayDeque<MessageSignature> var1) {
      ObjectOpenHashSet var2 = new ObjectOpenHashSet(var1);

      for(int var3 = 0; !var1.isEmpty() && var3 < this.entries.length; ++var3) {
         MessageSignature var4 = this.entries[var3];
         this.entries[var3] = (MessageSignature)var1.removeLast();
         if (var4 != null && !var2.contains(var4)) {
            var1.addFirst(var4);
         }
      }
   }
}
