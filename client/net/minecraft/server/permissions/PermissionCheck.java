package net.minecraft.server.permissions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;

public interface PermissionCheck {
   Codec<PermissionCheck> CODEC = BuiltInRegistries.PERMISSION_CHECK_TYPE.byNameCodec().dispatch(PermissionCheck::codec, (var0) -> var0);

   boolean check(PermissionSet var1);

   MapCodec<? extends PermissionCheck> codec();

   public static class AlwaysPass implements PermissionCheck {
      public static final AlwaysPass INSTANCE = new AlwaysPass();
      public static final MapCodec<AlwaysPass> MAP_CODEC;

      private AlwaysPass() {
         super();
      }

      public boolean check(PermissionSet var1) {
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
      public static final MapCodec<Require> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Permission.CODEC.fieldOf("permission").forGetter(Require::permission)).apply(var0, Require::new));

      public Require(Permission var1) {
         super();
         this.permission = var1;
      }

      public MapCodec<Require> codec() {
         return MAP_CODEC;
      }

      public boolean check(PermissionSet var1) {
         return var1.hasPermission(this.permission);
      }
   }
}
