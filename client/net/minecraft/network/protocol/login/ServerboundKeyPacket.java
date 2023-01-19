package net.minecraft.network.protocol.login;

import com.mojang.datafixers.util.Either;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.ProfilePublicKey;

public class ServerboundKeyPacket implements Packet<ServerLoginPacketListener> {
   private final byte[] keybytes;
   private final Either<byte[], Crypt.SaltSignaturePair> nonceOrSaltSignature;

   public ServerboundKeyPacket(SecretKey var1, PublicKey var2, byte[] var3) throws CryptException {
      super();
      this.keybytes = Crypt.encryptUsingKey(var2, var1.getEncoded());
      this.nonceOrSaltSignature = Either.left(Crypt.encryptUsingKey(var2, var3));
   }

   public ServerboundKeyPacket(SecretKey var1, PublicKey var2, long var3, byte[] var5) throws CryptException {
      super();
      this.keybytes = Crypt.encryptUsingKey(var2, var1.getEncoded());
      this.nonceOrSaltSignature = Either.right(new Crypt.SaltSignaturePair(var3, var5));
   }

   public ServerboundKeyPacket(FriendlyByteBuf var1) {
      super();
      this.keybytes = var1.readByteArray();
      this.nonceOrSaltSignature = var1.readEither(FriendlyByteBuf::readByteArray, Crypt.SaltSignaturePair::new);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByteArray(this.keybytes);
      var1.writeEither(this.nonceOrSaltSignature, FriendlyByteBuf::writeByteArray, Crypt.SaltSignaturePair::write);
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey var1) throws CryptException {
      return Crypt.decryptByteToSecretKey(var1, this.keybytes);
   }

   public boolean isChallengeSignatureValid(byte[] var1, ProfilePublicKey var2) {
      return this.nonceOrSaltSignature.map(var0 -> false, var2x -> var2.createSignatureValidator().validate(var2xx -> {
            var2xx.update(var1);
            var2xx.update(var2x.saltAsBytes());
         }, var2x.signature()));
   }

   public boolean isNonceValid(byte[] var1, PrivateKey var2) {
      Optional var3 = this.nonceOrSaltSignature.left();

      try {
         return var3.isPresent() && Arrays.equals(var1, Crypt.decryptUsingKey(var2, (byte[])var3.get()));
      } catch (CryptException var5) {
         return false;
      }
   }
}
