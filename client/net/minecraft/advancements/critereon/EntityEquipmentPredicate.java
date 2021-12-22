package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY;
   public static final EntityEquipmentPredicate CAPTAIN;
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainhand;
   private final ItemPredicate offhand;

   public EntityEquipmentPredicate(ItemPredicate var1, ItemPredicate var2, ItemPredicate var3, ItemPredicate var4, ItemPredicate var5, ItemPredicate var6) {
      super();
      this.head = var1;
      this.chest = var2;
      this.legs = var3;
      this.feet = var4;
      this.mainhand = var5;
      this.offhand = var6;
   }

   public boolean matches(@Nullable Entity var1) {
      if (this == ANY) {
         return true;
      } else if (!(var1 instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity var2 = (LivingEntity)var1;
         if (!this.head.matches(var2.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
         } else if (!this.chest.matches(var2.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
         } else if (!this.legs.matches(var2.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
         } else if (!this.feet.matches(var2.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
         } else if (!this.mainhand.matches(var2.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return false;
         } else {
            return this.offhand.matches(var2.getItemBySlot(EquipmentSlot.OFFHAND));
         }
      }
   }

   public static EntityEquipmentPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "equipment");
         ItemPredicate var2 = ItemPredicate.fromJson(var1.get("head"));
         ItemPredicate var3 = ItemPredicate.fromJson(var1.get("chest"));
         ItemPredicate var4 = ItemPredicate.fromJson(var1.get("legs"));
         ItemPredicate var5 = ItemPredicate.fromJson(var1.get("feet"));
         ItemPredicate var6 = ItemPredicate.fromJson(var1.get("mainhand"));
         ItemPredicate var7 = ItemPredicate.fromJson(var1.get("offhand"));
         return new EntityEquipmentPredicate(var2, var3, var4, var5, var6, var7);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("head", this.head.serializeToJson());
         var1.add("chest", this.chest.serializeToJson());
         var1.add("legs", this.legs.serializeToJson());
         var1.add("feet", this.feet.serializeToJson());
         var1.add("mainhand", this.mainhand.serializeToJson());
         var1.add("offhand", this.offhand.serializeToJson());
         return var1;
      }
   }

   static {
      ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
      CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().method_90(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   }

   public static class Builder {
      private ItemPredicate head;
      private ItemPredicate chest;
      private ItemPredicate legs;
      private ItemPredicate feet;
      private ItemPredicate mainhand;
      private ItemPredicate offhand;

      public Builder() {
         super();
         this.head = ItemPredicate.ANY;
         this.chest = ItemPredicate.ANY;
         this.legs = ItemPredicate.ANY;
         this.feet = ItemPredicate.ANY;
         this.mainhand = ItemPredicate.ANY;
         this.offhand = ItemPredicate.ANY;
      }

      public static EntityEquipmentPredicate.Builder equipment() {
         return new EntityEquipmentPredicate.Builder();
      }

      public EntityEquipmentPredicate.Builder head(ItemPredicate var1) {
         this.head = var1;
         return this;
      }

      public EntityEquipmentPredicate.Builder chest(ItemPredicate var1) {
         this.chest = var1;
         return this;
      }

      public EntityEquipmentPredicate.Builder legs(ItemPredicate var1) {
         this.legs = var1;
         return this;
      }

      public EntityEquipmentPredicate.Builder feet(ItemPredicate var1) {
         this.feet = var1;
         return this;
      }

      public EntityEquipmentPredicate.Builder mainhand(ItemPredicate var1) {
         this.mainhand = var1;
         return this;
      }

      public EntityEquipmentPredicate.Builder offhand(ItemPredicate var1) {
         this.offhand = var1;
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
      }
   }
}
