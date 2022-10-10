package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;

public class DisplayInfo {
   private final ITextComponent field_192300_a;
   private final ITextComponent field_193225_b;
   private final ItemStack field_192301_b;
   private final ResourceLocation field_192302_c;
   private final FrameType field_192303_d;
   private final boolean field_193226_f;
   private final boolean field_193227_g;
   private final boolean field_193228_h;
   private float field_192304_e;
   private float field_192305_f;

   public DisplayInfo(ItemStack var1, ITextComponent var2, ITextComponent var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8) {
      super();
      this.field_192300_a = var2;
      this.field_193225_b = var3;
      this.field_192301_b = var1;
      this.field_192302_c = var4;
      this.field_192303_d = var5;
      this.field_193226_f = var6;
      this.field_193227_g = var7;
      this.field_193228_h = var8;
   }

   public void func_192292_a(float var1, float var2) {
      this.field_192304_e = var1;
      this.field_192305_f = var2;
   }

   public ITextComponent func_192297_a() {
      return this.field_192300_a;
   }

   public ITextComponent func_193222_b() {
      return this.field_193225_b;
   }

   public ItemStack func_192298_b() {
      return this.field_192301_b;
   }

   @Nullable
   public ResourceLocation func_192293_c() {
      return this.field_192302_c;
   }

   public FrameType func_192291_d() {
      return this.field_192303_d;
   }

   public float func_192299_e() {
      return this.field_192304_e;
   }

   public float func_192296_f() {
      return this.field_192305_f;
   }

   public boolean func_193223_h() {
      return this.field_193226_f;
   }

   public boolean func_193220_i() {
      return this.field_193227_g;
   }

   public boolean func_193224_j() {
      return this.field_193228_h;
   }

   public static DisplayInfo func_192294_a(JsonObject var0, JsonDeserializationContext var1) {
      ITextComponent var2 = (ITextComponent)JsonUtils.func_188174_a(var0, "title", var1, ITextComponent.class);
      ITextComponent var3 = (ITextComponent)JsonUtils.func_188174_a(var0, "description", var1, ITextComponent.class);
      if (var2 != null && var3 != null) {
         ItemStack var4 = func_193221_a(JsonUtils.func_152754_s(var0, "icon"));
         ResourceLocation var5 = var0.has("background") ? new ResourceLocation(JsonUtils.func_151200_h(var0, "background")) : null;
         FrameType var6 = var0.has("frame") ? FrameType.func_192308_a(JsonUtils.func_151200_h(var0, "frame")) : FrameType.TASK;
         boolean var7 = JsonUtils.func_151209_a(var0, "show_toast", true);
         boolean var8 = JsonUtils.func_151209_a(var0, "announce_to_chat", true);
         boolean var9 = JsonUtils.func_151209_a(var0, "hidden", false);
         return new DisplayInfo(var4, var2, var3, var5, var6, var7, var8, var9);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack func_193221_a(JsonObject var0) {
      if (!var0.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
      } else {
         Item var1 = JsonUtils.func_188180_i(var0, "item");
         if (var0.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack var2 = new ItemStack(var1);
            if (var0.has("nbt")) {
               try {
                  NBTTagCompound var3 = JsonToNBT.func_180713_a(JsonUtils.func_151206_a(var0.get("nbt"), "nbt"));
                  var2.func_77982_d(var3);
               } catch (CommandSyntaxException var4) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
               }
            }

            return var2;
         }
      }
   }

   public void func_192290_a(PacketBuffer var1) {
      var1.func_179256_a(this.field_192300_a);
      var1.func_179256_a(this.field_193225_b);
      var1.func_150788_a(this.field_192301_b);
      var1.func_179249_a(this.field_192303_d);
      int var2 = 0;
      if (this.field_192302_c != null) {
         var2 |= 1;
      }

      if (this.field_193226_f) {
         var2 |= 2;
      }

      if (this.field_193228_h) {
         var2 |= 4;
      }

      var1.writeInt(var2);
      if (this.field_192302_c != null) {
         var1.func_192572_a(this.field_192302_c);
      }

      var1.writeFloat(this.field_192304_e);
      var1.writeFloat(this.field_192305_f);
   }

   public static DisplayInfo func_192295_b(PacketBuffer var0) {
      ITextComponent var1 = var0.func_179258_d();
      ITextComponent var2 = var0.func_179258_d();
      ItemStack var3 = var0.func_150791_c();
      FrameType var4 = (FrameType)var0.func_179257_a(FrameType.class);
      int var5 = var0.readInt();
      ResourceLocation var6 = (var5 & 1) != 0 ? var0.func_192575_l() : null;
      boolean var7 = (var5 & 2) != 0;
      boolean var8 = (var5 & 4) != 0;
      DisplayInfo var9 = new DisplayInfo(var3, var1, var2, var6, var4, var7, false, var8);
      var9.func_192292_a(var0.readFloat(), var0.readFloat());
      return var9;
   }

   public JsonElement func_200290_k() {
      JsonObject var1 = new JsonObject();
      var1.add("icon", this.func_200289_l());
      var1.add("title", ITextComponent.Serializer.func_200528_b(this.field_192300_a));
      var1.add("description", ITextComponent.Serializer.func_200528_b(this.field_193225_b));
      var1.addProperty("frame", this.field_192303_d.func_192307_a());
      var1.addProperty("show_toast", this.field_193226_f);
      var1.addProperty("announce_to_chat", this.field_193227_g);
      var1.addProperty("hidden", this.field_193228_h);
      if (this.field_192302_c != null) {
         var1.addProperty("background", this.field_192302_c.toString());
      }

      return var1;
   }

   private JsonObject func_200289_l() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.field_192301_b.func_77973_b()).toString());
      return var1;
   }
}
