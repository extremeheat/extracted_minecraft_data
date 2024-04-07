package net.minecraft.advancements;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
   @Nullable
   private Instant obtained;

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

   @Nullable
   public Instant getObtained() {
      return this.obtained;
   }

   @Override
   public String toString() {
      return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeNullable(this.obtained, FriendlyByteBuf::writeInstant);
   }

   public static CriterionProgress fromNetwork(FriendlyByteBuf var0) {
      CriterionProgress var1 = new CriterionProgress();
      var1.obtained = var0.readNullable(FriendlyByteBuf::readInstant);
      return var1;
   }
}
