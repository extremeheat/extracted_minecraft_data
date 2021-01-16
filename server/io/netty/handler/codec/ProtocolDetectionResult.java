package io.netty.handler.codec;

import io.netty.util.internal.ObjectUtil;

public final class ProtocolDetectionResult<T> {
   private static final ProtocolDetectionResult NEEDS_MORE_DATE;
   private static final ProtocolDetectionResult INVALID;
   private final ProtocolDetectionState state;
   private final T result;

   public static <T> ProtocolDetectionResult<T> needsMoreData() {
      return NEEDS_MORE_DATE;
   }

   public static <T> ProtocolDetectionResult<T> invalid() {
      return INVALID;
   }

   public static <T> ProtocolDetectionResult<T> detected(T var0) {
      return new ProtocolDetectionResult(ProtocolDetectionState.DETECTED, ObjectUtil.checkNotNull(var0, "protocol"));
   }

   private ProtocolDetectionResult(ProtocolDetectionState var1, T var2) {
      super();
      this.state = var1;
      this.result = var2;
   }

   public ProtocolDetectionState state() {
      return this.state;
   }

   public T detectedProtocol() {
      return this.result;
   }

   static {
      NEEDS_MORE_DATE = new ProtocolDetectionResult(ProtocolDetectionState.NEEDS_MORE_DATA, (Object)null);
      INVALID = new ProtocolDetectionResult(ProtocolDetectionState.INVALID, (Object)null);
   }
}
