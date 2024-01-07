package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
   private final Function<Entity, Optional<V>> getter;
   private final EntitySubPredicate.Type type;

   public static <V> EntityVariantPredicate<V> create(Registry<V> var0, Function<Entity, Optional<V>> var1) {
      return new EntityVariantPredicate<>(var0.byNameCodec(), var1);
   }

   public static <V> EntityVariantPredicate<V> create(Codec<V> var0, Function<Entity, Optional<V>> var1) {
      return new EntityVariantPredicate<>(var0, var1);
   }

   private EntityVariantPredicate(Codec<V> var1, Function<Entity, Optional<V>> var2) {
      super();
      this.getter = var2;
      MapCodec var3 = RecordCodecBuilder.mapCodec(
         var2x -> var2x.group(var1.fieldOf("variant").forGetter(EntityVariantPredicate.SubPredicate::variant)).apply(var2x, this::createPredicate)
      );
      this.type = new EntitySubPredicate.Type(var3);
   }

   public EntitySubPredicate.Type type() {
      return this.type;
   }

   public EntityVariantPredicate.SubPredicate<V> createPredicate(V var1) {
      return new EntityVariantPredicate.SubPredicate<>(this.type, this.getter, var1);
   }

   public static record SubPredicate<V>(EntitySubPredicate.Type b, Function<Entity, Optional<V>> c, V d) implements EntitySubPredicate {
      private final EntitySubPredicate.Type type;
      private final Function<Entity, Optional<V>> getter;
      private final V variant;

      public SubPredicate(EntitySubPredicate.Type var1, Function<Entity, Optional<V>> var2, V var3) {
         super();
         this.type = var1;
         this.getter = var2;
         this.variant = (V)var3;
      }

      @Override
      public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
         return this.getter.apply(var1).filter(var1x -> var1x.equals(this.variant)).isPresent();
      }
   }
}
