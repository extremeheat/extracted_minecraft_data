package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
   public RemoteChatSession {
      super();
   }

   public SignedMessageValidator createMessageValidator(final Duration gracePeriod) {
      return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator(), () -> this.profilePublicKey.data().hasExpired(gracePeriod));
   }

   public SignedMessageChain.Decoder createMessageDecoder(final UUID profileId) {
      return (new SignedMessageChain(profileId, this.sessionId)).decoder(this.profilePublicKey);
   }

   public Data asData() {
      return new Data(this.sessionId, this.profilePublicKey.data());
   }

   public boolean hasExpired() {
      return this.profilePublicKey.data().hasExpired();
   }

   public static record Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
      public Data {
         super();
      }

      public static Data read(final FriendlyByteBuf input) {
         return new Data(input.readUUID(), new ProfilePublicKey.Data(input));
      }

      public static void write(final FriendlyByteBuf output, final Data data) {
         output.writeUUID(data.sessionId);
         data.profilePublicKey.write(output);
      }

      public RemoteChatSession validate(final GameProfile profile, final SignatureValidator serviceSignatureValidator) throws ProfilePublicKey.ValidationException {
         return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(serviceSignatureValidator, profile.id(), this.profilePublicKey));
      }
   }
}
