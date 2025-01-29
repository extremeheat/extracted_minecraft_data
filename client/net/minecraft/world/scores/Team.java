package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public abstract class Team {
   public Team() {
      super();
   }

   public boolean isAlliedTo(@Nullable Team var1) {
      if (var1 == null) {
         return false;
      } else {
         return this == var1;
      }
   }

   public abstract String getName();

   public abstract MutableComponent getFormattedName(Component var1);

   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   public abstract Visibility getNameTagVisibility();

   public abstract ChatFormatting getColor();

   public abstract Collection<String> getPlayers();

   public abstract Visibility getDeathMessageVisibility();

   public abstract CollisionRule getCollisionRule();

   public static enum Visibility implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public static final Codec<Visibility> CODEC = StringRepresentable.<Visibility>fromEnum(Visibility::values);
      private static final IntFunction<Visibility> BY_ID = ByIdMap.<Visibility>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Visibility> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
      public final String name;
      public final int id;

      private Visibility(final String var3, final int var4) {
         this.name = var3;
         this.id = var4;
      }

      public Component getDisplayName() {
         return Component.translatable("team.visibility." + this.name);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Visibility[] $values() {
         return new Visibility[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
      }
   }

   public static enum CollisionRule implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      public static final Codec<CollisionRule> CODEC = StringRepresentable.<CollisionRule>fromEnum(CollisionRule::values);
      private static final IntFunction<CollisionRule> BY_ID = ByIdMap.<CollisionRule>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, CollisionRule> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
      public final String name;
      public final int id;

      private CollisionRule(final String var3, final int var4) {
         this.name = var3;
         this.id = var4;
      }

      public Component getDisplayName() {
         return Component.translatable("team.collision." + this.name);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static CollisionRule[] $values() {
         return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
      }
   }
}
