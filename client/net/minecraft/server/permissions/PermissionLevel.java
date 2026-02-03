package net.minecraft.server.permissions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum PermissionLevel implements StringRepresentable {
   ALL("all", 0),
   MODERATORS("moderators", 1),
   GAMEMASTERS("gamemasters", 2),
   ADMINS("admins", 3),
   OWNERS("owners", 4);

   public static final Codec<PermissionLevel> CODEC = StringRepresentable.<PermissionLevel>fromEnum(PermissionLevel::values);
   private static final IntFunction<PermissionLevel> BY_ID = ByIdMap.<PermissionLevel>continuous((level) -> level.id, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
   public static final Codec<PermissionLevel> INT_CODEC;
   private final String name;
   private final int id;

   private PermissionLevel(final String name, final int id) {
      this.name = name;
      this.id = id;
   }

   public boolean isEqualOrHigherThan(final PermissionLevel other) {
      return this.id >= other.id;
   }

   public static PermissionLevel byId(final int level) {
      return (PermissionLevel)BY_ID.apply(level);
   }

   public int id() {
      return this.id;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static PermissionLevel[] $values() {
      return new PermissionLevel[]{ALL, MODERATORS, GAMEMASTERS, ADMINS, OWNERS};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      IntFunction var10001 = BY_ID;
      Objects.requireNonNull(var10001);
      INT_CODEC = var10000.xmap(var10001::apply, (level) -> level.id);
   }
}
