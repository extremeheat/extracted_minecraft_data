package net.minecraft.commands;

import java.util.Map;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jspecify.annotations.Nullable;

public interface CommandSigningContext {
   CommandSigningContext ANONYMOUS = new CommandSigningContext() {
      public @Nullable PlayerChatMessage getArgument(final String name) {
         return null;
      }
   };

   @Nullable PlayerChatMessage getArgument(String name);

   public static record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {
      public SignedArguments {
         super();
      }

      public @Nullable PlayerChatMessage getArgument(final String name) {
         return (PlayerChatMessage)this.arguments.get(name);
      }
   }
}
