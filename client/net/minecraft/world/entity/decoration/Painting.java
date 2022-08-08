package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Painting extends HangingEntity {
   private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID;
   private static final ResourceKey<PaintingVariant> DEFAULT_VARIANT;

   private static Holder<PaintingVariant> getDefaultVariant() {
      return Registry.PAINTING_VARIANT.getHolderOrThrow(DEFAULT_VARIANT);
   }

   public Painting(EntityType<? extends Painting> var1, Level var2) {
      super(var1, var2);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_PAINTING_VARIANT_ID, getDefaultVariant());
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
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

   public static Optional<Painting> create(Level var0, BlockPos var1, Direction var2) {
      Painting var3 = new Painting(var0, var1);
      ArrayList var4 = new ArrayList();
      Iterable var10000 = Registry.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE);
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
            var4.removeIf((var1x) -> {
               return variantArea(var1x) < var5;
            });
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
      return ((PaintingVariant)var0.value()).getWidth() * ((PaintingVariant)var0.value()).getHeight();
   }

   private Painting(Level var1, BlockPos var2) {
      super(EntityType.PAINTING, var1, var2);
   }

   public Painting(Level var1, BlockPos var2, Direction var3, Holder<PaintingVariant> var4) {
      this(var1, var2);
      this.setVariant(var4);
      this.setDirection(var3);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putString("variant", ((ResourceKey)this.getVariant().unwrapKey().orElse(DEFAULT_VARIANT)).location().toString());
      var1.putByte("facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      ResourceKey var2 = ResourceKey.create(Registry.PAINTING_VARIANT_REGISTRY, ResourceLocation.tryParse(var1.getString("variant")));
      this.setVariant((Holder)Registry.PAINTING_VARIANT.getHolder(var2).orElseGet(Painting::getDefaultVariant));
      this.direction = Direction.from2DDataValue(var1.getByte("facing"));
      super.readAdditionalSaveData(var1);
      this.setDirection(this.direction);
   }

   public int getWidth() {
      return ((PaintingVariant)this.getVariant().value()).getWidth();
   }

   public int getHeight() {
      return ((PaintingVariant)this.getVariant().value()).getHeight();
   }

   public void dropItem(@Nullable Entity var1) {
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (var1 instanceof Player) {
            Player var2 = (Player)var1;
            if (var2.getAbilities().instabuild) {
               return;
            }
         }

         this.spawnAtLocation(Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPos(var1, var3, var5);
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPos(var1, var3, var5);
   }

   public Vec3 trackingPosition() {
      return Vec3.atLowerCornerOf(this.pos);
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDirection(Direction.from3DDataValue(var1.getData()));
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.PAINTING);
   }

   static {
      DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(Painting.class, EntityDataSerializers.PAINTING_VARIANT);
      DEFAULT_VARIANT = PaintingVariants.KEBAB;
   }
}
