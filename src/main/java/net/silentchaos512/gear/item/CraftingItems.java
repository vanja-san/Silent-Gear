/*
 * CraftingItems
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.silentchaos512.lib.item.IEnumItems;

import javax.annotation.Nonnull;

public enum CraftingItems implements IEnumItems<CraftingItems, Item> {
    BLUEPRINT_PAPER,
    UPGRADE_BASE,
    STONE_ROD,
    IRON_ROD,
    DIAMOND_SHARD,
    EMERALD_SHARD,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER,
    FLAX_FIBER;

    private Item item;

    @Nonnull
    @Override
    public CraftingItems getEnum() {
        return this;
    }

    @Nonnull
    @Override
    public Item getItem() {
        if (item == null) item = new Item();
        return item;
    }
}
