package net.minecraft.advancements;

import java.time.Instant;
import net.minecraft.network.FriendlyByteBuf;
import org.jspecify.annotations.Nullable;

public class CriterionProgress {
   private @Nullable Instant obtained;

   public CriterionProgress() {
      super();
   }

   public CriterionProgress(Instant var1) {
      super();
      this.obtained = var1;
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

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeNullable(this.obtained, FriendlyByteBuf::writeInstant);
   }

   public static CriterionProgress fromNetwork(FriendlyByteBuf var0) {
      CriterionProgress var1 = new CriterionProgress();
      var1.obtained = (Instant)var0.readNullable(FriendlyByteBuf::readInstant);
      return var1;
   }
}
