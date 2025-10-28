package net.minecraft.commands;

import java.util.Map;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jspecify.annotations.Nullable;

public interface CommandSigningContext {
   CommandSigningContext ANONYMOUS = new CommandSigningContext() {
      public @Nullable PlayerChatMessage getArgument(String var1) {
         return null;
      }
   };

   @Nullable PlayerChatMessage getArgument(String var1);

   public static record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {
      public SignedArguments(Map<String, PlayerChatMessage> var1) {
         super();
         this.arguments = var1;
      }

      public @Nullable PlayerChatMessage getArgument(String var1) {
         return (PlayerChatMessage)this.arguments.get(var1);
      }
   }
}
