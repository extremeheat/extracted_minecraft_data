package net.minecraft.world.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.UUIDLookup;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class EntityReference<StoredEntityType extends UniquelyIdentifyable> {
   private static final Codec<? extends EntityReference<?>> CODEC;
   private static final StreamCodec<ByteBuf, ? extends EntityReference<?>> STREAM_CODEC;
   private Either<UUID, StoredEntityType> entity;

   public static <Type extends UniquelyIdentifyable> Codec<EntityReference<Type>> codec() {
      return CODEC;
   }

   public static <Type extends UniquelyIdentifyable> StreamCodec<ByteBuf, EntityReference<Type>> streamCodec() {
      return STREAM_CODEC;
   }

   private EntityReference(StoredEntityType var1) {
      super();
      this.entity = Either.right(var1);
   }

   private EntityReference(UUID var1) {
      super();
      this.entity = Either.left(var1);
   }

   public static <T extends UniquelyIdentifyable> @Nullable EntityReference<T> of(@Nullable T var0) {
      return var0 != null ? new EntityReference(var0) : null;
   }

   public static <T extends UniquelyIdentifyable> EntityReference<T> of(UUID var0) {
      return new EntityReference<T>(var0);
   }

   public UUID getUUID() {
      return (UUID)this.entity.map((var0) -> var0, UniquelyIdentifyable::getUUID);
   }

   public @Nullable StoredEntityType getEntity(UUIDLookup<? extends UniquelyIdentifyable> var1, Class<StoredEntityType> var2) {
      Optional var3 = this.entity.right();
      if (var3.isPresent()) {
         UniquelyIdentifyable var4 = (UniquelyIdentifyable)var3.get();
         if (!var4.isRemoved()) {
            return (StoredEntityType)var4;
         }

         this.entity = Either.left(var4.getUUID());
      }

      Optional var6 = this.entity.left();
      if (var6.isPresent()) {
         UniquelyIdentifyable var5 = this.resolve(var1.lookup((UUID)var6.get()), var2);
         if (var5 != null && !var5.isRemoved()) {
            this.entity = Either.right(var5);
            return (StoredEntityType)var5;
         }
      }

      return null;
   }

   public @Nullable StoredEntityType getEntity(Level var1, Class<StoredEntityType> var2) {
      if (Player.class.isAssignableFrom(var2)) {
         Objects.requireNonNull(var1);
         return (StoredEntityType)this.getEntity(var1::getPlayerInAnyDimension, var2);
      } else {
         Objects.requireNonNull(var1);
         return (StoredEntityType)this.getEntity(var1::getEntityInAnyDimension, var2);
      }
   }

   private @Nullable StoredEntityType resolve(@Nullable UniquelyIdentifyable var1, Class<StoredEntityType> var2) {
      return (StoredEntityType)(var1 != null && var2.isAssignableFrom(var1.getClass()) ? (UniquelyIdentifyable)var2.cast(var1) : null);
   }

   public boolean matches(StoredEntityType var1) {
      return this.getUUID().equals(var1.getUUID());
   }

   public void store(ValueOutput var1, String var2) {
      var1.store(var2, UUIDUtil.CODEC, this.getUUID());
   }

   public static void store(@Nullable EntityReference<?> var0, ValueOutput var1, String var2) {
      if (var0 != null) {
         var0.store(var1, var2);
      }

   }

   public static <StoredEntityType extends UniquelyIdentifyable> @Nullable StoredEntityType get(@Nullable EntityReference<StoredEntityType> var0, Level var1, Class<StoredEntityType> var2) {
      return (StoredEntityType)(var0 != null ? var0.getEntity(var1, var2) : null);
   }

   public static @Nullable Entity getEntity(@Nullable EntityReference<Entity> var0, Level var1) {
      return (Entity)get(var0, var1, Entity.class);
   }

   public static @Nullable LivingEntity getLivingEntity(@Nullable EntityReference<LivingEntity> var0, Level var1) {
      return (LivingEntity)get(var0, var1, LivingEntity.class);
   }

   public static @Nullable Player getPlayer(@Nullable EntityReference<Player> var0, Level var1) {
      return (Player)get(var0, var1, Player.class);
   }

   public static <StoredEntityType extends UniquelyIdentifyable> @Nullable EntityReference<StoredEntityType> read(ValueInput var0, String var1) {
      return (EntityReference)var0.read(var1, codec()).orElse((Object)null);
   }

   public static <StoredEntityType extends UniquelyIdentifyable> @Nullable EntityReference<StoredEntityType> readWithOldOwnerConversion(ValueInput var0, String var1, Level var2) {
      Optional var3 = var0.read(var1, UUIDUtil.CODEC);
      return var3.isPresent() ? of((UUID)var3.get()) : (EntityReference)var0.getString(var1).map((var1x) -> OldUsersConverter.convertMobOwnerIfNecessary(var2.getServer(), var1x)).map(EntityReference::new).orElse((Object)null);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof EntityReference) {
            EntityReference var2 = (EntityReference)var1;
            if (this.getUUID().equals(var2.getUUID())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.getUUID().hashCode();
   }

   static {
      CODEC = UUIDUtil.CODEC.xmap(EntityReference::new, EntityReference::getUUID);
      STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityReference::new, EntityReference::getUUID);
   }
}
