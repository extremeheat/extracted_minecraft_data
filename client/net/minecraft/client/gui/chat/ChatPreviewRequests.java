package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundChatPreviewPacket;
import net.minecraft.util.RandomSource;

public class ChatPreviewRequests {
   private static final long MIN_REQUEST_INTERVAL_MS = 100L;
   private static final long MAX_REQUEST_INTERVAL_MS = 1000L;
   private final Minecraft minecraft;
   private final ChatPreviewRequests.QueryIdGenerator queryIdGenerator = new ChatPreviewRequests.QueryIdGenerator();
   @Nullable
   private ChatPreviewRequests.PendingPreview pending;
   private long lastRequestTime;

   public ChatPreviewRequests(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public boolean trySendRequest(String var1, long var2) {
      ClientPacketListener var4 = this.minecraft.getConnection();
      if (var4 == null) {
         this.clear();
         return true;
      } else if (this.pending != null && this.pending.matches(var1)) {
         return true;
      } else if (!this.minecraft.isLocalServer() && !this.isRequestReady(var2)) {
         return false;
      } else {
         ChatPreviewRequests.PendingPreview var5 = new ChatPreviewRequests.PendingPreview(this.queryIdGenerator.next(), var1);
         this.pending = var5;
         this.lastRequestTime = var2;
         var4.send(new ServerboundChatPreviewPacket(var5.id(), var5.query()));
         return true;
      }
   }

   @Nullable
   public String handleResponse(int var1) {
      if (this.pending != null && this.pending.matches(var1)) {
         String var2 = this.pending.query;
         this.pending = null;
         return var2;
      } else {
         return null;
      }
   }

   private boolean isRequestReady(long var1) {
      long var3 = this.lastRequestTime + 100L;
      if (var1 < var3) {
         return false;
      } else {
         long var5 = this.lastRequestTime + 1000L;
         return this.pending == null || var1 >= var5;
      }
   }

   public void clear() {
      this.pending = null;
      this.lastRequestTime = 0L;
   }

   public boolean isPending() {
      return this.pending != null;
   }

   static record PendingPreview(int a, String b) {
      private final int id;
      final String query;

      PendingPreview(int var1, String var2) {
         super();
         this.id = var1;
         this.query = var2;
      }

      public boolean matches(int var1) {
         return this.id == var1;
      }

      public boolean matches(String var1) {
         return this.query.equals(var1);
      }
   }

   static class QueryIdGenerator {
      private static final int MAX_STEP = 100;
      private final RandomSource random = RandomSource.createNewThreadLocalInstance();
      private int lastId;

      QueryIdGenerator() {
         super();
      }

      public int next() {
         int var1 = this.lastId + this.random.nextInt(100);
         this.lastId = var1;
         return var1;
      }
   }
}
