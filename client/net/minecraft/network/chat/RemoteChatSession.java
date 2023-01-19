package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID a, ProfilePublicKey b) {
   private final UUID sessionId;
   private final ProfilePublicKey profilePublicKey;

   public RemoteChatSession(UUID var1, ProfilePublicKey var2) {
      super();
      this.sessionId = var1;
      this.profilePublicKey = var2;
   }

   public SignedMessageValidator createMessageValidator() {
      return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator());
   }

   public SignedMessageChain.Decoder createMessageDecoder(UUID var1) {
      return new SignedMessageChain(var1, this.sessionId).decoder(this.profilePublicKey);
   }

   public RemoteChatSession.Data asData() {
      return new RemoteChatSession.Data(this.sessionId, this.profilePublicKey.data());
   }

   public static record Data(UUID a, ProfilePublicKey.Data b) {
      private final UUID sessionId;
      private final ProfilePublicKey.Data profilePublicKey;

      public Data(UUID var1, ProfilePublicKey.Data var2) {
         super();
         this.sessionId = var1;
         this.profilePublicKey = var2;
      }

      public static RemoteChatSession.Data read(FriendlyByteBuf var0) {
         return new RemoteChatSession.Data(var0.readUUID(), new ProfilePublicKey.Data(var0));
      }

      public static void write(FriendlyByteBuf var0, RemoteChatSession.Data var1) {
         var0.writeUUID(var1.sessionId);
         var1.profilePublicKey.write(var0);
      }

      public RemoteChatSession validate(GameProfile var1, SignatureValidator var2, Duration var3) throws ProfilePublicKey.ValidationException {
         return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(var2, var1.getId(), this.profilePublicKey, var3));
      }
   }
}
