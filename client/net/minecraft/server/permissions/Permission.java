package net.minecraft.server.permissions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public interface Permission {
   Codec<Permission> FULL_CODEC = BuiltInRegistries.PERMISSION_TYPE.byNameCodec().dispatch(Permission::codec, (c) -> c);
   Codec<Permission> CODEC = Codec.either(FULL_CODEC, Identifier.CODEC).xmap((e) -> (Permission)e.map((permission) -> permission, Atom::create), (permission) -> {
      Either var10000;
      if (permission instanceof Atom atom) {
         var10000 = Either.right(atom.id());
      } else {
         var10000 = Either.left(permission);
      }

      return var10000;
   });

   MapCodec<? extends Permission> codec();

   public static record Atom(Identifier id) implements Permission {
      public static final MapCodec<Atom> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(Atom::id)).apply(i, Atom::new));

      public Atom {
         super();
      }

      public MapCodec<Atom> codec() {
         return MAP_CODEC;
      }

      public static Atom create(final String name) {
         return create(Identifier.withDefaultNamespace(name));
      }

      public static Atom create(final Identifier id) {
         return new Atom(id);
      }
   }

   public static record HasCommandLevel(PermissionLevel level) implements Permission {
      public static final MapCodec<HasCommandLevel> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PermissionLevel.CODEC.fieldOf("level").forGetter(HasCommandLevel::level)).apply(i, HasCommandLevel::new));

      public HasCommandLevel {
         super();
      }

      public MapCodec<HasCommandLevel> codec() {
         return MAP_CODEC;
      }
   }
}
