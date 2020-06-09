package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class PartItem extends Item {
    private static final String NBT_COLOR = "Color";
    private static final String NBT_MATERIALS = "Materials";

    private final ResourceLocation partId;
    private final PartType partType;

    public PartItem(ResourceLocation partId, PartType partType, Properties properties) {
        super(properties);
        this.partId = partId;
        this.partType = partType;
    }

    public PartType getPartType() {
        return partType;
    }

    public ItemStack createFromItems(Collection<ItemStack> materials) {
        // TODO: Ignores invalid items, is that the best thing to do?
        return create(materials.stream()
                .map(MaterialManager::from)
                .filter(Objects::nonNull)
                .map(MaterialInstance::of)
                .collect(Collectors.toList()));
    }

    public ItemStack create(Collection<MaterialInstance> materials) {
        ListNBT materialListNbt = new ListNBT();
        materials.forEach(m -> materialListNbt.add(m.write(new CompoundNBT())));

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);
        tag.putInt(NBT_COLOR, calculateBlendedColor(materials));
        ItemStack result = new ItemStack(this);
        result.setTag(tag);

        return result;
    }

    public static Collection<MaterialInstance> getMaterials(ItemStack stack) {
        ListNBT materialListNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, 10);
        return materialListNbt.stream()
                .filter(nbt -> nbt instanceof CompoundNBT)
                .map(nbt -> (CompoundNBT) nbt)
                .map(MaterialInstance::read)
                .collect(Collectors.toList());
    }

    public static int getColor(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_COLOR);
    }

    private int calculateBlendedColor(Collection<MaterialInstance> materials) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (MaterialInstance mat : materials) {
            int color = mat.getMaterial().getColor(ItemStack.EMPTY, partType);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            // Add earlier colors multiple times, to give them greater weight
            int colorWeight = (materials.size() - i) * (materials.size() - i);
            for (int j = 0; j < colorWeight; ++j) {
                maxColorSum += Math.max(r, Math.max(g, b));
                componentSums[0] += r;
                componentSums[1] += g;
                componentSums[2] += b;
                ++colorCount;
            }
            ++i;
        }

        if (colorCount > 0) {
            int r = componentSums[0] / colorCount;
            int g = componentSums[1] / colorCount;
            int b = componentSums[2] / colorCount;
            float maxAverage = (float) maxColorSum / (float) colorCount;
            float max = (float) Math.max(r, Math.max(g, b));
            r = (int) ((float) r * maxAverage / max);
            g = (int) ((float) g * maxAverage / max);
            b = (int) ((float) b * maxAverage / max);
            int finalColor = (r << 8) + g;
            finalColor = (finalColor << 8) + b;
            return finalColor;
        }

        return Color.VALUE_WHITE;
    }
}