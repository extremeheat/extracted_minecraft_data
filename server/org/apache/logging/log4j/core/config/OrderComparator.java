package org.apache.logging.log4j.core.config;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class OrderComparator implements Comparator<Class<?>>, Serializable {
   private static final long serialVersionUID = 1L;
   private static final Comparator<Class<?>> INSTANCE = new OrderComparator();

   public OrderComparator() {
      super();
   }

   public static Comparator<Class<?>> getInstance() {
      return INSTANCE;
   }

   public int compare(Class<?> var1, Class<?> var2) {
      Order var3 = (Order)((Class)Objects.requireNonNull(var1, "lhs")).getAnnotation(Order.class);
      Order var4 = (Order)((Class)Objects.requireNonNull(var2, "rhs")).getAnnotation(Order.class);
      if (var3 == null && var4 == null) {
         return 0;
      } else if (var4 == null) {
         return -1;
      } else {
         return var3 == null ? 1 : Integer.signum(var4.value() - var3.value());
      }
   }
}
