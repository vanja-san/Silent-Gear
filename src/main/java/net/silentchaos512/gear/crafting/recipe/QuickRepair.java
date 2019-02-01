/*
 * Silent Gear -- QuickRepair
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;

@SuppressWarnings("unused")
public class QuickRepair implements IRecipe {
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ItemStack gear = ItemStack.EMPTY;
        int partsCount = 0;

        // Need 1 gear and 1+ parts
        for (ItemStack stack : StackList.from(inv)) {
            if (stack.getItem() instanceof ICoreItem) {
                if (gear.isEmpty())
                    gear = stack;
                else
                    return false;
            } else if (PartManager.from(stack) != null) {
                ++partsCount;
                // It needs to be a part with repair value
                PartData data = PartData.from(stack);
                if (data == null || data.getRepairAmount(gear, RepairContext.Type.QUICK) <= 0)
                    return false;
            } else {
                return false;
            }
        }

        return !gear.isEmpty() && partsCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class).copy();
        Collection<ItemStack> parts = list.allMatches(s -> PartManager.from(s) != null);

        if (gear.isEmpty() || parts.isEmpty()) return ItemStack.EMPTY;

        float repairValue = 0f;
        int materialCount = 0;
        for (ItemStack stack : parts) {
            PartData data = PartData.from(stack);
            if (data != null) {
                repairValue += data.getRepairAmount(gear, RepairContext.Type.QUICK);
                ++materialCount;
            }
        }

        // Makes odd repair values line up better
        repairValue += 1;

        // Repair efficiency instance tool class
        if (gear.getItem() instanceof ICoreItem)
            repairValue *= GearData.getStat(gear, CommonItemStats.REPAIR_EFFICIENCY);

        gear.attemptDamageItem(-Math.round(repairValue), SilentGear.random, null);
//            GearStatistics.incrementStat(gear, "silentgear.repair_count", materialCount);
        GearData.incrementRepairCount(gear, materialCount);
        GearData.recalculateStats(null, gear);
        return gear;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return Serializer.INSTANCE.getName();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements IRecipeSerializer<QuickRepair> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public QuickRepair read(ResourceLocation recipeId, JsonObject json) {
            return new QuickRepair();
        }

        @Override
        public QuickRepair read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new QuickRepair();
        }

        @Override
        public void write(PacketBuffer buffer, QuickRepair recipe) { }

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(SilentGear.MOD_ID, "quick_repair");
        }
    }
}
