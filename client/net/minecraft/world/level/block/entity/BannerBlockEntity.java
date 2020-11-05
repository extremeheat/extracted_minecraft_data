package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerBlockEntity extends BlockEntity implements Nameable {
   @Nullable
   private Component name;
   @Nullable
   private DyeColor baseColor;
   @Nullable
   private ListTag itemPatterns;
   private boolean receivedData;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> patterns;

   public BannerBlockEntity() {
      super(BlockEntityType.BANNER);
      this.baseColor = DyeColor.WHITE;
   }

   public BannerBlockEntity(DyeColor var1) {
      this();
      this.baseColor = var1;
   }

   @Nullable
   public static ListTag getItemPatterns(ItemStack var0) {
      ListTag var1 = null;
      CompoundTag var2 = var0.getTagElement("BlockEntityTag");
      if (var2 != null && var2.contains("Patterns", 9)) {
         var1 = var2.getList("Patterns", 10).copy();
      }

      return var1;
   }

   public void fromItem(ItemStack var1, DyeColor var2) {
      this.itemPatterns = getItemPatterns(var1);
      this.baseColor = var2;
      this.patterns = null;
      this.receivedData = true;
      this.name = var1.hasCustomHoverName() ? var1.getHoverName() : null;
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : new TranslatableComponent("block.minecraft.banner"));
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   public void setCustomName(Component var1) {
      this.name = var1;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (this.itemPatterns != null) {
         var1.put("Patterns", this.itemPatterns);
      }

      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return var1;
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      if (var2.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var2.getString("CustomName"));
      }

      if (this.hasLevel()) {
         this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.itemPatterns = var2.getList("Patterns", 10);
      this.patterns = null;
      this.receivedData = true;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 6, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public static int getPatternCount(ItemStack var0) {
      CompoundTag var1 = var0.getTagElement("BlockEntityTag");
      return var1 != null && var1.contains("Patterns") ? var1.getList("Patterns", 10).size() : 0;
   }

   public List<Pair<BannerPattern, DyeColor>> getPatterns() {
      if (this.patterns == null && this.receivedData) {
         this.patterns = createPatterns(this.getBaseColor(this::getBlockState), this.itemPatterns);
      }

      return this.patterns;
   }

   public static List<Pair<BannerPattern, DyeColor>> createPatterns(DyeColor var0, @Nullable ListTag var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(Pair.of(BannerPattern.BASE, var0));
      if (var1 != null) {
         for(int var3 = 0; var3 < var1.size(); ++var3) {
            CompoundTag var4 = var1.getCompound(var3);
            BannerPattern var5 = BannerPattern.byHash(var4.getString("Pattern"));
            if (var5 != null) {
               int var6 = var4.getInt("Color");
               var2.add(Pair.of(var5, DyeColor.byId(var6)));
            }
         }
      }

      return var2;
   }

   public static void removeLastPattern(ItemStack var0) {
      CompoundTag var1 = var0.getTagElement("BlockEntityTag");
      if (var1 != null && var1.contains("Patterns", 9)) {
         ListTag var2 = var1.getList("Patterns", 10);
         if (!var2.isEmpty()) {
            var2.remove(var2.size() - 1);
            if (var2.isEmpty()) {
               var0.removeTagKey("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack getItem(BlockState var1) {
      ItemStack var2 = new ItemStack(BannerBlock.byColor(this.getBaseColor(() -> {
         return var1;
      })));
      if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
         var2.getOrCreateTagElement("BlockEntityTag").put("Patterns", this.itemPatterns.copy());
      }

      if (this.name != null) {
         var2.setHoverName(this.name);
      }

      return var2;
   }

   public DyeColor getBaseColor(Supplier<BlockState> var1) {
      if (this.baseColor == null) {
         this.baseColor = ((AbstractBannerBlock)((BlockState)var1.get()).getBlock()).getColor();
      }

      return this.baseColor;
   }
}
