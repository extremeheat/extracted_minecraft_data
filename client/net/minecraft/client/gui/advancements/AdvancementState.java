package net.minecraft.client.gui.advancements;

public enum AdvancementState {
   OBTAINED(0),
   UNOBTAINED(1);

   private final int field_192671_d;

   private AdvancementState(int var3) {
      this.field_192671_d = var3;
   }

   public int func_192667_a() {
      return this.field_192671_d;
   }
}
