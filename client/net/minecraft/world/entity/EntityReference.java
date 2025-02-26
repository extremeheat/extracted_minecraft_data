package net.minecraft.world.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.UUIDLookup;
import net.minecraft.world.level.entity.UniquelyIdentifyable;

public class EntityReference<StoredEntityType extends UniquelyIdentifyable> {
   private static final Codec<? extends EntityReference<?>> CODEC;
   private static final StreamCodec<ByteBuf, ? extends EntityReference<?>> STREAM_CODEC;
   private Either<UUID, StoredEntityType> entity;

   public static <Type extends UniquelyIdentifyable> Codec<EntityReference<Type>> codec() {
      return CODEC;
   }

   public static <Type extends UniquelyIdentifyable> StreamCodec<ByteBuf, EntityReference<Type>> streamCodec() {
      return STREAM_CODEC;
   }

   public EntityReference(StoredEntityType var1) {
      super();
      this.entity = Either.right(var1);
   }

   public EntityReference(UUID var1) {
      super();
      this.entity = Either.left(var1);
   }

   public UUID getUUID() {
      return (UUID)this.entity.map((var0) -> var0, UniquelyIdentifyable::getUUID);
   }

   @Nullable
   public StoredEntityType getEntity(UUIDLookup<? super StoredEntityType> var1, Class<StoredEntityType> var2) {
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
         UniquelyIdentifyable var5 = this.resolve(var1.getEntity((UUID)var6.get()), var2);
         if (var5 != null && !var5.isRemoved()) {
            this.entity = Either.right(var5);
            return (StoredEntityType)var5;
         }
      }

      return null;
   }

   @Nullable
   private StoredEntityType resolve(@Nullable UniquelyIdentifyable var1, Class<StoredEntityType> var2) {
      return (StoredEntityType)(var1 != null && var2.isAssignableFrom(var1.getClass()) ? (UniquelyIdentifyable)var2.cast(var1) : null);
   }

   public boolean matches(StoredEntityType var1) {
      return this.getUUID().equals(var1.getUUID());
   }

   public void store(CompoundTag var1, String var2) {
      var1.store(var2, UUIDUtil.CODEC, this.getUUID());
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> StoredEntityType get(@Nullable EntityReference<StoredEntityType> var0, UUIDLookup<? super StoredEntityType> var1, Class<StoredEntityType> var2) {
      return (StoredEntityType)(var0 != null ? var0.getEntity(var1, var2) : null);
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> EntityReference<StoredEntityType> read(CompoundTag var0, String var1) {
      return (EntityReference)var0.read(var1, codec()).orElse((Object)null);
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> EntityReference<StoredEntityType> readWithOldOwnerConversion(CompoundTag var0, String var1, Level var2) {
      Optional var3 = var0.read(var1, UUIDUtil.CODEC);
      return var3.isPresent() ? new EntityReference((UUID)var3.get()) : (EntityReference)var0.getString(var1).map((var1x) -> OldUsersConverter.convertMobOwnerIfNecessary(var2.getServer(), var1x)).map(EntityReference::new).orElse((Object)null);
   }

   static {
      CODEC = UUIDUtil.CODEC.xmap(EntityReference::new, EntityReference::getUUID);
      STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityReference::new, EntityReference::getUUID);
   }
}
