package net.minecraft.world.inventory;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotRanges {
   private static final List<SlotRange> SLOTS = (List)Util.make(new ArrayList(), (var0) -> {
      addSingleSlot(var0, "contents", 0);
      addSlotRange(var0, "container.", 0, 54);
      addSlotRange(var0, "hotbar.", 0, 9);
      addSlotRange(var0, "inventory.", 9, 27);
      addSlotRange(var0, "enderchest.", 200, 27);
      addSlotRange(var0, "villager.", 300, 8);
      addSlotRange(var0, "horse.", 500, 15);
      int var1 = EquipmentSlot.MAINHAND.getIndex(98);
      int var2 = EquipmentSlot.OFFHAND.getIndex(98);
      addSingleSlot(var0, "weapon", var1);
      addSingleSlot(var0, "weapon.mainhand", var1);
      addSingleSlot(var0, "weapon.offhand", var2);
      addSlots(var0, "weapon.*", var1, var2);
      var1 = EquipmentSlot.HEAD.getIndex(100);
      var2 = EquipmentSlot.CHEST.getIndex(100);
      int var3 = EquipmentSlot.LEGS.getIndex(100);
      int var4 = EquipmentSlot.FEET.getIndex(100);
      int var5 = EquipmentSlot.BODY.getIndex(105);
      addSingleSlot(var0, "armor.head", var1);
      addSingleSlot(var0, "armor.chest", var2);
      addSingleSlot(var0, "armor.legs", var3);
      addSingleSlot(var0, "armor.feet", var4);
      addSingleSlot(var0, "armor.body", var5);
      addSlots(var0, "armor.*", var1, var2, var3, var4, var5);
      addSingleSlot(var0, "horse.saddle", 400);
      addSingleSlot(var0, "horse.chest", 499);
      addSingleSlot(var0, "player.cursor", 499);
      addSlotRange(var0, "player.crafting.", 500, 4);
   });
   public static final Codec<SlotRange> CODEC = StringRepresentable.fromValues(() -> {
      return (SlotRange[])SLOTS.toArray(new SlotRange[0]);
   });
   private static final Function<String, SlotRange> NAME_LOOKUP;

   public SlotRanges() {
      super();
   }

   private static SlotRange create(String var0, int var1) {
      return SlotRange.of(var0, IntLists.singleton(var1));
   }

   private static SlotRange create(String var0, IntList var1) {
      return SlotRange.of(var0, IntLists.unmodifiable(var1));
   }

   private static SlotRange create(String var0, int... var1) {
      return SlotRange.of(var0, IntList.of(var1));
   }

   private static void addSingleSlot(List<SlotRange> var0, String var1, int var2) {
      var0.add(create(var1, var2));
   }

   private static void addSlotRange(List<SlotRange> var0, String var1, int var2, int var3) {
      IntArrayList var4 = new IntArrayList(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = var2 + var5;
         var0.add(create(var1 + var5, var6));
         var4.add(var6);
      }

      var0.add(create(var1 + "*", (IntList)var4));
   }

   private static void addSlots(List<SlotRange> var0, String var1, int... var2) {
      var0.add(create(var1, var2));
   }

   @Nullable
   public static SlotRange nameToIds(String var0) {
      return (SlotRange)NAME_LOOKUP.apply(var0);
   }

   public static Stream<String> allNames() {
      return SLOTS.stream().map(StringRepresentable::getSerializedName);
   }

   public static Stream<String> singleSlotNames() {
      return SLOTS.stream().filter((var0) -> {
         return var0.size() == 1;
      }).map(StringRepresentable::getSerializedName);
   }

   static {
      NAME_LOOKUP = StringRepresentable.createNameLookup((SlotRange[])SLOTS.toArray(new SlotRange[0]), (var0) -> {
         return var0;
      });
   }
}
