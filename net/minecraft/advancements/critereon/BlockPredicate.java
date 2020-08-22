package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate {
   public static final BlockPredicate ANY;
   @Nullable
   private final Tag tag;
   @Nullable
   private final Block block;
   private final StatePropertiesPredicate properties;
   private final NbtPredicate nbt;

   public BlockPredicate(@Nullable Tag var1, @Nullable Block var2, StatePropertiesPredicate var3, NbtPredicate var4) {
      this.tag = var1;
      this.block = var2;
      this.properties = var3;
      this.nbt = var4;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (this == ANY) {
         return true;
      } else if (!var1.isLoaded(var2)) {
         return false;
      } else {
         BlockState var3 = var1.getBlockState(var2);
         Block var4 = var3.getBlock();
         if (this.tag != null && !this.tag.contains(var4)) {
            return false;
         } else if (this.block != null && var4 != this.block) {
            return false;
         } else if (!this.properties.matches(var3)) {
            return false;
         } else {
            if (this.nbt != NbtPredicate.ANY) {
               BlockEntity var5 = var1.getBlockEntity(var2);
               if (var5 == null || !this.nbt.matches((net.minecraft.nbt.Tag)var5.save(new CompoundTag()))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static BlockPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "block");
         NbtPredicate var2 = NbtPredicate.fromJson(var1.get("nbt"));
         Block var3 = null;
         if (var1.has("block")) {
            ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "block"));
            var3 = (Block)Registry.BLOCK.get(var4);
         }

         Tag var7 = null;
         if (var1.has("tag")) {
            ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
            var7 = BlockTags.getAllTags().getTag(var5);
            if (var7 == null) {
               throw new JsonSyntaxException("Unknown block tag '" + var5 + "'");
            }
         }

         StatePropertiesPredicate var6 = StatePropertiesPredicate.fromJson(var1.get("state"));
         return new BlockPredicate(var7, var3, var6, var2);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         if (this.tag != null) {
            var1.addProperty("tag", this.tag.getId().toString());
         }

         var1.add("nbt", this.nbt.serializeToJson());
         var1.add("state", this.properties.serializeToJson());
         return var1;
      }
   }

   static {
      ANY = new BlockPredicate((Tag)null, (Block)null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);
   }

   public static class Builder {
      @Nullable
      private Block block;
      @Nullable
      private Tag blocks;
      private StatePropertiesPredicate properties;
      private NbtPredicate nbt;

      private Builder() {
         this.properties = StatePropertiesPredicate.ANY;
         this.nbt = NbtPredicate.ANY;
      }

      public static BlockPredicate.Builder block() {
         return new BlockPredicate.Builder();
      }

      public BlockPredicate.Builder of(Tag var1) {
         this.blocks = var1;
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.blocks, this.block, this.properties, this.nbt);
      }
   }
}
