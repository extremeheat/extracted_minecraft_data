package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
   private DyeColor baseColor;
   @Nullable
   private ListTag itemPatterns;
   private boolean receivedData;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> patterns;

   public BannerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BANNER, var1, var2);
      this.baseColor = ((AbstractBannerBlock)var2.getBlock()).getColor();
   }

   public BannerBlockEntity(BlockPos var1, BlockState var2, DyeColor var3) {
      this(var1, var2);
      this.baseColor = var3;
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

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

      this.itemPatterns = var1.getList("Patterns", 10);
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
         this.patterns = createPatterns(this.baseColor, this.itemPatterns);
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

   public ItemStack getItem() {
      ItemStack var1 = new ItemStack(BannerBlock.byColor(this.baseColor));
      if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
         var1.getOrCreateTagElement("BlockEntityTag").put("Patterns", this.itemPatterns.copy());
      }

      if (this.name != null) {
         var1.setHoverName(this.name);
      }

      return var1;
   }

   public DyeColor getBaseColor() {
      return this.baseColor;
   }
}
