/*
 * Silent Gear -- GearPartIngredient
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

package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.parts.PartManager;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class GearPartIngredient extends Ingredient {
    private final PartType type;

    public GearPartIngredient(PartType type) {
        super(Stream.of());
        this.type = type;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        IGearPart part = PartManager.from(stack);
        return part != null && part.getType().equals(type);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return super.getMatchingStacks();
    }

    public static class Serializer implements IIngredientSerializer<GearPartIngredient> {
        @Override
        public GearPartIngredient parse(PacketBuffer buffer) {
            String str = buffer.readString(255);
            PartType type = PartType.get(str);
            return new GearPartIngredient(type);
        }

        @Override
        public GearPartIngredient parse(JsonObject json) {
            String typeName = JsonUtils.getString(json, "part_type", "");
            if (typeName.isEmpty())
                throw new JsonSyntaxException("'part_type' is missing");

            PartType type = PartType.get(typeName);
            if (type == null)
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");

            return new GearPartIngredient(type);
        }

        @Override
        public void write(PacketBuffer buffer, GearPartIngredient ingredient) {
            buffer.writeString(ingredient.type.getName());
        }
    }
}
