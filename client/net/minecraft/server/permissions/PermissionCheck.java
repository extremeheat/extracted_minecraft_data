package net.minecraft.server.permissions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;

public interface PermissionCheck {
   Codec<PermissionCheck> CODEC = BuiltInRegistries.PERMISSION_CHECK_TYPE.byNameCodec().dispatch(PermissionCheck::codec, (c) -> c);

   boolean check(PermissionSet source);

   MapCodec<? extends PermissionCheck> codec();

   public static class AlwaysPass implements PermissionCheck {
      public static final AlwaysPass INSTANCE = new AlwaysPass();
      public static final MapCodec<AlwaysPass> MAP_CODEC;

      private AlwaysPass() {
         super();
      }

      public boolean check(final PermissionSet source) {
         return true;
      }

      public MapCodec<AlwaysPass> codec() {
         return MAP_CODEC;
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
      }
   }

   public static record Require(Permission permission) implements PermissionCheck {
      public static final MapCodec<Require> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Permission.CODEC.fieldOf("permission").forGetter(Require::permission)).apply(i, Require::new));

      public Require {
         super();
      }

      public MapCodec<Require> codec() {
         return MAP_CODEC;
      }

      public boolean check(final PermissionSet source) {
         return source.hasPermission(this.permission);
      }
   }
}
