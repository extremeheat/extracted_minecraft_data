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
   ANY(0, "any", (var0) -> {
      return true;
   }),
   MAINHAND(1, "mainhand", EquipmentSlot.MAINHAND),
   OFFHAND(2, "offhand", EquipmentSlot.OFFHAND),
   HAND(3, "hand", (var0) -> {
      return var0.getType() == EquipmentSlot.Type.HAND;
   }),
   FEET(4, "feet", EquipmentSlot.FEET),
   LEGS(5, "legs", EquipmentSlot.LEGS),
   CHEST(6, "chest", EquipmentSlot.CHEST),
   HEAD(7, "head", EquipmentSlot.HEAD),
   ARMOR(8, "armor", EquipmentSlot::isArmor),
   BODY(9, "body", EquipmentSlot.BODY);

   public static final IntFunction<EquipmentSlotGroup> BY_ID = ByIdMap.continuous((var0) -> {
      return var0.id;
   }, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec<EquipmentSlotGroup> CODEC = StringRepresentable.fromEnum(EquipmentSlotGroup::values);
   public static final StreamCodec<ByteBuf, EquipmentSlotGroup> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> {
      return var0.id;
   });
   private final int id;
   private final String key;
   private final Predicate<EquipmentSlot> predicate;

   private EquipmentSlotGroup(int var3, String var4, Predicate var5) {
      this.id = var3;
      this.key = var4;
      this.predicate = var5;
   }

   private EquipmentSlotGroup(int var3, String var4, EquipmentSlot var5) {
      this(var3, var4, (var1x) -> {
         return var1x == var5;
      });
   }

   public static EquipmentSlotGroup bySlot(EquipmentSlot var0) {
      EquipmentSlotGroup var10000;
      switch (var0) {
         case MAINHAND -> var10000 = MAINHAND;
         case OFFHAND -> var10000 = OFFHAND;
         case FEET -> var10000 = FEET;
         case LEGS -> var10000 = LEGS;
         case CHEST -> var10000 = CHEST;
         case HEAD -> var10000 = HEAD;
         case BODY -> var10000 = BODY;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String getSerializedName() {
      return this.key;
   }

   public boolean test(EquipmentSlot var1) {
      return this.predicate.test(var1);
   }

   // $FF: synthetic method
   private static EquipmentSlotGroup[] $values() {
      return new EquipmentSlotGroup[]{ANY, MAINHAND, OFFHAND, HAND, FEET, LEGS, CHEST, HEAD, ARMOR, BODY};
   }
}
