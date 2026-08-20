package net.minecraft.advancements;

import io.netty.buffer.ByteBuf;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public class CriterionProgress {
   public static final StreamCodec<ByteBuf, CriterionProgress> STREAM_CODEC;
   private @Nullable Instant obtained;

   public CriterionProgress() {
      super();
   }

   public CriterionProgress(final @Nullable Instant obtained) {
      super();
      this.obtained = obtained;
   }

   public boolean isDone() {
      return this.obtained != null;
   }

   public void grant() {
      this.obtained = Instant.now();
   }

   public void revoke() {
      this.obtained = null;
   }

   public @Nullable Instant getObtained() {
      return this.obtained;
   }

   public String toString() {
      Object var10000 = this.obtained == null ? "false" : this.obtained;
      return "CriterionProgress{obtained=" + String.valueOf(var10000) + "}";
   }

   static {
      STREAM_CODEC = ByteBufCodecs.INSTANT.apply(ByteBufCodecs::optional).map((obtained) -> new CriterionProgress((Instant)obtained.orElse((Object)null)), (progress) -> Optional.ofNullable(progress.obtained));
   }
}
