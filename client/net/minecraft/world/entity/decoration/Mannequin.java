package net.minecraft.world.entity.decoration;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Mannequin extends Avatar {
   protected static final EntityDataAccessor<Either<MannequinProfile, ResolvableProfile>> DATA_PROFILE;
   private static final byte ALL_LAYERS;
   private static final Codec<Byte> LAYERS_CODEC;
   public static final MannequinProfile DEFAULT_PROFILE;
   protected static EntityType.EntityFactory<Mannequin> constructor;
   private static final String PROFILE_FIELD = "profile";
   private static final String HIDDEN_LAYERS_FIELD = "hidden_layers";
   private static final String MAIN_HAND_FIELD = "main_hand";

   public Mannequin(EntityType<Mannequin> var1, Level var2) {
      super(var1, var2);
      this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ALL_LAYERS);
   }

   protected Mannequin(Level var1) {
      this(EntityType.MANNEQUIN, var1);
   }

   @Nullable
   public static Mannequin create(EntityType<Mannequin> var0, Level var1) {
      return constructor.create(var0, var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PROFILE, Either.left(DEFAULT_PROFILE));
   }

   protected Either<MannequinProfile, ResolvableProfile> getProfile() {
      return (Either)this.entityData.get(DATA_PROFILE);
   }

   private void setProfile(Either<MannequinProfile, ResolvableProfile> var1) {
      this.entityData.set(DATA_PROFILE, var1);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("profile", MannequinProfile.PLAYER_OR_MANNEQUIN_CODEC, this.getProfile());
      var1.store("hidden_layers", LAYERS_CODEC, (Byte)this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION));
      var1.store("main_hand", HumanoidArm.CODEC, this.getMainArm());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      var1.read("profile", MannequinProfile.PLAYER_OR_MANNEQUIN_CODEC).ifPresent(this::setProfile);
      var1.read("hidden_layers", LAYERS_CODEC).ifPresent((var1x) -> this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, var1x));
      var1.read("main_hand", HumanoidArm.CODEC).ifPresent(this::setMainArm);
   }

   static {
      DATA_PROFILE = SynchedEntityData.<Either<MannequinProfile, ResolvableProfile>>defineId(Mannequin.class, EntityDataSerializers.MANNEQUIN_PROFILE);
      ALL_LAYERS = (byte)Arrays.stream(PlayerModelPart.values()).mapToInt(PlayerModelPart::getMask).reduce(0, (var0, var1) -> var0 | var1);
      LAYERS_CODEC = PlayerModelPart.CODEC.listOf().xmap((var0) -> (byte)var0.stream().mapToInt(PlayerModelPart::getMask).reduce(ALL_LAYERS, (var0x, var1) -> var0x & ~var1), (var0) -> Arrays.stream(PlayerModelPart.values()).filter((var1) -> (var0 & var1.getMask()) == 0).toList());
      DEFAULT_PROFILE = new MannequinProfile(ResourceLocation.withDefaultNamespace("entity/player/wide/steve"), Optional.empty(), Optional.empty(), PlayerModelType.WIDE);
      constructor = Mannequin::new;
   }
}
