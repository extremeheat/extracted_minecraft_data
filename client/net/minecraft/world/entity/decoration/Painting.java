package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Painting extends HangingEntity implements VariantHolder<Holder<PaintingVariant>> {
   private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(
      Painting.class, EntityDataSerializers.PAINTING_VARIANT
   );
   public static final MapCodec<Holder<PaintingVariant>> VARIANT_MAP_CODEC = PaintingVariant.CODEC.fieldOf("variant");
   public static final Codec<Holder<PaintingVariant>> VARIANT_CODEC = VARIANT_MAP_CODEC.codec();
   public static final float DEPTH = 0.0625F;

   public Painting(EntityType<? extends Painting> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_PAINTING_VARIANT_ID, this.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getAny().orElseThrow());
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_PAINTING_VARIANT_ID.equals(var1)) {
         this.recalculateBoundingBox();
      }
   }

   public void setVariant(Holder<PaintingVariant> var1) {
      this.entityData.set(DATA_PAINTING_VARIANT_ID, var1);
   }

   public Holder<PaintingVariant> getVariant() {
      return this.entityData.get(DATA_PAINTING_VARIANT_ID);
   }

   public static Optional<Painting> create(Level var0, BlockPos var1, Direction var2) {
      Painting var3 = new Painting(var0, var1);
      ArrayList var4 = new ArrayList();
      var0.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(var4::add);
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         var3.setDirection(var2);
         var4.removeIf(var1x -> {
            var3.setVariant((Holder<PaintingVariant>)var1x);
            return !var3.survives();
         });
         if (var4.isEmpty()) {
            return Optional.empty();
         } else {
            int var5 = var4.stream().mapToInt(Painting::variantArea).max().orElse(0);
            var4.removeIf(var1x -> variantArea((Holder<PaintingVariant>)var1x) < var5);
            Optional var6 = Util.getRandomSafe(var4, var3.random);
            if (var6.isEmpty()) {
               return Optional.empty();
            } else {
               var3.setVariant((Holder<PaintingVariant>)var6.get());
               var3.setDirection(var2);
               return Optional.of(var3);
            }
         }
      }
   }

   private static int variantArea(Holder<PaintingVariant> var0) {
      return var0.value().area();
   }

   private Painting(Level var1, BlockPos var2) {
      super(EntityType.PAINTING, var1, var2);
   }

   public Painting(Level var1, BlockPos var2, Direction var3, Holder<PaintingVariant> var4) {
      this(var1, var2);
      this.setVariant(var4);
      this.setDirection(var3);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      VARIANT_CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.getVariant())
         .ifSuccess(var1x -> var1.merge((CompoundTag)var1x));
      var1.putByte("facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      VARIANT_CODEC.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1).ifSuccess(this::setVariant);
      this.direction = Direction.from2DDataValue(var1.getByte("facing"));
      super.readAdditionalSaveData(var1);
      this.setDirection(this.direction);
   }

   @Override
   protected AABB calculateBoundingBox(BlockPos var1, Direction var2) {
      float var3 = 0.46875F;
      Vec3 var4 = Vec3.atCenterOf(var1).relative(var2, -0.46875);
      PaintingVariant var5 = this.getVariant().value();
      double var6 = this.offsetForPaintingSize(var5.width());
      double var8 = this.offsetForPaintingSize(var5.height());
      Direction var10 = var2.getCounterClockWise();
      Vec3 var11 = var4.relative(var10, var6).relative(Direction.UP, var8);
      Direction.Axis var12 = var2.getAxis();
      double var13 = var12 == Direction.Axis.X ? 0.0625 : (double)var5.width();
      double var15 = (double)var5.height();
      double var17 = var12 == Direction.Axis.Z ? 0.0625 : (double)var5.width();
      return AABB.ofSize(var11, var13, var15, var17);
   }

   private double offsetForPaintingSize(int var1) {
      return var1 % 2 == 0 ? 0.5 : 0.0;
   }

   @Override
   public void dropItem(@Nullable Entity var1) {
      if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (var1 instanceof Player var2 && var2.hasInfiniteMaterials()) {
            return;
         }

         this.spawnAtLocation(Items.PAINTING);
      }
   }

   @Override
   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   @Override
   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPos(var1, var3, var5);
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.setPos(var1, var3, var5);
   }

   @Override
   public Vec3 trackingPosition() {
      return Vec3.atLowerCornerOf(this.pos);
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDirection(Direction.from3DDataValue(var1.getData()));
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.PAINTING);
   }
}
