package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Painting extends HangingEntity {
   private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID;
   public static final float DEPTH = 0.0625F;

   public Painting(EntityType<? extends Painting> var1, Level var2) {
      super(var1, var2);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PAINTING_VARIANT_ID, VariantUtils.getAny(this.registryAccess(), Registries.PAINTING_VARIANT));
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_PAINTING_VARIANT_ID.equals(var1)) {
         this.recalculateBoundingBox();
      }

   }

   private void setVariant(Holder<PaintingVariant> var1) {
      this.entityData.set(DATA_PAINTING_VARIANT_ID, var1);
   }

   public Holder<PaintingVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_PAINTING_VARIANT_ID);
   }

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.PAINTING_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.PAINTING_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.PAINTING_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.PAINTING_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public static Optional<Painting> create(Level var0, BlockPos var1, Direction var2) {
      Painting var3 = new Painting(var0, var1);
      ArrayList var4 = new ArrayList();
      Iterable var10000 = var0.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE);
      Objects.requireNonNull(var4);
      var10000.forEach(var4::add);
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         var3.setDirection(var2);
         var4.removeIf((var1x) -> {
            var3.setVariant(var1x);
            return !var3.survives();
         });
         if (var4.isEmpty()) {
            return Optional.empty();
         } else {
            int var5 = var4.stream().mapToInt(Painting::variantArea).max().orElse(0);
            var4.removeIf((var1x) -> variantArea(var1x) < var5);
            Optional var6 = Util.getRandomSafe(var4, var3.random);
            if (var6.isEmpty()) {
               return Optional.empty();
            } else {
               var3.setVariant((Holder)var6.get());
               var3.setDirection(var2);
               return Optional.of(var3);
            }
         }
      }
   }

   private static int variantArea(Holder<PaintingVariant> var0) {
      return ((PaintingVariant)var0.value()).area();
   }

   private Painting(Level var1, BlockPos var2) {
      super(EntityType.PAINTING, var1, var2);
   }

   public Painting(Level var1, BlockPos var2, Direction var3, Holder<PaintingVariant> var4) {
      this(var1, var2);
      this.setVariant(var4);
      this.setDirection(var3);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      var1.store("facing", Direction.LEGACY_ID_CODEC_2D, this.getDirection());
      super.addAdditionalSaveData(var1);
      VariantUtils.writeVariant(var1, this.getVariant());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      Direction var2 = (Direction)var1.read("facing", Direction.LEGACY_ID_CODEC_2D).orElse(Direction.SOUTH);
      super.readAdditionalSaveData(var1);
      this.setDirection(var2);
      VariantUtils.readVariant(var1, Registries.PAINTING_VARIANT).ifPresent(this::setVariant);
   }

   protected AABB calculateBoundingBox(BlockPos var1, Direction var2) {
      float var3 = 0.46875F;
      Vec3 var4 = Vec3.atCenterOf(var1).relative(var2, -0.46875);
      PaintingVariant var5 = (PaintingVariant)this.getVariant().value();
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

   public void dropItem(ServerLevel var1, @Nullable Entity var2) {
      if ((Boolean)var1.getGameRules().get(GameRules.ENTITY_DROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (var2 instanceof Player) {
            Player var3 = (Player)var2;
            if (var3.hasInfiniteMaterials()) {
               return;
            }
         }

         this.spawnAtLocation(var1, Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void snapTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPos(var1, var3, var5);
   }

   public Vec3 trackingPosition() {
      return Vec3.atLowerCornerOf(this.pos);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDirection(Direction.from3DDataValue(var1.getData()));
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.PAINTING);
   }

   static {
      DATA_PAINTING_VARIANT_ID = SynchedEntityData.<Holder<PaintingVariant>>defineId(Painting.class, EntityDataSerializers.PAINTING_VARIANT);
   }
}
