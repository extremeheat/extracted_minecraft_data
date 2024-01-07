package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(GameEvent b, float c, Vec3 d, @Nullable UUID e, @Nullable UUID f, @Nullable Entity g) {
   private final GameEvent gameEvent;
   private final float distance;
   private final Vec3 pos;
   @Nullable
   private final UUID uuid;
   @Nullable
   private final UUID projectileOwnerUuid;
   @Nullable
   private final Entity entity;
   public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BuiltInRegistries.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationInfo::gameEvent),
               Codec.floatRange(0.0F, 3.4028235E38F).fieldOf("distance").forGetter(VibrationInfo::distance),
               Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos),
               UUIDUtil.CODEC.optionalFieldOf("source").forGetter(var0x -> Optional.ofNullable(var0x.uuid())),
               UUIDUtil.CODEC.optionalFieldOf("projectile_owner").forGetter(var0x -> Optional.ofNullable(var0x.projectileOwnerUuid()))
            )
            .apply(var0, (var0x, var1, var2, var3, var4) -> new VibrationInfo(var0x, var1, var2, (UUID)var3.orElse(null), (UUID)var4.orElse(null)))
   );

   public VibrationInfo(GameEvent var1, float var2, Vec3 var3, @Nullable UUID var4, @Nullable UUID var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   public VibrationInfo(GameEvent var1, float var2, Vec3 var3, @Nullable Entity var4) {
      this(var1, var2, var3, var4 == null ? null : var4.getUUID(), getProjectileOwner(var4), var4);
   }

   public VibrationInfo(GameEvent var1, float var2, Vec3 var3, @Nullable UUID var4, @Nullable UUID var5, @Nullable Entity var6) {
      super();
      this.gameEvent = var1;
      this.distance = var2;
      this.pos = var3;
      this.uuid = var4;
      this.projectileOwnerUuid = var5;
      this.entity = var6;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
