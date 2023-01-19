package net.minecraft.network.chat;

public record LastSeenTrackedEntry(MessageSignature a, boolean b) {
   private final MessageSignature signature;
   private final boolean pending;

   public LastSeenTrackedEntry(MessageSignature var1, boolean var2) {
      super();
      this.signature = var1;
      this.pending = var2;
   }

   public LastSeenTrackedEntry acknowledge() {
      return this.pending ? new LastSeenTrackedEntry(this.signature, false) : this;
   }
}
