package net.minecraft.network.chat;

public record LastSeenTrackedEntry(MessageSignature signature, boolean pending) {
   public LastSeenTrackedEntry(MessageSignature signature, boolean pending) {
      super();
      this.signature = signature;
      this.pending = pending;
   }

   public LastSeenTrackedEntry acknowledge() {
      return this.pending ? new LastSeenTrackedEntry(this.signature, false) : this;
   }

   public MessageSignature signature() {
      return this.signature;
   }

   public boolean pending() {
      return this.pending;
   }
}
