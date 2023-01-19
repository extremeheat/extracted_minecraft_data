package net.minecraft.network.chat;

import java.util.UUID;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfileKeyPair;

public record LocalChatSession(UUID a, ProfileKeyPair b) {
   private final UUID sessionId;
   private final ProfileKeyPair keyPair;

   public LocalChatSession(UUID var1, ProfileKeyPair var2) {
      super();
      this.sessionId = var1;
      this.keyPair = var2;
   }

   public static LocalChatSession create(ProfileKeyPair var0) {
      return new LocalChatSession(UUID.randomUUID(), var0);
   }

   public SignedMessageChain.Encoder createMessageEncoder(UUID var1) {
      return new SignedMessageChain(var1, this.sessionId).encoder(Signer.from(this.keyPair.privateKey(), "SHA256withRSA"));
   }

   public RemoteChatSession asRemote() {
      return new RemoteChatSession(this.sessionId, this.keyPair.publicKey());
   }
}
