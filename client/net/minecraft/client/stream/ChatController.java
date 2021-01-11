package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController {
   private static final Logger field_153018_p = LogManager.getLogger();
   protected ChatController.ChatListener field_153003_a = null;
   protected String field_153004_b = "";
   protected String field_153006_d = "";
   protected String field_153007_e = "";
   protected Core field_175992_e = null;
   protected Chat field_153008_f = null;
   protected ChatController.ChatState field_153011_i;
   protected AuthToken field_153012_j;
   protected HashMap<String, ChatController.ChatChannelListener> field_175998_i;
   protected int field_153015_m;
   protected ChatController.EnumEmoticonMode field_175997_k;
   protected ChatController.EnumEmoticonMode field_175995_l;
   protected ChatEmoticonData field_175996_m;
   protected int field_175993_n;
   protected int field_175994_o;
   protected IChatAPIListener field_175999_p;

   public void func_152990_a(ChatController.ChatListener var1) {
      this.field_153003_a = var1;
   }

   public void func_152994_a(AuthToken var1) {
      this.field_153012_j = var1;
   }

   public void func_152984_a(String var1) {
      this.field_153006_d = var1;
   }

   public void func_152998_c(String var1) {
      this.field_153004_b = var1;
   }

   public ChatController.ChatState func_153000_j() {
      return this.field_153011_i;
   }

   public boolean func_175990_d(String var1) {
      if (!this.field_175998_i.containsKey(var1)) {
         return false;
      } else {
         ChatController.ChatChannelListener var2 = (ChatController.ChatChannelListener)this.field_175998_i.get(var1);
         return var2.func_176040_a() == ChatController.EnumChannelState.Connected;
      }
   }

   public ChatController.EnumChannelState func_175989_e(String var1) {
      if (!this.field_175998_i.containsKey(var1)) {
         return ChatController.EnumChannelState.Disconnected;
      } else {
         ChatController.ChatChannelListener var2 = (ChatController.ChatChannelListener)this.field_175998_i.get(var1);
         return var2.func_176040_a();
      }
   }

   public ChatController() {
      super();
      this.field_153011_i = ChatController.ChatState.Uninitialized;
      this.field_153012_j = new AuthToken();
      this.field_175998_i = new HashMap();
      this.field_153015_m = 128;
      this.field_175997_k = ChatController.EnumEmoticonMode.None;
      this.field_175995_l = ChatController.EnumEmoticonMode.None;
      this.field_175996_m = null;
      this.field_175993_n = 500;
      this.field_175994_o = 2000;
      this.field_175999_p = new IChatAPIListener() {
         public void chatInitializationCallback(ErrorCode var1) {
            if (ErrorCode.succeeded(var1)) {
               ChatController.this.field_153008_f.setMessageFlushInterval(ChatController.this.field_175993_n);
               ChatController.this.field_153008_f.setUserChangeEventInterval(ChatController.this.field_175994_o);
               ChatController.this.func_153001_r();
               ChatController.this.func_175985_a(ChatController.ChatState.Initialized);
            } else {
               ChatController.this.func_175985_a(ChatController.ChatState.Uninitialized);
            }

            try {
               if (ChatController.this.field_153003_a != null) {
                  ChatController.this.field_153003_a.func_176023_d(var1);
               }
            } catch (Exception var3) {
               ChatController.this.func_152995_h(var3.toString());
            }

         }

         public void chatShutdownCallback(ErrorCode var1) {
            if (ErrorCode.succeeded(var1)) {
               ErrorCode var2 = ChatController.this.field_175992_e.shutdown();
               if (ErrorCode.failed(var2)) {
                  String var3 = ErrorCode.getString(var2);
                  ChatController.this.func_152995_h(String.format("Error shutting down the Twitch sdk: %s", var3));
               }

               ChatController.this.func_175985_a(ChatController.ChatState.Uninitialized);
            } else {
               ChatController.this.func_175985_a(ChatController.ChatState.Initialized);
               ChatController.this.func_152995_h(String.format("Error shutting down Twith chat: %s", var1));
            }

            try {
               if (ChatController.this.field_153003_a != null) {
                  ChatController.this.field_153003_a.func_176022_e(var1);
               }
            } catch (Exception var4) {
               ChatController.this.func_152995_h(var4.toString());
            }

         }

         public void chatEmoticonDataDownloadCallback(ErrorCode var1) {
            if (ErrorCode.succeeded(var1)) {
               ChatController.this.func_152988_s();
            }

         }
      };
      this.field_175992_e = Core.getInstance();
      if (this.field_175992_e == null) {
         this.field_175992_e = new Core(new StandardCoreAPI());
      }

      this.field_153008_f = new Chat(new StandardChatAPI());
   }

   public boolean func_175984_n() {
      if (this.field_153011_i != ChatController.ChatState.Uninitialized) {
         return false;
      } else {
         this.func_175985_a(ChatController.ChatState.Initializing);
         ErrorCode var1 = this.field_175992_e.initialize(this.field_153006_d, (String)null);
         if (ErrorCode.failed(var1)) {
            this.func_175985_a(ChatController.ChatState.Uninitialized);
            String var4 = ErrorCode.getString(var1);
            this.func_152995_h(String.format("Error initializing Twitch sdk: %s", var4));
            return false;
         } else {
            this.field_175995_l = this.field_175997_k;
            HashSet var2 = new HashSet();
            switch(this.field_175997_k) {
            case None:
               var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
               break;
            case Url:
               var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
               break;
            case TextureAtlas:
               var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
            }

            var1 = this.field_153008_f.initialize(var2, this.field_175999_p);
            if (ErrorCode.failed(var1)) {
               this.field_175992_e.shutdown();
               this.func_175985_a(ChatController.ChatState.Uninitialized);
               String var3 = ErrorCode.getString(var1);
               this.func_152995_h(String.format("Error initializing Twitch chat: %s", var3));
               return false;
            } else {
               this.func_175985_a(ChatController.ChatState.Initialized);
               return true;
            }
         }
      }
   }

   public boolean func_152986_d(String var1) {
      return this.func_175987_a(var1, false);
   }

   protected boolean func_175987_a(String var1, boolean var2) {
      if (this.field_153011_i != ChatController.ChatState.Initialized) {
         return false;
      } else if (this.field_175998_i.containsKey(var1)) {
         this.func_152995_h("Already in channel: " + var1);
         return false;
      } else if (var1 != null && !var1.equals("")) {
         ChatController.ChatChannelListener var3 = new ChatController.ChatChannelListener(var1);
         this.field_175998_i.put(var1, var3);
         boolean var4 = var3.func_176038_a(var2);
         if (!var4) {
            this.field_175998_i.remove(var1);
         }

         return var4;
      } else {
         return false;
      }
   }

   public boolean func_175991_l(String var1) {
      if (this.field_153011_i != ChatController.ChatState.Initialized) {
         return false;
      } else if (!this.field_175998_i.containsKey(var1)) {
         this.func_152995_h("Not in channel: " + var1);
         return false;
      } else {
         ChatController.ChatChannelListener var2 = (ChatController.ChatChannelListener)this.field_175998_i.get(var1);
         return var2.func_176034_g();
      }
   }

   public boolean func_152993_m() {
      if (this.field_153011_i != ChatController.ChatState.Initialized) {
         return false;
      } else {
         ErrorCode var1 = this.field_153008_f.shutdown();
         if (ErrorCode.failed(var1)) {
            String var2 = ErrorCode.getString(var1);
            this.func_152995_h(String.format("Error shutting down chat: %s", var2));
            return false;
         } else {
            this.func_152996_t();
            this.func_175985_a(ChatController.ChatState.ShuttingDown);
            return true;
         }
      }
   }

   public void func_175988_p() {
      if (this.func_153000_j() != ChatController.ChatState.Uninitialized) {
         this.func_152993_m();
         if (this.func_153000_j() == ChatController.ChatState.ShuttingDown) {
            while(this.func_153000_j() != ChatController.ChatState.Uninitialized) {
               try {
                  Thread.sleep(200L);
                  this.func_152997_n();
               } catch (InterruptedException var2) {
               }
            }
         }
      }

   }

   public void func_152997_n() {
      if (this.field_153011_i != ChatController.ChatState.Uninitialized) {
         ErrorCode var1 = this.field_153008_f.flushEvents();
         if (ErrorCode.failed(var1)) {
            String var2 = ErrorCode.getString(var1);
            this.func_152995_h(String.format("Error flushing chat events: %s", var2));
         }

      }
   }

   public boolean func_175986_a(String var1, String var2) {
      if (this.field_153011_i != ChatController.ChatState.Initialized) {
         return false;
      } else if (!this.field_175998_i.containsKey(var1)) {
         this.func_152995_h("Not in channel: " + var1);
         return false;
      } else {
         ChatController.ChatChannelListener var3 = (ChatController.ChatChannelListener)this.field_175998_i.get(var1);
         return var3.func_176037_b(var2);
      }
   }

   protected void func_175985_a(ChatController.ChatState var1) {
      if (var1 != this.field_153011_i) {
         this.field_153011_i = var1;

         try {
            if (this.field_153003_a != null) {
               this.field_153003_a.func_176017_a(var1);
            }
         } catch (Exception var3) {
            this.func_152995_h(var3.toString());
         }

      }
   }

   protected void func_153001_r() {
      if (this.field_175995_l != ChatController.EnumEmoticonMode.None) {
         if (this.field_175996_m == null) {
            ErrorCode var1 = this.field_153008_f.downloadEmoticonData();
            if (ErrorCode.failed(var1)) {
               String var2 = ErrorCode.getString(var1);
               this.func_152995_h(String.format("Error trying to download emoticon data: %s", var2));
            }
         }

      }
   }

   protected void func_152988_s() {
      if (this.field_175996_m == null) {
         this.field_175996_m = new ChatEmoticonData();
         ErrorCode var1 = this.field_153008_f.getEmoticonData(this.field_175996_m);
         if (ErrorCode.succeeded(var1)) {
            try {
               if (this.field_153003_a != null) {
                  this.field_153003_a.func_176021_d();
               }
            } catch (Exception var3) {
               this.func_152995_h(var3.toString());
            }
         } else {
            this.func_152995_h("Error preparing emoticon data: " + ErrorCode.getString(var1));
         }

      }
   }

   protected void func_152996_t() {
      if (this.field_175996_m != null) {
         ErrorCode var1 = this.field_153008_f.clearEmoticonData();
         if (ErrorCode.succeeded(var1)) {
            this.field_175996_m = null;

            try {
               if (this.field_153003_a != null) {
                  this.field_153003_a.func_176024_e();
               }
            } catch (Exception var3) {
               this.func_152995_h(var3.toString());
            }
         } else {
            this.func_152995_h("Error clearing emoticon data: " + ErrorCode.getString(var1));
         }

      }
   }

   protected void func_152995_h(String var1) {
      field_153018_p.error(TwitchStream.field_152949_a, "[Chat controller] {}", new Object[]{var1});
   }

   public class ChatChannelListener implements IChatChannelListener {
      protected String field_176048_a = null;
      protected boolean field_176046_b = false;
      protected ChatController.EnumChannelState field_176047_c;
      protected List<ChatUserInfo> field_176044_d;
      protected LinkedList<ChatRawMessage> field_176045_e;
      protected LinkedList<ChatTokenizedMessage> field_176042_f;
      protected ChatBadgeData field_176043_g;

      public ChatChannelListener(String var2) {
         super();
         this.field_176047_c = ChatController.EnumChannelState.Created;
         this.field_176044_d = Lists.newArrayList();
         this.field_176045_e = new LinkedList();
         this.field_176042_f = new LinkedList();
         this.field_176043_g = null;
         this.field_176048_a = var2;
      }

      public ChatController.EnumChannelState func_176040_a() {
         return this.field_176047_c;
      }

      public boolean func_176038_a(boolean var1) {
         this.field_176046_b = var1;
         ErrorCode var2 = ErrorCode.TTV_EC_SUCCESS;
         if (var1) {
            var2 = ChatController.this.field_153008_f.connectAnonymous(this.field_176048_a, this);
         } else {
            var2 = ChatController.this.field_153008_f.connect(this.field_176048_a, ChatController.this.field_153004_b, ChatController.this.field_153012_j.data, this);
         }

         if (ErrorCode.failed(var2)) {
            String var3 = ErrorCode.getString(var2);
            ChatController.this.func_152995_h(String.format("Error connecting: %s", var3));
            this.func_176036_d(this.field_176048_a);
            return false;
         } else {
            this.func_176035_a(ChatController.EnumChannelState.Connecting);
            this.func_176041_h();
            return true;
         }
      }

      public boolean func_176034_g() {
         switch(this.field_176047_c) {
         case Connected:
         case Connecting:
            ErrorCode var1 = ChatController.this.field_153008_f.disconnect(this.field_176048_a);
            if (ErrorCode.failed(var1)) {
               String var2 = ErrorCode.getString(var1);
               ChatController.this.func_152995_h(String.format("Error disconnecting: %s", var2));
               return false;
            }

            this.func_176035_a(ChatController.EnumChannelState.Disconnecting);
            return true;
         case Created:
         case Disconnected:
         case Disconnecting:
         default:
            return false;
         }
      }

      protected void func_176035_a(ChatController.EnumChannelState var1) {
         if (var1 != this.field_176047_c) {
            this.field_176047_c = var1;
         }
      }

      public void func_176032_a(String var1) {
         if (ChatController.this.field_175995_l == ChatController.EnumEmoticonMode.None) {
            this.field_176045_e.clear();
            this.field_176042_f.clear();
         } else {
            ListIterator var2;
            if (this.field_176045_e.size() > 0) {
               var2 = this.field_176045_e.listIterator();

               while(var2.hasNext()) {
                  ChatRawMessage var3 = (ChatRawMessage)var2.next();
                  if (var3.userName.equals(var1)) {
                     var2.remove();
                  }
               }
            }

            if (this.field_176042_f.size() > 0) {
               var2 = this.field_176042_f.listIterator();

               while(var2.hasNext()) {
                  ChatTokenizedMessage var5 = (ChatTokenizedMessage)var2.next();
                  if (var5.displayName.equals(var1)) {
                     var2.remove();
                  }
               }
            }
         }

         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_176019_a(this.field_176048_a, var1);
            }
         } catch (Exception var4) {
            ChatController.this.func_152995_h(var4.toString());
         }

      }

      public boolean func_176037_b(String var1) {
         if (this.field_176047_c != ChatController.EnumChannelState.Connected) {
            return false;
         } else {
            ErrorCode var2 = ChatController.this.field_153008_f.sendMessage(this.field_176048_a, var1);
            if (ErrorCode.failed(var2)) {
               String var3 = ErrorCode.getString(var2);
               ChatController.this.func_152995_h(String.format("Error sending chat message: %s", var3));
               return false;
            } else {
               return true;
            }
         }
      }

      protected void func_176041_h() {
         if (ChatController.this.field_175995_l != ChatController.EnumEmoticonMode.None) {
            if (this.field_176043_g == null) {
               ErrorCode var1 = ChatController.this.field_153008_f.downloadBadgeData(this.field_176048_a);
               if (ErrorCode.failed(var1)) {
                  String var2 = ErrorCode.getString(var1);
                  ChatController.this.func_152995_h(String.format("Error trying to download badge data: %s", var2));
               }
            }

         }
      }

      protected void func_176039_i() {
         if (this.field_176043_g == null) {
            this.field_176043_g = new ChatBadgeData();
            ErrorCode var1 = ChatController.this.field_153008_f.getBadgeData(this.field_176048_a, this.field_176043_g);
            if (ErrorCode.succeeded(var1)) {
               try {
                  if (ChatController.this.field_153003_a != null) {
                     ChatController.this.field_153003_a.func_176016_c(this.field_176048_a);
                  }
               } catch (Exception var3) {
                  ChatController.this.func_152995_h(var3.toString());
               }
            } else {
               ChatController.this.func_152995_h("Error preparing badge data: " + ErrorCode.getString(var1));
            }

         }
      }

      protected void func_176033_j() {
         if (this.field_176043_g != null) {
            ErrorCode var1 = ChatController.this.field_153008_f.clearBadgeData(this.field_176048_a);
            if (ErrorCode.succeeded(var1)) {
               this.field_176043_g = null;

               try {
                  if (ChatController.this.field_153003_a != null) {
                     ChatController.this.field_153003_a.func_176020_d(this.field_176048_a);
                  }
               } catch (Exception var3) {
                  ChatController.this.func_152995_h(var3.toString());
               }
            } else {
               ChatController.this.func_152995_h("Error releasing badge data: " + ErrorCode.getString(var1));
            }

         }
      }

      protected void func_176031_c(String var1) {
         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_180606_a(var1);
            }
         } catch (Exception var3) {
            ChatController.this.func_152995_h(var3.toString());
         }

      }

      protected void func_176036_d(String var1) {
         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_180607_b(var1);
            }
         } catch (Exception var3) {
            ChatController.this.func_152995_h(var3.toString());
         }

      }

      private void func_176030_k() {
         if (this.field_176047_c != ChatController.EnumChannelState.Disconnected) {
            this.func_176035_a(ChatController.EnumChannelState.Disconnected);
            this.func_176036_d(this.field_176048_a);
            this.func_176033_j();
         }

      }

      public void chatStatusCallback(String var1, ErrorCode var2) {
         if (!ErrorCode.succeeded(var2)) {
            ChatController.this.field_175998_i.remove(var1);
            this.func_176030_k();
         }
      }

      public void chatChannelMembershipCallback(String var1, ChatEvent var2, ChatChannelInfo var3) {
         switch(var2) {
         case TTV_CHAT_JOINED_CHANNEL:
            this.func_176035_a(ChatController.EnumChannelState.Connected);
            this.func_176031_c(var1);
            break;
         case TTV_CHAT_LEFT_CHANNEL:
            this.func_176030_k();
         }

      }

      public void chatChannelUserChangeCallback(String var1, ChatUserInfo[] var2, ChatUserInfo[] var3, ChatUserInfo[] var4) {
         int var5;
         int var6;
         for(var5 = 0; var5 < var3.length; ++var5) {
            var6 = this.field_176044_d.indexOf(var3[var5]);
            if (var6 >= 0) {
               this.field_176044_d.remove(var6);
            }
         }

         for(var5 = 0; var5 < var4.length; ++var5) {
            var6 = this.field_176044_d.indexOf(var4[var5]);
            if (var6 >= 0) {
               this.field_176044_d.remove(var6);
            }

            this.field_176044_d.add(var4[var5]);
         }

         for(var5 = 0; var5 < var2.length; ++var5) {
            this.field_176044_d.add(var2[var5]);
         }

         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_176018_a(this.field_176048_a, var2, var3, var4);
            }
         } catch (Exception var7) {
            ChatController.this.func_152995_h(var7.toString());
         }

      }

      public void chatChannelRawMessageCallback(String var1, ChatRawMessage[] var2) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.field_176045_e.addLast(var2[var3]);
         }

         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_180605_a(this.field_176048_a, var2);
            }
         } catch (Exception var4) {
            ChatController.this.func_152995_h(var4.toString());
         }

         while(this.field_176045_e.size() > ChatController.this.field_153015_m) {
            this.field_176045_e.removeFirst();
         }

      }

      public void chatChannelTokenizedMessageCallback(String var1, ChatTokenizedMessage[] var2) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.field_176042_f.addLast(var2[var3]);
         }

         try {
            if (ChatController.this.field_153003_a != null) {
               ChatController.this.field_153003_a.func_176025_a(this.field_176048_a, var2);
            }
         } catch (Exception var4) {
            ChatController.this.func_152995_h(var4.toString());
         }

         while(this.field_176042_f.size() > ChatController.this.field_153015_m) {
            this.field_176042_f.removeFirst();
         }

      }

      public void chatClearCallback(String var1, String var2) {
         this.func_176032_a(var2);
      }

      public void chatBadgeDataDownloadCallback(String var1, ErrorCode var2) {
         if (ErrorCode.succeeded(var2)) {
            this.func_176039_i();
         }

      }
   }

   public interface ChatListener {
      void func_176023_d(ErrorCode var1);

      void func_176022_e(ErrorCode var1);

      void func_176021_d();

      void func_176024_e();

      void func_176017_a(ChatController.ChatState var1);

      void func_176025_a(String var1, ChatTokenizedMessage[] var2);

      void func_180605_a(String var1, ChatRawMessage[] var2);

      void func_176018_a(String var1, ChatUserInfo[] var2, ChatUserInfo[] var3, ChatUserInfo[] var4);

      void func_180606_a(String var1);

      void func_180607_b(String var1);

      void func_176019_a(String var1, String var2);

      void func_176016_c(String var1);

      void func_176020_d(String var1);
   }

   public static enum EnumEmoticonMode {
      None,
      Url,
      TextureAtlas;

      private EnumEmoticonMode() {
      }
   }

   public static enum EnumChannelState {
      Created,
      Connecting,
      Connected,
      Disconnecting,
      Disconnected;

      private EnumChannelState() {
      }
   }

   public static enum ChatState {
      Uninitialized,
      Initializing,
      Initialized,
      ShuttingDown;

      private ChatState() {
      }
   }
}
