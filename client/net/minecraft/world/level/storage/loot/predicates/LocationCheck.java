package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record LocationCheck(Optional<LocationPredicate> predicate, BlockPos offset) implements LootItemCondition {
   private static final MapCodec<BlockPos> OFFSET_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.INT.optionalFieldOf("offsetX", 0).forGetter(Vec3i::getX), Codec.INT.optionalFieldOf("offsetY", 0).forGetter(Vec3i::getY), Codec.INT.optionalFieldOf("offsetZ", 0).forGetter(Vec3i::getZ)).apply(var0, BlockPos::new));
   public static final MapCodec<LocationCheck> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LocationPredicate.CODEC.optionalFieldOf("predicate").forGetter(LocationCheck::predicate), OFFSET_CODEC.forGetter(LocationCheck::offset)).apply(var0, LocationCheck::new));

   public LocationCheck(Optional<LocationPredicate> var1, BlockPos var2) {
      super();
      this.predicate = var1;
      this.offset = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.LOCATION_CHECK;
   }

   public boolean test(LootContext var1) {
      Vec3 var2 = (Vec3)var1.getOptionalParameter(LootContextParams.ORIGIN);
      return var2 != null && (this.predicate.isEmpty() || ((LocationPredicate)this.predicate.get()).matches(var1.getLevel(), var2.x() + (double)this.offset.getX(), var2.y() + (double)this.offset.getY(), var2.z() + (double)this.offset.getZ()));
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ORIGIN);
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0) {
      return () -> new LocationCheck(Optional.of(var0.build()), BlockPos.ZERO);
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0, BlockPos var1) {
      return () -> new LocationCheck(Optional.of(var0.build()), var1);
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
