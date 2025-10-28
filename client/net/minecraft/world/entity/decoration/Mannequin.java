package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Mannequin extends Avatar {
   protected static final EntityDataAccessor<ResolvableProfile> DATA_PROFILE;
   private static final EntityDataAccessor<Boolean> DATA_IMMOVABLE;
   private static final EntityDataAccessor<Optional<Component>> DATA_DESCRIPTION;
   private static final byte ALL_LAYERS;
   private static final Set<Pose> VALID_POSES;
   public static final Codec<Pose> POSE_CODEC;
   private static final Codec<Byte> LAYERS_CODEC;
   public static final ResolvableProfile DEFAULT_PROFILE;
   private static final Component DEFAULT_DESCRIPTION;
   protected static EntityType.EntityFactory<Mannequin> constructor;
   private static final String PROFILE_FIELD = "profile";
   private static final String HIDDEN_LAYERS_FIELD = "hidden_layers";
   private static final String MAIN_HAND_FIELD = "main_hand";
   private static final String POSE_FIELD = "pose";
   private static final String IMMOVABLE_FIELD = "immovable";
   private static final String DESCRIPTION_FIELD = "description";
   private static final String HIDE_DESCRIPTION_FIELD = "hide_description";
   private Component description;
   private boolean hideDescription;

   public Mannequin(EntityType<Mannequin> var1, Level var2) {
      super(var1, var2);
      this.description = DEFAULT_DESCRIPTION;
      this.hideDescription = false;
      this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ALL_LAYERS);
   }

   protected Mannequin(Level var1) {
      this(EntityType.MANNEQUIN, var1);
   }

   public static @Nullable Mannequin create(EntityType<Mannequin> var0, Level var1) {
      return constructor.create(var0, var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PROFILE, DEFAULT_PROFILE);
      var1.define(DATA_IMMOVABLE, false);
      var1.define(DATA_DESCRIPTION, Optional.of(DEFAULT_DESCRIPTION));
   }

   protected ResolvableProfile getProfile() {
      return (ResolvableProfile)this.entityData.get(DATA_PROFILE);
   }

   private void setProfile(ResolvableProfile var1) {
      this.entityData.set(DATA_PROFILE, var1);
   }

   private boolean getImmovable() {
      return (Boolean)this.entityData.get(DATA_IMMOVABLE);
   }

   private void setImmovable(boolean var1) {
      this.entityData.set(DATA_IMMOVABLE, var1);
   }

   protected @Nullable Component getDescription() {
      return (Component)((Optional)this.entityData.get(DATA_DESCRIPTION)).orElse((Object)null);
   }

   private void setDescription(Component var1) {
      this.description = var1;
      this.updateDescription();
   }

   private void setHideDescription(boolean var1) {
      this.hideDescription = var1;
      this.updateDescription();
   }

   private void updateDescription() {
      this.entityData.set(DATA_DESCRIPTION, this.hideDescription ? Optional.empty() : Optional.of(this.description));
   }

   protected boolean isImmobile() {
      return this.getImmovable() || super.isImmobile();
   }

   public boolean isEffectiveAi() {
      return !this.getImmovable() && super.isEffectiveAi();
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("profile", ResolvableProfile.CODEC, this.getProfile());
      var1.store("hidden_layers", LAYERS_CODEC, (Byte)this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION));
      var1.store("main_hand", HumanoidArm.CODEC, this.getMainArm());
      var1.store("pose", POSE_CODEC, this.getPose());
      var1.putBoolean("immovable", this.getImmovable());
      Component var2 = this.getDescription();
      if (var2 != null) {
         if (!var2.equals(DEFAULT_DESCRIPTION)) {
            var1.store("description", ComponentSerialization.CODEC, var2);
         }
      } else {
         var1.putBoolean("hide_description", true);
      }

   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      var1.read("profile", ResolvableProfile.CODEC).ifPresent(this::setProfile);
      this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (Byte)var1.read("hidden_layers", LAYERS_CODEC).orElse(ALL_LAYERS));
      this.setMainArm((HumanoidArm)var1.read("main_hand", HumanoidArm.CODEC).orElse(DEFAULT_MAIN_HAND));
      this.setPose((Pose)var1.read("pose", POSE_CODEC).orElse(Pose.STANDING));
      this.setImmovable(var1.getBooleanOr("immovable", false));
      this.setHideDescription(var1.getBooleanOr("hide_description", false));
      this.setDescription((Component)var1.read("description", ComponentSerialization.CODEC).orElse(DEFAULT_DESCRIPTION));
   }

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.PROFILE ? castComponentValue(var1, this.getProfile()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.PROFILE);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.PROFILE) {
         this.setProfile((ResolvableProfile)castComponentValue(DataComponents.PROFILE, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   static {
      DATA_PROFILE = SynchedEntityData.<ResolvableProfile>defineId(Mannequin.class, EntityDataSerializers.RESOLVABLE_PROFILE);
      DATA_IMMOVABLE = SynchedEntityData.<Boolean>defineId(Mannequin.class, EntityDataSerializers.BOOLEAN);
      DATA_DESCRIPTION = SynchedEntityData.<Optional<Component>>defineId(Mannequin.class, EntityDataSerializers.OPTIONAL_COMPONENT);
      ALL_LAYERS = (byte)Arrays.stream(PlayerModelPart.values()).mapToInt(PlayerModelPart::getMask).reduce(0, (var0, var1) -> var0 | var1);
      VALID_POSES = Set.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING, Pose.FALL_FLYING, Pose.SLEEPING);
      POSE_CODEC = Pose.CODEC.validate((var0) -> VALID_POSES.contains(var0) ? DataResult.success(var0) : DataResult.error(() -> "Invalid pose: " + var0.getSerializedName()));
      LAYERS_CODEC = PlayerModelPart.CODEC.listOf().xmap((var0) -> (byte)var0.stream().mapToInt(PlayerModelPart::getMask).reduce(ALL_LAYERS, (var0x, var1) -> var0x & ~var1), (var0) -> Arrays.stream(PlayerModelPart.values()).filter((var1) -> (var0 & var1.getMask()) == 0).toList());
      DEFAULT_PROFILE = ResolvableProfile.Static.EMPTY;
      DEFAULT_DESCRIPTION = Component.translatable("entity.minecraft.mannequin.label");
      constructor = Mannequin::new;
   }
}
