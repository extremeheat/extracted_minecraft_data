package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Painting extends HangingEntity implements VariantHolder<Holder<PaintingVariant>> {
   private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(
      Painting.class, EntityDataSerializers.PAINTING_VARIANT
   );
   private static final ResourceKey<PaintingVariant> DEFAULT_VARIANT = PaintingVariants.KEBAB;
   public static final String VARIANT_TAG = "variant";

   private static Holder<PaintingVariant> getDefaultVariant() {
      return BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(DEFAULT_VARIANT);
   }

   public Painting(EntityType<? extends Painting> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(DATA_PAINTING_VARIANT_ID, getDefaultVariant());
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
      BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(var4::add);
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         var3.setDirection(var2);
         var4.removeIf(var1x -> {
            var3.setVariant(var1x);
            return !var3.survives();
         });
         if (var4.isEmpty()) {
            return Optional.empty();
         } else {
            int var5 = var4.stream().mapToInt(Painting::variantArea).max().orElse(0);
            var4.removeIf(var1x -> variantArea(var1x) < var5);
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
      return var0.value().getWidth() * var0.value().getHeight();
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
      storeVariant(var1, this.getVariant());
      var1.putByte("facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      Holder var2 = loadVariant(var1).orElseGet(Painting::getDefaultVariant);
      this.setVariant(var2);
      this.direction = Direction.from2DDataValue(var1.getByte("facing"));
      super.readAdditionalSaveData(var1);
      this.setDirection(this.direction);
   }

   public static void storeVariant(CompoundTag var0, Holder<PaintingVariant> var1) {
      var0.putString("variant", var1.unwrapKey().orElse(DEFAULT_VARIANT).location().toString());
   }

   public static Optional<Holder<PaintingVariant>> loadVariant(CompoundTag var0) {
      return Optional.ofNullable(ResourceLocation.tryParse(var0.getString("variant")))
         .map(var0x -> ResourceKey.create(Registries.PAINTING_VARIANT, var0x))
         .flatMap(BuiltInRegistries.PAINTING_VARIANT::getHolder);
   }

   @Override
   public int getWidth() {
      return this.getVariant().value().getWidth();
   }

   @Override
   public int getHeight() {
      return this.getVariant().value().getHeight();
   }

   @Override
   public void dropItem(@Nullable Entity var1) {
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (var1 instanceof Player var2 && var2.getAbilities().instabuild) {
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
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPos(var1, var3, var5);
   }

   @Override
   public Vec3 trackingPosition() {
      return Vec3.atLowerCornerOf(this.pos);
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
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
