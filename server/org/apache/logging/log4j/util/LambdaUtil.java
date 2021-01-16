package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

public final class LambdaUtil {
   private LambdaUtil() {
      super();
   }

   public static Object[] getAll(Supplier<?>... var0) {
      if (var0 == null) {
         return null;
      } else {
         Object[] var1 = new Object[var0.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = get(var0[var2]);
         }

         return var1;
      }
   }

   public static Object get(Supplier<?> var0) {
      if (var0 == null) {
         return null;
      } else {
         Object var1 = var0.get();
         return var1 instanceof Message ? ((Message)var1).getFormattedMessage() : var1;
      }
   }

   public static Message get(MessageSupplier var0) {
      return var0 == null ? null : var0.get();
   }

   public static Message getMessage(Supplier<?> var0, MessageFactory var1) {
      if (var0 == null) {
         return null;
      } else {
         Object var2 = var0.get();
         return var2 instanceof Message ? (Message)var2 : var1.newMessage(var2);
      }
   }
}
