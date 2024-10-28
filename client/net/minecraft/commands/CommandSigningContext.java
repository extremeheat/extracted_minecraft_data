package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {
   CommandSigningContext ANONYMOUS = new CommandSigningContext() {
      @Nullable
      public PlayerChatMessage getArgument(String var1) {
         return null;
      }
   };

   @Nullable
   PlayerChatMessage getArgument(String var1);

   public static record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {
      public SignedArguments(Map<String, PlayerChatMessage> var1) {
         super();
         this.arguments = var1;
      }

      @Nullable
      public PlayerChatMessage getArgument(String var1) {
         return (PlayerChatMessage)this.arguments.get(var1);
      }

      public Map<String, PlayerChatMessage> arguments() {
         return this.arguments;
      }
   }
}
