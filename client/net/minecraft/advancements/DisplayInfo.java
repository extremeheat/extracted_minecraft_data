package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   private final Component title;
   private final Component description;
   private final ItemStack icon;
   @Nullable
   private final ResourceLocation background;
   private final FrameType frame;
   private final boolean showToast;
   private final boolean announceChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(
      ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8
   ) {
      super();
      this.title = var2;
      this.description = var3;
      this.icon = var1;
      this.background = var4;
      this.frame = var5;
      this.showToast = var6;
      this.announceChat = var7;
      this.hidden = var8;
   }

   public void setLocation(float var1, float var2) {
      this.x = var1;
      this.y = var2;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   @Nullable
   public ResourceLocation getBackground() {
      return this.background;
   }

   public FrameType getFrame() {
      return this.frame;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceChat() {
      return this.announceChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public static DisplayInfo fromJson(JsonObject var0) {
      MutableComponent var1 = Component.Serializer.fromJson(var0.get("title"));
      MutableComponent var2 = Component.Serializer.fromJson(var0.get("description"));
      if (var1 != null && var2 != null) {
         ItemStack var3 = getIcon(GsonHelper.getAsJsonObject(var0, "icon"));
         ResourceLocation var4 = var0.has("background") ? new ResourceLocation(GsonHelper.getAsString(var0, "background")) : null;
         FrameType var5 = var0.has("frame") ? FrameType.byName(GsonHelper.getAsString(var0, "frame")) : FrameType.TASK;
         boolean var6 = GsonHelper.getAsBoolean(var0, "show_toast", true);
         boolean var7 = GsonHelper.getAsBoolean(var0, "announce_to_chat", true);
         boolean var8 = GsonHelper.getAsBoolean(var0, "hidden", false);
         return new DisplayInfo(var3, var1, var2, var4, var5, var6, var7, var8);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack getIcon(JsonObject var0) {
      if (!var0.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
      } else {
         Item var1 = GsonHelper.getAsItem(var0, "item");
         if (var0.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack var2 = new ItemStack(var1);
            if (var0.has("nbt")) {
               try {
                  CompoundTag var3 = TagParser.parseTag(GsonHelper.convertToString(var0.get("nbt"), "nbt"));
                  var2.setTag(var3);
               } catch (CommandSyntaxException var4) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
               }
            }

            return var2;
         }
      }
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeComponent(this.title);
      var1.writeComponent(this.description);
      var1.writeItem(this.icon);
      var1.writeEnum(this.frame);
      int var2 = 0;
      if (this.background != null) {
         var2 |= 1;
      }

      if (this.showToast) {
         var2 |= 2;
      }

      if (this.hidden) {
         var2 |= 4;
      }

      var1.writeInt(var2);
      if (this.background != null) {
         var1.writeResourceLocation(this.background);
      }

      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
   }

   public static DisplayInfo fromNetwork(FriendlyByteBuf var0) {
      Component var1 = var0.readComponent();
      Component var2 = var0.readComponent();
      ItemStack var3 = var0.readItem();
      FrameType var4 = var0.readEnum(FrameType.class);
      int var5 = var0.readInt();
      ResourceLocation var6 = (var5 & 1) != 0 ? var0.readResourceLocation() : null;
      boolean var7 = (var5 & 2) != 0;
      boolean var8 = (var5 & 4) != 0;
      DisplayInfo var9 = new DisplayInfo(var3, var1, var2, var6, var4, var7, false, var8);
      var9.setLocation(var0.readFloat(), var0.readFloat());
      return var9;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.add("icon", this.serializeIcon());
      var1.add("title", Component.Serializer.toJsonTree(this.title));
      var1.add("description", Component.Serializer.toJsonTree(this.description));
      var1.addProperty("frame", this.frame.getName());
      var1.addProperty("show_toast", this.showToast);
      var1.addProperty("announce_to_chat", this.announceChat);
      var1.addProperty("hidden", this.hidden);
      if (this.background != null) {
         var1.addProperty("background", this.background.toString());
      }

      return var1;
   }

   private JsonObject serializeIcon() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
      if (this.icon.hasTag()) {
         var1.addProperty("nbt", this.icon.getTag().toString());
      }

      return var1;
   }
}
