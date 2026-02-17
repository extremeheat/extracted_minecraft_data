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

   public Mannequin(final EntityType<Mannequin> type, final Level level) {
      super(type, level);
      this.description = DEFAULT_DESCRIPTION;
      this.hideDescription = false;
      this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ALL_LAYERS);
   }

   protected Mannequin(final Level level) {
      this(EntityType.MANNEQUIN, level);
   }

   public static @Nullable Mannequin create(final EntityType<Mannequin> type, final Level level) {
      return constructor.create(type, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_PROFILE, DEFAULT_PROFILE);
      entityData.define(DATA_IMMOVABLE, false);
      entityData.define(DATA_DESCRIPTION, Optional.of(DEFAULT_DESCRIPTION));
   }

   public ResolvableProfile getProfile() {
      return (ResolvableProfile)this.entityData.get(DATA_PROFILE);
   }

   private void setProfile(final ResolvableProfile profile) {
      this.entityData.set(DATA_PROFILE, profile);
   }

   private boolean getImmovable() {
      return (Boolean)this.entityData.get(DATA_IMMOVABLE);
   }

   private void setImmovable(final boolean immovable) {
      this.entityData.set(DATA_IMMOVABLE, immovable);
   }

   protected @Nullable Component getDescription() {
      return (Component)((Optional)this.entityData.get(DATA_DESCRIPTION)).orElse((Object)null);
   }

   private void setDescription(final Component description) {
      this.description = description;
      this.updateDescription();
   }

   private void setHideDescription(final boolean hideDescription) {
      this.hideDescription = hideDescription;
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

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("profile", ResolvableProfile.CODEC, this.getProfile());
      output.store("hidden_layers", LAYERS_CODEC, (Byte)this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION));
      output.store("main_hand", HumanoidArm.CODEC, this.getMainArm());
      output.store("pose", POSE_CODEC, this.getPose());
      output.putBoolean("immovable", this.getImmovable());
      Component description = this.getDescription();
      if (description != null) {
         if (!description.equals(DEFAULT_DESCRIPTION)) {
            output.store("description", ComponentSerialization.CODEC, description);
         }
      } else {
         output.putBoolean("hide_description", true);
      }

   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      input.read("profile", ResolvableProfile.CODEC).ifPresent(this::setProfile);
      this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (Byte)input.read("hidden_layers", LAYERS_CODEC).orElse(ALL_LAYERS));
      this.setMainArm((HumanoidArm)input.read("main_hand", HumanoidArm.CODEC).orElse(DEFAULT_MAIN_HAND));
      this.setPose((Pose)input.read("pose", POSE_CODEC).orElse(Pose.STANDING));
      this.setImmovable(input.getBooleanOr("immovable", false));
      this.setHideDescription(input.getBooleanOr("hide_description", false));
      this.setDescription((Component)input.read("description", ComponentSerialization.CODEC).orElse(DEFAULT_DESCRIPTION));
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.PROFILE ? castComponentValue(type, this.getProfile()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.PROFILE);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.PROFILE) {
         this.setProfile((ResolvableProfile)castComponentValue(DataComponents.PROFILE, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public void aiStep() {
      super.aiStep();
      this.updateSwingTime();
   }

   static {
      DATA_PROFILE = SynchedEntityData.<ResolvableProfile>defineId(Mannequin.class, EntityDataSerializers.RESOLVABLE_PROFILE);
      DATA_IMMOVABLE = SynchedEntityData.<Boolean>defineId(Mannequin.class, EntityDataSerializers.BOOLEAN);
      DATA_DESCRIPTION = SynchedEntityData.<Optional<Component>>defineId(Mannequin.class, EntityDataSerializers.OPTIONAL_COMPONENT);
      ALL_LAYERS = (byte)Arrays.stream(PlayerModelPart.values()).mapToInt(PlayerModelPart::getMask).reduce(0, (a, b) -> a | b);
      VALID_POSES = Set.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING, Pose.FALL_FLYING, Pose.SLEEPING);
      POSE_CODEC = Pose.CODEC.validate((pose) -> VALID_POSES.contains(pose) ? DataResult.success(pose) : DataResult.error(() -> "Invalid pose: " + pose.getSerializedName()));
      LAYERS_CODEC = PlayerModelPart.CODEC.listOf().xmap((list) -> (byte)list.stream().mapToInt(PlayerModelPart::getMask).reduce(ALL_LAYERS, (a, b) -> a & ~b), (mask) -> Arrays.stream(PlayerModelPart.values()).filter((part) -> (mask & part.getMask()) == 0).toList());
      DEFAULT_PROFILE = ResolvableProfile.Static.EMPTY;
      DEFAULT_DESCRIPTION = Component.translatable("entity.minecraft.mannequin.label");
      constructor = Mannequin::new;
   }
}
