package io.netty.util;

public interface ByteProcessor {
   ByteProcessor FIND_NUL = new ByteProcessor.IndexOfProcessor((byte)0);
   ByteProcessor FIND_NON_NUL = new ByteProcessor.IndexNotOfProcessor((byte)0);
   ByteProcessor FIND_CR = new ByteProcessor.IndexOfProcessor((byte)13);
   ByteProcessor FIND_NON_CR = new ByteProcessor.IndexNotOfProcessor((byte)13);
   ByteProcessor FIND_LF = new ByteProcessor.IndexOfProcessor((byte)10);
   ByteProcessor FIND_NON_LF = new ByteProcessor.IndexNotOfProcessor((byte)10);
   ByteProcessor FIND_SEMI_COLON = new ByteProcessor.IndexOfProcessor((byte)59);
   ByteProcessor FIND_COMMA = new ByteProcessor.IndexOfProcessor((byte)44);
   ByteProcessor FIND_ASCII_SPACE = new ByteProcessor.IndexOfProcessor((byte)32);
   ByteProcessor FIND_CRLF = new ByteProcessor() {
      public boolean process(byte var1) {
         return var1 != 13 && var1 != 10;
      }
   };
   ByteProcessor FIND_NON_CRLF = new ByteProcessor() {
      public boolean process(byte var1) {
         return var1 == 13 || var1 == 10;
      }
   };
   ByteProcessor FIND_LINEAR_WHITESPACE = new ByteProcessor() {
      public boolean process(byte var1) {
         return var1 != 32 && var1 != 9;
      }
   };
   ByteProcessor FIND_NON_LINEAR_WHITESPACE = new ByteProcessor() {
      public boolean process(byte var1) {
         return var1 == 32 || var1 == 9;
      }
   };

   boolean process(byte var1) throws Exception;

   public static class IndexNotOfProcessor implements ByteProcessor {
      private final byte byteToNotFind;

      public IndexNotOfProcessor(byte var1) {
         super();
         this.byteToNotFind = var1;
      }

      public boolean process(byte var1) {
         return var1 == this.byteToNotFind;
      }
   }

   public static class IndexOfProcessor implements ByteProcessor {
      private final byte byteToFind;

      public IndexOfProcessor(byte var1) {
         super();
         this.byteToFind = var1;
      }

      public boolean process(byte var1) {
         return var1 != this.byteToFind;
      }
   }
}
