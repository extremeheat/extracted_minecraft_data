package net.minecraft.world.level;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Abilities;
import org.jetbrains.annotations.Contract;

public enum GameType implements StringRepresentable {
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   public static final GameType DEFAULT_MODE = SURVIVAL;
   public static final StringRepresentable.EnumCodec<GameType> CODEC = StringRepresentable.fromEnum(GameType::values);
   private static final IntFunction<GameType> BY_ID = ByIdMap.continuous(GameType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   private static final int NOT_SET = -1;
   private final int id;
   private final String name;
   private final Component shortName;
   private final Component longName;

   private GameType(final int param3, final String param4) {
      this.id = nullxx;
      this.name = nullxxx;
      this.shortName = Component.translatable("selectWorld.gameMode." + nullxxx);
      this.longName = Component.translatable("gameMode." + nullxxx);
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public Component getLongDisplayName() {
      return this.longName;
   }

   public Component getShortDisplayName() {
      return this.shortName;
   }

   public void updatePlayerAbilities(Abilities var1) {
      if (this == CREATIVE) {
         var1.mayfly = true;
         var1.instabuild = true;
         var1.invulnerable = true;
      } else if (this == SPECTATOR) {
         var1.mayfly = true;
         var1.instabuild = false;
         var1.invulnerable = true;
         var1.flying = true;
      } else {
         var1.mayfly = false;
         var1.instabuild = false;
         var1.invulnerable = false;
         var1.flying = false;
      }

      var1.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int var0) {
      return BY_ID.apply(var0);
   }

   public static GameType byName(String var0) {
      return byName(var0, SURVIVAL);
   }

   @Nullable
   @Contract("_,!null->!null;_,null->_")
   public static GameType byName(String var0, @Nullable GameType var1) {
      GameType var2 = CODEC.byName(var0);
      return var2 != null ? var2 : var1;
   }

   public static int getNullableId(@Nullable GameType var0) {
      return var0 != null ? var0.id : -1;
   }

   @Nullable
   public static GameType byNullableId(int var0) {
      return var0 == -1 ? null : byId(var0);
   }
}
