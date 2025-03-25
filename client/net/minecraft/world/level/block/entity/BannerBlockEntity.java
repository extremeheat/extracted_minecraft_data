package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerBlockEntity extends BlockEntity implements Nameable {
   public static final int MAX_PATTERNS = 6;
   private static final String TAG_PATTERNS = "patterns";
   @Nullable
   private Component name;
   private final DyeColor baseColor;
   private BannerPatternLayers patterns;

   public BannerBlockEntity(BlockPos var1, BlockState var2) {
      this(var1, var2, ((AbstractBannerBlock)var2.getBlock()).getColor());
   }

   public BannerBlockEntity(BlockPos var1, BlockState var2, DyeColor var3) {
      super(BlockEntityType.BANNER, var1, var2);
      this.patterns = BannerPatternLayers.EMPTY;
      this.baseColor = var3;
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : Component.translatable("block.minecraft.banner"));
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      if (!this.patterns.equals(BannerPatternLayers.EMPTY)) {
         var1.store("patterns", BannerPatternLayers.CODEC, var3, this.patterns);
      }

      var1.storeNullable("CustomName", ComponentSerialization.CODEC, var3, this.name);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.name = parseCustomNameSafe(var1.get("CustomName"), var2);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      this.patterns = (BannerPatternLayers)var1.read("patterns", BannerPatternLayers.CODEC, var3).orElse(BannerPatternLayers.EMPTY);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveWithoutMetadata(var1);
   }

   public BannerPatternLayers getPatterns() {
      return this.patterns;
   }

   public ItemStack getItem() {
      ItemStack var1 = new ItemStack(BannerBlock.byColor(this.baseColor));
      var1.applyComponents(this.collectComponents());
      return var1;
   }

   public DyeColor getBaseColor() {
      return this.baseColor;
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      this.patterns = (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      this.name = (Component)var1.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.BANNER_PATTERNS, this.patterns);
      var1.set(DataComponents.CUSTOM_NAME, this.name);
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      var1.remove("patterns");
      var1.remove("CustomName");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
