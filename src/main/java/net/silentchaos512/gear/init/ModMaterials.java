package net.silentchaos512.gear.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModMaterials implements IPhasedInitializer {

    public static final ModMaterials INSTANCE = new ModMaterials();

    public static PartMain mainWood, mainStone, mainFlint, mainIron, mainGold, mainEmerald, mainDiamond, mainTest;
    public static PartRod rodWood, rodBone, rodStone, rodIron;
    public static PartTip tipIron, tipGold, tipDiamond, tipEmerald, tipRedstone, tipGlowstone, tipLapis;
    public static PartBowstring bowstringString, bowstringSinew;

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {

        mainWood = PartRegistry.putPart(new PartMain(getPath("main_wood")));
        mainStone = PartRegistry.putPart(new PartMain(getPath("main_stone")));
        mainFlint = PartRegistry.putPart(new PartMain(getPath("main_flint")));
        mainIron = PartRegistry.putPart(new PartMain(getPath("main_iron")));
        mainGold = PartRegistry.putPart(new PartMain(getPath("main_gold")));
        mainEmerald = PartRegistry.putPart(new PartMain(getPath("main_emerald")));
        mainDiamond = PartRegistry.putPart(new PartMain(getPath("main_diamond")));
        //mainTest = PartRegistry.putPart(new PartMain(getPath("main_test")));

        rodWood = PartRegistry.putPart(new PartRod(getPath("rod_wood")));
        rodBone = PartRegistry.putPart(new PartRod(getPath("rod_bone")));
        rodStone = PartRegistry.putPart(new PartRod(getPath("rod_stone")));
        rodIron = PartRegistry.putPart(new PartRod(getPath("rod_iron")));

        tipIron = PartRegistry.putPart(new PartTip(getPath("tip_iron")));
        tipGold = PartRegistry.putPart(new PartTip(getPath("tip_gold")));
        tipDiamond = PartRegistry.putPart(new PartTip(getPath("tip_diamond")));
        tipEmerald = PartRegistry.putPart(new PartTip(getPath("tip_emerald")));
        tipRedstone = PartRegistry.putPart(new PartTip(getPath("tip_redstone")));
        tipGlowstone = PartRegistry.putPart(new PartTip(getPath("tip_glowstone")));
        tipLapis = PartRegistry.putPart(new PartTip(getPath("tip_lapis")));

        bowstringString = PartRegistry.putPart(new PartBowstring(getPath("bowstring_string")));
        bowstringSinew = PartRegistry.putPart(new PartBowstring(getPath("bowstring_sinew")));

        UserDefined.loadUserParts();
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        // Update part caches
        // All mods should have added their parts during pre-init
        PartRegistry.resetVisiblePartCaches();
        GearHelper.resetSubItemsCache();
    }

    private static ResourceLocation getPath(String key) {
        return new ResourceLocation(SilentGear.MOD_ID, key);
    }

    private static final class UserDefined {

        static void loadUserParts() {
            final File directory = new File(Config.INSTANCE.getDirectory(), "materials");
            final File[] files = directory.listFiles();
            if (!directory.isDirectory() || files == null) {
                SilentGear.log.warn("File \"{}\" is not a directory?", directory);
                return;
            }

            final Pattern typeRegex = Pattern.compile("^[a-z]+");
            for (File file : files) {
                SilentGear.log.info("Material file found: {}", file);
                String name = file.getName().replace(".json", "");
                ResourceLocation path = getPath(name);

                // Add to registered parts if it doesn't exist
                if (!PartRegistry.getKeySet().contains(path.toString())) {
                    Matcher match = typeRegex.matcher(name);
                    if (match.find()) {
                        String type = match.group();
                        SilentGear.log.info("Trying to add part {}, type {}", path, type);
                        if ("main".equals(type))
                            PartRegistry.putPart(new PartMain(path));
                        else if ("rod".equals(type))
                            PartRegistry.putPart(new PartRod(path));
                        else if ("bowstring".equals(type))
                            PartRegistry.putPart(new PartBowstring(path));
                        else if ("tip".equals(type))
                            PartRegistry.putPart(new PartTip(path));
                        else if ("grip".equals(type))
                            PartRegistry.putPart(new PartGrip(path));
                        else
                            SilentGear.log.warn("Unknown part type \"{}\" for {}", type, name);
                    }
                } else {
                    SilentGear.log.info("Part already registered. Must be an override.");
                }
            }
        }
    }
}
