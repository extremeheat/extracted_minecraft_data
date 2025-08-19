package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SkullBlockEntity extends BlockEntity {
   private static final String TAG_PROFILE = "profile";
   private static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
   private static final String TAG_CUSTOM_NAME = "custom_name";
   @Nullable
   private ResolvableProfile owner;
   @Nullable
   private ResourceLocation noteBlockSound;
   private int animationTickCount;
   private boolean isAnimating;
   @Nullable
   private Component customName;

   public SkullBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SKULL, var1, var2);
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      var1.storeNullable("profile", ResolvableProfile.CODEC, this.owner);
      var1.storeNullable("note_block_sound", ResourceLocation.CODEC, this.noteBlockSound);
      var1.storeNullable("custom_name", ComponentSerialization.CODEC, this.customName);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.owner = (ResolvableProfile)var1.read("profile", ResolvableProfile.CODEC).orElse((Object)null);
      this.noteBlockSound = (ResourceLocation)var1.read("note_block_sound", ResourceLocation.CODEC).orElse((Object)null);
      this.customName = parseCustomNameSafe(var1, "custom_name");
   }

   public static void animation(Level var0, BlockPos var1, BlockState var2, SkullBlockEntity var3) {
      if (var2.hasProperty(SkullBlock.POWERED) && (Boolean)var2.getValue(SkullBlock.POWERED)) {
         var3.isAnimating = true;
         ++var3.animationTickCount;
      } else {
         var3.isAnimating = false;
      }

   }

   public float getAnimation(float var1) {
      return this.isAnimating ? (float)this.animationTickCount + var1 : (float)this.animationTickCount;
   }

   @Nullable
   public ResolvableProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ResourceLocation getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      this.owner = (ResolvableProfile)var1.get(DataComponents.PROFILE);
      this.noteBlockSound = (ResourceLocation)var1.get(DataComponents.NOTE_BLOCK_SOUND);
      this.customName = (Component)var1.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.PROFILE, this.owner);
      var1.set(DataComponents.NOTE_BLOCK_SOUND, this.noteBlockSound);
      var1.set(DataComponents.CUSTOM_NAME, this.customName);
   }

   public void removeComponentsFromTag(ValueOutput var1) {
      super.removeComponentsFromTag(var1);
      var1.discard("profile");
      var1.discard("note_block_sound");
      var1.discard("custom_name");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
