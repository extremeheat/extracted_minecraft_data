package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public enum HumanoidArm implements OptionEnum, StringRepresentable {
   LEFT(0, "left", "options.mainHand.left"),
   RIGHT(1, "right", "options.mainHand.right");

   public static final Codec<HumanoidArm> CODEC = StringRepresentable.fromEnum(HumanoidArm::values);
   public static final IntFunction<HumanoidArm> BY_ID = ByIdMap.continuous(HumanoidArm::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   private final int id;
   private final String name;
   private final String translationKey;

   private HumanoidArm(final int var3, final String var4, final String var5) {
      this.id = var3;
      this.name = var4;
      this.translationKey = var5;
   }

   public HumanoidArm getOpposite() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.translationKey;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static HumanoidArm[] $values() {
      return new HumanoidArm[]{LEFT, RIGHT};
   }
}
