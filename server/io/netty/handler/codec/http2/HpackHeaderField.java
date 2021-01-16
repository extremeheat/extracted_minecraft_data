package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

class HpackHeaderField {
   static final int HEADER_ENTRY_OVERHEAD = 32;
   final CharSequence name;
   final CharSequence value;

   static long sizeOf(CharSequence var0, CharSequence var1) {
      return (long)(var0.length() + var1.length() + 32);
   }

   HpackHeaderField(CharSequence var1, CharSequence var2) {
      super();
      this.name = (CharSequence)ObjectUtil.checkNotNull(var1, "name");
      this.value = (CharSequence)ObjectUtil.checkNotNull(var2, "value");
   }

   final int size() {
      return this.name.length() + this.value.length() + 32;
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof HpackHeaderField)) {
         return false;
      } else {
         HpackHeaderField var2 = (HpackHeaderField)var1;
         return (HpackUtil.equalsConstantTime(this.name, var2.name) & HpackUtil.equalsConstantTime(this.value, var2.value)) != 0;
      }
   }

   public String toString() {
      return this.name + ": " + this.value;
   }
}
