package net.minecraft.network.chat;

import java.util.UUID;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfileKeyPair;

public record LocalChatSession(UUID sessionId, ProfileKeyPair keyPair) {
   public LocalChatSession(UUID sessionId, ProfileKeyPair keyPair) {
      super();
      this.sessionId = sessionId;
      this.keyPair = keyPair;
   }

   public static LocalChatSession create(ProfileKeyPair var0) {
      return new LocalChatSession(UUID.randomUUID(), var0);
   }

   public SignedMessageChain.Encoder createMessageEncoder(UUID var1) {
      return (new SignedMessageChain(var1, this.sessionId)).encoder(Signer.from(this.keyPair.privateKey(), "SHA256withRSA"));
   }

   public RemoteChatSession asRemote() {
      return new RemoteChatSession(this.sessionId, this.keyPair.publicKey());
   }

   public UUID sessionId() {
      return this.sessionId;
   }

   public ProfileKeyPair keyPair() {
      return this.keyPair;
   }
}
