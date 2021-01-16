package com.mojang.brigadier;

public class LiteralMessage implements Message {
   private final String string;

   public LiteralMessage(String var1) {
      super();
      this.string = var1;
   }

   public String getString() {
      return this.string;
   }

   public String toString() {
      return this.string;
   }
}
