package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.security.PrivateKey;
import java.time.Instant;
import net.minecraft.util.Crypt;
import net.minecraft.util.ExtraCodecs;

public record ProfileKeyPair(PrivateKey b, ProfilePublicKey c, Instant d) {
   private final PrivateKey privateKey;
   private final ProfilePublicKey publicKey;
   private final Instant refreshedAfter;
   public static final Codec<ProfileKeyPair> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Crypt.PRIVATE_KEY_CODEC.fieldOf("private_key").forGetter(ProfileKeyPair::privateKey),
               ProfilePublicKey.TRUSTED_CODEC.fieldOf("public_key").forGetter(ProfileKeyPair::publicKey),
               ExtraCodecs.INSTANT_ISO8601.fieldOf("refreshed_after").forGetter(ProfileKeyPair::refreshedAfter)
            )
            .apply(var0, ProfileKeyPair::new)
   );

   public ProfileKeyPair(PrivateKey var1, ProfilePublicKey var2, Instant var3) {
      super();
      this.privateKey = var1;
      this.publicKey = var2;
      this.refreshedAfter = var3;
   }

   public boolean dueRefresh() {
      return this.refreshedAfter.isBefore(Instant.now());
   }
}
