package net.minecraft.network.chat;

import java.security.SignatureException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageHeader(@Nullable MessageSignature a, UUID b) {
   @Nullable
   private final MessageSignature previousSignature;
   private final UUID sender;

   public SignedMessageHeader(FriendlyByteBuf var1) {
      this(var1.readNullable(MessageSignature::new), var1.readUUID());
   }

   public SignedMessageHeader(@Nullable MessageSignature var1, UUID var2) {
      super();
      this.previousSignature = var1;
      this.sender = var2;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeNullable(this.previousSignature, (var0, var1x) -> var1x.write(var0));
      var1.writeUUID(this.sender);
   }

   public void updateSignature(SignatureUpdater.Output var1, byte[] var2) throws SignatureException {
      if (this.previousSignature != null) {
         var1.update(this.previousSignature.bytes());
      }

      var1.update(UUIDUtil.uuidToByteArray(this.sender));
      var1.update(var2);
   }
}
