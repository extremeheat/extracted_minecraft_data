package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
   public RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
      super();
      this.sessionId = sessionId;
      this.profilePublicKey = profilePublicKey;
   }

   public SignedMessageValidator createMessageValidator(Duration var1) {
      return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator(), () -> this.profilePublicKey.data().hasExpired(var1));
   }

   public SignedMessageChain.Decoder createMessageDecoder(UUID var1) {
      return new SignedMessageChain(var1, this.sessionId).decoder(this.profilePublicKey);
   }

   public RemoteChatSession.Data asData() {
      return new RemoteChatSession.Data(this.sessionId, this.profilePublicKey.data());
   }

   public boolean hasExpired() {
      return this.profilePublicKey.data().hasExpired();
   }

   public static record Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
      public Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
         super();
         this.sessionId = sessionId;
         this.profilePublicKey = profilePublicKey;
      }

      public static RemoteChatSession.Data read(FriendlyByteBuf var0) {
         return new RemoteChatSession.Data(var0.readUUID(), new ProfilePublicKey.Data(var0));
      }

      public static void write(FriendlyByteBuf var0, RemoteChatSession.Data var1) {
         var0.writeUUID(var1.sessionId);
         var1.profilePublicKey.write(var0);
      }

      public RemoteChatSession validate(GameProfile var1, SignatureValidator var2) throws ProfilePublicKey.ValidationException {
         return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(var2, var1.getId(), this.profilePublicKey));
      }
   }
}