package io.netty.handler.codec.http2;

public final class Http2Flags {
   public static final short END_STREAM = 1;
   public static final short END_HEADERS = 4;
   public static final short ACK = 1;
   public static final short PADDED = 8;
   public static final short PRIORITY = 32;
   private short value;

   public Http2Flags() {
      super();
   }

   public Http2Flags(short var1) {
      super();
      this.value = var1;
   }

   public short value() {
      return this.value;
   }

   public boolean endOfStream() {
      return this.isFlagSet((short)1);
   }

   public boolean endOfHeaders() {
      return this.isFlagSet((short)4);
   }

   public boolean priorityPresent() {
      return this.isFlagSet((short)32);
   }

   public boolean ack() {
      return this.isFlagSet((short)1);
   }

   public boolean paddingPresent() {
      return this.isFlagSet((short)8);
   }

   public int getNumPriorityBytes() {
      return this.priorityPresent() ? 5 : 0;
   }

   public int getPaddingPresenceFieldLength() {
      return this.paddingPresent() ? 1 : 0;
   }

   public Http2Flags endOfStream(boolean var1) {
      return this.setFlag(var1, (short)1);
   }

   public Http2Flags endOfHeaders(boolean var1) {
      return this.setFlag(var1, (short)4);
   }

   public Http2Flags priorityPresent(boolean var1) {
      return this.setFlag(var1, (short)32);
   }

   public Http2Flags paddingPresent(boolean var1) {
      return this.setFlag(var1, (short)8);
   }

   public Http2Flags ack(boolean var1) {
      return this.setFlag(var1, (short)1);
   }

   public Http2Flags setFlag(boolean var1, short var2) {
      if (var1) {
         this.value |= var2;
      } else {
         this.value = (short)(this.value & ~var2);
      }

      return this;
   }

   public boolean isFlagSet(short var1) {
      return (this.value & var1) != 0;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + this.value;
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         return this.value == ((Http2Flags)var1).value;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("value = ").append(this.value).append(" (");
      if (this.ack()) {
         var1.append("ACK,");
      }

      if (this.endOfHeaders()) {
         var1.append("END_OF_HEADERS,");
      }

      if (this.endOfStream()) {
         var1.append("END_OF_STREAM,");
      }

      if (this.priorityPresent()) {
         var1.append("PRIORITY_PRESENT,");
      }

      if (this.paddingPresent()) {
         var1.append("PADDING_PRESENT,");
      }

      var1.append(')');
      return var1.toString();
   }
}
