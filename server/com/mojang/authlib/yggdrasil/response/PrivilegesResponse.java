package com.mojang.authlib.yggdrasil.response;

public class PrivilegesResponse extends Response {
   private PrivilegesResponse.Privileges privileges = new PrivilegesResponse.Privileges();

   public PrivilegesResponse() {
      super();
   }

   public PrivilegesResponse.Privileges getPrivileges() {
      return this.privileges;
   }

   public class Privileges {
      private PrivilegesResponse.Privileges.Privilege onlineChat = new PrivilegesResponse.Privileges.Privilege();
      private PrivilegesResponse.Privileges.Privilege multiplayerServer = new PrivilegesResponse.Privileges.Privilege();
      private PrivilegesResponse.Privileges.Privilege multiplayerRealms = new PrivilegesResponse.Privileges.Privilege();

      public Privileges() {
         super();
      }

      public PrivilegesResponse.Privileges.Privilege getOnlineChat() {
         return this.onlineChat;
      }

      public PrivilegesResponse.Privileges.Privilege getMultiplayerServer() {
         return this.multiplayerServer;
      }

      public PrivilegesResponse.Privileges.Privilege getMultiplayerRealms() {
         return this.multiplayerRealms;
      }

      public class Privilege {
         private boolean enabled;

         public Privilege() {
            super();
         }

         public boolean isEnabled() {
            return this.enabled;
         }
      }
   }
}
