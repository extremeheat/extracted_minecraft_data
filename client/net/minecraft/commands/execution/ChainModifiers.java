package net.minecraft.commands.execution;

public record ChainModifiers(byte flags) {
   public static final ChainModifiers DEFAULT = new ChainModifiers((byte)0);
   private static final byte FLAG_FORKED = 1;
   private static final byte FLAG_IS_RETURN = 2;

   public ChainModifiers(byte var1) {
      super();
      this.flags = var1;
   }

   private ChainModifiers setFlag(byte var1) {
      int var2 = this.flags | var1;
      return var2 != this.flags ? new ChainModifiers((byte)var2) : this;
   }

   public boolean isForked() {
      return (this.flags & 1) != 0;
   }

   public ChainModifiers setForked() {
      return this.setFlag((byte)1);
   }

   public boolean isReturn() {
      return (this.flags & 2) != 0;
   }

   public ChainModifiers setReturn() {
      return this.setFlag((byte)2);
   }

   public byte flags() {
      return this.flags;
   }
}
