package net.minecraft.world.item;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PoisonousPotatoPlantItem extends ArmorItem {
   private static final Style INSPECTION_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
   private static final int RUMBLED_CLICKS = 4;

   public PoisonousPotatoPlantItem(Holder<ArmorMaterial> var1, ArmorItem.Type var2, Item.Properties var3) {
      super(var1, var2, var3);
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      int var5 = var1.getOrDefault(DataComponents.CLICKS, Integer.valueOf(0));
      if (var5 >= 4) {
         var3.add(ComponentUtils.mergeStyles(Component.translatable("item.minecraft.poisonous_potato_plant.rumbled.line1"), INSPECTION_STYLE));
         var3.add(
            ComponentUtils.mergeStyles(
               Component.translatable("item.minecraft.poisonous_potato_plant.rumbled.line2", Component.translatable("item.minecraft.poisonous_potato")),
               INSPECTION_STYLE
            )
         );
      }
   }

   @Override
   public void onViewedInContainer(ItemStack var1, Level var2, BlockPos var3, Container var4) {
      List var5 = var4.getMatching(var0 -> var0.has(DataComponents.UNDERCOVER_ID));

      for(ItemStack var7 : var5) {
         if (var7.get(DataComponents.UNDERCOVER_ID) == 0) {
            var7.set(DataComponents.UNDERCOVER_ID, var2.getRandom().nextInt());
         }
      }

      int var18 = var1.get(DataComponents.UNDERCOVER_ID);
      var5.removeIf(var1x -> var1x.get(DataComponents.UNDERCOVER_ID) == var18);
      if (!var5.isEmpty()) {
         ItemStack var19 = (ItemStack)var5.get(var2.getRandom().nextInt(var5.size()));
         int var8 = var19.get(DataComponents.UNDERCOVER_ID);
         Int2IntMap var9 = var19.getOrDefault(DataComponents.CONTACTS_MESSAGES, new Int2IntOpenHashMap());
         Int2IntMap var10 = var1.getOrDefault(DataComponents.CONTACTS_MESSAGES, new Int2IntOpenHashMap());
         int var11 = var9.getOrDefault(var18, -1);
         int var12 = var10.getOrDefault(var8, -1);
         if (var11 > var12) {
            var10.put(var8, var11);
            var1.set(DataComponents.CONTACTS_MESSAGES, var10);
         } else {
            int var13 = var12 + 1;
            Optional var14 = writeSecretMessage(var13, var2.getNearestPlayer(var3, 4.0, false));
            if (!var14.isEmpty()) {
               var10.put(var8, var13);
               var1.set(DataComponents.CONTACTS_MESSAGES, var10);
               List var15 = var4.getMatching(var0 -> var0.is(Items.PAPER));
               var15.removeIf(var2x -> {
                  if (!var2x.has(DataComponents.SECRET_MESSAGE)) {
                     return true;
                  } else {
                     IntIntPair var3xx = var2x.get(DataComponents.SECRET_MESSAGE);
                     return var3xx.firstInt() != var18 && var3xx.firstInt() != var8;
                  }
               });
               ItemStack var16;
               if (var15.isEmpty()) {
                  var16 = new ItemStack(Items.PAPER);
                  int var17 = ContainerHelper.tryAddItem(var4, var16);
                  if (var17 < 0) {
                     return;
                  }
               } else {
                  var16 = (ItemStack)var15.get(var2.getRandom().nextInt(var15.size()));
               }

               var16.set(DataComponents.SECRET_MESSAGE, new IntIntImmutablePair(var8, var13));
               var16.set(DataComponents.CUSTOM_NAME, (MutableComponent)var14.get());
            }
         }
      }
   }

   private static Optional<MutableComponent> writeSecretMessage(int var0, @Nullable Player var1) {
      MutableComponent var2 = Component.translatable(
         "item.minecraft.paper.secret." + var0, Optionull.mapOrDefault(var1, Player::getDisplayName, Component.translatable("the.player"))
      );
      return var2.getString().startsWith("item.minecraft.paper.secret.") ? Optional.empty() : Optional.of(var2);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      int var5 = var4.getOrDefault(DataComponents.CLICKS, Integer.valueOf(0));
      return var5 >= 4 ? super.use(var1, var2, var3) : InteractionResultHolder.pass(var4);
   }
}
