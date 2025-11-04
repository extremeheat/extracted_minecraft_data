package net.minecraft.server.permissions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public interface Permission {
   Codec<Permission> FULL_CODEC = BuiltInRegistries.PERMISSION_TYPE.byNameCodec().dispatch(Permission::codec, (var0) -> var0);
   Codec<Permission> CODEC = Codec.either(FULL_CODEC, Identifier.CODEC).xmap((var0) -> (Permission)var0.map((var0x) -> var0x, Atom::create), (var0) -> {
      Either var10000;
      if (var0 instanceof Atom var1) {
         var10000 = Either.right(var1.id());
      } else {
         var10000 = Either.left(var0);
      }

      return var10000;
   });

   MapCodec<? extends Permission> codec();

   public static record Atom(Identifier id) implements Permission {
      public static final MapCodec<Atom> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("id").forGetter(Atom::id)).apply(var0, Atom::new));

      public Atom(Identifier var1) {
         super();
         this.id = var1;
      }

      public MapCodec<Atom> codec() {
         return MAP_CODEC;
      }

      public static Atom create(String var0) {
         return create(Identifier.withDefaultNamespace(var0));
      }

      public static Atom create(Identifier var0) {
         return new Atom(var0);
      }
   }

   public static record HasCommandLevel(PermissionLevel level) implements Permission {
      public static final MapCodec<HasCommandLevel> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PermissionLevel.CODEC.fieldOf("level").forGetter(HasCommandLevel::level)).apply(var0, HasCommandLevel::new));

      public HasCommandLevel(PermissionLevel var1) {
         super();
         this.level = var1;
      }

      public MapCodec<HasCommandLevel> codec() {
         return MAP_CODEC;
      }
   }
}
