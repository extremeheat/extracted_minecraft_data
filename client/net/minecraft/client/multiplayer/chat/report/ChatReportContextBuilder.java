package net.minecraft.client.multiplayer.chat.report;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;

public class ChatReportContextBuilder {
   private final int leadingCount;
   private final List<Collector> activeCollectors = new ArrayList();

   public ChatReportContextBuilder(final int leadingCount) {
      super();
      this.leadingCount = leadingCount;
   }

   public void collectAllContext(final ChatLog chatLog, final IntCollection roots, final Handler handler) {
      IntSortedSet uncollectedRoots = new IntRBTreeSet(roots);

      for(int id = uncollectedRoots.lastInt(); id >= chatLog.start() && (this.isActive() || !uncollectedRoots.isEmpty()); --id) {
         LoggedChatEvent var7 = chatLog.lookup(id);
         if (var7 instanceof LoggedChatMessage.Player event) {
            boolean context = this.acceptContext(event.message());
            if (uncollectedRoots.remove(id)) {
               this.trackContext(event.message());
               handler.accept(id, event);
            } else if (context) {
               handler.accept(id, event);
            }
         }
      }

   }

   public void trackContext(final PlayerChatMessage message) {
      this.activeCollectors.add(new Collector(message));
   }

   public boolean acceptContext(final PlayerChatMessage message) {
      boolean collected = false;
      Iterator<Collector> iterator = this.activeCollectors.iterator();

      while(iterator.hasNext()) {
         Collector collector = (Collector)iterator.next();
         if (collector.accept(message)) {
            collected = true;
            if (collector.isComplete()) {
               iterator.remove();
            }
         }
      }

      return collected;
   }

   public boolean isActive() {
      return !this.activeCollectors.isEmpty();
   }

   private class Collector {
      private final Set<MessageSignature> lastSeenSignatures;
      private PlayerChatMessage lastChainMessage;
      private boolean collectingChain;
      private int count;

      private Collector(final PlayerChatMessage fromMessage) {
         Objects.requireNonNull(ChatReportContextBuilder.this);
         super();
         this.collectingChain = true;
         this.lastSeenSignatures = new ObjectOpenHashSet(fromMessage.signedBody().lastSeen().entries());
         this.lastChainMessage = fromMessage;
      }

      private boolean accept(final PlayerChatMessage message) {
         if (message.equals(this.lastChainMessage)) {
            return false;
         } else {
            boolean selected = this.lastSeenSignatures.remove(message.signature());
            if (this.collectingChain && this.lastChainMessage.sender().equals(message.sender())) {
               if (this.lastChainMessage.link().isDescendantOf(message.link())) {
                  selected = true;
                  this.lastChainMessage = message;
               } else {
                  this.collectingChain = false;
               }
            }

            if (selected) {
               ++this.count;
            }

            return selected;
         }
      }

      private boolean isComplete() {
         return this.count >= ChatReportContextBuilder.this.leadingCount || !this.collectingChain && this.lastSeenSignatures.isEmpty();
      }
   }

   public interface Handler {
      void accept(int id, LoggedChatMessage.Player event);
   }
}
