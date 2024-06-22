package me.zxoir.picksurvivaltwo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

import static me.zxoir.picksurvivaltwo.util.Colors.colorize;

@SuppressWarnings({"unused"})
public class ItemStackBuilder {
    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(Material mat) {
        this.ITEM_STACK = new ItemStack(mat);
    }

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;

        if (ITEM_STACK.getItemMeta() == null) {
            ItemMeta im = ITEM_STACK.hasItemMeta() ? ITEM_STACK.getItemMeta() : Bukkit.getItemFactory().getItemMeta(ITEM_STACK.getType());
        }
    }

    public ItemStackBuilder withOwningPlayer(OfflinePlayer owningPlayer) {
        if (ITEM_STACK.getType() == Material.PLAYER_HEAD)
            return this;

        SkullMeta skullMeta = (SkullMeta) ITEM_STACK.getItemMeta();
        skullMeta.setOwningPlayer(owningPlayer);
        ITEM_STACK.setItemMeta(skullMeta);

        return this;
    }

    public ItemStackBuilder withCustomModelData(int model) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(model);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacySection().deserialize(colorize(name)));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(String name) {
        if (name == null) return this;
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<Component> lore = meta.lore();

        if (lore == null)
            lore = new ArrayList<>();

        lore.add(LegacyComponentSerializer.legacySection().deserialize(colorize(name)));
        meta.lore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(List<String> name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        List<Component> finalLore = lore;
        name.forEach(s -> finalLore.add(LegacyComponentSerializer.legacySection().deserialize(colorize(s))));
        meta.lore(finalLore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withDurability(int durability) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        ((Damageable) meta).setDamage(10);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.lore(new ArrayList<>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStackBuilder resetFlags() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ITEM_STACK.setItemMeta(meta);

        return this;
    }

    public ItemStack build() {
        return ITEM_STACK;
    }
}
