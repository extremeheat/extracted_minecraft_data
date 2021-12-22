package net.minecraft.client.gui.screens.advancements;

public enum AdvancementWidgetType {
   OBTAINED(0),
   UNOBTAINED(1);

   // $FF: renamed from: y int
   private final int field_352;

   private AdvancementWidgetType(int var3) {
      this.field_352 = var3;
   }

   public int getIndex() {
      return this.field_352;
   }

   // $FF: synthetic method
   private static AdvancementWidgetType[] $values() {
      return new AdvancementWidgetType[]{OBTAINED, UNOBTAINED};
   }
}
