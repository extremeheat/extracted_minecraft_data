package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.Level;

public class Priority {
   private final Facility facility;
   private final Severity severity;

   public Priority(Facility var1, Severity var2) {
      super();
      this.facility = var1;
      this.severity = var2;
   }

   public static int getPriority(Facility var0, Level var1) {
      return toPriority(var0, Severity.getSeverity(var1));
   }

   private static int toPriority(Facility var0, Severity var1) {
      return (var0.getCode() << 3) + var1.getCode();
   }

   public Facility getFacility() {
      return this.facility;
   }

   public Severity getSeverity() {
      return this.severity;
   }

   public int getValue() {
      return toPriority(this.facility, this.severity);
   }

   public String toString() {
      return Integer.toString(this.getValue());
   }
}
