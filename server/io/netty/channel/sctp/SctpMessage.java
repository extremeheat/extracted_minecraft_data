package io.netty.channel.sctp;

import com.sun.nio.sctp.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public final class SctpMessage extends DefaultByteBufHolder {
   private final int streamIdentifier;
   private final int protocolIdentifier;
   private final boolean unordered;
   private final MessageInfo msgInfo;

   public SctpMessage(int var1, int var2, ByteBuf var3) {
      this(var1, var2, false, var3);
   }

   public SctpMessage(int var1, int var2, boolean var3, ByteBuf var4) {
      super(var4);
      this.protocolIdentifier = var1;
      this.streamIdentifier = var2;
      this.unordered = var3;
      this.msgInfo = null;
   }

   public SctpMessage(MessageInfo var1, ByteBuf var2) {
      super(var2);
      if (var1 == null) {
         throw new NullPointerException("msgInfo");
      } else {
         this.msgInfo = var1;
         this.streamIdentifier = var1.streamNumber();
         this.protocolIdentifier = var1.payloadProtocolID();
         this.unordered = var1.isUnordered();
      }
   }

   public int streamIdentifier() {
      return this.streamIdentifier;
   }

   public int protocolIdentifier() {
      return this.protocolIdentifier;
   }

   public boolean isUnordered() {
      return this.unordered;
   }

   public MessageInfo messageInfo() {
      return this.msgInfo;
   }

   public boolean isComplete() {
      return this.msgInfo != null ? this.msgInfo.isComplete() : true;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         SctpMessage var2 = (SctpMessage)var1;
         if (this.protocolIdentifier != var2.protocolIdentifier) {
            return false;
         } else if (this.streamIdentifier != var2.streamIdentifier) {
            return false;
         } else {
            return this.unordered != var2.unordered ? false : this.content().equals(var2.content());
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.streamIdentifier;
      var1 = 31 * var1 + this.protocolIdentifier;
      var1 = 31 * var1 + (this.unordered ? 1231 : 1237);
      var1 = 31 * var1 + this.content().hashCode();
      return var1;
   }

   public SctpMessage copy() {
      return (SctpMessage)super.copy();
   }

   public SctpMessage duplicate() {
      return (SctpMessage)super.duplicate();
   }

   public SctpMessage retainedDuplicate() {
      return (SctpMessage)super.retainedDuplicate();
   }

   public SctpMessage replace(ByteBuf var1) {
      return this.msgInfo == null ? new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.unordered, var1) : new SctpMessage(this.msgInfo, var1);
   }

   public SctpMessage retain() {
      super.retain();
      return this;
   }

   public SctpMessage retain(int var1) {
      super.retain(var1);
      return this;
   }

   public SctpMessage touch() {
      super.touch();
      return this;
   }

   public SctpMessage touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public String toString() {
      return "SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", unordered=" + this.unordered + ", data=" + this.contentToString() + '}';
   }
}
