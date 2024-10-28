package net.minecraft.world.entity;

import net.minecraft.util.StringRepresentable;

public enum EquipmentSlot implements StringRepresentable {
   MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
   OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
   FEET(EquipmentSlot.Type.ARMOR, 0, 1, "feet"),
   LEGS(EquipmentSlot.Type.ARMOR, 1, 2, "legs"),
   CHEST(EquipmentSlot.Type.ARMOR, 2, 3, "chest"),
   HEAD(EquipmentSlot.Type.ARMOR, 3, 4, "head"),
   BODY(EquipmentSlot.Type.BODY, 0, 6, "body");

   public static final StringRepresentable.EnumCodec<EquipmentSlot> CODEC = StringRepresentable.fromEnum(EquipmentSlot::values);
   private final Type type;
   private final int index;
   private final int filterFlag;
   private final String name;

   private EquipmentSlot(final Type var3, final int var4, final int var5, final String var6) {
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

   public boolean isArmor() {
      return this.type == EquipmentSlot.Type.ARMOR;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static EquipmentSlot byName(String var0) {
      EquipmentSlot var1 = (EquipmentSlot)CODEC.byName(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalArgumentException("Invalid slot '" + var0 + "'");
      }
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

      String var10002 = String.valueOf(var0);
      throw new IllegalArgumentException("Invalid slot '" + var10002 + "': " + var1);
   }

   // $FF: synthetic method
   private static EquipmentSlot[] $values() {
      return new EquipmentSlot[]{MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD, BODY};
   }

   public static enum Type {
      HAND,
      ARMOR,
      BODY;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{HAND, ARMOR, BODY};
      }
   }
}
