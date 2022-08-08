package net.minecraft.commands;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.Crypt;

public interface CommandSigningContext {
   CommandSigningContext NONE = (var0) -> {
      return MessageSignature.unsigned();
   };

   MessageSignature getArgumentSignature(String var1);

   default boolean signedArgumentPreview(String var1) {
      return false;
   }

   public static record SignedArguments(UUID b, Instant c, ArgumentSignatures d, boolean e) implements CommandSigningContext {
      private final UUID sender;
      private final Instant timeStamp;
      private final ArgumentSignatures argumentSignatures;
      private final boolean signedPreview;

      public SignedArguments(UUID var1, Instant var2, ArgumentSignatures var3, boolean var4) {
         super();
         this.sender = var1;
         this.timeStamp = var2;
         this.argumentSignatures = var3;
         this.signedPreview = var4;
      }

      public MessageSignature getArgumentSignature(String var1) {
         Crypt.SaltSignaturePair var2 = this.argumentSignatures.get(var1);
         return var2 != null ? new MessageSignature(this.sender, this.timeStamp, var2) : MessageSignature.unsigned();
      }

      public boolean signedArgumentPreview(String var1) {
         return this.signedPreview;
      }

      public UUID sender() {
         return this.sender;
      }

      public Instant timeStamp() {
         return this.timeStamp;
      }

      public ArgumentSignatures argumentSignatures() {
         return this.argumentSignatures;
      }

      public boolean signedPreview() {
         return this.signedPreview;
      }
   }
}
