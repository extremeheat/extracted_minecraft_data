package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(
   Holder<GameEvent> gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity
) {
   public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               GameEvent.CODEC.fieldOf("game_event").forGetter(VibrationInfo::gameEvent),
               Codec.floatRange(0.0F, 3.4028235E38F).fieldOf("distance").forGetter(VibrationInfo::distance),
               Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos),
               UUIDUtil.CODEC.lenientOptionalFieldOf("source").forGetter(var0x -> Optional.ofNullable(var0x.uuid())),
               UUIDUtil.CODEC.lenientOptionalFieldOf("projectile_owner").forGetter(var0x -> Optional.ofNullable(var0x.projectileOwnerUuid()))
            )
            .apply(var0, (var0x, var1, var2, var3, var4) -> new VibrationInfo(var0x, var1, var2, (UUID)var3.orElse(null), (UUID)var4.orElse(null)))
   );

   public VibrationInfo(Holder<GameEvent> var1, float var2, Vec3 var3, @Nullable UUID var4, @Nullable UUID var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   public VibrationInfo(Holder<GameEvent> var1, float var2, Vec3 var3, @Nullable Entity var4) {
      this(var1, var2, var3, var4 == null ? null : var4.getUUID(), getProjectileOwner(var4), var4);
   }

   public VibrationInfo(Holder<GameEvent> gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
      super();
      this.gameEvent = gameEvent;
      this.distance = distance;
      this.pos = pos;
      this.uuid = uuid;
      this.projectileOwnerUuid = projectileOwnerUuid;
      this.entity = entity;
   }

   @Nullable
   private static UUID getProjectileOwner(@Nullable Entity var0) {
      if (var0 instanceof Projectile var1 && var1.getOwner() != null) {
         return var1.getOwner().getUUID();
      }

      return null;
   }

   public Optional<Entity> getEntity(ServerLevel var1) {
      return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(var1::getEntity));
   }

   public Optional<Entity> getProjectileOwner(ServerLevel var1) {
      return this.getEntity(var1)
         .filter(var0 -> var0 instanceof Projectile)
         .map(var0 -> (Projectile)var0)
         .map(Projectile::getOwner)
         .or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(var1::getEntity));
   }
}
