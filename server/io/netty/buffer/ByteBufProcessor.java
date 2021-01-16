package io.netty.buffer;

import io.netty.util.ByteProcessor;

/** @deprecated */
@Deprecated
public interface ByteBufProcessor extends ByteProcessor {
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NUL = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 != 0;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 == 0;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_CR = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 != 13;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NON_CR = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 == 13;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_LF = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 != 10;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NON_LF = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 == 10;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_CRLF = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 != 13 && var1 != 10;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 == 13 || var1 == 10;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 != 32 && var1 != 9;
      }
   };
   /** @deprecated */
   @Deprecated
   ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor() {
      public boolean process(byte var1) throws Exception {
         return var1 == 32 || var1 == 9;
      }
   };
}
