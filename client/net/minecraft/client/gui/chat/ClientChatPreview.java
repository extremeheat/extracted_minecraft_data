package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class ClientChatPreview {
   private static final long PREVIEW_VALID_AFTER_MS = 200L;
   @Nullable
   private String lastQuery;
   @Nullable
   private String scheduledRequest;
   private final ChatPreviewRequests requests;
   @Nullable
   private Preview preview;

   public ClientChatPreview(Minecraft var1) {
      super();
      this.requests = new ChatPreviewRequests(var1);
   }

   public void tick() {
      String var1 = this.scheduledRequest;
      if (var1 != null && this.requests.trySendRequest(var1, Util.getMillis())) {
         this.scheduledRequest = null;
      }

   }

   public void update(String var1) {
      var1 = normalizeQuery(var1);
      if (!var1.isEmpty()) {
         if (!var1.equals(this.lastQuery)) {
            this.lastQuery = var1;
            this.sendOrScheduleRequest(var1);
         }
      } else {
         this.clear();
      }

   }

   private void sendOrScheduleRequest(String var1) {
      if (!this.requests.trySendRequest(var1, Util.getMillis())) {
         this.scheduledRequest = var1;
      } else {
         this.scheduledRequest = null;
      }

   }

   public void disable() {
      this.clear();
   }

   private void clear() {
      this.lastQuery = null;
      this.scheduledRequest = null;
      this.preview = null;
      this.requests.clear();
   }

   public void handleResponse(int var1, @Nullable Component var2) {
      String var3 = this.requests.handleResponse(var1);
      if (var3 != null) {
         this.preview = new Preview(Util.getMillis(), var3, var2);
      }

   }

   public boolean hasScheduledRequest() {
      return this.scheduledRequest != null || this.preview != null && !this.preview.isPreviewValid();
   }

   public boolean queryEquals(String var1) {
      return normalizeQuery(var1).equals(this.lastQuery);
   }

   @Nullable
   public Preview peek() {
      return this.preview;
   }

   @Nullable
   public Preview pull(String var1) {
      if (this.preview != null && this.preview.canPull(var1)) {
         Preview var2 = this.preview;
         this.preview = null;
         return var2;
      } else {
         return null;
      }
   }

   static String normalizeQuery(String var0) {
      return StringUtils.normalizeSpace(var0.trim());
   }

   public static record Preview(long a, String b, @Nullable Component c) {
      private final long receivedTimeStamp;
      private final String query;
      @Nullable
      private final Component response;

      public Preview(long var1, String var3, @Nullable Component var4) {
         super();
         var3 = ClientChatPreview.normalizeQuery(var3);
         this.receivedTimeStamp = var1;
         this.query = var3;
         this.response = var4;
      }

      private boolean queryEquals(String var1) {
         return this.query.equals(ClientChatPreview.normalizeQuery(var1));
      }

      boolean canPull(String var1) {
         return this.queryEquals(var1) ? this.isPreviewValid() : false;
      }

      boolean isPreviewValid() {
         long var1 = this.receivedTimeStamp + 200L;
         return Util.getMillis() >= var1;
      }

      public long receivedTimeStamp() {
         return this.receivedTimeStamp;
      }

      public String query() {
         return this.query;
      }

      @Nullable
      public Component response() {
         return this.response;
      }
   }
}
