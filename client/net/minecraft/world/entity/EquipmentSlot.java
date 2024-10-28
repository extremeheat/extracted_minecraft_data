package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public enum EquipmentSlot implements StringRepresentable {
   MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
   OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
   FEET(EquipmentSlot.Type.HUMANOID_ARMOR, 0, 1, 1, "feet"),
   LEGS(EquipmentSlot.Type.HUMANOID_ARMOR, 1, 1, 2, "legs"),
   CHEST(EquipmentSlot.Type.HUMANOID_ARMOR, 2, 1, 3, "chest"),
   HEAD(EquipmentSlot.Type.HUMANOID_ARMOR, 3, 1, 4, "head"),
   BODY(EquipmentSlot.Type.ANIMAL_ARMOR, 0, 1, 6, "body");

   public static final int NO_COUNT_LIMIT = 0;
   public static final List<EquipmentSlot> VALUES = List.of(values());
   public static final IntFunction<EquipmentSlot> BY_ID = ByIdMap.continuous((var0) -> {
      return var0.id;
   }, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StringRepresentable.EnumCodec<EquipmentSlot> CODEC = StringRepresentable.fromEnum(EquipmentSlot::values);
   public static final StreamCodec<ByteBuf, EquipmentSlot> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> {
      return var0.id;
   });
   private final Type type;
   private final int index;
   private final int countLimit;
   private final int id;
   private final String name;

   private EquipmentSlot(final Type var3, final int var4, final int var5, final int var6, final String var7) {
      this.type = var3;
      this.index = var4;
      this.countLimit = var5;
      this.id = var6;
      this.name = var7;
   }

   private EquipmentSlot(final Type var3, final int var4, final int var5, final String var6) {
      this(var3, var4, 0, var5, var6);
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

   public ItemStack limit(ItemStack var1) {
      return this.countLimit > 0 ? var1.split(this.countLimit) : var1;
   }

   public int getId() {
      return this.id;
   }

   public int getFilterBit(int var1) {
      return this.id + var1;
   }

   public String getName() {
      return this.name;
   }

   public boolean isArmor() {
      return this.type == EquipmentSlot.Type.HUMANOID_ARMOR || this.type == EquipmentSlot.Type.ANIMAL_ARMOR;
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

   // $FF: synthetic method
   private static EquipmentSlot[] $values() {
      return new EquipmentSlot[]{MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD, BODY};
   }

   public static enum Type {
      HAND,
      HUMANOID_ARMOR,
      ANIMAL_ARMOR;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{HAND, HUMANOID_ARMOR, ANIMAL_ARMOR};
      }
   }
}
