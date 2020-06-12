package net.silentchaos512.gear.gear.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;

public class LazyMaterialInstance implements IMaterialInstance {
    private final ResourceLocation materialId;
    private final MaterialGrade grade;

    public LazyMaterialInstance(ResourceLocation materialId) {
        this(materialId, MaterialGrade.NONE);
    }

    public LazyMaterialInstance(ResourceLocation materialId, MaterialGrade grade) {
        this.materialId = materialId;
        this.grade = grade;
    }

    @Override
    public ResourceLocation getMaterialId() {
        return materialId;
    }

    @Nullable
    @Override
    public IMaterial getMaterial() {
        return MaterialManager.get(materialId);
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getItem() {
        IMaterial material = getMaterial();
        return material != null ? MaterialInstance.of(material).getItem() : ItemStack.EMPTY;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("ID", materialId.toString());
        nbt.putString("Grade", grade.name());
        return nbt;
    }

    @Override
    public int getColor(PartType partType, ItemStack gear) {
        IMaterial material = getMaterial();
        return material != null ? material.getColor(gear, partType) : Color.VALUE_WHITE;
    }
}