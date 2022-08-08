package net.minecraft.world.entity;

public enum EquipmentSlot {
   MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
   OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
   FEET(EquipmentSlot.Type.ARMOR, 0, 1, "feet"),
   LEGS(EquipmentSlot.Type.ARMOR, 1, 2, "legs"),
   CHEST(EquipmentSlot.Type.ARMOR, 2, 3, "chest"),
   HEAD(EquipmentSlot.Type.ARMOR, 3, 4, "head");

   private final Type type;
   private final int index;
   private final int filterFlag;
   private final String name;

   private EquipmentSlot(Type var3, int var4, int var5, String var6) {
      this.type = var3;
      this.index = var4;
      this.filterFlag = var5;
      this.name = var6;
   }

   public Type getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public int getIndex(int var1) {
      return var1 + this.index;
   }

   public int getFilterFlag() {
      return this.filterFlag;
   }

   public String getName() {
      return this.name;
   }

   public static EquipmentSlot byName(String var0) {
      EquipmentSlot[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EquipmentSlot var4 = var1[var3];
         if (var4.getName().equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + var0 + "'");
   }

   public static EquipmentSlot byTypeAndIndex(Type var0, int var1) {
      EquipmentSlot[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EquipmentSlot var5 = var2[var4];
         if (var5.getType() == var0 && var5.getIndex() == var1) {
            return var5;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + var0 + "': " + var1);
   }

   // $FF: synthetic method
   private static EquipmentSlot[] $values() {
      return new EquipmentSlot[]{MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD};
   }

   public static enum Type {
      HAND,
      ARMOR;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{HAND, ARMOR};
      }
   }
}
