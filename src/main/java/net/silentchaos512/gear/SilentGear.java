package net.silentchaos512.gear;

import net.minecraft.init.Items;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;

@Mod(SilentGear.MOD_ID)
public class SilentGear {
    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";
    public static final String VERSION = "0.5.0";
    public static final int BUILD_NUM = 0;

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    static {
        CommonItemStats.init();
    }

    public static final Random random = new Random();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static SilentGear INSTANCE;
    public static SideProxy PROXY;

    public SilentGear() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
    }

    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(SilentGear.MOD_ID);
        if (o.isPresent()) return o.get().getModInfo().getVersion().toString();
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        // TODO
        return true;
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.BEEF); // FIXME
        }
    };
}
