package io.netty.channel.embedded;

import io.netty.channel.ChannelId;

final class EmbeddedChannelId implements ChannelId {
   private static final long serialVersionUID = -251711922203466130L;
   static final ChannelId INSTANCE = new EmbeddedChannelId();

   private EmbeddedChannelId() {
      super();
   }

   public String asShortText() {
      return this.toString();
   }

   public String asLongText() {
      return this.toString();
   }

   public int compareTo(ChannelId var1) {
      return var1 instanceof EmbeddedChannelId ? 0 : this.asLongText().compareTo(var1.asLongText());
   }

   public int hashCode() {
      return 0;
   }

   public boolean equals(Object var1) {
      return var1 instanceof EmbeddedChannelId;
   }

   public String toString() {
      return "embedded";
   }
}
