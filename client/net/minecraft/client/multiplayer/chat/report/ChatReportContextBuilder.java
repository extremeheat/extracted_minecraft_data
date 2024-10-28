package net.minecraft.client.multiplayer.chat.report;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;

public class ChatReportContextBuilder {
   final int leadingCount;
   private final List<Collector> activeCollectors = new ArrayList();

   public ChatReportContextBuilder(int var1) {
      super();
      this.leadingCount = var1;
   }

   public void collectAllContext(ChatLog var1, IntCollection var2, Handler var3) {
      IntRBTreeSet var4 = new IntRBTreeSet(var2);

      for(int var5 = var4.lastInt(); var5 >= var1.start() && (this.isActive() || !var4.isEmpty()); --var5) {
         LoggedChatEvent var7 = var1.lookup(var5);
         if (var7 instanceof LoggedChatMessage.Player var6) {
            boolean var8 = this.acceptContext(var6.message());
            if (var4.remove(var5)) {
               this.trackContext(var6.message());
               var3.accept(var5, var6);
            } else if (var8) {
               var3.accept(var5, var6);
            }
         }
      }

   }

   public void trackContext(PlayerChatMessage var1) {
      this.activeCollectors.add(new Collector(var1));
   }

   public boolean acceptContext(PlayerChatMessage var1) {
      boolean var2 = false;
      Iterator var3 = this.activeCollectors.iterator();

      while(var3.hasNext()) {
         Collector var4 = (Collector)var3.next();
         if (var4.accept(var1)) {
            var2 = true;
            if (var4.isComplete()) {
               var3.remove();
            }
         }
      }

      return var2;
   }

   public boolean isActive() {
      return !this.activeCollectors.isEmpty();
   }

   public interface Handler {
      void accept(int var1, LoggedChatMessage.Player var2);
   }

   private class Collector {
      private final Set<MessageSignature> lastSeenSignatures;
      private PlayerChatMessage lastChainMessage;
      private boolean collectingChain = true;
      private int count;

      Collector(PlayerChatMessage var2) {
         super();
         this.lastSeenSignatures = new ObjectOpenHashSet(var2.signedBody().lastSeen().entries());
         this.lastChainMessage = var2;
      }

      boolean accept(PlayerChatMessage var1) {
         if (var1.equals(this.lastChainMessage)) {
            return false;
         } else {
            boolean var2 = this.lastSeenSignatures.remove(var1.signature());
            if (this.collectingChain && this.lastChainMessage.sender().equals(var1.sender())) {
               if (this.lastChainMessage.link().isDescendantOf(var1.link())) {
                  var2 = true;
                  this.lastChainMessage = var1;
               } else {
                  this.collectingChain = false;
               }
            }

            if (var2) {
               ++this.count;
            }

            return var2;
         }
      }

      boolean isComplete() {
         return this.count >= ChatReportContextBuilder.this.leadingCount || !this.collectingChain && this.lastSeenSignatures.isEmpty();
      }
   }
}
