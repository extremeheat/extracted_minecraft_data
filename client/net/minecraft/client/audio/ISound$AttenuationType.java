package net.minecraft.client.audio;

public enum ISound$AttenuationType {
   NONE(0),
   LINEAR(2);

   private final int field_148589_c;

   private ISound$AttenuationType(int var3) {
      this.field_148589_c = var3;
   }

   public int func_148586_a() {
      return this.field_148589_c;
   }
}
