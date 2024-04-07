package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum EquipmentSlotGroup implements StringRepresentable {
   ANY(0, "any", var0 -> true),
   MAINHAND(1, "mainhand", EquipmentSlot.MAINHAND),
   OFFHAND(2, "offhand", EquipmentSlot.OFFHAND),
   HAND(3, "hand", var0 -> var0.getType() == EquipmentSlot.Type.HAND),
   FEET(4, "feet", EquipmentSlot.FEET),
   LEGS(5, "legs", EquipmentSlot.LEGS),
   CHEST(6, "chest", EquipmentSlot.CHEST),
   HEAD(7, "head", EquipmentSlot.HEAD),
   ARMOR(8, "armor", EquipmentSlot::isArmor),
   BODY(9, "body", EquipmentSlot.BODY);

   public static final IntFunction<EquipmentSlotGroup> BY_ID = ByIdMap.continuous(var0 -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec<EquipmentSlotGroup> CODEC = StringRepresentable.fromEnum(EquipmentSlotGroup::values);
   public static final StreamCodec<ByteBuf, EquipmentSlotGroup> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, var0 -> var0.id);
   private final int id;
   private final String key;
   private final Predicate<EquipmentSlot> predicate;

   private EquipmentSlotGroup(int var3, String var4, Predicate<EquipmentSlot> var5) {
      this.id = var3;
      this.key = var4;
      this.predicate = var5;
   }

   private EquipmentSlotGroup(int var3, String var4, EquipmentSlot var5) {
      this(var3, var4, var1x -> var1x == var5);
   }

   public static EquipmentSlotGroup bySlot(EquipmentSlot var0) {
      return switch (var0) {
         case MAINHAND -> MAINHAND;
         case OFFHAND -> OFFHAND;
         case FEET -> FEET;
         case LEGS -> LEGS;
         case CHEST -> CHEST;
         case HEAD -> HEAD;
         case BODY -> BODY;
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   public String getSerializedName() {
      return this.key;
   }

   public boolean test(EquipmentSlot var1) {
      return this.predicate.test(var1);
   }
}
