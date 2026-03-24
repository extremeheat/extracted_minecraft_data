package net.minecraft.advancements;

import java.time.Instant;
import net.minecraft.network.FriendlyByteBuf;
import org.jspecify.annotations.Nullable;

public class CriterionProgress {
   private @Nullable Instant obtained;

   public CriterionProgress() {
      super();
   }

   public CriterionProgress(final Instant obtained) {
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

   public void serializeToNetwork(final FriendlyByteBuf output) {
      output.writeNullable(this.obtained, FriendlyByteBuf::writeInstant);
   }

   public static CriterionProgress fromNetwork(final FriendlyByteBuf input) {
      CriterionProgress result = new CriterionProgress();
      result.obtained = (Instant)input.readNullable(FriendlyByteBuf::readInstant);
      return result;
   }
}
